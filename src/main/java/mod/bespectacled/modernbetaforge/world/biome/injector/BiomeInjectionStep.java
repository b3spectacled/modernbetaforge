package mod.bespectacled.modernbetaforge.world.biome.injector;

public enum BiomeInjectionStep {
    ALL,          // Includes all steps
    PRE_SURFACE,  // Before surface generation
    POST_SURFACE, // After surface generation
    CUSTOM        // For custom biome injections, in between PRE_SURFACE and POST_SURFACE steps
}
