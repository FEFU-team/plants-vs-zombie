import greenfoot.*;
import java.util.List;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();
    private SunManager sunManager;
    private Actor sunCounterDisplay;
    private GreenfootImage currentLevel;
    private boolean isPaused = true;
    private boolean inProgress = true;
    private enum WorldStyles {
        BARREN("background1unsodded.jpg"),
        DEFAULT_DAY("background1.jpg"),
        DEFAULT_NIGHT("background2.jpg"),
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
        //Для тестов конкретный уровень
        String level = "POOL_DAY";
        WorldStyles style = WorldStyles.valueOf(level);
        setBackground(style.getBg());
        createLawn(style);
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

        /*addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"), 0, 120);
        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"), 0, 210);
        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"), 0, 300);

        addObject(new PeaShooter(reanimManager), 90, 120);
        addObject(new SunFlower(reanimManager), 180, 120);
        addObject(new WallNut(reanimManager), 270, 120);
        addObject(new SunFlower(reanimManager), 90, 210);
        addObject(new WallNut(reanimManager), 270, 210);
        addObject(new PotatoMine(reanimManager), 90, 300);
        addObject(new Chomper(reanimManager), 180, 300);*/

        {
            // TODO: create subclasses for different types of zombies
            // TODO: maybe add pauses in move cycle like in original
            var zombie = new ZombieWithCone(reanimManager);
            addObject(zombie, 300, 80 - (int)Zombie.TOP_HEIGHT);
        }
        /*{
            var zombie = new ZombiePolevaulter(reanimManager);
            addObject(zombie, 400, 210 - (int)Zombie.TOP_HEIGHT);
        }*/
        {
            var zombie = new ZombiePolevaulter(reanimManager);
            addObject(zombie, 400, 300 - (int)Zombie.TOP_HEIGHT);
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
        checkGameStatus();
    }
    
    void checkGameStatus() {
        List<Zombie> zombies = this.getObjects(Zombie.class);
        for (Zombie z : zombies) {
            if (z.isZombieWon()) {
                this.removeObject(z);
                showText("The Zombies Ate Your Brain!",500,300);
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

    public SunManager getSunManager() {
        return sunManager;
    }
    
    void createLawn(WorldStyles level) {
        int dx = 80;
        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images", "IMAGE_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");
        if (level == WorldStyles.POOL_DAY || level == WorldStyles.POOL_NIGHT) {
            int dy = 90;
            addObject(new Door(level.name()),72,345);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 6; j++) {
                    if (i == 0) {
                        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"),175,85+j*dy);
                        addObject(new Cell(true),215+i*dx,130+j*dy);
                    } 
                    else {
                     addObject(new Cell(false),215+i*dx,130+j*dy);   
                    }
                }
            }
        }
        else if (level == WorldStyles.ROOF_DAY || level == WorldStyles.ROOF_NIGHT) {
                addObject(new Door(level.name()),110,135);
                addObject(new Cell(true),425,205);
            }
        else {
            int dy = 100;
            addObject(new Door(level.name()),132,345);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 5; j++) {
                    if (i == 0) {
                        addObject(new LawnMower(reanimManager, "REANIM_LAWNMOWER"),215,130+j*dy);
                        addObject(new Cell(true),215+i*dx,130+j*dy);
                    } else {
                     addObject(new Cell(false),215+i*dx,130+j*dy);   
                    }
                }
            }
        }
    }
}
