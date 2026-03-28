import greenfoot.*;

public class MyWorld extends World {
    private ReanimManager reanimManager = new ReanimManager();

    public MyWorld() {
        super(600, 400, 1);

        reanimManager.loadReanims("./reanim", "REANIM_");
        reanimManager.loadImages("./images/reanim", "IMAGE_REANIM_");

        addObject(new TestReanimActor(reanimManager), 150, 150);
    }
}
