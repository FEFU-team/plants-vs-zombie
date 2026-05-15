public class Timer {
    private long lastUpdateTimeNanos;
    private long stopTimeNanos;

    public Timer() {
        lastUpdateTimeNanos = System.nanoTime();
        stopTimeNanos = lastUpdateTimeNanos;
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

    public float getDeltaSeconds() {
        long currentTimeNanos = stopped() ? stopTimeNanos : System.nanoTime();
        long deltaNanos = currentTimeNanos - lastUpdateTimeNanos;
        return deltaNanos / 1_000_000_000.f;
    }

    public float getDeltaSecondsAndReset() {
        long currentTimeNanos = stopped() ? stopTimeNanos : System.nanoTime();
        long deltaNanos = currentTimeNanos - lastUpdateTimeNanos;
        float deltaSeconds = deltaNanos / 1_000_000_000.f;
        lastUpdateTimeNanos = currentTimeNanos;

        return deltaSeconds;
    }
}
