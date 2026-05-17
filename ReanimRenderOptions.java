import java.util.*;
import greenfoot.*;

public interface ReanimRenderOptions {
    ReanimExtraState getMainState();
    
    Collection<? extends ReanimExtraState> getExtraStates();
    
    Collection<String> getHiddenLayers();
    
    Map<String, String> getImageSwaps();
    
    GreenfootImage getCanvas();
}
