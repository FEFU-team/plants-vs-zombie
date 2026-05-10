import greenfoot.*;
import java.util.Random;

public class SunManager {
    private static final int SPAWN_INTERVAL = 300;
    private static final int MIN_X = 50;
    private static final int MAX_X = 550;
    private static final int START_Y = 0; 
    private static final int GROUND_Y = 350;

    private MyWorld world;
    private ReanimManager reanimManager;
    private int sunCount = 0;
    private int spawnTimer = 0;
    private Random random = new Random();
    private GreenfootImage counterImage;

    public SunManager(MyWorld world, ReanimManager reanimManager) {
        this.world = world;
        this.reanimManager = reanimManager;
        updateCounterDisplay();
    }

    public void act() {
        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;
            spawnSun();
        }
    }

    private void spawnSun() {
        int randomX = MIN_X + random.nextInt(MAX_X - MIN_X);
        int randomTargetY = 100 + random.nextInt(GROUND_Y - 100);

        Sun sun = new Sun(reanimManager,  randomTargetY);
        world.addObject(sun, randomX, START_Y);
    }

    public void addSun(int amount) {
        sunCount += amount;
        updateCounterDisplay();
    }

    public boolean spendSun(int amount) {
        if (sunCount >= amount) {
            sunCount -= amount;
            updateCounterDisplay();
            return true;
        }
        return false;
    }

    public int getSunCount() {
        return sunCount;
    }

    private void updateCounterDisplay() {
        GreenfootImage sunBank = new GreenfootImage("images/SunBank.png");

        counterImage = new GreenfootImage(sunBank.getWidth(), sunBank.getHeight());
        counterImage.drawImage(sunBank, 0, 0);


        counterImage.setColor(Color.BLACK);
        counterImage.setFont(new Font(20));


        String sunText = String.valueOf(sunCount);
        int textWidth = counterImage.getFont().getSize() * sunText.length() / 2;
        int textX = (sunBank.getWidth() - textWidth) / 2;
        int textY = sunBank.getHeight() - 8; 

        counterImage.drawString(sunText, textX, textY);
    }

    public GreenfootImage getCounterImage() {
        return counterImage;
    }
}
