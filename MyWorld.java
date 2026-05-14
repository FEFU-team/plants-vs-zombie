import greenfoot.*;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private boolean isPaused = true;

    public MyWorld() {
        super(600, 400, 1);
        Greenfoot.setSpeed(50);

        setPaintOrder(HitboxMap.class, Sun.class, Zombie.class, Plant.class);

        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");

        /*addObject(new AnimatedActor(reanimManager, "REANIM_PEASHOOTERSINGLE", "anim_full_idle"), 80, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_SUNFLOWER", "anim_idle"), 160, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_BLOVER", "anim_idle"), 240, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_CACTUS", "anim_idle"), 320, 0);
        addObject(new AnimatedActor(reanimManager, "REANIM_CHOMPER", "anim_chew"), 400, 0);*/

        addObject(new PeaShooter(reanimManager), 100, 120);
        addObject(new SunFlower(reanimManager), 190, 120);
        addObject(new WallNut(reanimManager), 280, 120);
        addObject(new SunFlower(reanimManager), 100, 210);
        addObject(new PotatoMine(reanimManager), 100, 300);
        addObject(new Chomper(reanimManager), 190, 300);
        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"), 0, 120);
        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"), 0, 300);

        {
            // TODO: create subclasses for diffreent types of zombies
            // TODO: maybe add pauses in move cycle like in original
            var zombie = new Zombie(reanimManager, "REANIM_ZOMBIE_PAPER", 200, 1 / 4.7f);
            zombie.setReanimSpeed(1.4f);
            addObject(zombie, 400, 120 - (int)Zombie.TOP_HEIGHT);
        }
        {
            var zombie = new Zombie(reanimManager, "REANIM_ZOMBIE_FOOTBALL", 200, 1 / 2.5f);
            zombie.setReanimSpeed(1.4f);
            addObject(zombie, 240, 210 - (int)Zombie.TOP_HEIGHT);
        }

        //addObject(new ZombiesWon(reanimManager), 400, 120);

        // Инициализация системы солнышек
        sunManager = new SunManager(this, reanimManager);
        
        // Debug: draw hitboxes
        addObject(new HitboxMap(), getWidth() / 2, getHeight() / 2);
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
