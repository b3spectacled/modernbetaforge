package mod.bespectacled.modernbetaforge.compat;

@Deprecated
public interface NetherCompat {
    default boolean isCompatible() {
        return false;
    }
}
