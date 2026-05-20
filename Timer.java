public class Timer {
    private static final float NANO = 1_000_000_000.f;
    
    private long lastUpdateTimeNanos;
    private long stopTimeNanos;
    private static float debugMultiplier = 1.f;

    public Timer() {
        lastUpdateTimeNanos = System.nanoTime();
        stopTimeNanos = lastUpdateTimeNanos;
    }
    
    public static void debugSpeedUp(float multiplier) {
        debugMultiplier = multiplier;
    }

    public boolean stopped() {
        return stopTimeNanos > 0;
    }

    public boolean started() {
        return !stopped();
    }

    public void stop() {
        stopTimeNanos = System.nanoTime();
    }

    public void start() {
        if (stopped()) {
            lastUpdateTimeNanos += System.nanoTime() - stopTimeNanos;
            stopTimeNanos = 0;
        }
    }

    public void reset() {
        lastUpdateTimeNanos = System.nanoTime();
        if (stopped()) {
            stopTimeNanos = lastUpdateTimeNanos;
        }
    }
    
    public void add(float delta) {
        lastUpdateTimeNanos -= (long)(delta * NANO);
    }

    public float getDeltaSeconds() {
        long currentTimeNanos = stopped() ? stopTimeNanos : System.nanoTime();
        long deltaNanos = currentTimeNanos - lastUpdateTimeNanos;
        return deltaNanos / NANO * debugMultiplier;
    }

    public float getDeltaSecondsAndReset() {
        long currentTimeNanos = stopped() ? stopTimeNanos : System.nanoTime();
        long deltaNanos = currentTimeNanos - lastUpdateTimeNanos;
        float deltaSeconds = deltaNanos / NANO;
        lastUpdateTimeNanos = currentTimeNanos;

        return deltaSeconds * debugMultiplier;
    }
}
