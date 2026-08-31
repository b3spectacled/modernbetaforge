package mod.bespectacled.modernbetaforge.compat;

public interface Compat {
    void load();
    
    String getModId();
    
    default String getRecommendedModVersion() {
        return "";
    }
    
    default String getModTooltip() {
        return "";
    }
}
