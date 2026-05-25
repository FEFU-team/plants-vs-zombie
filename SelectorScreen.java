import greenfoot.*;
import java.util.*;

public class SelectorScreen extends AnimatedActor {
    public static final float POSITION_X = 0;
    public static final float POSITION_Y = 0;
    
    public enum Button {
        // I don't know why there are so strange image names)
        StartAdventure("StartAdventure", "STARTADVENTURE_BUTTON1", "STARTADVENTURE_HIGHLIGHT"),
        Adventure("Adventure", "ADVENTURE_BUTTON", "ADVENTURE_HIGHLIGHT"),
        MiniGames("Survival", "SURVIVAL_BUTTON", "SURVIVAL_HIGHLIGHT"),
        Challenges("Challenges", "CHALLENGES_BUTTON", "CHALLENGES_HIGHLIGHT"),
        Survival("ZenGarden", "VASEBREAKER_BUTTON", "VASEBREAKER_HIGHLIGHT");
        
        private final String reanimTrackButtonName;
        private final String inactiveImageKey;
        private final String activeImageKey;
        
        Button(String reanimTrackButtonName, String inactiveImageKey, String activeImageKey) {
            this.reanimTrackButtonName = reanimTrackButtonName;
            this.inactiveImageKey = "IMAGE_REANIM_SELECTORSCREEN_" + inactiveImageKey;
            this.activeImageKey = "IMAGE_REANIM_SELECTORSCREEN_" + activeImageKey;
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
    private EnumSet<Button> shownButtons = EnumSet.allOf(Button.class);
    private Button currentHoveredButton;
    
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
        
        for (var button : Button.values()) {
            //hideButton(button);
        }
        
        setCanvas(new GreenfootImage(1600, 1200));
        updateFrame();
    }
    
    public void hideButton(Button button) {
        if (shownButtons.contains(button)) return;
        
        var reanimTrackButtonName = button.getReanimTrackButtonName();
        
        hideLayer("SelectorScreen_" + reanimTrackButtonName + "_shadow");
        hideLayer("SelectorScreen_" + reanimTrackButtonName + "_button");
        
        shownButtons.remove(button);
    }
    
    public void showButton(Button button) {
        if (!shownButtons.contains(button)) return;
        
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
        }
        
        if (openAnimationFinished) {
            var mouseInfo = Greenfoot.getMouseInfo();
            Button newHovered = null;
            
            if (mouseInfo != null) {
                var mx = mouseInfo.getX();
                var my = mouseInfo.getY();
                
                for (var button : shownButtons) {
                    if (checkHoverLayer(mx, my, "SelectorScreen_" + button.getReanimTrackButtonName() + "_button")) {
                        newHovered = button;
                        break;
                    }
                }
            }
            
            if (newHovered != currentHoveredButton) {
                if (currentHoveredButton != null) darkenButton(currentHoveredButton);
                if (newHovered != null) highlightButton(newHovered);
                currentHoveredButton = newHovered;
            }
        }
    }
}
