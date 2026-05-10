import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.AlphaComposite;
import greenfoot.*;
import java.util.*;

public class ReanimManager {
    private ReanimParser reanimParser = new ReanimParser();
    private Map<String, Reanim> reanims = new HashMap<>();
    private Map<String, GreenfootImage> images = new HashMap<>();

    void loadReanims(String dir, String keyPrefix) {
        File reanimDirectory = new File(dir);
        if (reanimDirectory.exists() && reanimDirectory.isDirectory()) {
            File[] reanimFiles = reanimDirectory.listFiles((_dir, name) -> name.toLowerCase().endsWith(".reanim"));
            if (reanimFiles != null) {
                for (File file : reanimFiles) {
                    try {
                        String fileName = file.getName();
                        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                        String key = keyPrefix + baseName.toUpperCase().replaceAll("[^A-Z0-9]", "_");

                        reanims.put(key, reanimParser.parse(file.getAbsolutePath()));
                    } catch (Exception e) {
                        System.err.println("Error loading reanim file: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        }
    }

    void loadImages(String dir, String keyPrefix) {
        File imageDirectory = new File(dir);
        if (imageDirectory.exists() && imageDirectory.isDirectory()) {
            File[] imageFiles = imageDirectory.listFiles((_dir, name) ->
                name.toLowerCase().endsWith(".png") ||
                name.toLowerCase().endsWith(".jpg") ||
                name.toLowerCase().endsWith(".jpeg")
            );
            if (imageFiles != null) {
                for (File file : imageFiles) {
                    try {
                        String fileName = file.getName();
                        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                        String key = keyPrefix + baseName.toUpperCase().replaceAll("[^A-Z0-9]", "_");

                        images.put(key, new GreenfootImage(file.getAbsolutePath()));
                    } catch (Exception e) {
                        System.err.println("Error loading image file: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        }
    }

    public <T extends ReanimStateWithFrameIndex>
    GreenfootImage generateSprite(String reanimKey, List<? extends ReanimStateWithFrameIndex> overlapStates, T state) {
        Reanim reanim = reanims.get(reanimKey);
        if (reanim == null || state.getCurrentFrame() < 0) {
            return new GreenfootImage(1, 1);
        }

        int canvasW = 450;
        int canvasH = 300;
        BufferedImage canvas = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int originX = canvasW / 2;
        int originY = canvasH / 2;
        
        class TransformParams {
            double x, y;
            double sx, sy;
            double kx, ky;
            double alpha;
            GreenfootImage gfImg;
            
            boolean fillWithFrame(ReanimTrack track, String state, float frameIndex, boolean needInterpolate) {
                var frameA = (int) Math.floor(frameIndex);
                if (frameA < track.firstFrame || frameA > track.lastFrame) {
                    return false;
                }
                
                float t = frameIndex - frameA;
                
                var f1 = track.frames.get(frameA);
                if (f1 == null || f1.f == null || f1.f == -1) {
                    return false;
                }
                
                this.gfImg = images.get(f1.image);
                if (gfImg == null) return false;
                
                var f2 = f1;
                if (needInterpolate) {
                    int frameB = (int)Math.floor(getNextFrame(reanimKey, state, frameA, 1));
                    //int frameB = track.firstFrame + (frameA + 1 - track.firstFrame) % (track.lastFrame - track.firstFrame);
                    
                    if (frameB < track.frames.size() && track.frames.get(frameB).image == track.frames.get(frameA).image) {
                        f2 = track.frames.get(frameB);
                    }
                }
                
                this.x = lerp(f1.x != null ? f1.x : 0, f2.x != null ? f2.x : 0, t);
                this.y = lerp(f1.y != null ? f1.y : 0, f2.y != null ? f2.y : 0, t);
        
                this.sx = lerp(f1.sx != null ? f1.sx : 1.0, f2.sx != null ? f2.sx : 1.0, t);
                this.sy = lerp(f1.sy != null ? f1.sy : 1.0, f2.sy != null ? f2.sy : 1.0, t);
        
                this.kx = Math.toRadians(lerp(
                    f1.kx != null ? f1.kx : 0,
                    f2.kx != null ? f2.kx : 0,
                    t
                ));
        
                this.ky = Math.toRadians(lerp(
                    f1.ky != null ? f1.ky : 0,
                    f2.ky != null ? f2.ky : 0,
                    t
                ));
                
                this.alpha = lerp(f1.a != null ? f1.a : 1, f2.a != null ? f2.a : 1, t);
                
                return true;
            }
            
            BufferedImage getAwtImage() {
                return gfImg.getAwtImage();
            }
            
            AffineTransform getTransform() {
                double a = Math.cos(kx) * sx;
                double b = Math.sin(kx) * sx;
                double c = -Math.sin(ky) * sy;
                double d = Math.cos(ky) * sy;
    
                double tx = originX + x;
                double ty = originY + y;
    
                return new AffineTransform(
                    a, b,
                    c, d,
                    tx, ty
                );
            };
        }

        for (ReanimTrack track : reanim.tracks) {
            var tp = new TransformParams();
            if (!tp.fillWithFrame(track, state.getName(), state.getCurrentFrame(), true)) {
                continue;
            }
            
            var at = tp.getTransform();
            
            if (overlapStates != null) {
                for (var overlapState : overlapStates) {
                    var overlapParams = new TransformParams();
                    if (!overlapParams.fillWithFrame(track, overlapState.getName(), overlapState.getCurrentFrame(), true)) {
                        continue;
                    }
                    
                    var initParams = new TransformParams();
                    if (!initParams.fillWithFrame(track, overlapState.getName(), overlapState.getInitFrame(), false)) {
                        continue;
                    }
                    
                    tp.gfImg = overlapParams.gfImg;
                    // TODO: Alpha?
                    
                    try {
                        var transform = initParams.getTransform();
                        transform.invert();
                        transform.concatenate(overlapParams.getTransform());
                        at.concatenate(transform);
                    } catch (NoninvertibleTransformException e) {
                        e.printStackTrace();
                    }
                }
            }

            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)tp.alpha);
            g2d.setComposite(ac);
            g2d.drawImage(tp.getAwtImage(), at, null);
        }

        g2d.dispose();

        return createGreenfootImageFromAwt(canvas);
    }
    
    private static double normalizeAngle(double a) {
        while (a > Math.PI) a -= 2 * Math.PI;
        while (a < -Math.PI) a += 2 * Math.PI;
        return a;
    }

    public float getNextFrame(String key, String state, float currentFrame, float speed, boolean loop) {
        for (var track : reanims.get(key).tracks) {
            if (track.name.equals(state)) {
                currentFrame += speed;
                
                if (currentFrame > track.lastFrame) {
                    return loop ? track.firstFrame : -1.f;
                }
    
                if (currentFrame < track.firstFrame) {
                    return track.firstFrame;
                }
    
                return currentFrame;
            }
        }
        return -1f;
    }
    
    public float getNextFrame(String key, String state, float currentFrame, float speed) {
        return getNextFrame(key, state, currentFrame, speed, true);
    }
    
    public float getFirstFrame(String key, String state) {
        return getNextFrame(key, state, -1, 0);
    }
    
    public float getFPS(String key) {
        Reanim reanim = reanims.get(key);
        if (reanim == null) {
            return 0;
        }
        
        return reanim.fps;
    }

    private static String toUpperSnakeCase(String input) {
        return input.toUpperCase().replaceAll("[^A-Z0-9]", "_");
    }

    public static GreenfootImage createGreenfootImageFromAwt(BufferedImage awtImage) {
        int width = awtImage.getWidth(null);
        int height = awtImage.getHeight(null);

        GreenfootImage gfImage = new GreenfootImage(width, height);
        BufferedImage backingBuffer = gfImage.getAwtImage();
        Graphics g = backingBuffer.getGraphics();

        g.drawImage(awtImage, 0, 0, null);
        g.dispose();

        return gfImage;
    }
    
    private double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}