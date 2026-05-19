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
                .addWave(6, 10.f)
                .addWave(10, 180.f)
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

        /*{
            // TODO: maybe add pauses in move cycle like in original
            var zombie = new ZombieWithCone(reanimManager);
            addObject(zombie, CELL_GRID_START_X + 9 * Cell.WIDTH, CELL_GRID_START_Y + (int)(0.1 * Cell.HEIGHT) - (int)Zombie.TOP_HEIGHT);
        }
        {
            var zombie = new BasicZombie(reanimManager);
            addObject(zombie, CELL_GRID_START_X + 9 * Cell.WIDTH, CELL_GRID_START_Y + (int)(3.1 * Cell.HEIGHT) - (int)Zombie.TOP_HEIGHT);
        }
        {
            var zombie = new ZombiePolevaulter(reanimManager);
            addObject(zombie, CELL_GRID_START_X + 6 * Cell.WIDTH, CELL_GRID_START_Y + (int)(2.1 * Cell.HEIGHT) - (int)Zombie.TOP_HEIGHT);
        }*/

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
        
        level.lifecycleStop();
    }

    @Override
    public void started() {
        isPaused = false;
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStart();
        }
        
        level.lifecycleStart();
    }

    @Override
    public void act() {
        sunManager.act();
        level.act();
        checkGameStatus();
    }
    
    void checkGameStatus() {
        // TODO: move to Level and rewrite
        /*var zombies = this.getObjects(Zombie.class);
        if (zombies.isEmpty() && currentWave == waves) {
            showText("Victory!", 500, 300);
            Greenfoot.stop();
        }
        
        for (Zombie zombie : zombies) {
            if (zombie.isZombieWon()) {
                this.removeObject(zombie);
                showText("The Zombies Ate Your Brain!", 500, 300);
                Greenfoot.stop();
                
            }
        }*/
    }

    @Override
    public void addObject(Actor actor, int x, int y) {
        super.addObject(actor, x, y);
        if (!isPaused && actor instanceof BaseActor actorWithLifecycle) {
            actorWithLifecycle.lifecycleStart();
        }
    }
    
    void growPlant(Function<ReanimManager, ? extends Plant> create, int x, int y) {
        // TODO: move to Level
        //addObject(create.apply(reanimManager), CELL_GRID_START_X + (x + 1) * Cell.WIDTH, CELL_GRID_START_Y + y * Cell.HEIGHT);
    }

    public SunManager getSunManager() {
        return sunManager;
    }
}