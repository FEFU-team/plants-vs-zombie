import greenfoot.*;
import java.util.List;
import java.awt.*;

public class Zombie extends AnimatedActor {
    public enum State implements ZombieState { IDLE, WALKING, EATING, DEAD }

    public static final float LEFT_INDENT = 10;
    public static final float TOP_HEIGHT = 50;

    protected ZombieState currentState = State.IDLE;
    protected float maxHp;
    protected float currentHp;
    protected boolean lostArm = false;
    protected float armLossThreshold;

    protected Timer moveTimer = new Timer();
    protected float moveSpeed;
    protected float attackDamage = 100;
    protected Timer attackTimer = new Timer();

    protected String animBaseName = "anim_idle";
    protected boolean gotBrain = false;

    @Override
    public float getHitboxWidth() {
        return 80;
    }

    @Override
    public float getHitboxHeight() {
        return 80;
    }

    @Override
    public Rectangle.Float getHitbox() {
        float width = getHitboxWidth();
        float height = getHitboxHeight();

        return new Rectangle.Float(
            getRealX() + LEFT_INDENT, getRealY() + TOP_HEIGHT + 5,
            width, height
        );
    }

    public Zombie(ReanimManager manager, String key, float hp, float moveSpeed) {
        super(manager, key);
        this.maxHp = hp;
        this.currentHp = hp;
        this.moveSpeed = moveSpeed;
        this.armLossThreshold = hp / 2;

        setReanimSpeed(1.4f);
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
        if (gameIsStopped()) return;
        
        if (currentState == State.DEAD) {
            if (getReanimCurrentFrame() < 0) {
                var world = getWorld();
                if (world != null) world.removeObject(this);
            }
            super.act();
            return;
        }

        handleStateLogic();
        super.act();
    }

    protected void handleStateLogic() {
        if (currentState == State.WALKING) {
            if (!isWinning()) {
                moveForward();
                checkForPlants();
            } else  {
                moveToDoor();
            }
        } else if (currentState == State.EATING) {
            attackPlant();
        } else if (currentState == State.IDLE) {
            checkForPlants();
        }
    }
    
    protected void moveToDoor() {
        var doorDistance = getDistanceFromDoor();
        if (doorDistance != null) {
            if (doorDistance.y > 60) {
                setLocation(getRealX(), getRealY() + getCellsPassedAndResetTimer() * Cell.WIDTH);
            } else if (doorDistance.y < 40) {
                setLocation(getRealX(), getRealY() - getCellsPassedAndResetTimer() * Cell.WIDTH);
            } else if (doorDistance.x < 10){
                setLocation(getRealX() - getCellsPassedAndResetTimer() * Cell.WIDTH, getRealY());
            } else {
                gotBrain = true;
            }
        }
    }

    protected void moveForward() {
        setLocation(getRealX() - getCellsPassedAndResetTimer() * Cell.WIDTH, getRealY());
    }
    
    public Point.Float getDistanceFromDoor(Door door) {
        return new Point.Float(door.getX() - getRealX(), door.getY() - getRealY());
    }
    
    public Point.Float getDistanceFromDoor() {
        var door = getWorld().getObjects(Door.class).getFirst();
        if (door == null) return null;
        
        return getDistanceFromDoor(door);
    }
    
    protected boolean isWinning() {
        var world = getWorldOfType(MyWorld.class);
        if (world == null) return false;

        return world.getLevel().getWinHitbox().intersects(getHitbox());
    }

    protected void checkForPlants() {
        var hitbox = getHitbox();

        for (Plant plant : getWorld().getObjects(Plant.class)) {
            float distanceX = getRealX() - plant.getX();

            if (hitbox.intersects(plant.getHitbox()) && distanceX > 0 && distanceX < 30) {
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
            float distanceX = getRealX() - plant.getX();

            if (hitbox.intersects(plant.getHitbox()) && distanceX > 0) {
                plant.takeDamage(attackDelta * attackDamage);
                return;
            }
        }

        setState(State.WALKING);
    }

    public void takeDamage(float amount) {
        if (currentState == State.DEAD) return;

        currentHp -= amount;

        if (!lostArm && currentHp <= armLossThreshold) {
            loseArm();
        }

        if (currentHp <= 0) {
            setState(State.DEAD);
        }
    }
    
    public boolean isWon() {
        return gotBrain;
    }
    
    public void instantKill() {
        currentHp = 0;

        var world = getWorld();
        if (world != null) world.removeObject(this);
    }

    public void setState(ZombieState newState) {
        this.currentState = newState;
        if (newState instanceof State baseState) {
            switch (baseState) {
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
                    setReanimState(animBaseName, false);
                    return;
                }
                case IDLE -> {
                    animBaseName = "anim_idle";
                }
            }
            setReanimState(animBaseName);
        }
    }

    protected void loseArm() {
        this.lostArm = true;
    }

    protected float getCellsPassedAndResetTimer() {
        return moveTimer.getDeltaSecondsAndReset() * moveSpeed;
    }

    public boolean isAlive() {
        return currentState != State.DEAD;
    }
    
    public boolean isUntouchable() {
        return !isAlive() || isWinning();
    }
}