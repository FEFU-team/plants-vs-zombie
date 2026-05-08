import greenfoot.*;
import java.util.List;

public class Zombie extends AnimatedActor {
    
    public enum State { IDLE, WALKING, EATING, DEAD }

    protected State currentState = State.IDLE;
    protected int maxHp;
    protected int currentHp;
    protected boolean lostArm = false;
    protected int armLossThreshold;

    protected float speed;
    protected int attackDamage = 10;
    protected int attackInterval = 40;
    protected int attackTimer = 0;
    
    protected String animBaseName = "anim_idle";

    public Zombie(ReanimManager manager, String key, int hp, float speed) {
        // Базово зомби создается в IDLE, но конструктор переключает его в WALKING
        super(manager, key, "anim_idle");
        this.maxHp = hp;
        this.currentHp = hp;
        this.speed = speed;
        this.armLossThreshold = hp / 2;
        

        setState(State.WALKING); 
    }

    @Override
    public void act() {
        if (currentState == State.DEAD) {
            super.act(); 
            checkDeathFinished();
            return;
        }
        handleStateLogic();
        

        super.act(); 
    }

    protected void handleStateLogic() {
        switch (currentState) {
            case WALKING:
                moveZombie();
                checkForPlants();
                break;
            case EATING:
                eatLogic();
                break;
            case IDLE:
                checkForPlants(); 
                break;
        }
    }

    protected void moveZombie() {
        // Плавное движение с использованием float координат из AnimatedActor
        setLocation(getFloatX()-speed, getFloatY());
    }

    protected void checkForPlants() {
        List<Plant> plants = getWorld().getObjects(Plant.class);
        for (Plant plant : plants) {
            float distanceX = getFloatX() - plant.getX();
            float distanceY = Math.abs(getFloatY() - plant.getY());

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

    protected void eatLogic() {
        Actor plant = getOneIntersectingObject(Plant.class);
        if (plant == null) {
            setState(State.WALKING);
            return;
        }
        
        attackTimer++;
        if (attackTimer >= attackInterval) {
            // Здесь будет вызов урона растению: ((Plant)plant).takeDamage(attackDamage);
            attackTimer = 0;
        }
    }

    public void takeDamage(int amount) {
        if (currentState == State.DEAD) return;
        
        currentHp -= amount;
        
        // Проверка потери руки
        if (!lostArm && currentHp <= armLossThreshold) {
            loseArm();
        }

        // При здоровье 0 переходим в состояние DEAD
        if (currentHp <= 0) {
            setState(State.DEAD);
        }

    }
    protected void checkDeathFinished() {
        // Проверяем, завершилась ли анимация смерти
        // Если currentFrame стал отрицательным, значит анимация закончилась (loop=false)
        if (getReanimCurrentFrame() < 0) {
            getWorld().removeObject(this);
        }
    }
    public void setState(State newState) {
        if (this.currentState == newState) return;

        this.currentState = newState;
        this.attackTimer = 0;

        // Привязка состояний к именам анимаций в .reanim файле
        switch (newState) {
            case WALKING: animBaseName = "anim_walk"; break;
            case EATING:  animBaseName = "anim_eat";  break;
            case DEAD:
                animBaseName = "anim_death";
                // Для анимации смерти отключаем зацикливание
                setReanimState(getFullAnimName(), false);
                return; // Выходим, чтобы не вызывать setReanimState ниже
            case IDLE:    animBaseName = "anim_idle"; break;
        }

        // Синхронизация визуального ряда через базовый класс
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

            getWorld().removeObject(this);
        }
    }
}