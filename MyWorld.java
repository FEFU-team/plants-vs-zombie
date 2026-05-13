import greenfoot.*;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private Actor sunCounterDisplay;
    private boolean isPaused = true;

    public MyWorld() {
        super(600, 400, 1);
        Greenfoot.setSpeed(50);

        setPaintOrder(Sun.class, Zombie.class, Plant.class);

        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");
        addObject(new AnimatedActor(reanimManager, "REANIM_PEASHOOTERSINGLE", "anim_full_idle"), 80, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_SUNFLOWER", "anim_idle"), 160, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_BLOVER", "anim_idle"), 240, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_CACTUS", "anim_idle"), 320, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_CHOMPER", "anim_chew"), 400, 0);

        addObject(new SunFlower(reanimManager), 0, 240);
        addObject(new SunFlower(reanimManager), 100, 120);

        {
            // TODO: create subclasses for diffreent types of zombies
            // TODO: maybe add pauses in move cycle like in original
            var zombie = new Zombie(reanimManager, "REANIM_ZOMBIE_PAPER", 200, 1 / 4.7f);
            zombie.setReanimSpeed(1.4f);
            addObject(zombie, 280, 70);
        }
        {
            var zombie = new Zombie(reanimManager, "REANIM_ZOMBIE_FOOTBALL", 200, 1 / 2.5f);
            zombie.setReanimSpeed(1.4f);
            addObject(zombie, 240, 190);
        }

        // Инициализация системы солнышек
        sunManager = new SunManager(this, reanimManager);
        sunCounterDisplay = new Actor() {};
        sunCounterDisplay.setImage(sunManager.getCounterImage());
        addObject(sunCounterDisplay, 40, 40);
    }

    @Override
    public void stopped() {
        isPaused = true;
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStop();
        }
    }

    @Override
    public void started() {
        isPaused = false;
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStart();
        }
    }

    @Override
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

    @Override
    public void addObject(Actor actor, int x, int y) {
        super.addObject(actor, x, y);
        if (!isPaused && actor instanceof BaseActor actorWithLifecycle) {
            actorWithLifecycle.lifecycleStart();
        }
    }

    public SunManager getSunManager() {
        return sunManager;
    }
}
