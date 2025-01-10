package mod.bespectacled.modernbetaforge.util;

import net.minecraft.util.math.Vec3d;

public class MathUtil {
    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }
    
    public static double clampedLerp(double start, double end, double delta) {
        if (delta < 0.0) {
            return start;
        }
        
        if (delta > 1.0) {
            return end;
        }
        
        return lerp(delta, start, end);
    }
    
    public static double distance(int x, int z, int pX, int pZ) {
        return Math.sqrt((pX - x) * (pX - x) + (pZ - z) * (pZ - z));
    }
    
    public static Vec3d convertColorIntToVec3d(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        
        return new Vec3d((float)(r / 255.0), (float)(g / 255.0), (float)(b / 255.0));
    }
    
    public static int convertColorComponentsToInt(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }
}
