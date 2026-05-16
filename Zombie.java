import greenfoot.*;
import java.util.List;
import java.awt.Rectangle;

public class Zombie extends AnimatedActor {

    public enum State implements ZombieState { IDLE, WALKING, EATING, DEAD }

    public static final float LEFT_INDENT = 10;
    public static final float TOP_HEIGHT = 50;

    protected static final float CELL_WIDTH = 90; // TODO: adjust and move to more suitable place
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
            moveZombie();
            checkForPlants();
        } else if (currentState == State.EATING) {
            attackPlant();
        } else if (currentState == State.IDLE) {
            checkForPlants();
        }
    }

    protected void moveZombie() {
        setLocation(getRealX() - getCellsPassedAndResetTimer() * CELL_WIDTH, getRealY());
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

    float getCellsPassedAndResetTimer() {
        return moveTimer.getDeltaSecondsAndReset() * moveSpeed;
    }

    boolean isAlive() {
        return currentState != State.DEAD;
    }
}