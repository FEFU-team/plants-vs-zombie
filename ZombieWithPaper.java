import greenfoot.*; 

public class ZombieWithPaper extends Zombie {   
    public enum PaperState implements ZombieState {
        GASPING,
        EAT_NOPAPER,
        WALK_NOPAPER
    }

    protected static final float PAPER_HEALTH_BUFF = 150f;
    private static final float GASP_DURATION = 0.75f; 
    private Timer gaspTimer = new Timer();
    private boolean isEnraged = false;

    private final float NORMAL_SPEED = 1 / 5.f;
    private final float ENRAGED_SPEED = 1 / 2.f; 

    public ZombieWithPaper(ReanimManager manager) {
        super(manager, "REANIM_ZOMBIE_PAPER", 420, 1 / 5.f);
        setState(Zombie.State.WALKING);
        unhideLayer("Zombie_paper_paper");
        hideLayer("anim_hair"); 
        updateFrame();
    }
    
    @Override
    public void takeDamage(float amount) {
        super.takeDamage(amount);
        float paperHealth = currentHp - (maxHp - PAPER_HEALTH_BUFF);
        
        if (paperHealth <= 0) {
            if (!isEnraged) {
                enrage(); 
            }
        } else {
            if (paperHealth < PAPER_HEALTH_BUFF * 0.35f) {
                addImageSwap("IMAGE_REANIM_ZOMBIE_PAPER_PAPER2", "IMAGE_REANIM_ZOMBIE_PAPER_PAPER3");
            } else if (paperHealth < PAPER_HEALTH_BUFF * 0.7f) {
                addImageSwap("IMAGE_REANIM_ZOMBIE_PAPER_PAPER1", "IMAGE_REANIM_ZOMBIE_PAPER_PAPER2");
            }
        }
    }
    @Override
    protected void handleStateLogic() {
        if (currentState == PaperState.GASPING) {
            
            float elapsed = gaspTimer.getDeltaSeconds();

            if (elapsed >= GASP_DURATION) {
                gaspTimer.stop();
                addImageSwap("IMAGE_REANIM_ZOMBIE_HEAD", "IMAGE_REANIM_ZOMBIE_PAPER_MADHEAD");
                addImageSwap("IMAGE_REANIM_ZOMBIE_PAPER_HEAD_LOOK", "IMAGE_REANIM_ZOMBIE_PAPER_MADHEAD");
                setState(PaperState.WALK_NOPAPER); 
            }
        }
        if (currentState == PaperState.WALK_NOPAPER) {
            if (!winning()) {
                checkForPlants();
                moveForward(); 
            } else {
                moveToDoor();
            }
        } else if (currentState == PaperState.EAT_NOPAPER) {
            attackPlant();
            attackTimer.reset();
            super.handleStateLogic(); 
        } else {
            super.handleStateLogic();
        }
    }


    private void enrage() {
        //TODO: appearance question mark
        isEnraged = true;
        moveSpeed = ENRAGED_SPEED; 
        setState(PaperState.GASPING);
    }

    @Override
    public void setState(ZombieState newState) {
        if (isEnraged) {
            if (newState == Zombie.State.WALKING) {
                newState = PaperState.WALK_NOPAPER;
            } else if (newState == Zombie.State.EATING) {
                newState = PaperState.EAT_NOPAPER;
            }
        }
        super.setState(newState);
        if (newState instanceof PaperState pState) {
            this.currentState = pState;

            switch (pState) {
                case GASPING -> {
                    animBaseName = "anim_gasp";
                    gaspTimer.reset();
                    gaspTimer.start(); 
                }
                case EAT_NOPAPER -> {
                    animBaseName = "anim_eat_nopaper";
                    attackTimer.reset();
                }
                case WALK_NOPAPER -> {
                    animBaseName = "anim_walk_nopaper";
                    moveTimer.reset();
                }
            }
            setReanimState(animBaseName, false);
        }
    }
}