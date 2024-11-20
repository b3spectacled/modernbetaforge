package mod.bespectacled.modernbetaforge.registry;

public class ModernBetaBuiltInTypes {
    public enum Chunk {
        BETA("beta", "Beta"),
        ALPHA("alpha", "Alpha"),
        SKYLANDS("skylands", "Skylands"),
        INFDEV_227("infdev_227", "Infdev 227"),
        INFDEV_420("infdev_420", "Infdev 420"),
        INFDEV_415("infdev_415", "Infdev 415"),
        INFDEV_611("infdev_611", "Infdev 611"),
        PE("pe", "Pocket Ed."),
        RELEASE("release", "Release"),
        INDEV("indev", "Indev")
        ;
        
        public final String id;
        public final String name;
        
        private Chunk(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    public enum Biome {
        BETA("beta", "Beta"),
        SINGLE("single", "Single"),
        PE("pe", "Pocket Ed."),
        RELEASE("release", "Release")
        ;
        
        public final String id;
        public final String name;
        
        private Biome(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    public enum Surface {
        BETA("beta", "Beta"),
        ALPHA("alpha", "Alpha"),
        ALPHA_1_2("alpha_1_2", "Alpha 1.2"),
        SKYLANDS("skylands", "Skylands"),
        INFDEV("infdev", "Infdev"),
        INFDEV_227("infdev_227", "Infdev 227"),
        PE("pe", "Pocket Ed."),
        RELEASE("release", "Release")
        ;
        
        public final String id;
        public final String name;
        
        private Surface(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    public enum Carver {
        BETA("beta", "Beta"),
        RELEASE("release", "Release")
        ;
        
        public final String id;
        public final String name;
        
        private Carver(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
    }
}
