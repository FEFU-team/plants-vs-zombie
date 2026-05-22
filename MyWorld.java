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
    private PlantGhost selectedPlant;
    private PlantGhost selectedPlantGhost;

    public MyWorld() {
        super(1000, 600, 1);
        Greenfoot.setSpeed(50);
        
        setPaintOrder(
            HitboxMap.class,
            ZombiesWon.class,
            PlantGhost.class,
            PlantGhost.Transparent.class,
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
        level.growPlant(new SunFlower(reanimManager), 2, 0);
        level.growPlant(new SunFlower(reanimManager), 3, 0);
        level.growPlant(new PeaShooterRepeater(reanimManager), 1, 0);
        level.growPlant(new PeaShooter(reanimManager), 2, 1);
        level.growPlant(new SunFlower(reanimManager), 3, 1);
        level.growPlant(new PeaShooterRepeater(reanimManager), 2, 2);
        level.growPlant(new SunFlower(reanimManager), 3, 2);
        level.growPlant(new SunFlower(reanimManager), 3, 3);
        level.growPlant(new SunFlower(reanimManager), 3, 4);
        level.growPlant(new Chomper(reanimManager), 4, 3);

        sunManager = new SunManager(this, reanimManager);
        
        var seeds = new ArrayList<SeedType>();
        seeds.add(SeedType.SunFlower);
        seeds.add(SeedType.PeaShooter);
        seeds.add(SeedType.PeaShooterRepeater);
        seeds.add(SeedType.WallNut);
        seeds.add(SeedType.PotatoMine);
        seeds.add(SeedType.Chomper);
        seedBank = new SeedBank(this, sunManager, reanimManager, seeds);
        
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
        
        checkForPlantBuy();
    }
    
    public void checkForPlantBuy() {
        var mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            if (selectedPlant != null) {
                if (Greenfoot.mouseClicked(null)) {
                    removeObject(selectedPlant);
                    removeObject(selectedPlantGhost);
                    
                    var cell = level.getCellAt(mouse.getX(), mouse.getY());
                    
                    if (cell != null && level.isCellEmpty(cell.x, cell.y)) {
                        var seedType = selectedPlant.getSeedType();
                        sunManager.spendSun(seedType.getSunCost());
                        seedBank.resetTimerForSeed(seedType);
                        level.growPlant(seedType.create(reanimManager), cell.x, cell.y);
                    }
                    
                    selectedPlant = null;
                    selectedPlantGhost = null;
                } else {
                    selectedPlant.setLocation(
                        (float)mouse.getX() - selectedPlant.getHitboxWidth() / 2,
                        (float)mouse.getY() - selectedPlant.getHitboxHeight() / 2
                    );
                    placeSelectedPlantGhost(mouse.getX(), mouse.getY());
                }
            } else if (Greenfoot.mousePressed(null)) {
                var seedType = seedBank.getReadySeedAt(mouse.getX(), mouse.getY());
                if (seedType != null) {
                    selectedPlant = new PlantGhost(reanimManager, seedType);
                    addObject(
                        selectedPlant,
                        (float)mouse.getX() - selectedPlant.getHitboxWidth() / 2,
                        (float)mouse.getY() - selectedPlant.getHitboxHeight() / 2
                    );
                    
                    selectedPlantGhost = new PlantGhost.Transparent(reanimManager, seedType);
                    placeSelectedPlantGhost(mouse.getX(), mouse.getY());
                }
            }
        } else {
            removeObject(selectedPlant);
            selectedPlant = null;
        }
    }
    
    private void placeSelectedPlantGhost(int mouseX, int mouseY) {
        var cell = level.getCellAt(mouseX, mouseY);
        
        if (cell == null || !level.isCellEmpty(cell.x, cell.y)) {
            removeObject(selectedPlantGhost);
        } else {
            float globalX = Level.CELL_GRID_START_X + cell.x * Cell.WIDTH;
            float globalY = Level.CELL_GRID_START_Y + cell.y * Cell.HEIGHT;
            
            if (selectedPlantGhost.getWorld() == null) {
                addObject(selectedPlantGhost, globalX, globalY);
            } else {
                selectedPlantGhost.setLocation(globalX, globalY);
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

    public void addObject(BaseActor actor, float x, float y) {
        super.addObject(actor, (int)x, (int)y);
        actor.setLocation(x, y);
        if (!isPaused) {
            actor.lifecycleStart();
        }
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