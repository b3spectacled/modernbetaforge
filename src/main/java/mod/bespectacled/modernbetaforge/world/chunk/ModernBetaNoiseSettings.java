package mod.bespectacled.modernbetaforge.world.chunk;

public class ModernBetaNoiseSettings {
    public static final ModernBetaNoiseSettings BETA = new ModernBetaNoiseSettings(2, 1, new SlideSettings(-10, 3, 0), new SlideSettings(15, 3, 0));
    public static final ModernBetaNoiseSettings ALPHA = new ModernBetaNoiseSettings(2, 1, new SlideSettings(-10, 3, 0), new SlideSettings(15, 3, 0));
    public static final ModernBetaNoiseSettings SKYLANDS = new ModernBetaNoiseSettings(1, 2, new SlideSettings(-30, 31, 0), new SlideSettings(-30, 7, 1));
    public static final ModernBetaNoiseSettings INFDEV_415 = new ModernBetaNoiseSettings(1, 1, new SlideSettings(0, 0, 0), new SlideSettings(0, 0, 0));
    
    public final int sizeVertical;
    public final int sizeHorizontal;

    public final SlideSettings topSlideSettings;
    public final SlideSettings bottomSlideSettings;
    
    public ModernBetaNoiseSettings(
        int sizeVertical,
        int sizeHorizontal,
        SlideSettings topSlideSettings,
        SlideSettings bottomSlideSettings
    ) {
        this.sizeVertical = sizeVertical;
        this.sizeHorizontal = sizeHorizontal;
        
        this.topSlideSettings = topSlideSettings;
        this.bottomSlideSettings = bottomSlideSettings;
    }
    
    public static class SlideSettings {
        public final int slideTarget;
        public final int slideSize;
        public final int slideOffset;
        
        public SlideSettings(int slideTarget, int slideSize, int slideOffset) {
            this.slideTarget = slideTarget;
            this.slideSize = slideSize;
            this.slideOffset = slideOffset;
        }
    }
}
