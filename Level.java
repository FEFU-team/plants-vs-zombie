import greenfoot.*;
import java.util.*;
import java.awt.Rectangle;

public class Level {
    public static class Wave {
        private int totalZombies;
        private int zombiesSpawned;
        private float startsAt;
        
        Wave(int totalZombies, float startsAt) {
            this.totalZombies = totalZombies;
            this.startsAt = startsAt;
        }
    }
    
    public static class WavesBuilder {
        private final List<Wave> waves = new ArrayList<>();
        
        public WavesBuilder addWave(int totalZombies, float startsAt) {
            waves.add(new Wave(totalZombies, startsAt));
            return this;
        }
        
        public List<Wave> build() {
            return new ArrayList<>(waves);
        }
    }
    
    public enum Style {
        BARREN("IMAGE_BACKGROUND1UNSODDED"),
        GARDEN_DAY("IMAGE_BACKGROUND1"),
        GARDEN_NIGHT("IMAGE_BACKGROUND2"),
        POOL_DAY("IMAGE_BACKGROUND3"),
        POOL_NIGHT("IMAGE_BACKGROUND4"),
        ROOF_DAY("IMAGE_BACKGROUND5"),
        ROOF_NIGHT("IMAGE_BACKGROUND6BOSS");
        
        private String key;
        
        Style(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
    }
    
    public enum ZombieType {
        Basic,
        WithCone,
        Polevaulter
    }
    
    public static final int CELL_GRID_START_X = 260;
    public static final int CELL_GRID_START_Y = 80;
    
    private static final int WAVE_APPROACH_MESSAGE_INTERVAL = 5;
    private static final int WAVE_MESSAGE_HIDE_INTERVAL = 5;
    
    private static final float ZOMBIE_SPAWN_DELAY = 1.2f; 
    
    private MyWorld world;
    private ReanimManager reanimManager;
    private Random random = new Random();
    private Style style;
    
    private List<Wave> waves;
    private int currentWaveIdx = -1;
    private float nextZombieSpawnTime = -1.0f;
    
    private Timer levelTimer = new Timer();
    
    public Level(MyWorld world, ReanimManager reanimManager, List<Wave> waves) {
        if (waves.isEmpty()) {
            throw new IllegalArgumentException("Level must have at least one wave");
        }
        
        this.world = world;
        this.reanimManager = reanimManager;
        
        this.waves = waves;
        waves.sort(Comparator.comparingDouble(wave -> wave.startsAt));
        
        setStyle(Style.GARDEN_DAY);
    }
    
    public void lifecycleStop() {
        levelTimer.stop();
    }
    
    public void lifecycleStart() {
        levelTimer.start();
    }
    
    public void setStyle(Style style) {
        this.style = style;
        world.setBackground(reanimManager.getImage(style.getKey()));
    }
    
    public void act() {
        tryToSpawnZombieWave();
        trySpawnNextZombie();
        
        if (currentWaveIdx >= waves.size() - 1 && levelTimer.getDeltaSeconds() > waves.get(currentWaveIdx).startsAt + WAVE_MESSAGE_HIDE_INTERVAL) {
            world.showText("", 500, 200);
        }
        
        checkGameStatus();
    }
    
    private void tryToSpawnZombieWave() {
        if (currentWaveIdx >= waves.size() - 1) return;
        
        if (levelTimer.getDeltaSeconds() < waves.get(currentWaveIdx + 1).startsAt) {
            if (levelTimer.getDeltaSeconds() > waves.get(currentWaveIdx + 1).startsAt - WAVE_APPROACH_MESSAGE_INTERVAL) {
                world.showText("A Wave of Zombies is Approaching", 500, 200);
            } else if (currentWaveIdx >= 0 && levelTimer.getDeltaSeconds() > waves.get(currentWaveIdx).startsAt + WAVE_MESSAGE_HIDE_INTERVAL) {
                world.showText("", 500, 200);
            }
            return;
        }
        
        ++currentWaveIdx;
        
        world.showText("The wave " + (currentWaveIdx + 1) + " has begun!", 500, 200);
        nextZombieSpawnTime = levelTimer.getDeltaSeconds() + ZOMBIE_SPAWN_DELAY;
    }
    
    private void trySpawnNextZombie() {
        if (currentWaveIdx == -1 || currentWaveIdx >= waves.size()) return;
        Wave currentWave = waves.get(currentWaveIdx);
        
        if (currentWave.zombiesSpawned >= currentWave.totalZombies) return;
        
        if (levelTimer.getDeltaSeconds() >= nextZombieSpawnTime) {
            spawnSingleZombie(currentWave);
            currentWave.zombiesSpawned++;
            nextZombieSpawnTime += ZOMBIE_SPAWN_DELAY;
        }
    }

    private void spawnSingleZombie(Wave wave) {
        ZombieType[] types = ZombieType.values();
        Zombie zombie = switch (types[random.nextInt(types.length)]) {
            case Basic -> new BasicZombie(reanimManager);
            case WithCone -> new ZombieWithCone(reanimManager);
            case Polevaulter -> new ZombiePolevaulter(reanimManager);
            default -> null;
        };
        
        if (zombie != null) {
            int rowIndex = random.nextInt(5);
            world.addObject(
                zombie,
                CELL_GRID_START_X + Cell.WIDTH * 11,
                CELL_GRID_START_Y + ((0.1f + rowIndex) * Cell.HEIGHT) - Zombie.TOP_HEIGHT
            );
            
            orderZombieZLayer();
        }
    }
    
    public void orderZombieZLayer() {
        var zombies = world.getObjects(Zombie.class)
            .stream()
            .sorted(Comparator.comparingInt((zombie) -> {
                var hitbox = zombie.getHitbox();
                return hitbox != null ? (int)hitbox.y : 0;
            }))
            .toList();
        
        Map<Zombie, float[]> positions = new HashMap<>();
        for (var a : zombies) {
            positions.put(a, new float[] { a.getRealX(), a.getRealY() });
        }
    
        for (var zombie : zombies) {
            world.removeObject(zombie);
        }
        for (var zombie : zombies) {
            var pos = positions.get(zombie);
            world.addObject(zombie, pos[0], pos[1]);
        }
    }
    
    public void createLawn() {
        if (style == Style.POOL_DAY || style == Style.POOL_NIGHT) {
            // TODO: maybe add but i think it's too hard for our project
            // Pool levels have 6 rows instead of 5
            // Also need to render pool, and draw swimming zombies
        } else if (style == Style.ROOF_DAY || style == Style.ROOF_NIGHT) {
            // TODO: maybe add but i think it's too hard for our project
        } else {
            world.addObject(new Door(reanimManager, style), 145, 335);
            
            for (int j = 0; j < 5; j++) {
                world.addObject(
                    new LawnMower(reanimManager),
                    CELL_GRID_START_X - (Cell.WIDTH * 0.9f),
                    CELL_GRID_START_Y + ((j + 0.3f) * Cell.HEIGHT)
                );
            }
        }
    }
    
    public void checkGameStatus() {
        var zombies = world.getObjects(Zombie.class);
        if (zombies.isEmpty()) {
            var lastWave = waves.getLast();
            if (lastWave.zombiesSpawned == lastWave.totalZombies) {
                world.showText("Victory!", 500, 300);
                Greenfoot.stop();
            }
        }
        
        for (var zombie : zombies) {
            if (zombie.isWon()) {
                world.removeObject(zombie);
                world.stopGame();
                world.addObject(new ZombiesWon(reanimManager), ZombiesWon.POSITION_X, ZombiesWon.POSITION_Y);
                
            }
        }
    }

    public Rectangle.Float getWinHitbox() {
        return new Rectangle.Float(0, 0, CELL_GRID_START_X - Cell.WIDTH, world.getHeight());
    }
}
