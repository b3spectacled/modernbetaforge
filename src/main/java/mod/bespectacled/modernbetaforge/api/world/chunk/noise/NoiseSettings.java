package mod.bespectacled.modernbetaforge.api.world.chunk.noise;

import mod.bespectacled.modernbetaforge.util.MathUtil;

public class NoiseSettings {
    public final int sizeVertical;
    public final int sizeHorizontal;

    public final SlideSettings topSlideSettings;
    public final SlideSettings bottomSlideSettings;
    
    /**
     * Constructs a new container for basic noise settings not included in the generator settings.
     * 
     * @param sizeVertical Vertical subchunk size.
     * @param sizeHorizontal Horizontal subchunk size.
     * @param topSlideSettings Top slide settings for interpolating density at top of world.
     * @param bottomSlideSettings Bottom slide settings for interpolating density at bottom of world.
     */
    public NoiseSettings(
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
        
        /**
         * Construct a new slide settings for interpolating density at top/bottom of world.
         * 
         * @param slideTarget Target density for the slide.
         * @param slideSize Rate of change for interpolation delta.
         * @param slideOffset y-coordinate offset in noise coordinates.
         */
        public SlideSettings(int slideTarget, int slideSize, int slideOffset) {
            this.slideTarget = slideTarget;
            this.slideSize = slideSize;
            this.slideOffset = slideOffset;
        }
        
        /**
         * Interpolates density for terrain curve at top of world.
         * 
         * @param density Current terrain density.
         * @param noiseY y-coordinate in noise coordinates.
         * @param noiseSizeY Number of subchunks in world height.
         * @return Modified terrain density.
         */
        public double applyTopSlide(double density, int noiseY, int noiseSizeY) {
            if (this.slideSize > 0.0) {
                double delta = ((double)(noiseSizeY - noiseY) - this.slideOffset) / this.slideSize;
                density = MathUtil.clampedLerp(this.slideTarget, density, delta);
            }
            
            return density;
        }
        
        /**
         * Interpolates density for terrain curve at bottom of world.
         * 
         * @param density Current terrain density.
         * @param noiseY y-coordinate in noise coordinates.
         * @return Modified terrain density.
         */
        public double applyBottomSlide(double density, int noiseY) {
            if (this.slideSize > 0.0) {
                double delta = ((double)noiseY - this.slideOffset) / this.slideSize;
                density = MathUtil.clampedLerp(this.slideTarget, density, delta);
            }
            
            return density;
        }
    }
}
