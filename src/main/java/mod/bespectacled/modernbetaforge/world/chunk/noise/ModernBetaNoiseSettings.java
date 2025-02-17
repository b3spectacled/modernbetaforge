package mod.bespectacled.modernbetaforge.world.chunk.noise;

import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings;
import mod.bespectacled.modernbetaforge.api.world.chunk.noise.NoiseSettings.SlideSettings;

public class ModernBetaNoiseSettings {
    public static final NoiseSettings BETA = new NoiseSettings(2, 1, new SlideSettings(-10, 3, 0), new SlideSettings(15, 3, 0));
    public static final NoiseSettings ALPHA = new NoiseSettings(2, 1, new SlideSettings(-10, 3, 0), new SlideSettings(15, 3, 0));
    public static final NoiseSettings SKYLANDS = new NoiseSettings(1, 2, new SlideSettings(-30, 31, 0), new SlideSettings(-30, 7, 1));
    public static final NoiseSettings INFDEV_415 = new NoiseSettings(1, 1, new SlideSettings(0, 0, 0), new SlideSettings(0, 0, 0));
    public static final NoiseSettings INFDEV_420 = new NoiseSettings(2, 1, new SlideSettings(0, 0, 0), new SlideSettings(0, 0, 0));
    public static final NoiseSettings INFDEV_611 = new NoiseSettings(2, 1, new SlideSettings(0, 0, 0), new SlideSettings(0, 0, 0));
    public static final NoiseSettings PE = new NoiseSettings(2, 1, new SlideSettings(-10, 3, 0), new SlideSettings(15, 3, 0));
    public static final NoiseSettings RELEASE = new NoiseSettings(2, 1, new SlideSettings(-10, 3, 0), new SlideSettings(15, 3, 0));
    public static final NoiseSettings END = new NoiseSettings(1, 2, new SlideSettings(-3000, 64, -46), new SlideSettings(-30, 7, 1));
}
