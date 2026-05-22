import greenfoot.*;
import java.awt.Rectangle;

public class PotatoMine extends Plant {
    protected enum State {
        IDLE,
        RISE,
        ARMED,
        MASHED,
    }
    
    protected static final float LOAD_TIME = 15.f;
    protected static final float MASHED_TIME = 3.f;

    private Timer loadTimer = new Timer();
    private Timer mashedTimer = new Timer();
    private State state;

    public PotatoMine(ReanimManager manager) {
        super(manager, "REANIM_POTATOMINE", 300);

        setState(State.IDLE);
        updateFrame();
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        loadTimer.stop();
        
        if (state == State.MASHED) {
            mashedTimer.stop();
        }
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        loadTimer.start();
        
        if (state == State.MASHED) {
            mashedTimer.start();
        }
    }

    @Override
    public void act() {
        if (gameIsStopped()) return;

        switch (state) {
            case IDLE -> {
                if (loadTimer.getDeltaSeconds() >= LOAD_TIME) {
                    setState(State.RISE);
                }
            }
            case RISE -> {
                if (isMainReanimFinished()) {
                    setState(State.ARMED);
                }
            }
            case ARMED -> {
                if (checkAttackTargets()) {
                    mashedTimer.start();
                    setState(State.MASHED);
                    dealDamage();
                    // TODO: looks boring without particles
                }
            }
            case MASHED -> {
                if (mashedTimer.getDeltaSeconds() >= MASHED_TIME) {
                    var world = getWorld();
                    if (world != null) {
                        world.removeObject(this);
                    }
                }
            }
        }

        super.act();
    }

    @Override
    public Rectangle.Float getAttackTargetBox() {
        var hitbox = getHitbox();
        return new Rectangle.Float(
            hitbox.x + hitbox.width * 0.4f, hitbox.y + hitbox.height * 0.2f,
            hitbox.width * 0.6f, hitbox.height * 0.6f
        );
    }
    
    public void setState(State state) {
        this.state = state;
        setReanimSpeed(1.2f);
        
        switch (state) {
            case State.IDLE -> setReanimState("anim_idle");
            case State.RISE -> setReanimState("anim_rise", false);
            case State.ARMED -> setReanimState("anim_armed");
            case State.MASHED -> setReanimState("anim_mashed");
        }
    }

    protected void dealDamage() {
        var world = getWorld();
        if (world == null) return;
        
        var hitbox = getAttackTargetBox();

        for (Zombie zombie : world.getObjects(Zombie.class)) {
            if (!zombie.isUntouchable() && hitbox.intersects(zombie.getHitbox())) {
                zombie.instantKill();
            }
        }
    }
    
    @Override
    public boolean isGhost() {
        return state == State.MASHED;
    }
}
