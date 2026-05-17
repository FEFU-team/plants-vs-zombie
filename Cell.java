import greenfoot.*;

public class Cell extends Actor {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 100;
    
    private boolean finish = false;

    public Cell(boolean finish) {
        GreenfootImage img = new GreenfootImage(WIDTH, HEIGHT);
        this.finish = finish;

        // Для тестов
        img.setColor(Color.RED);
        img.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        setImage(img);
    }

    public boolean getStatus() {
        return finish;
    }

    public void act() {
    }
}
