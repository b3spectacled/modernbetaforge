package mod.bespectacled.modernbetaforge.util;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public class VersionUtil {
    public static boolean isHigherVersion(ComparableVersion otherVersion) {
        ComparableVersion thisVersion = new ComparableVersion(ModernBeta.VERSION);
        
        return thisVersion.compareTo(otherVersion) < 0;
    }
    
    public static boolean isLowerVersion(ComparableVersion otherVersion) {
        ComparableVersion thisVersion = new ComparableVersion(ModernBeta.VERSION);
        
        return thisVersion.compareTo(otherVersion) > 0;
    }
    
    public static boolean isSameVersion(ComparableVersion otherVersion) {
        ComparableVersion thisVersion = new ComparableVersion(ModernBeta.VERSION);
        
        return thisVersion.compareTo(otherVersion) == 0;
    }
}
