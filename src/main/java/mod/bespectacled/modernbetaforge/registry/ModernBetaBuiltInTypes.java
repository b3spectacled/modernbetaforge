package mod.bespectacled.modernbetaforge.registry;

public class ModernBetaBuiltInTypes {
    public enum Chunk {
        BETA("beta", "Beta"),
        ALPHA("alpha", "Alpha"),
        SKYLANDS("skylands", "Skylands"),
        INFDEV_420("infdev_420", "Infdev 420"),
        INFDEV_415("infdev_415", "Infdev 415"),
        PE("pe", "Pocket Edition"),
        RELEASE("release", "Release")
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
        PE("pe", "Pocket Edition"),
        RELEASE("release", "Release")
        ;
        
        public final String id;
        public final String name;
        
        private Biome(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
