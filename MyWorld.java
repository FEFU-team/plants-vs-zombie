import greenfoot.*;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();

    public MyWorld() {
        super(600, 400, 1);
        Greenfoot.setSpeed(50);

        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");
        
        addObject(new AnimatedActor(reanimManager, "REANIM_PEASHOOTER", "anim_full_idle"), 0, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_PEASHOOTERSINGLE", "anim_full_idle"), 80, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_SUNFLOWER", "anim_idle"), 160, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_BLOVER", "anim_idle"), 240, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_CACTUS", "anim_idle"), 320, 0);
    }
    
    public void started() {
        for (var object : getObjects(AnimatedActor.class)) {
            object.resume();
        }
    }
    
    public void act() {
        // Shoot example:
        if (Greenfoot.isKeyDown("space")) {
            for (var actor : getObjects(AnimatedActor.class)) {
                actor.setReanimSpecialState("anim_shooting", false);
                actor.updateFrame();
            }
        }
    }
}
