import greenfoot.*;

public class Zombie_polevaulter extends Zombie {

    public enum PolevaulterState implements ZombieState { 
        RUNNING_WITH_POLE, 
        JUMPING 
    }
    private static final float JUMP_DISTANCE = 130f;
    private static final float JUMP_DURATION = 2.3f; 
    
    private Timer jumpTimer = new Timer();
    private float jumpStartX; 

    public Zombie_polevaulter(ReanimManager manager, String key, float hp, float moveSpeed) {
        super(manager, key, hp, moveSpeed);
        setState(PolevaulterState.RUNNING_WITH_POLE); 
        updateFrame();
    }

    @Override
    protected void handleStateLogic() {
        if (currentState == PolevaulterState.RUNNING_WITH_POLE) {
            moveZombieFastWithPole();
            checkForPlantsToJump();
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
                }
                case JUMPING -> {
                    animBaseName = "anim_jump";
                    jumpStartX = getRealX(); 
                    jumpTimer.reset();
                    jumpTimer.start();
                }
            }
            setReanimState(animBaseName, false); 
        }
    }

    protected void moveZombieFastWithPole() {
        setLocation(getRealX() - getCellsPassedAndResetTimer() * CELL_WIDTH * 1.5f, getRealY());
    }

    protected void checkForPlantsToJump() {
        var hitbox = getHitbox();
        for (Plant plant : getWorld().getObjects(Plant.class)) {
            float distanceX = getRealX() - plant.getX();
            if (hitbox.intersects(plant.getHitbox()) && distanceX > 0 && distanceX < 70) {
                setState(PolevaulterState.JUMPING);
                return;
            }
        }
    }
    protected void handleJumpLogic() {
        float elapsed = jumpTimer.getDeltaSeconds(); 

        if (elapsed < JUMP_DURATION) {
            setLocation(jumpStartX, getRealY()); 
        } else {
            setLocation(jumpStartX - JUMP_DISTANCE, getRealY());
            jumpTimer.stop();
            setState(Zombie.State.WALKING); 
        }
    }
}
