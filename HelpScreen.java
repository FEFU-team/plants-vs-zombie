import greenfoot.*;

public class HelpScreen extends Actor {
    public HelpScreen() {
        int w = 700;
        int h = 500;
        var image = new GreenfootImage(w, h);
        
        // Фон панели
        image.setColor(new Color(40, 40, 40, 220));
        image.fill();
        image.setColor(new Color(200, 200, 200));
        image.drawRect(0, 0, w - 1, h - 1);
        image.drawRect(1, 1, w - 3, h - 3);
        
        // Заголовок
        image.setColor(new Color(255, 220, 100));
        image.setFont(new Font(28));
        image.drawString("КАК ИГРАТЬ", 260, 45);
        
        // Текст
        image.setColor(Color.WHITE);
        image.setFont(new Font(16));
        
        String[] lines = {
            "Защищай свой дом от зомби!",
            "",
            "Собирай солнце, кликая по нему.",
            "Подсолнухи дают дополнительное солнце.",
            "",
            "Выбирай семена из банка, затем",
            "кликай по клетке газона, чтобы посадить растение.",
            "",
            "Газонокосилки уничтожают всех зомби",
            "в ряду, если зомби до них доберется.",
            "",
            "Орехи блокируют, Горохострелы атакуют,",
            "Подсолнухи дают солнце.",
            "",
            "Кликни в любое место, чтобы закрыть."
        };
        
        int x = 40;
        int y = 90;
        for (String line : lines) {
            image.drawString(line, x, y);
            y += 24;
        }
        
        setImage(image);
    }
    
    @Override
    public void act() {
        if (Greenfoot.mouseClicked(null)) {
            getWorld().removeObject(this);
        }
    }
}
