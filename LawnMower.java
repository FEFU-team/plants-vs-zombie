import greenfoot.*;
import java.util.List;

public class LawnMower extends AnimatedActor {
    public enum State { IDLE, TRIGGERED }

    private State currentState = State.IDLE;
    private Timer moveTimer = new Timer();
    private float moveSpeed = 3.f;
    private static final float CELL_WIDTH = 90;

    @Override
    public float getHitboxWidth() {
        return 70;
    }

    @Override
    public float getHitboxHeight() {
        return 70;
    }

    public LawnMower(ReanimManager manager) {
        super(manager, "REANIM_LAWNMOWER");
        setReanimState("anim_normal");
        updateFrame();
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        moveTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        moveTimer.start();
    }

    @Override
    public void act() {
        if (currentState == State.IDLE) {
            checkForZombies();
        } else if (currentState == State.TRIGGERED) {
            super.act();
            moveLawnMower();
            mowZombies();
            checkIfOffScreen();
        }
    }

    private void checkForZombies() {
        var world = getWorld();
        if (world == null) return;

        var hitbox = getHitbox();

        for (Zombie zombie : world.getObjects(Zombie.class)) {
            if (hitbox.getX() <= zombie.getHitbox().getX() && hitbox.intersects(zombie.getHitbox())) {
                trigger();
                return;
            }
        }
    }

    private void trigger() {
        currentState = State.TRIGGERED;
        setReanimState("anim_normal");
        moveTimer.reset();
    }

    private void moveLawnMower() {
        float cellsPassed = moveTimer.getDeltaSecondsAndReset() * moveSpeed;
        setLocation(getRealX() + cellsPassed * CELL_WIDTH, getRealY());
    }

    private void mowZombies() {
        var world = getWorld();
        if (world == null) return;

        var hitbox = getHitbox();

        for (Zombie zombie : world.getObjects(Zombie.class)) {
            if (hitbox.intersects(zombie.getHitbox())) {
                zombie.takeDamage(999999);

                // Создаем эффекты частиц
                /*if (world instanceof MyWorld myWorld) {
                    ParticleSystem ps = myWorld.getParticleSystem();
                    if (ps != null) {
                        ps.spawnMowerEffects(zombie.getRealX(), zombie.getRealY());
                    }
                }*/
            }
        }
    }

    private void checkIfOffScreen() {
        var world = getWorld();
        if (world == null){
            return;
        }
        if (getRealX() > world.getWidth() + 100) {
            world.removeObject(this);
        }
    }
}
