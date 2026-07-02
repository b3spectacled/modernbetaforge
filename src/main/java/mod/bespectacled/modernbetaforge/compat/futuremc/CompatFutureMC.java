package mod.bespectacled.modernbetaforge.compat.futuremc;

import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thedarkcolour.futuremc.block.villagepillage.BlockFlower;

public class CompatFutureMC implements Compat {
    public static final String MOD_ID = "futuremc";
    public static final String ADDON_ID = "compat" + MOD_ID;

    public static final ResourceLocation KEY_BEE_NEST_FLOWER_CHECK = new ResourceLocation(ADDON_ID, "beeNestFlowerCheck");
    
    private static final ResourceLocation LILY_OF_THE_VALLEY = new ResourceLocation(MOD_ID, "lily_of_the_valley");
    private static final ResourceLocation CORNFLOWER = new ResourceLocation(MOD_ID, "cornflower");
    private static final ResourceLocation SWEET_BERRY_BUSH = new ResourceLocation(MOD_ID, "sweet_berry_bush");
    
    @Override
    public void load() {
        addFlowerToBiome(LILY_OF_THE_VALLEY, ModernBetaBiomeHolders.BETA_FOREST);
        addFlowerToBiome(CORNFLOWER, ModernBetaBiomeHolders.BETA_PLAINS);
        addFlowerToBiome(SWEET_BERRY_BUSH, ModernBetaBiomeHolders.BETA_TAIGA);
        
        ModernBetaRegistries.PROPERTY.register(KEY_BEE_NEST_FLOWER_CHECK, new BooleanProperty(false));
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    private static void addFlowerToBiome(ResourceLocation flowerKey, Biome ...biomes) {
        BlockFlower flower = (BlockFlower)ForgeRegistryUtil.get(flowerKey, ForgeRegistries.BLOCKS);
        
        for (int i = 0; i < biomes.length; ++i) {
            flower.getValidBiomes().add(biomes[i]);
        }
    }
}
