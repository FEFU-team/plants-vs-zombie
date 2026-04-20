import greenfoot.*;

public class TestReanimActor extends Actor {
    private ReanimManager reanimManager;
    private String key = "REANIM_PEASHOOTER";
    private String animState = "anim_full_idle";
    private float currentFrame = -1.f;
    private long lastUpdateTimeNanos;

    public TestReanimActor(ReanimManager reanimManager) {
        this.reanimManager = reanimManager;
        this.lastUpdateTimeNanos = System.nanoTime();
        currentFrame = reanimManager.getFirstFrame(key, animState);
        setImage(reanimManager.generateSprite(key, currentFrame));
    }
    
    void resume() {
        this.lastUpdateTimeNanos = System.nanoTime();
    }

    public void act() {
        updateFrame();
    }

    public void updateFrame() {
        long currentTimeNanos = System.nanoTime();
        long deltaNanos = currentTimeNanos - lastUpdateTimeNanos;
        float deltaSeconds = deltaNanos / 1_000_000_000.f;
        float framesPassed = deltaSeconds * reanimManager.getFPS(key);
        this.lastUpdateTimeNanos = currentTimeNanos;
        
        currentFrame = reanimManager.getNextFrame(key, animState, currentFrame, framesPassed);
        setImage(reanimManager.generateSprite(key, currentFrame));
    }
}
