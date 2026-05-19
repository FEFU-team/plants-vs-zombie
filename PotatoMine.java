import greenfoot.*;

public class PotatoMine extends Plant {
    private static final float LOAD_TIME = 15.f;

    private Timer loadTimer = new Timer();

    public PotatoMine(ReanimManager manager) {
        super(manager, "REANIM_POTATOMINE", 300);

        setReanimState("anim_idle");
        updateFrame();
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        loadTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        loadTimer.start();
    }

    @Override
    public void act() {
        if (gameIsStopped()) return;
        
        super.act();
    }
}
