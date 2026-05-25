public interface ReanimExtraState {
    String getName();
    
    float getCurrentFrame();
    
    float getInitFrame();
    
    default boolean isIndependent() {
        return false;
    }
    
    default boolean isStatic() {
        return false;
    }
}
