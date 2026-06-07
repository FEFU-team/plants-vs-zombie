import greenfoot.*;
import java.util.*;

public class SelectorScreen extends AnimatedActor {
    public static final float POSITION_X = 0;
    public static final float POSITION_Y = 0;
    
    public enum Button {
        StartAdventure("StartAdventure", "STARTADVENTURE_BUTTON1", "STARTADVENTURE_HIGHLIGHT"),
        Adventure("Adventure", "ADVENTURE_BUTTON", "ADVENTURE_HIGHLIGHT"),
        MiniGames("Survival", "SURVIVAL_BUTTON", "SURVIVAL_HIGHLIGHT"),
        Puzzle("Challenges", "CHALLENGES_BUTTON", "CHALLENGES_HIGHLIGHT"),
        Survival("ZenGarden", "VASEBREAKER_BUTTON", "VASEBREAKER_HIGHLIGHT"),
        Help("Help", "HELP1", "HELP2", false);
        
        private final String reanimTrackButtonName;
        private final String inactiveImageKey;
        private final String activeImageKey;
        private final boolean reanimPrefix;
        
        Button(String reanimTrackButtonName, String inactiveImageKey, String activeImageKey) {
            this(reanimTrackButtonName, inactiveImageKey, activeImageKey, true);
        }
        
        Button(String reanimTrackButtonName, String inactiveImageKey, String activeImageKey, boolean reanimPrefix) {
            this.reanimTrackButtonName = reanimTrackButtonName;
            this.reanimPrefix = reanimPrefix;
            this.inactiveImageKey = (reanimPrefix ? "IMAGE_REANIM_SELECTORSCREEN_" : "IMAGE_SELECTORSCREEN_") + inactiveImageKey;
            this.activeImageKey = (reanimPrefix ? "IMAGE_REANIM_SELECTORSCREEN_" : "IMAGE_SELECTORSCREEN_") + activeImageKey;
        }
        
        public String getReanimTrackButtonName() {
            return reanimTrackButtonName;
        }
        
        public String getInactiveImageKey() {
            return inactiveImageKey;
        }
        
        public String getActiveImageKey() {
            return activeImageKey;
        }
    };
    
    private boolean openAnimationFinished = false;
    private Set<Button> shownButtons = EnumSet.allOf(Button.class);
    private Map<Button, Runnable> buttonCallbacks = new EnumMap<>(Button.class);
    private Button currentHoveredButton;
    private Runnable onOpenFinishedCallback;
    
    private boolean helpVisible = false;
    private boolean helpHovered = false;
    private static final int HELP_X = 740;
    private static final int HELP_Y = 520;
    
    public SelectorScreen(ReanimManager manager) {
        super(manager, "REANIM_SELECTORSCREEN");
        setReanimState("anim_open", false);
        
        for (int i = 1; i <= 7; ++i) {
            addReanimExtraState(
                new AnimationStateBuilder()
                    .name("anim_cloud" + i)
                    .independent(true)
                    .speed(0.25f)
                    .build()
            );
        }
        addReanimExtraState(new AnimationStateBuilder().name("anim_grass").speed(0.5f).build());
        setReanimSpeed(0.7f);
        
        hideButton(Button.Adventure);
        hideButton(Button.Help);
        
        setCanvas(new GreenfootImage(1600, 1200));
        updateFrame();
    }
    
    public void hideButton(Button button) {
        if (!shownButtons.contains(button)) return;
        
        if (button == Button.Help) {
            helpVisible = false;
            shownButtons.remove(button);
            return;
        }
        
        var reanimTrackButtonName = button.getReanimTrackButtonName();
        
        hideLayer("SelectorScreen_" + reanimTrackButtonName + "_shadow");
        hideLayer("SelectorScreen_" + reanimTrackButtonName + "_button");
        
        shownButtons.remove(button);
    }
    
    public void showButton(Button button) {
        if (shownButtons.contains(button)) return;
        
        if (button == Button.Help) {
            helpVisible = true;
            shownButtons.add(button);
            return;
        }
        
        var reanimTrackButtonName = button.getReanimTrackButtonName();
        
        unhideLayer("SelectorScreen_" + reanimTrackButtonName + "_shadow");
        unhideLayer("SelectorScreen_" + reanimTrackButtonName + "_button");
        
        shownButtons.add(button);
    }
    
    public void highlightButton(Button button) {
        addImageSwap(button.getInactiveImageKey(), button.getActiveImageKey());
    }
    
    public void darkenButton(Button button) {
        removeImageSwap(button.getInactiveImageKey());
    }
    
    public void addButtonCallback(Button button, Runnable callback) {
        buttonCallbacks.put(button, callback);
    }
    
    public void setOnOpenAnimationFinished(Runnable callback) {
        this.onOpenFinishedCallback = callback;
    }
    
    public boolean isOpenAnimationFinished() {
        return openAnimationFinished;
    }
    
    @Override
    public void act() {
        super.act();
        
        if (!openAnimationFinished && isMainReanimFinished()) {
            setReanimState(
                new AnimationStateBuilder()
                    .name("anim_open")
                    .isStatic(true)
                    .initFrame(reanimManager.getLastFrame(getReanimKey(), "anim_open"))
                    .build()
            );
            openAnimationFinished = true;
            if (onOpenFinishedCallback != null) {
                onOpenFinishedCallback.run();
            }
        }
        
        if (openAnimationFinished) {
            var mouseInfo = Greenfoot.getMouseInfo();
            Button newHovered = null;
            
            if (mouseInfo != null) {
                var mx = mouseInfo.getX();
                var my = mouseInfo.getY();
                
                for (var button : shownButtons) {
                    if (button == Button.Help) {
                        if (mx >= HELP_X - 24 && mx <= HELP_X + 24 && my >= HELP_Y - 11 && my <= HELP_Y + 11) {
                            newHovered = button;
                            break;
                        }
                    } else {
                        if (checkHoverLayer(mx, my, "SelectorScreen_" + button.getReanimTrackButtonName() + "_button")) {
                            newHovered = button;
                            break;
                        }
                    }
                }
            }
            
            helpHovered = (newHovered == Button.Help);
            
            if (newHovered != currentHoveredButton) {
                if (currentHoveredButton != null) darkenButton(currentHoveredButton);
                if (newHovered != null) highlightButton(newHovered);
                currentHoveredButton = newHovered;
            }
            
            if (currentHoveredButton != null && Greenfoot.mouseClicked(null)) {
                var callback = buttonCallbacks.get(currentHoveredButton);
                if (callback != null) callback.run();
            }
        }
        
        drawHelpButton();
    }
    
    private void drawHelpButton() {
        if (!helpVisible) return;
        
        String key = helpHovered ? Button.Help.getActiveImageKey() : Button.Help.getInactiveImageKey();
        var sprite = reanimManager.getImage(key);
        if (sprite == null) return;
        
        var canvas = getImage();
        int originX = canvas.getWidth() / 2;
        int originY = canvas.getHeight() / 2;
        
        int x = originX + HELP_X - sprite.getWidth() / 2;
        int y = originY + HELP_Y - sprite.getHeight() / 2;
        canvas.drawImage(sprite, x, y);
    }
}
