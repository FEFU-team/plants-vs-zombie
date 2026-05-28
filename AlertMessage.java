import greenfoot.*;

public class AlertMessage extends CustomText{
    public static final String FONT_NAME = "HouseofTerror";
    public static final int FONT_SIZE = 32;
    public static final Color COLOR = Color.RED;

    public static final float POSITION_X = 600;
    public static final float POSITION_Y = 320;

    public AlertMessage(String text, float lifeTime) {
        super(text, FONT_NAME, FONT_SIZE, COLOR, lifeTime);
    }

    public static void show(MyWorld world, String text, float lifeTime) {
        world.addObject(new AlertMessage(text, lifeTime), POSITION_X, POSITION_Y);
    }
}