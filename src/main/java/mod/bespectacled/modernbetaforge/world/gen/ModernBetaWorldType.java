package mod.bespectacled.modernbetaforge.world.gen;

import mod.bespectacled.modernbetaforge.client.gui.GuiCustomizeWorldScreen;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModernBetaWorldType extends WorldType {

    public ModernBetaWorldType(String name) {
        super(name);
    }

    public static void register() {
        new ModernBetaWorldType("modernbeta");
    }
    
    @Override
    public boolean isCustomizable() {
        return true;
    }

    @Override
    public boolean hasInfoNotice() {
        return true;
    }
    
    @Override
    public float getCloudHeight() {
        return (float)ModernBetaConfig.visualOptions.cloudHeight;
    }
    
    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
        return new ModernBetaChunkGenerator(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
    }
    
    @Override
    public BiomeProvider getBiomeProvider(World world) {
        return new ModernBetaBiomeProvider(world.getWorldInfo());
    }
    
    @Override
    public int getSpawnFuzz(WorldServer world, MinecraftServer server) {
        return ModernBetaConfig.spawnOptions.useSpawnFuzz ? Math.max(0, server.getSpawnRadius(world)) : 0;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void onCustomizeButton(Minecraft minecraft, GuiCreateWorld guiCreateWorld) {
        minecraft.displayGuiScreen(new GuiCustomizeWorldScreen(guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson));
    }
}
