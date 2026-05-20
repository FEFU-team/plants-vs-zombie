import java.util.*;
import greenfoot.*;

public interface ReanimRenderOptions {
    ReanimExtraState getMainState();
    
    default Collection<? extends ReanimExtraState> getExtraStates() {
        return null;
    }
    
    default Collection<String> getHiddenLayers() {
        return null;
    }
    
    default Map<String, String> getImageSwaps() {
        return null;
    }
    
    default GreenfootImage getCanvas() {
        return null;
    }
}
