import greenfoot.*;
import java.util.*;
import java.util.stream.*;

abstract public class AnimatedActor extends BaseActor {
    public static class AnimationState implements ReanimExtraState {
        private String name;
        private float currentFrame;
        private float initFrame;
        private float speed = 1.f;
        private boolean loop;
        
        private AnimationState(String name, boolean loop) {
            this(name, -1.f, -1.f, loop);
        }
        
        private AnimationState(
            String name,
            float initFrame,
            boolean loop
        ) {
            this(name, initFrame, initFrame, loop);
        }
        
        private AnimationState(
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
    
    public static class AnimationStateBuilder {
        private String name;
        private float initFrame = -1.f;
        private float speed = 1.f;
        private boolean loop = true;
    
        public AnimationStateBuilder name(String name) {
            this.name = name;
            return this;
        }
    
        public AnimationStateBuilder initFrame(float initFrame) {
            this.initFrame = initFrame;
            return this;
        }
    
        public AnimationStateBuilder speed(float speed) {
            this.speed = Math.max(0.01f, speed);
            return this;
        }
    
        public AnimationStateBuilder loop(boolean loop) {
            this.loop = loop;
            return this;
        }
    
        public AnimationState build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("AnimationState name cannot be null or blank");
            }

            var state = new AnimationState(name, initFrame, loop);
            state.speed = speed;
            return state;
        }
    }

    protected ReanimManager reanimManager;
    private String reanimKey;
    private AnimationState reanimState;
    private List<AnimationState> reanimExtraStates = new ArrayList<>();
    private Timer frameTimer = new Timer();
    private float realX;
    private float realY;
    private GreenfootImage canvas;
    private Set<String> hiddenLayers = new HashSet<>();
    private Map<String, String> imageSwaps = new HashMap<>();
    
    public AnimatedActor(ReanimManager reanimManager, String reanimKey) {
        this(reanimManager, reanimKey, null);
    }

    public AnimatedActor(ReanimManager reanimManager, String reanimKey, String reanimState) {
        this.reanimManager = reanimManager;
        this.reanimKey = reanimKey;
        this.reanimState = new AnimationState(reanimState, true);
        this.reanimState.currentFrame = reanimManager.getFirstFrame(reanimKey, reanimState);

        updateFrame();
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
            reanimExtraStates.clear();
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
            reanimExtraStates.clear();
            frameTimer.reset();
        }
    }
    
    public void addReanimExtraState(AnimationState state) {
        if (state != null) {
            if (state.initFrame < 0.f) {
                state.initFrame = reanimManager.getFirstFrame(reanimKey, state.getName());
                state.currentFrame = state.initFrame;
            }
            
            reanimExtraStates.add(state);
        }

        updateFrame();
    }
    
    public void setReanimExtraState(AnimationState state) {
        reanimExtraStates.clear();
        addReanimExtraState(state);
    }

    public void setReanimSpeed(float speed) {
        if (reanimKey != null && reanimState != null) {
            reanimState.speed = speed;
        }
    }
    
    public void hideLayer(String name) {
        hiddenLayers.add(name);
    }
    
    public void unhideLayer(String name) {
        hiddenLayers.remove(name);
    }
    
    public void addImageSwap(String srcName, String newName) {
        imageSwaps.put(srcName, newName);
    }
    
    public void removeImageSwap(String srcName) {
        hiddenLayers.remove(srcName);
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

            Stream.concat(Stream.of(reanimState), reanimExtraStates.stream())
                .forEach((state) -> {
                    state.currentFrame = reanimManager.getNextFrame(reanimKey, state.name, state.currentFrame, framesPassed * state.speed, state.loop);
                });
                
            reanimExtraStates.removeIf(state -> !state.loop && state.currentFrame < 0.f);
            
            var options = new ReanimRenderOptions() {
                public ReanimExtraState getMainState() {
                    return reanimState;
                }
    
                public List<AnimationState> getExtraStates() {
                    return reanimExtraStates;
                }
                
                public Set<String> getHiddenLayers() {
                    return hiddenLayers;
                }
                
                public Map<String, String> getImageSwaps() {
                    return imageSwaps;
                }
                
                public GreenfootImage getCanvas() {
                    return canvas;
                }
            };

            canvas = reanimManager.renderSprite(reanimKey, options);
            setImage(canvas);
        }
    }
    
    float getFramesPassedAndUpdateTimer() {
        return frameTimer.getDeltaSecondsAndReset() * reanimManager.getFPS(reanimKey);
    }
}
