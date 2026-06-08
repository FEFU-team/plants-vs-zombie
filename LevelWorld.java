import greenfoot.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;
import java.awt.Font;
import java.io.File;
import java.awt.GraphicsEnvironment;

public class LevelWorld extends World {
    private ReanimManager reanimManager;
    private SunManager sunManager;
    private SeedBank seedBank;
    private Level level;
    private boolean isPaused = true;
    private boolean isStopped = false;
    private PlantGhost selectedPlant;
    private PlantGhost selectedPlantGhost;

    public LevelWorld(ReanimManager reanimManager) {
        super(1000, 600, 1);
        setPaintOrder(
            HitboxMap.class,
            ZombiesWon.class,
            CustomText.class,
            PlantGhost.class,
            PlantGhost.Transparent.class,
            Sun.class,
            Shovel.class,
            PeaProjectile.class,
            LawnMower.class,
            Zombie.class,
            Plant.class
        );

        registerCustomFont("HouseofTerrorRegular.otf");

        this.reanimManager = reanimManager;
        
        sunManager = new SunManager(this, reanimManager);

        level = new Level(
            this,
            reanimManager,
            new Level.WavesBuilder()
                // Волна 1 — одиночный зомби-разведчик
                .addBasicScout(20.f)
                // Волна 2 — ещё один одиночный, чуть позже
                .addBasicScout(45.f)
                .addBasicScout(80.f)
                .addBasicScout(120.f)
                .addBasicScout(120.f)
                // Волна 3 — двое с небольшой паузой между ними
                .addGroup(2, 160.f, 6.0f)
                // Волна 4 — тройка зомби потоком
                .addGroup(3, 200.f, 3.5f)
                // Волна 5 — HUGE WAVE, много зомби плотным потоком
                .addHugeWave(10, 250.f, 1.5f)
                .build()
        );
        level.setStyle(Level.Style.GARDEN_DAY);
        level.createLawn();
        
        // TODO: random single zombies between waves
        // TODO: wave timeline visualization
        // TODO: specify types and probabilities of zombies in wave
        
        var seeds = new ArrayList<SeedType>();
        seeds.add(SeedType.SunFlower);
        seeds.add(SeedType.PeaShooter);
        seeds.add(SeedType.PeaShooterRepeater);
        seeds.add(SeedType.WallNut);
        seeds.add(SeedType.PotatoMine);
        seeds.add(SeedType.Chomper);
        seedBank = new SeedBank(this, sunManager, reanimManager, seeds);
        
        addObject(new ShovelBank(reanimManager), ShovelBank.POSITION_X, ShovelBank.POSITION_Y);
        addObject(new Shovel(reanimManager), Shovel.IN_BANK_POSITION_X, Shovel.IN_BANK_POSITION_Y);
        
        // Debug: draw hitboxes
        // var hitboxMap = new HitboxMap();
        // hitboxMap.toggleAttackBoxes(true);
        // hitboxMap.toggleCellBoxes(true);
        // addObject(hitboxMap, getWidth() / 2, getHeight() / 2);
        
        started();
    }

    private void registerCustomFont(String fileName) {
        try {
            var fontFile = new File(fileName);
            if (fontFile.exists()) {
                var awtFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(awtFont);
            } else {
                System.err.println("Warning: Font file '" + fileName + "' not found");
            }
        } catch (Exception e) {
            System.err.println("Error to register font: " + e);
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
        
        checkForPlantBuy();
    }
    
    public void checkForPlantBuy() {
        var mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            if (selectedPlant != null) {
                if (Greenfoot.mouseClicked(null)) {
                    removeObject(selectedPlant);
                    removeObject(selectedPlantGhost);
                    
                    if (mouse.getButton() == 1) {
                        var cell = level.getCellAt(mouse.getX(), mouse.getY());
                        
                        if (cell != null && level.isCellEmpty(cell.x, cell.y)) {
                            var seedType = selectedPlant.getSeedType();
                            sunManager.spendSun(seedType.getSunCost());
                            seedBank.resetTimerForSeed(seedType);
                            level.growPlant(seedType.create(reanimManager), cell.x, cell.y);
                        }
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
            } else if (Greenfoot.mousePressed(null) && mouse.getButton() == 1) {
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