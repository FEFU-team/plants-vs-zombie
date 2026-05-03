import greenfoot.*;
import java.util.*;
import java.util.stream.*;

public class AnimatedActor extends Actor {
    private class AnimationState implements ReanimStateWithFrameIndex {
        String name;
        float currentFrame;
        float initFrame;
        boolean loop;
        
        AnimationState(String name, boolean loop) {
            this(name, -1.f, -1.f, loop);
        }
        
        AnimationState(
            String name,
            float initFrame,
            boolean loop
        ) {
            this(name, initFrame, initFrame, loop);
        }
        
        AnimationState(
            String name,
            float currentFrame,
            float initFrame,
            boolean loop
        ) {
            this.name = name;
            this.currentFrame = currentFrame;
            this.initFrame = initFrame;
            this.loop = loop;
        }
        
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public float getCurrentFrame() {
            return currentFrame;
        }
    
        @Override
        public float getInitFrame() {
            return initFrame;
        }
    }

    protected ReanimManager reanimManager;
    private String reanimKey;
    private AnimationState reanimState;
    private List<AnimationState> reanimOverlapStates = new ArrayList<AnimationState>();
    private long lastUpdateTimeNanos;
    private float realX;
    private float realY;

    public AnimatedActor(ReanimManager reanimManager, String reanimKey, String reanimState) {
        // TODO: add animation speed multiplier for base state and overlap states
        this.reanimManager = reanimManager;
        this.reanimKey = reanimKey;
        this.reanimState = new AnimationState(reanimState, true);
        this.reanimState.currentFrame = reanimManager.getFirstFrame(reanimKey, reanimState);
        
        setImage(reanimManager.generateSprite(reanimKey, reanimOverlapStates, this.reanimState));
        lastUpdateTimeNanos = System.nanoTime();
    }

    public String getReanimKey() {
        return reanimKey;
    }

    public String getReanimState() {
        return reanimState.name;
    }

    public float getFloatX() {
        return realX;
    }

    public float getFloatY() {
        return realY;
    }

    @Override
    public int getX() {
        return Math.round(realX);
    }

    @Override
    public int getY() {
        return Math.round(realY);
    }

    @Override
    public void setLocation(int x, int y) {
        setLocation((float)x, (float)y);
    }

    public void setLocation(float x, float y) {
        realX = x;
        realX = y;
        super.setLocation((int)x, (int)y);
    }

    public void setReanimKey(String key) {
        setReanimKey(key, this.reanimKey);
    }

    public void setReanimKey(String key, String state) {
        if (reanimKey != null && !reanimKey.equals(key)) {
            reanimKey = key;
            reanimState.name = state;
            reanimState.currentFrame = reanimManager.getFirstFrame(this.reanimKey, this.reanimState.name);
            reanimOverlapStates.clear();
            lastUpdateTimeNanos = System.nanoTime();
        }
    }

    public void setReanimState(String state) {
        if (reanimState != null && !reanimState.equals(state)) {
            reanimState.name = state;
            reanimState.currentFrame = reanimManager.getFirstFrame(this.reanimKey, this.reanimState.name);
            reanimOverlapStates.clear();
            lastUpdateTimeNanos = System.nanoTime();
        }
    }

    public void setReanimSpecialState(String state, boolean loop) {
        reanimOverlapStates.clear();
        reanimOverlapStates.add(new AnimationState(
            state,
            reanimManager.getFirstFrame(reanimKey, state),
            loop
        ));
    }

    public void resume() {
        lastUpdateTimeNanos = System.nanoTime();
    }

    public void act() {
        updateFrame();
    }

    public void updateFrame() {
        if (this.reanimKey != null && this.reanimState != null) {
            float framesPassed = getFramesPassedAndUpdateTimer();

            Stream.concat(Stream.of(reanimState), reanimOverlapStates.stream())
                .forEach((state) -> {
                    state.currentFrame = reanimManager.getNextFrame(reanimKey, state.name, state.currentFrame, framesPassed * 1.4f, state.loop);
                });
                
            reanimOverlapStates.removeIf(state -> !state.loop && state.currentFrame < 0.f);
                
            setImage(reanimManager.generateSprite(reanimKey, reanimOverlapStates, reanimState));
        }
    }
    
    float getFramesPassedAndUpdateTimer() {
        long currentTimeNanos = System.nanoTime();
        long deltaNanos = currentTimeNanos - lastUpdateTimeNanos;
        float deltaSeconds = deltaNanos / 1_000_000_000.f;
        float framesPassed = deltaSeconds * reanimManager.getFPS(reanimKey);
        lastUpdateTimeNanos = currentTimeNanos;
        
        return framesPassed;
    }
}
