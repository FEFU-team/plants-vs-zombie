public class Timer {
    private long lastUpdateTimeNanos;
    private long stopTimeNanos;
    
    public Timer() {
        lastUpdateTimeNanos = System.nanoTime();
        stopTimeNanos = lastUpdateTimeNanos;
    }
    
    public void stop() {
        stopTimeNanos = System.nanoTime();
    }
    
    public void start() {
        if (stopTimeNanos > 0) {
            lastUpdateTimeNanos += System.nanoTime() - stopTimeNanos;
            stopTimeNanos = 0;
        }
    }
    
    public void reset() {
        lastUpdateTimeNanos = System.nanoTime();
        if (stopTimeNanos > 0) {
            stopTimeNanos = lastUpdateTimeNanos;
        }
    }
    
    public float getDeltaSeconds() {
        long currentTimeNanos = stopTimeNanos > 0 ? stopTimeNanos : System.nanoTime();
        long deltaNanos = currentTimeNanos - lastUpdateTimeNanos;
        return deltaNanos / 1_000_000_000.f;
    }
    
    public float getDeltaSecondsAndReset() {
        long currentTimeNanos = stopTimeNanos > 0 ? stopTimeNanos : System.nanoTime();
        long deltaNanos = currentTimeNanos - lastUpdateTimeNanos;
        float deltaSeconds = deltaNanos / 1_000_000_000.f;
        lastUpdateTimeNanos = currentTimeNanos;
        
        return deltaSeconds;
    }
}
