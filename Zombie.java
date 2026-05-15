import greenfoot.*;
import java.util.List;
import java.awt.Rectangle;

public class Zombie extends AnimatedActor {
    
    public enum State { IDLE, WALKING, EATING, DEAD }

    protected static final float CELL_WIDTH = 90; // TODO: adjust and move to more suitable place
    
    protected State currentState = State.IDLE;
    protected float maxHp;
    protected float currentHp;
    protected boolean lostArm = false;
    protected float armLossThreshold;

    protected Timer moveTimer = new Timer();
    protected float moveSpeed;
    protected float attackDamage = 100;
    protected Timer attackTimer = new Timer();
    
    protected String animBaseName = "anim_idle";
    
    @Override
    public float getHitboxWidth() {
        return 80;
    }
    
    @Override
    public float getHitboxHeight() {
        return 90;
    }
    
    @Override
    public Rectangle.Float getHitbox() {
        float width = getHitboxWidth();
        float height = getHitboxHeight();
        
        return new Rectangle.Float(
            getRealX() + 10, getRealY() + 50,
            width, height
        );
    }

    public Zombie(ReanimManager manager, String key, float hp, float moveSpeed) {
        super(manager, key);
        this.maxHp = hp;
        this.currentHp = hp;
        this.moveSpeed = moveSpeed;
        this.armLossThreshold = hp / 2;
        //setState(State.IDLE);
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
            if (getReanimCurrentFrame() < 0) {
                var world = getWorld();
                if (world != null) {
                    world.removeObject(this);
                }
            }
            
            super.act();
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
        var world = getWorld();
        if (world == null) return;
        
        var hitbox = getHitbox();
        var attackDelta = attackTimer.getDeltaSecondsAndReset();
        
        for (var plant : world.getObjects(Plant.class)) {
            if (hitbox.intersects(plant.getHitbox())) {
                plant.takeDamage(attackDelta * attackDamage);
                return;
            }
        }
        
        setState(State.WALKING);
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
        //if (this.currentState == newState) return;

        this.currentState = newState;

        switch (newState) {
            case WALKING -> {
                animBaseName = "anim_walk";
                moveTimer.reset();
            }
            case EATING -> {
                animBaseName = "anim_eat";
                attackTimer.reset();
            }
            case DEAD -> {
                animBaseName = "anim_death";
                setReanimState(getFullAnimName(), false);
                return;
            }
            case IDLE -> {
                animBaseName = "anim_idle";

            }
            
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
    
    float getCellsPassedAndResetTimer() {
        return moveTimer.getDeltaSecondsAndReset() * moveSpeed;
    }
}