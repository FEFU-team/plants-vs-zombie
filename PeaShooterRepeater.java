import greenfoot.*;
import java.awt.Rectangle;

public class PeaShooterRepeater extends PeaShooter {
    protected boolean secondAttackDelayActive = false;
    protected Timer secondAttackDelayTimer = new Timer();

    public PeaShooterRepeater(ReanimManager manager) {
        super(manager, "REANIM_PEASHOOTER");
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();

        if (secondAttackDelayActive) {
            secondAttackDelayTimer.stop();
        }
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();

        if (secondAttackDelayActive) {
            secondAttackDelayTimer.start();
        }
    }

    @Override
    public void act() {
        if (gameIsStopped()) return;
        
        super.act();
        
        if (secondAttackDelayActive && secondAttackDelayTimer.getDeltaSeconds() > getAttackDelay()) {
            shoot();
            secondAttackDelayActive = false;
            secondAttackDelayTimer.stop();
            secondAttackDelayTimer.reset();
        }
    }
    
    @Override
    public float getAttackDelay() {
        return 0.25f;
    }
    
    @Override
    public float getAttackAnimationSpeed() {
        return 2.8f;
    }

    @Override
    protected void shoot() {
        super.shoot();
        
        if (!secondAttackDelayActive) {
            secondAttackDelayActive = true;
            secondAttackDelayTimer.start();
            playShootAnimation();
        }
    }
}
