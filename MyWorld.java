import greenfoot.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private SeedBank seedBank;
    private Level level;
    private boolean isPaused = true;
    private boolean isStopped = false;
    private java.awt.Rectangle[][] cellGrid = new java.awt.Rectangle[9][5];
    private SeedBank.Seed selectedSeed = null;

    public MyWorld() {
        super(1000, 600, 1);
        Greenfoot.setSpeed(50);
        
        setPaintOrder(
            HitboxMap.class,
            ZombiesWon.class,
            Sun.class,
            PeaProjectile.class,
            Zombie.class,
            Plant.class
        );

        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images", "IMAGE_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");

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
        
        //некоторые растения для тестов
        growPlant(SunFlower::new, 1, 0);
        growPlant(SunFlower::new, 2, 0);
        growPlant(SunFlower::new, 3, 0);
        growPlant(PeaShooter::new, 1, 1);
        growPlant(PeaShooter::new, 2, 1);
        growPlant(SunFlower::new, 3, 1);
        growPlant(PeaShooter::new, 2, 2);
        growPlant(SunFlower::new, 3, 2);
        growPlant(SunFlower::new, 3, 3);
        growPlant(SunFlower::new, 3, 4);
        growPlant(Chomper::new, 4, 3);

        sunManager = new SunManager(this, reanimManager);
        
        var seeds = new ArrayList<SeedBank.SeedType>();
        seeds.add(SeedBank.SeedType.SunFlower);
        seeds.add(SeedBank.SeedType.PeaShooter);
        seeds.add(SeedBank.SeedType.WallNut);
        seeds.add(SeedBank.SeedType.PotatoMine);
        seeds.add(SeedBank.SeedType.Chomper);
        seedBank = new SeedBank(this, sunManager, reanimManager, seeds);
        // Debug: draw hitboxes
        var hitboxMap = new HitboxMap();
        hitboxMap.toggleAttackBoxes(true);
        hitboxMap.toggleCellBoxes(true);
        addObject(hitboxMap, getWidth() / 2, getHeight() / 2);
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 5; ++j) {
                int cellLeftX = Level.CELL_GRID_START_X + i * Cell.WIDTH;
                int cellTopY = Level.CELL_GRID_START_Y + j * Cell.HEIGHT;
                
                cellGrid[i][j] = new java.awt.Rectangle(cellLeftX, cellTopY, Cell.WIDTH, Cell.HEIGHT);
            }
        }
    }

    @Override
    public void stopped() {
        isPaused = true;
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStop();
        }
        
        sunManager.lifecycleStop();
        seedBank.lifecycleStop();
        level.lifecycleStop();
    }

    @Override
    public void started() {
        isPaused = false;
        for (var actor : getObjects(BaseActor.class)) {
            actor.lifecycleStart();
        }
        
        sunManager.lifecycleStart();
        seedBank.lifecycleStart();
        level.lifecycleStart();
    }

    @Override
    public void act() {
        sunManager.act();
        seedBank.act();
        level.act();
    }
    public void setSelectedSeed(SeedBank.Seed seed) {
        this.selectedSeed = seed;
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
        addObject(create.apply(reanimManager), (float)Level.CELL_GRID_START_X + x * Cell.WIDTH, (float)Level.CELL_GRID_START_Y + y * Cell.HEIGHT);
    }
    
    public boolean gameIsStopped() {
        return isStopped;
    }
    
    private Stream<BaseActor> stopGameActors() {
        return Stream.of(
            getObjects(LawnMower.class).stream(),
            getObjects(Zombie.class).stream(),
            getObjects(Plant.class).stream(),
            getObjects(Sun.class).stream(),
            getObjects(PeaProjectile.class).stream()
        ).flatMap(s -> s);
    }
    
    public void stopGame() {
        isStopped = true;
        
        sunManager.lifecycleStop();
        level.lifecycleStop();
        stopGameActors().forEach(BaseActor::lifecycleStop);
    }
    
    public void resumeGame() {
        isStopped = false;
        
        sunManager.lifecycleStart();
        level.lifecycleStart();
        stopGameActors().forEach(BaseActor::lifecycleStart);
    }

    public SunManager getSunManager() {
        return sunManager;
    }

    public Level getLevel() {
        return level;
    }
}