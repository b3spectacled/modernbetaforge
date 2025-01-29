package mod.bespectacled.modernbetaforge.util.datafix;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.util.datafix.ModDataFixers.ModDataFix;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ModDataFixer {
    public static final ModDataFixer INSTANCE = new ModDataFixer();
    
    private final CompoundDataFixer forgeDataFixer;
    private final ModFixs modFixer;
    
    private ModDataFixer() {
        this.forgeDataFixer = FMLCommonHandler.instance().getDataFixer();
        this.modFixer = this.forgeDataFixer.init(ModernBeta.MODID, ModernBeta.DATA_VERSION);
    }
    
    public void register() {
        this.registerModDataFix(ModDataFixers.BIOME_MAP_FIX);
        this.registerModDataFix(ModDataFixers.SANDSTONE_WOLVES_SURFACE_FIX);
        this.registerModDataFix(ModDataFixers.SKYLANDS_SURFACE_FIX);
        this.registerModDataFix(ModDataFixers.SINGLE_BIOME_FIX);
        this.registerModDataFix(ModDataFixers.INDEV_HOUSE_FIX);
        this.registerModDataFix(ModDataFixers.RESOURCE_LOCATION_FIX);
        this.registerModDataFix(ModDataFixers.SCALE_NOISE_FIX);
        this.registerModDataFix(ModDataFixers.LAYER_SIZE_FIX);
        this.registerModDataFix(ModDataFixers.CAVE_CARVER_NONE_FIX);
        this.registerModDataFix(ModDataFixers.SPAWN_LOCATOR_FIX);
    }
    
    private void registerModDataFix(ModDataFix fix) {
        this.modFixer.registerFix(fix.getFixType(), fix.getFixableData());
    }
}
