package mod.bespectacled.modernbetaforge.registry;

public class ModernBetaBuiltInTypes {
    public enum Chunk {
        BETA("beta"),
        ALPHA("alpha"),
        SKYLANDS("skylands"),
        INFDEV_227("infdev_227"),
        INFDEV_420("infdev_420"),
        INFDEV_415("infdev_415"),
        INFDEV_611("infdev_611"),
        PE("pe"),
        RELEASE("release"),
        INDEV("indev"),
        CLASSIC_0_0_23A("classic_0_0_23a");
        
        public final String id;
        
        private Chunk(String id) {
            this.id = id;
        }
    }
    
    public enum Biome {
        BETA("beta"),
        SINGLE("single"),
        PE("pe"),
        RELEASE("release");
        
        public final String id;
        
        private Biome(String id) {
            this.id = id;
        }
    }
    
    public enum Surface {
        BETA("beta"),
        ALPHA("alpha"),
        ALPHA_1_2("alpha_1_2"),
        SKYLANDS("skylands"),
        INFDEV("infdev"),
        INFDEV_227("infdev_227"),
        PE("pe"),
        RELEASE("release");
        
        public final String id;
        
        private Surface(String id) {
            this.id = id;
        }
    }
    
    public enum Carver {
        BETA("beta"),
        RELEASE("release");
        
        public final String id;
        
        private Carver(String id) {
            this.id = id;
        }
    }
}
