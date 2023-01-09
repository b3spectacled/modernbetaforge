package mod.bespectacled.modernbetaforge.util;

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
}
