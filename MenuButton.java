import greenfoot.*;

/**
 * Простая кнопка с двумя состояниями: обычная и нажатая.
 */
public class MenuButton extends Actor 
{
    private final GreenfootImage normalImg;
    private final GreenfootImage pressedImg;
    private final Runnable action;

    /**
     * Конструктор кнопки.
     * @param normal  Картинка в обычном состоянии
     * @param pressed Картинка при наведении/нажатии
     * @param onClick Что сделать при клике
     */
    public MenuButton(String normal, String pressed, Runnable onClick) 
    {
        this.normalImg = new GreenfootImage(normal);
        this.pressedImg = new GreenfootImage(pressed);
        this.action = onClick;
        setImage(normalImg);
    }

    public void act() 
    {
        if (Greenfoot.mouseMoved(this) || Greenfoot.mousePressed(this)) {
            setImage(pressedImg);
        } else if (Greenfoot.mouseMoved(null)) {
            setImage(normalImg);
        }

        if (Greenfoot.mouseClicked(this) && action != null) {
            action.run();
        }
    }
}