import greenfoot.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private Level level;
    private boolean isPaused = true;

    public MyWorld() {
        super(1000, 600, 1);
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

        // Для тестов конкретный уровень и сложность
        level = new Level(
            this,
            reanimManager,
            new Level.WavesBuilder()
                .addWave(6, 5.f)
                .addWave(10, 40.f)
                .build()
        );
        level.setStyle(Level.Style.GARDEN_NIGHT);
        level.createLawn();
        // TODO: random single zombies between waves
        // TODO: wave timeline visualization
        // TODO: specify types and probabilities of zombies in wave
        
        /*growPlant(SunFlower::new, 0, 0);
        growPlant(PeaShooter::new, 1, 0);
        growPlant(WallNut::new, 5, 0);
        growPlant(WallNut::new, 3, 2);
        growPlant(SunFlower::new, 0, 3);
        growPlant(PotatoMine::new, 5, 3);
        growPlant(Chomper::new, 4, 3);*/

        sunManager = new SunManager(this, reanimManager);

        // Debug: draw hitboxes
        var hitboxMap = new HitboxMap();
        hitboxMap.toggleAttackBoxes(true);
        hitboxMap.toggleCellBoxes(true);
        addObject(hitboxMap, getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void stopped() {
        isPaused = true;
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStop();
        }
        
        sunManager.lifecycleStop();
        level.lifecycleStop();
    }

    @Override
    public void started() {
        isPaused = false;
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStart();
        }
        
        sunManager.lifecycleStart();
        level.lifecycleStart();
    }

    @Override
    public void act() {
        sunManager.act();
        level.act();
    }

    @Override
    public void addObject(Actor actor, int x, int y) {
        super.addObject(actor, x, y);
        if (!isPaused && actor instanceof BaseActor actorWithLifecycle) {
            actorWithLifecycle.lifecycleStart();
        }
    }

    public void addObject(BaseActor actor, float x, float y) {
        super.addObject(actor, (int)x, (int)y);
        actor.setLocation(x, y);
        if (!isPaused) {
            actor.lifecycleStart();
        }
    }
    
    void growPlant(Function<ReanimManager, ? extends Plant> create, int x, int y) {
        // TODO: move to Level
        //addObject(create.apply(reanimManager), CELL_GRID_START_X + (x + 1) * Cell.WIDTH, CELL_GRID_START_Y + y * Cell.HEIGHT);
    }

    public SunManager getSunManager() {
        return sunManager;
    }

    public Level getLevel() {
        return level;
    }
}