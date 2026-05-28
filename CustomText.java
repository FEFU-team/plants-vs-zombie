import greenfoot.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class CustomText extends BaseActor {
    private Timer lifeTimer = new Timer();
    private float lifeTime;

    public CustomText(String text, String fontName, int fontSize, greenfoot.Color gfColor, float lifeTime) {
        this.lifeTime = lifeTime;

        var img = new GreenfootImage(600, 80);
        BufferedImage bi = img.getAwtImage();
        java.awt.Graphics2D g = bi.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.FAMILY, fontName);
            attributes.put(TextAttribute.SIZE, (float)fontSize);
            attributes.put(TextAttribute.TRACKING, 0.f);
            
            var derivedFont = Font.getFont(attributes);
            
            var textColor = new Color(gfColor.getRed(), gfColor.getGreen(), gfColor.getBlue(), gfColor.getAlpha());
            final var outlineColor = Color.BLACK;
        
            var frc = g.getFontRenderContext();
            var textLayout = new TextLayout(text, derivedFont, frc);
            
            var textShape = textLayout.getOutline(null);
            
            var bounds = textShape.getBounds2D();
            double offsetX = (img.getWidth() - bounds.getWidth()) / 2 - bounds.getX();
            double offsetY = (img.getHeight() - bounds.getHeight()) / 2 - bounds.getY();
            
            g.translate(offsetX, offsetY);
            
            var shadowTransform = AffineTransform.getTranslateInstance(1., 1.);
            var shadowShape = shadowTransform.createTransformedShape(textShape);
            g.setStroke(new BasicStroke(5.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(outlineColor);
            g.fill(shadowShape);
            g.draw(shadowShape);
            
            g.setColor(textColor);
            g.fill(textShape);
        } catch (Exception e) {
            // System.err.println("Failed to render text: " + e);
        } finally {
            g.dispose();
        }

        setImage(img);
    }

    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
    }

    @Override
    protected void addedToWorld(World world) {
        for (var txt : world.getObjects(CustomText.class)) {
            if (txt != this) {
                world.removeObject(txt);
            }
        }
    }

    @Override
    public void lifecycleStop() {
        lifeTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        lifeTimer.start();
    }

    @Override
    public void act() {
        if (lifeTimer.getDeltaSeconds() >= lifeTime && getWorld() != null) {
            getWorld().removeObject(this);
        }
    }
}