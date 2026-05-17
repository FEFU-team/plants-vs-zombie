import greenfoot.*;
import java.util.List;
import java.util.function.Function;
import java.util.Random;

public class MyWorld extends World {
    private static final int CELL_GRID_START_X = 170;
    private static final int CELL_GRID_START_Y = 80;
    
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private boolean isPaused = true;
    private int waves = 0;
    private int timer = 1800;
    private int messageTimer = 300;
    private int currentWave = 0;
    
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
    
    private enum ZombieType {
        Basic,
        WithCone,
        Polevaulter
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

        // Для тестов конкретный уровень и сложность
        WorldStyles style = WorldStyles.GARDEN_NIGHT;
        waves = 3;
        
        setBackground(style.getBg());
        createLawn(style);
        
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
        timer--;
        messageTimer--;
        sunManager.act();
        checkGameStatus();
        
        //Тесты
        showText("" + timer,50,20);
        showText("" + messageTimer,50,50);
        
        
        if (messageTimer < 0) {
        showText("",500,200);
        }
        if (timer <= 0) {
            createWavesOfZombies(waves);
        }
    }
    
    void checkGameStatus() {
        var zombies = this.getObjects(Zombie.class);
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
        }
    }

    @Override
    public void addObject(Actor actor, int x, int y) {
        super.addObject(actor, x, y);
        if (!isPaused && actor instanceof BaseActor actorWithLifecycle) {
            actorWithLifecycle.lifecycleStart();
        }
    }
    
    public void createWavesOfZombies(int waves) {
        List<Zombie> zombies = this.getObjects(Zombie.class);
        if (zombies.isEmpty() && currentWave < waves) {
            timer = 1800;
            currentWave++;
            showText("The wave "+currentWave+ " has begun!",500,200);
            messageTimer = 300;
            Random random = new Random();
            ZombieType[] types = ZombieType.values();
            for (int i = 0; i < random.nextInt(8); i++) {
                ZombieType type = types[random.nextInt(types.length)];
                int cellIndex = random.nextInt(4);
                switch (type) {
                    case Basic:
                        addObject(new BasicZombie(reanimManager),1010+i*CELL_GRID_START_Y/5,CELL_GRID_START_Y + (int)(2.1 * Cell.HEIGHT) - (int)Zombie.TOP_HEIGHT + CELL_GRID_START_Y*(cellIndex));
                        break;
                    case WithCone:
                        addObject(new ZombieWithCone(reanimManager),1005+i*CELL_GRID_START_Y/5,CELL_GRID_START_Y + (int)(0.1 * Cell.HEIGHT) - (int)Zombie.TOP_HEIGHT + CELL_GRID_START_Y*(cellIndex));
                        break;
                    case Polevaulter:
                        addObject(new ZombiePolevaulter(reanimManager),1008+i*CELL_GRID_START_Y/5,CELL_GRID_START_Y + (int)(2.1 * Cell.HEIGHT) - (int)Zombie.TOP_HEIGHT + CELL_GRID_START_Y*(cellIndex));
                        break;
                }
            } 
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
            
            addObject(new Door(level.name()), 145, 335);
            
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