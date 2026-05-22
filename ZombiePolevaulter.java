import greenfoot.*;
import java.util.List;

public class ZombiePolevaulter extends Zombie {
    public enum PolevaulterState implements ZombieState {
        RUNNING_WITH_POLE,
        JUMPING
    }

    private static final float JUMP_DISTANCE = Cell.WIDTH * 1.65f;
    private static final float JUMP_DURATION = 2.3f;

    private Timer jumpTimer = new Timer();
    private float jumpStartX;

    public ZombiePolevaulter(ReanimManager manager) {
        super(manager, "REANIM_ZOMBIE_POLEVAULTER", 335, 1 / 2.5f);
        setState(PolevaulterState.RUNNING_WITH_POLE);
        updateFrame();
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();

        if (currentState == PolevaulterState.JUMPING) {
            jumpTimer.stop();
        }
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        
        if (currentState == PolevaulterState.JUMPING) {
            jumpTimer.start();
        }
    }

    @Override
    protected void handleStateLogic() {
        if (currentState == PolevaulterState.RUNNING_WITH_POLE) {
            if (!isWinning()) {
                moveForward();
                checkForPlantsToJump();
            } else {
                moveToDoor();
            }
        } else if (currentState == PolevaulterState.JUMPING) {
            handleJumpLogic();
        } else {
            super.handleStateLogic();
        }
    }

    @Override
    public void setState(ZombieState newState) {
        super.setState(newState);

        if (newState instanceof PolevaulterState pState) {
            this.currentState = pState;

            switch (pState) {
                case RUNNING_WITH_POLE -> {
                    animBaseName = "anim_run";
                    moveTimer.reset();
                    setReanimState(animBaseName);
                }
                case JUMPING -> {
                    animBaseName = "anim_jump";
                    jumpStartX = getRealX();
                    jumpTimer.reset();
                    jumpTimer.start();
                    setReanimState(animBaseName, false);
                }
            }
        }
    }

    protected void checkForPlantsToJump() {
        var world = getWorld();
        if (world == null) return;

        var hitbox = getHitbox();
        for (Plant plant : world.getObjects(Plant.class)) {
            float distanceX = getRealX() - plant.getX();
            if (!plant.isGhost() && hitbox.intersects(plant.getHitbox()) && distanceX > 0 && distanceX < 70) {
                setState(PolevaulterState.JUMPING);
                return;
            }
        }
    }
    protected void handleJumpLogic() {
        // TODO: fix to jump over Chomper before be eaten
        float elapsed = jumpTimer.getDeltaSeconds();

        if (elapsed < JUMP_DURATION) {
            setLocation(jumpStartX, getRealY());
        } else {
            moveSpeed = 1 / 5.f;
            setLocation(jumpStartX - JUMP_DISTANCE, getRealY());
            jumpTimer.stop();
            setState(Zombie.State.WALKING);
        }
    }
    
    public boolean isUntouchable() {
        return super.isUntouchable() || currentState == PolevaulterState.JUMPING;
    }
}
