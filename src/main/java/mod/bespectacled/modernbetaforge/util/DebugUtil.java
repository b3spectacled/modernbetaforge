package mod.bespectacled.modernbetaforge.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DebugUtil {
    // Significant amount of world gen time (~0.002s is dedicated just to Stronghold placement)
    public static final String SECTION_GEN_CHUNK = "generateChunk";         // ~0.005s per chunk (as of 1.3.3.0)
    public static final String SECTION_GEN_CHUNK_VANILLA = "generateChunk"; // ~0.0026s per chunk
    public static final String SECTION_POP_CHUNK = "populate";
    
    public static final String SECTION_GET_BASE_BIOMES = "getBaseBiomes";
    
    public static final Map<String, Long> TOTAL_TIME = new ConcurrentHashMap<>();
    public static final Map<String, Long> CURRENT_TIME = new ConcurrentHashMap<>();
    public static final Map<String, Long> ITERATIONS = new ConcurrentHashMap<>();
    
    public static void startDebug(String section) {
        CURRENT_TIME.put(section, System.currentTimeMillis());
    }
    
    public static void endDebug(String section) {
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
    
    public static String getAverageTime(String section) {
        if (!TOTAL_TIME.containsKey(section)  || !ITERATIONS.containsKey(section))
            return "";
        
        long totalTime = TOTAL_TIME.get(section);
        long iterations = ITERATIONS.get(section);

        return String.format("Avg time for '%s': %fs", section, totalTime / (double)iterations / 1000.0);
    }
    
    public static String getTotalTime(String section) {
        if (!TOTAL_TIME.containsKey(section))
            return "";
        
        long totalTime = TOTAL_TIME.get(section);

        return String.format("Total time for '%s': %fs", section, totalTime / 1000.0);
    }
    
    public static String getIterations(String section) {
        if (!ITERATIONS.containsKey(section))
            return "";
        
        long iterations = ITERATIONS.get(section);
        
        return String.format("Iterations for '%s': %d", section, iterations);
    }
    
    public static void resetDebug(String section) {
        TOTAL_TIME.put(section, 0L);
        CURRENT_TIME.put(section, 0L);
        ITERATIONS.put(section, 0L);
    }
    
    public static void resetDebug() {
        TOTAL_TIME.clear();
        CURRENT_TIME.clear();
        ITERATIONS.clear();
    }
}
