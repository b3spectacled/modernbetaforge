package mod.bespectacled.modernbetaforge.util;

import org.lwjgl.util.vector.Vector4f;

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
    
    public static Vec3d convertRGBIntToVec3d(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        
        return new Vec3d((float)(r / 255.0), (float)(g / 255.0), (float)(b / 255.0));
    }
    
    public static Vector4f convertARGBIntToVector4f(int color) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        
        return new Vector4f((float)(a / 255.0), (float)(r / 255.0), (float)(g / 255.0), (float)(b / 255.0));
    }
    
    public static int convertRGBVec3dToInt(Vec3d color) {
        return convertRGBComponentsToInt((int)(color.x * 255.0), (int)(color.y * 255.0), (int)(color.z * 255.0));
    }
    
    public static int convertARGBVector4fToInt(Vector4f color) {
        return convertARGBComponentsToInt((int)(color.x * 255.0), (int)(color.y * 255.0), (int)(color.z * 255.0), (int)(color.w * 255.0));
    }
    
    public static int convertRGBComponentsToInt(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }
    
    public static int convertARGBComponentsToInt(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    public static int convertRGBtoARGB(int color) {
        Vec3d rgb = convertRGBIntToVec3d(color);
        
        return convertARGBComponentsToInt(255, (int)(rgb.x * 255.0), (int)(rgb.y * 255.0), (int)(rgb.z * 255.0));
    }
}
