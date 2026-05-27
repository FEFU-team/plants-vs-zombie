import greenfoot.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class CustomText extends Actor {
    private int lifeTime = 200;
    public CustomText(String text, String fontName, int fontSize, greenfoot.Color gfColor) {
        GreenfootImage img = new GreenfootImage(580, 110);
        BufferedImage bi = img.getAwtImage();
        Graphics2D g = bi.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.FAMILY, fontName);
        attributes.put(TextAttribute.SIZE, (float)fontSize);
        attributes.put(TextAttribute.TRACKING, 0.14f);
        
        Font derivedFont = Font.getFont(attributes);
        
        Color textColor = new Color(gfColor.getRed(), gfColor.getGreen(), gfColor.getBlue(), gfColor.getAlpha());
        Color outlineColor = Color.BLACK;

        try {
            FontRenderContext frc = g.getFontRenderContext();
            TextLayout textLayout = new TextLayout(text, derivedFont, frc);
            
            g.translate(15, 70);
            java.awt.Shape textShape = textLayout.getOutline(null);
            
            g.setStroke(new java.awt.BasicStroke(6.0f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            g.setColor(outlineColor);
            g.draw(textShape);
            
            g.setColor(textColor);
            g.fill(textShape);
        } catch (Exception e) {
           // System.out.println("Тихое предупреждение: не удалось отрисовать кастомный текст.");
        } finally {
            g.dispose();
        }
        setImage(img);
    }
    @Override
    protected void addedToWorld(World world) {
        ArrayList<CustomText> allTexts = new ArrayList<>(world.getObjects(CustomText.class));
        for (CustomText txt : allTexts) {
            if (txt != this && txt.getWorld() != null) {
                world.removeObject(txt);
            }
        }
    }
    public void act() {
        lifeTime--;
        if (lifeTime <= 0 && getWorld() != null) {
            getWorld().removeObject(this);
        }
    }
}