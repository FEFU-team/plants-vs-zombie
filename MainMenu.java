import greenfoot.*;

import java.io.File;

public class MainMenu extends World {
    private ReanimManager reanimManager = new ReanimManager();
    
    public MainMenu()  {
        super(800, 600, 1);
        Greenfoot.setSpeed(50);
        
        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images", "IMAGE_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");
        
        var selectorScreen = new SelectorScreen(reanimManager);
        selectorScreen.hideButton(SelectorScreen.Button.MiniGames);
        selectorScreen.hideButton(SelectorScreen.Button.Puzzle);
        selectorScreen.hideButton(SelectorScreen.Button.Survival);
        selectorScreen.updateFrame();
        addObject(selectorScreen, (int)SelectorScreen.POSITION_X, (int)SelectorScreen.POSITION_Y);
        
        selectorScreen.addButtonCallback(
            SelectorScreen.Button.StartAdventure,
            () -> Greenfoot.setWorld(new LevelWorld(reanimManager))
        );
    }

    @Override
    public void stopped() {
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStop();
        }
    }

    @Override
    public void started() {
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStart();
        }
    }
}