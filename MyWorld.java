import greenfoot.*;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private boolean isPaused = true;

    public MyWorld() {
        super(600, 400, 1);
        Greenfoot.setSpeed(50);

        setPaintOrder(
            HitboxMap.class,
            Sun.class,
            PeaProjectile.class,
            Zombie.class,
            Plant.class
        );

        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images", "IMAGE_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");

        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"), 0, 120);
        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"), 0, 210);
        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"), 0, 300);

        addObject(new PeaShooter(reanimManager), 90, 120);
        addObject(new SunFlower(reanimManager), 180, 120);
        addObject(new WallNut(reanimManager), 270, 120);
        addObject(new SunFlower(reanimManager), 90, 210);
        addObject(new PotatoMine(reanimManager), 90, 300);
        addObject(new Chomper(reanimManager), 180, 300);

        {
            // TODO: create subclasses for different types of zombies
            // TODO: maybe add pauses in move cycle like in original
            var zombie = new Zombie_polevaulter(reanimManager, "REANIM_ZOMBIE_POLEVAULTER", 200, 1 / 4.7f);
            zombie.setReanimSpeed(1.4f);
            addObject(zombie, 400, 120 - (int)Zombie_polevaulter.TOP_HEIGHT);
        }
        {
            var zombie = new Zombie_polevaulter(reanimManager, "REANIM_ZOMBIE_POLEVAULTER", 200, 1 / 2.5f);
            zombie.setReanimSpeed(1.4f);
            addObject(zombie, 400, 300- (int)Zombie_polevaulter.TOP_HEIGHT);
        }

        //addObject(new ZombiesWon(reanimManager), 400, 120);

        // Инициализация системы солнышек
        sunManager = new SunManager(this, reanimManager);

        // Debug: draw hitboxes
        var hitboxMap = new HitboxMap();
        hitboxMap.toggleAttackBoxes(true);
        addObject(hitboxMap, getWidth() / 2, getHeight() / 2);
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
