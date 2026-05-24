import greenfoot.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.File;

/**
 * Главное меню.
 */
public class MainMenu extends World 
{
    public MainMenu() 
    {    
        super(1000, 600, 1); 
        setBackground(buildBackground());
    }
    
    private GreenfootImage buildBackground() 
    {
        GreenfootImage canvas = new GreenfootImage(1000, 600);
           // 🔹 Полная очистка холста перед отрисовкой
        canvas.setColor(new Color(0, 0, 0));
        
        canvas.fillRect(0, 0, 1000, 600);
        
        
        // Фон
        GreenfootImage bg = makeTransparent(
            "images/reanim/SelectorScreen_BG_Right.jpg",
            "images/reanim/SelectorScreen_BG_Right_.png"
        );
         GreenfootImage house = makeTransparent(
            "images/reanim/SelectorScreen_BG_Center.jpg",
            "images/reanim/SelectorScreen_BG_Center_.png"
        );
         GreenfootImage tree = makeTransparent(
            "images/reanim/SelectorScreen_BG_Left.jpg",
            "images/reanim/SelectorScreen_BG_Left_.png"
        );
       drawBlueSky(canvas);
        canvas.drawImage(new GreenfootImage("images/reanim/SelectorScreen_BG.jpg"),0,0);
        canvas.drawImage(house, 40, 250);
         canvas.drawImage(bg, 290, 40);
        canvas.drawImage(tree, 0, 0);
        // Логотип
        GreenfootImage logo = makeTransparent(
            "images/PvZ_Logo.jpg",
            "images/PvZ_Logo_.png"
        );
       
        int logoX = (canvas.getWidth() - logo.getWidth()) / 2;
        canvas.drawImage(logo, logoX, 10);

        return canvas;
    }
    
    /**
     * Загружает картинку с прозрачностью через ImageIO (без кеша).
     */
    private GreenfootImage makeTransparent(String imagePath, String maskPath) 
    {
        try {
            // 🔹 Читаем файлы НАПРЯМУЮ, минуя Greenfoot
            BufferedImage img = ImageIO.read(new File(imagePath));
            BufferedImage mask = ImageIO.read(new File(maskPath));
            
            if (img == null || mask == null) {
                System.out.println("Image not found: " + imagePath);
                return new GreenfootImage(100, 50);
            }
            
            int w = img.getWidth();
            int h = img.getHeight();
            
            // 🔹 Создаём новый буфер с альфа-каналом
            BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            
            // 🔹 Применяем маску
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int maskRGB = mask.getRGB(x, y);
                    int maskRed = (maskRGB >> 16) & 0xFF;
                    
                    if (maskRed < 50) {
                        // Прозрачный пиксель
                        result.setRGB(x, y, 0x00000000);
                    } else {
                        // Копируем пиксель из оригинала
                        result.setRGB(x, y, img.getRGB(x, y));
                    }
                }
            }
            
            // 🔹 Создаём GreenfootImage из результата
            GreenfootImage resultImage = new GreenfootImage(w, h);
            java.awt.Graphics2D g2 = resultImage.getAwtImage().createGraphics();
            g2.drawImage(result, 0, 0, null);
            g2.dispose();
            
            return resultImage;
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return new GreenfootImage(100, 50);
        }
    }
    private void drawBlueSky(GreenfootImage canvas) 
    {
        int skyHeight = 500; // Высота неба (остальное закроет панель и газон)
        
        // Цвета градиента: сверху темнее, к горизонту светлее
        Color topColor    = new Color(100, 180, 230);
        Color bottomColor = new Color(180, 220, 255);
        
        for (int y = 0; y < skyHeight; y++) {
            float ratio = (float) y / skyHeight;
            
            // Линейная интерполяция цвета
            int r = (int) (topColor.getRed() * (1 - ratio) + bottomColor.getRed() * ratio);
            int g = (int) (topColor.getGreen() * (1 - ratio) + bottomColor.getGreen() * ratio);
            int b = (int) (topColor.getBlue() * (1 - ratio) + bottomColor.getBlue() * ratio);
            
            canvas.setColor(new Color(r, g, b));
            canvas.fillRect(0, y, 1000, 1); // Рисуем полоску высотой 1px
        }
        
        // Если хочешь просто однотонное небо без градиента, замени цикл на:
        // canvas.setColor(new Color(135, 206, 250));
        // canvas.fillRect(0, 0, 1000, skyHeight);
    }
}