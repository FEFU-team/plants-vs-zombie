import greenfoot.*;
import java.util.Random;

public class SunManager {
    private static final int SPAWN_INTERVAL = 5;
    private static final int MIN_X = 150;
    private static final int MAX_X = 900;
    private static final int START_Y = 0;
    private static final int GROUND_Y = 350;

    private MyWorld world;
    private ReanimManager reanimManager;
    private int sunCount = 0;
    private Timer spawnTimer = new Timer();
    private Random random = new Random();

    public SunManager(MyWorld world, ReanimManager reanimManager) {
        this.world = world;
        this.reanimManager = reanimManager;
    }

    public void lifecycleStop() {
        spawnTimer.stop();
    }

    public void lifecycleStart() {
        spawnTimer.start();
    }

    public void act() {
        if (spawnTimer.getDeltaSeconds() >= SPAWN_INTERVAL) {
            spawnTimer.reset();
            spawnSun();
        }
    }

    private void spawnSun() {
        int randomX = MIN_X + random.nextInt(MAX_X - MIN_X);
        int randomTargetY = 100 + random.nextInt(GROUND_Y - 100);

        Sun sun = new Sun(reanimManager, randomTargetY);
        world.addObject(sun, (float)randomX, (float)START_Y);
    }

    public void addSun(int amount) {
        sunCount += amount;
    }

    public boolean spendSun(int amount) {
        if (sunCount >= amount) {
            sunCount -= amount;
            return true;
        }
        return false;
    }

    public int getSunCount() {
        return sunCount;
    }
}
