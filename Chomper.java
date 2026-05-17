import greenfoot.*;
import java.awt.Rectangle;

public class Chomper extends Plant {
    enum State {
        IDLE,
        BITE,
        BITE_EATEN,
        CHEW,
        SWALLOW,
    }
    
    private static final float CHARGE_INTERVAL = 10.f;
    private static final float ATTACK_DELAY = 0.9f;

    private Timer chargeTimer = new Timer();
    private Timer attackDelayTimer = new Timer();
    private State state;

    public Chomper(ReanimManager manager) {
        super(manager, "REANIM_CHOMPER", 300);

        setState(State.IDLE);
        updateFrame();
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        
        if (state == State.CHEW) {
            chargeTimer.stop();
        } else if (state == State.BITE) {
            attackDelayTimer.stop();
        }
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        
        if (state == State.CHEW) {
            chargeTimer.start();
        } else if (state == State.BITE) {
            attackDelayTimer.start();
        }
    }

    @Override
    public void act() {
        if (state == State.BITE && attackDelayTimer.getDeltaSeconds() >= ATTACK_DELAY) {
            attackDelayTimer.stop();
            attackDelayTimer.reset();
            
            var attackTarget = findAttackTarget();
            if (attackTarget != null) {
                attackTarget.instantKill();
                setState(State.BITE_EATEN);
            }
        }
        
        if (getReanimCurrentFrame() < 0) {
            if (state == State.BITE) {
                setState(State.IDLE);
            } else if (state == State.BITE_EATEN) {
                chargeTimer.start();
                setState(State.CHEW);
            }
        }
        
        if (state == State.CHEW && chargeTimer.getDeltaSeconds() >= CHARGE_INTERVAL) {
            chargeTimer.stop();
            chargeTimer.reset();
            setState(State.SWALLOW);
        }
        
        if (state == State.SWALLOW && getReanimCurrentFrame() < 0) {
            setState(State.IDLE);
        }

        if (state == State.IDLE && checkAttackTargets()) {
            attackDelayTimer.start();
            setState(State.BITE);
        }
        
        super.act();
    }

    @Override
    public Rectangle.Float getAttackTargetBox() {
        var hitbox = getHitbox();
        var x = (float)hitbox.getCenterX();

        return new Rectangle.Float(
            x, getRealY() + hitbox.height * 0.2f,
            80, hitbox.height * 0.6f
        );
    }
    
    public void setState(State state) {
        this.state = state;
        setReanimSpeed(1.2f);
        
        switch (state) {
            case State.IDLE -> setReanimState("anim_idle");
            case State.BITE, State.BITE_EATEN -> {
                setReanimState("anim_bite", false);
                setReanimSpeed(1.5f);
            }
            case State.CHEW -> setReanimState("anim_chew");
            case State.SWALLOW -> setReanimState("anim_swallow", false);
        }
    }
}
