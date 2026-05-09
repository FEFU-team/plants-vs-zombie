import greenfoot.*;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private Actor sunCounterDisplay;

    public MyWorld() {
        super(600, 400, 1);
        Greenfoot.setSpeed(50);

        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");
        addObject(new AnimatedActor(reanimManager, "REANIM_PEASHOOTERSINGLE", "anim_full_idle"), 80, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_SUNFLOWER", "anim_idle"), 160, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_BLOVER", "anim_idle"), 240, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_CACTUS", "anim_idle"), 320, 0);
        addObject(new Plant(reanimManager, "REANIM_SUNFLOWER"), 0, 240);
        addObject(new Plant(reanimManager, "REANIM_SUNFLOWER"), 100, 120);
        addObject(new Zombie(reanimManager, "REANIM_ZOMBIE_PAPER",200,0.5f), 240,200);
        addObject(new Zombie(reanimManager, "REANIM_ZOMBIE_FOOTBALL",200,1.0f), 240,70);
        // Инициализация системы солнышек
        sunManager = new SunManager(this, reanimManager);
        sunCounterDisplay = new Actor() {};
        sunCounterDisplay.setImage(sunManager.getCounterImage());
        addObject(sunCounterDisplay, 40, 40); 
    }
    
    public void started() {
        for (var object : getObjects(AnimatedActor.class)) {
            object.resume();
        }
    }
    
    public void act() {
        sunManager.act();
        sunCounterDisplay.setImage(sunManager.getCounterImage());

        // Shoot example:
        if (Greenfoot.isKeyDown("space")) {
            for (var actor : getObjects(AnimatedActor.class)) {
                actor.setReanimSpecialState("anim_shooting", false);
                actor.updateFrame();
            }
        }
    }

    public SunManager getSunManager() {
        return sunManager;
    }
}
