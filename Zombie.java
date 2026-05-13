import greenfoot.*;
import java.util.List;

public class Zombie extends AnimatedActor {
    
    public enum State { IDLE, WALKING, EATING, DEAD }

    protected static final float CELL_WIDTH = 90; // TODO: adjust and move to more suitable place
    
    protected State currentState = State.IDLE;
    protected int maxHp;
    protected int currentHp;
    protected boolean lostArm = false;
    protected int armLossThreshold;

    protected Timer moveTimer = new Timer();
    protected float moveSpeed;
    protected int attackDamage = 10;
    protected float attackInterval = 40;
    protected Timer attackTimer = new Timer();
    
    protected String animBaseName = "anim_idle";

    public Zombie(ReanimManager manager, String key, int hp, float moveSpeed) {
        super(manager, key);
        this.maxHp = hp;
        this.currentHp = hp;
        this.moveSpeed = moveSpeed;
        this.armLossThreshold = hp / 2;

        setState(State.WALKING);
        updateFrame();
    }
    
    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        moveTimer.stop();
        attackTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        moveTimer.start();
        attackTimer.start();
    }
    
    @Override
    public void act() {
        if (currentState == State.DEAD) {
            super.act();
            
            if (getReanimCurrentFrame() < 0) {
                // Animation finished
                getWorld().removeObject(this);
            }
            
            return;
        }
        
        handleStateLogic();

        super.act(); 
    }

    protected void handleStateLogic() {
        switch (currentState) {
            case WALKING -> {
                moveZombie();
                checkForPlants();
            }
            case EATING -> {
                attackPlant();
            }
            case IDLE -> {
                checkForPlants(); 
            }
        }
    }

    protected void moveZombie() {
        setLocation(getRealX() - getCellsPassedAndResetTimer() * CELL_WIDTH, getRealY());
    }

    protected void checkForPlants() {
        List<Plant> plants = getWorld().getObjects(Plant.class);
        for (Plant plant : plants) {
            float distanceX = getRealX() - plant.getX();
            float distanceY = Math.abs(getRealY() - plant.getY());

            // Дистанция атаки (укуса)
            if (distanceY < 60 && distanceX > 0 && distanceX < 30) {
                setState(State.EATING);
                return;
            }
        }
        if (currentState == State.EATING) {
            setState(State.WALKING);
        }
    }

    protected void attackPlant() {
        // TODO: manual check for intersecting plant, to not eat plants from up
        var target = getOneIntersectingObject(Plant.class);
        if (!(target instanceof Plant plant)) {
            setState(State.WALKING);
            return;
        }
        
        if (attackTimer.getDeltaSeconds() >= attackInterval) {
            plant.takeDamage(attackDamage);
            attackTimer.reset();
        }
    }

    public void takeDamage(int amount) {
        if (currentState == State.DEAD) return;
        
        currentHp -= amount;
        
        if (!lostArm && currentHp <= armLossThreshold) {
            loseArm();
        }

        if (currentHp <= 0) {
            setState(State.DEAD);
        }

    }
    
    public void setState(State newState) {
        if (this.currentState == newState) return;

        this.currentState = newState;

        switch (newState) {
            case WALKING -> animBaseName = "anim_walk";
            case EATING -> animBaseName = "anim_eat";
            case DEAD -> {
                animBaseName = "anim_death";
                setReanimState(getFullAnimName(), false);
                return;
            }
            case IDLE -> animBaseName = "anim_idle";
        }

        setReanimState(getFullAnimName());
    }

    protected String getFullAnimName() {
        return lostArm ? animBaseName + "_noarm" : animBaseName;
    }

    protected void loseArm() {
        this.lostArm = true;
        setReanimState(getFullAnimName());
    }

    protected void checkDeathAnimation() {
        if (currentState == State.DEAD) {
            var world = getWorld();
            
            if (world != null) {
                world.removeObject(this);
            }
        }
    }
    
    float getCellsPassedAndResetTimer() {
        return moveTimer.getDeltaSecondsAndReset() * moveSpeed;
    }
}