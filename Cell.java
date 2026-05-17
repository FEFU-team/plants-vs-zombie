import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Lawn here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Cell extends Actor
{   
    private int width = 80;
    private int height = 90;
    private boolean finish = false;
    public Cell(boolean s) {
        GreenfootImage img = new GreenfootImage(width,height);
        finish = (s == true) ? true : false;
        //Для тестов
        img.setColor(Color.RED);
        img.drawRect(0, 0, width - 1, height - 1);
        //
        setImage(img);
    }
    
    public boolean getStatus() {
        return finish;
    }
    
    public void act()
    {
    
    }
}
