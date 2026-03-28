import greenfoot.*;

public class TestReanimActor extends Actor {
    private ReanimManager reanimManager;
    private String key = "REANIM_PEASHOOTER";
    private String animState = "anim_full_idle";
    private int currentFrame = -1;

    public TestReanimActor(ReanimManager reanimManager) {
        this.reanimManager = reanimManager;
        updateFrame();
    }

    public void act() {
        updateFrame();
    }

    public void updateFrame() {
        currentFrame = reanimManager.getNextFrame(key, animState, currentFrame);
        setImage(reanimManager.generateSprite(key, currentFrame));
    }
}
