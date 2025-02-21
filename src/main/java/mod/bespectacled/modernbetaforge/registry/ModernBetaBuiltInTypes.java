package mod.bespectacled.modernbetaforge.registry;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.util.ResourceLocation;

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
        END("end"),
        INDEV("indev"),
        CLASSIC_0_0_23A("classic_0_0_23a");
        
        private final String id;
        
        private Chunk(String id) {
            this.id = id;
        }
        
        public ResourceLocation getRegistryKey() {
            return ModernBeta.createRegistryKey(this.id);
        }
        
        public String getRegistryString() {
            return this.getRegistryKey().toString();
        }
        
        public String getId() {
            return this.id;
        }
    }
    
    public enum Biome {
        BETA("beta"),
        SINGLE("single"),
        PE("pe"),
        RELEASE("release");
        
        private final String id;
        
        private Biome(String id) {
            this.id = id;
        }
        
        public ResourceLocation getRegistryKey() {
            return ModernBeta.createRegistryKey(this.id);
        }
        
        public String getRegistryString() {
            return this.getRegistryKey().toString();
        }
        
        public String getId() {
            return this.id;
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
        
        private final String id;
        
        private Surface(String id) {
            this.id = id;
        }
        
        public ResourceLocation getRegistryKey() {
            return ModernBeta.createRegistryKey(this.id);
        }
        
        public String getRegistryString() {
            return this.getRegistryKey().toString();
        }
        
        public String getId() {
            return this.id;
        }
    }
    
    public enum Carver {
        NONE("none"),
        BETA("beta"),
        BETA_NETHER("beta_nether"),
        BETA_1_8("beta_1_8"),
        RELEASE("release");
        
        private final String id;
        
        private Carver(String id) {
            this.id = id;
        }
        
        public ResourceLocation getRegistryKey() {
            return ModernBeta.createRegistryKey(this.id);
        }
        
        public String getRegistryString() {
            return this.getRegistryKey().toString();
        }
        
        public String getId() {
            return this.id;
        }
    }
    
    public enum WorldSpawner {
        BETA("beta"),
        INFDEV("infdev"),
        PE("pe"),
        FAR_LANDS("far_lands"),
        DEFAULT("default"),
        NONE("none");
        
        private final String id;
        
        private WorldSpawner(String id) {
            this.id = id;
        }
        
        public ResourceLocation getRegistryKey() {
            return ModernBeta.createRegistryKey(this.id);
        }
        
        public String getRegistryString() {
            return this.getRegistryKey().toString();
        }
        
        public String getId() {
            return this.id;
        }
    }
}
