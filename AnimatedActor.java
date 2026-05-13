import greenfoot.*;
import java.util.*;
import java.util.stream.*;

abstract public class AnimatedActor extends BaseActor {
    private class AnimationState implements ReanimStateWithFrameIndex {
        String name;
        float currentFrame;
        float initFrame;
        float speed = 1.f;
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
    private Timer frameTimer = new Timer();
    private float realX;
    private float realY;
    
    public AnimatedActor(ReanimManager reanimManager, String reanimKey) {
        this(reanimManager, reanimKey, null);
    }

    public AnimatedActor(ReanimManager reanimManager, String reanimKey, String reanimState) {
        this.reanimManager = reanimManager;
        this.reanimKey = reanimKey;
        this.reanimState = new AnimationState(reanimState, true);
        this.reanimState.currentFrame = reanimManager.getFirstFrame(reanimKey, reanimState);
        
        setImage(reanimManager.generateSprite(reanimKey, reanimOverlapStates, this.reanimState));
        frameTimer.reset();
    }

    public String getReanimKey() {
        return reanimKey;
    }

    public String getReanimState() {
        return reanimState.name;
    }

    public float getReanimCurrentFrame() {
        return reanimState != null ? reanimState.currentFrame : -1;
    }

    public void setReanimKey(String key) {
        setReanimKey(key, this.reanimKey);
    }

    public void setReanimKey(String key, String state) {
        if (reanimKey == null || !reanimKey.equals(key)) {
            reanimKey = key;
            reanimState.name = state;
            reanimState.currentFrame = reanimManager.getFirstFrame(this.reanimKey, this.reanimState.name);
            reanimOverlapStates.clear();
            frameTimer.reset();
        }
    }

    public void setReanimState(String state) {
        setReanimState(state, true);
    }

    public void setReanimState(String state, boolean loop) {
        if (reanimState != null && (state == null || reanimState.name == null || !reanimState.name.equals(state))) {
            reanimState.name = state;
            reanimState.loop = loop;
            reanimState.currentFrame = reanimManager.getFirstFrame(this.reanimKey, this.reanimState.name);
            reanimOverlapStates.clear();
            frameTimer.reset();
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

    public void setReanimSpeed(float speed) {
        if (reanimKey != null && reanimState != null) {
            reanimState.speed = speed;
            for (var state : reanimOverlapStates) {
                state.speed = speed;
            }
        }
    }

    @Override
    public void lifecycleStop() {
        frameTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        frameTimer.start();
    }

    @Override
    public void act() {
        updateFrame();
    }

    public void updateFrame() {
        if (this.reanimKey != null && this.reanimState != null) {
            float framesPassed = getFramesPassedAndUpdateTimer();

            Stream.concat(Stream.of(reanimState), reanimOverlapStates.stream())
                .forEach((state) -> {
                    state.currentFrame = reanimManager.getNextFrame(reanimKey, state.name, state.currentFrame, framesPassed * state.speed, state.loop);
                });
                
            reanimOverlapStates.removeIf(state -> !state.loop && state.currentFrame < 0.f);
                
            setImage(reanimManager.generateSprite(reanimKey, reanimOverlapStates, reanimState));
        }
    }
    
    float getFramesPassedAndUpdateTimer() {
        return frameTimer.getDeltaSecondsAndReset() * reanimManager.getFPS(reanimKey);
    }
}
