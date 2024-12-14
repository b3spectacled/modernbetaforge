package mod.bespectacled.modernbetaforge.util;

import java.util.HashMap;
import java.util.Map;

public class DebugUtil {
    public static final String SECTION_GEN_CHUNK = "modernbetaforge.generateChunk";   // ~0.0042s per chunk (as of 1.3.3.0)
    public static final String SECTION_GEN_CHUNK_VANILLA = "minecraft.generateChunk"; // ~0.0026s per chunk
    
    public static final Map<String, Long> TOTAL_TIME = new HashMap<>();
    public static final Map<String, Long> CURRENT_TIME = new HashMap<>();
    public static final Map<String, Long> ITERATIONS = new HashMap<>();
    
    public static synchronized void startDebug(String section) {
        CURRENT_TIME.put(section, System.currentTimeMillis());
    }
    
    public static synchronized void endDebug(String section) {
        long duration = System.currentTimeMillis() - CURRENT_TIME.get(section);
        
        if (!TOTAL_TIME.containsKey(section)) {
            TOTAL_TIME.put(section, 0L);
        }
        
        if (!ITERATIONS.containsKey(section)) {
            ITERATIONS.put(section, 0L);
        }
        
        TOTAL_TIME.put(section, TOTAL_TIME.get(section) + duration);
        ITERATIONS.put(section, ITERATIONS.get(section) + 1);
    }
    
    public static synchronized String getAverageTime(String section) {
        if (!TOTAL_TIME.containsKey(section)  || !ITERATIONS.containsKey(section))
            return "";
        
        long totalTime = TOTAL_TIME.get(section);
        long iterations = ITERATIONS.get(section);

        return String.format("Average time (in s) for section '%s': %f", section, totalTime / (double)iterations / 1000.0);
    }
    
    public static synchronized String getTotalTime(String section) {
        if (!TOTAL_TIME.containsKey(section))
            return "";
        
        long totalTime = TOTAL_TIME.get(section);

        return String.format("Total time (in s) for section '%s': %f", section, totalTime / 1000.0);
    }
}
