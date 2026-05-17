import greenfoot.*;
import java.util.List;
import java.util.function.Function;

public class MyWorld extends World {
    private static final int CELL_GRID_START_X = 170;
    private static final int CELL_GRID_START_Y = 80;
    
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private boolean isPaused = true;
    private boolean inProgress = true;

    private enum WorldStyles {
        BARREN("background1unsodded.jpg"),
        GARDEN_DAY("background1.jpg"),
        GARDEN_NIGHT("background2.jpg"),
        POOL_DAY("background3.jpg"),
        POOL_NIGHT("background4.jpg"),
        ROOF_DAY("background5.jpg"),
        ROOF_NIGHT("background6boss.jpg");
        
        private String bg;
        
        WorldStyles(String bg) {
            this.bg = bg;
        }
        
        public String getBg() {
            return bg;
        }
    }

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

        // Для тестов конкретный уровень
        WorldStyles style = WorldStyles.GARDEN_NIGHT;
        setBackground(style.getBg());
        createLawn(style);
        
        growPlant(SunFlower::new, 0, 0);
        growPlant(PeaShooter::new, 1, 0);
        growPlant(WallNut::new, 5, 0);
        growPlant(WallNut::new, 3, 2);
        growPlant(SunFlower::new, 0, 3);
        growPlant(PotatoMine::new, 5, 3);
        growPlant(Chomper::new, 4, 3);

        {
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
        }

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
        checkGameStatus();
    }
    
    void checkGameStatus() {
        var zombies = this.getObjects(Zombie.class);
        for (Zombie zombie : zombies) {
            if (zombie.isZombieWon()) {
                this.removeObject(zombie);
                showText("The Zombies Ate Your Brain!", 500, 300);
                Greenfoot.stop();
                
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
    
    void growPlant(Function<ReanimManager, ? extends Plant> create, int x, int y) {
        addObject(create.apply(reanimManager), CELL_GRID_START_X + (x + 1) * Cell.WIDTH, CELL_GRID_START_Y + y * Cell.HEIGHT);
    }

    public SunManager getSunManager() {
        return sunManager;
    }
    
    void createLawn(WorldStyles level) {
        if (level == WorldStyles.POOL_DAY || level == WorldStyles.POOL_NIGHT) {
            // addObject(new Door(level.name()), 72, 345);
            
            // TODO: maybe add but i think it's too hard for our project
            // Pool levels have 6 rows instead of 5
            // Also need to render pool, and draw swimming zombies
        } else if (level == WorldStyles.ROOF_DAY || level == WorldStyles.ROOF_NIGHT) {
            // addObject(new Door(level.name()), 110, 135);
            
            // TODO: maybe add but i think it's too hard for our project
        } else {
            final var cellGridX = CELL_GRID_START_X + Cell.WIDTH / 2;
            final var cellGridY = CELL_GRID_START_Y + Cell.HEIGHT / 2;
            
            addObject(new Door(level.name()), 132, 345);
            
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 0) {
                        var lawnMower = new LawnMower(reanimManager);
                        addObject(
                            lawnMower,
                            CELL_GRID_START_X + (int)(Cell.WIDTH * 0.1f),
                            CELL_GRID_START_Y + (int)((j + 0.3f) * Cell.HEIGHT)
                        );
                    }
                    addObject(new Cell(i == 0), cellGridX + i * Cell.WIDTH, cellGridY + j * Cell.HEIGHT);
                }
            }
        }
    }
}
