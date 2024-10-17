package mod.bespectacled.modernbetaforge.util;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public class VersionUtil {
    public static boolean isHigherVersion(String otherVersionStr) {
        ComparableVersion thisVersion = new ComparableVersion(ModernBeta.VERSION);
        ComparableVersion otherVersion = new ComparableVersion(otherVersionStr);
        
        return thisVersion.compareTo(otherVersion) < 0;
    }
    
    public static boolean isLowerVersion(String otherVersionStr) {
        ComparableVersion thisVersion = new ComparableVersion(ModernBeta.VERSION);
        ComparableVersion otherVersion = new ComparableVersion(otherVersionStr);
        
        return thisVersion.compareTo(otherVersion) > 0;
    }
    
    public static boolean isSameVersion(String otherVersionStr) {
        ComparableVersion thisVersion = new ComparableVersion(ModernBeta.VERSION);
        ComparableVersion otherVersion = new ComparableVersion(otherVersionStr);
        
        return thisVersion.compareTo(otherVersion) == 0;
    }
}
