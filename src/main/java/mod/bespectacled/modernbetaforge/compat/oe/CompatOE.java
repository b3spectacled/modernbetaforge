package mod.bespectacled.modernbetaforge.compat.oe;

import com.sirsquidly.oe.init.OEBlocks;
import com.sirsquidly.oe.util.handlers.ConfigHandler;
import com.sirsquidly.oe.util.handlers.ConfigHandler.configBlock;
import com.sirsquidly.oe.util.handlers.ConfigHandler.configWorldGen;
import com.sirsquidly.oe.world.GeneratorFrozenOcean;
import com.sirsquidly.oe.world.GeneratorWarmOcean;
import com.sirsquidly.oe.world.feature.WorldGenCoconutTree;
import com.sirsquidly.oe.world.feature.WorldGenKelpForest;
import com.sirsquidly.oe.world.feature.WorldGenOceanPatch;
import com.sirsquidly.oe.world.feature.WorldGenPrismarinePot;
import com.sirsquidly.oe.world.feature.WorldGenSeaOats;
import com.sirsquidly.oe.world.feature.WorldGenShellSand;
import com.sirsquidly.oe.world.structure.GeneratorCoquinaOutcrop;
import com.sirsquidly.oe.world.structure.GeneratorShipwreck;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiPredicates;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenNoOpForge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class CompatOE implements Compat, ClientCompat {
    public static final String MOD_ID = "oe";
    public static final String ADDON_ID = "compat" + MOD_ID;

    private static final ResourceLocation KEY_USE_COQUINA_OUTCROPS = new ResourceLocation(ADDON_ID, "useCoquinaOutcrops");
    private static final ResourceLocation KEY_USE_KELP = new ResourceLocation(ADDON_ID, "useKelp");
    private static final ResourceLocation KEY_USE_PALM_TREES = new ResourceLocation(ADDON_ID, "usePalmTrees");
    private static final ResourceLocation KEY_USE_PRISMARINE_POTS = new ResourceLocation(ADDON_ID, "usePrismarinePots");
    private static final ResourceLocation KEY_USE_SEAGRASS_PATCHES = new ResourceLocation(ADDON_ID, "useSeagrassPatches");
    private static final ResourceLocation KEY_USE_SEA_OATS_PATCHES = new ResourceLocation(ADDON_ID, "useSeaOatsPatches");
    private static final ResourceLocation KEY_USE_SHELL_PATCHES = new ResourceLocation(ADDON_ID, "useShellPatches");
    private static final ResourceLocation KEY_USE_SHIPWRECKS = new ResourceLocation(ADDON_ID, "useShipwrecks");
    private static final ResourceLocation KEY_USE_FROZEN_OCEANS = new ResourceLocation(ADDON_ID, "useFrozenOceans");
    private static final ResourceLocation KEY_USE_WARM_OCEANS = new ResourceLocation(ADDON_ID, "useWarmOceans");
    
    private static final ResourceLocation KEY_COQUINA_OUTCROP = new ResourceLocation(ADDON_ID, "coquinaOutcrop");
    private static final ResourceLocation KEY_KELP = new ResourceLocation(ADDON_ID, "kelp");
    private static final ResourceLocation KEY_PALM_TREE = new ResourceLocation(ADDON_ID, "palmTree");
    private static final ResourceLocation KEY_PRISMARINE_POT_0 = new ResourceLocation(ADDON_ID, "prismarinePot0");
    private static final ResourceLocation KEY_PRISMARINE_POT_1 = new ResourceLocation(ADDON_ID, "prismarinePot1");
    private static final ResourceLocation KEY_SEAGRASS_PATCH_OCEAN = new ResourceLocation(ADDON_ID, "seagrassPatchOcean");
    private static final ResourceLocation KEY_SEAGRASS_PATCH_SWAMP = new ResourceLocation(ADDON_ID, "seagrassPatchSwamp");
    private static final ResourceLocation KEY_SEA_OATS_PATCH = new ResourceLocation(ADDON_ID, "seaOatsPatch");
    private static final ResourceLocation KEY_SHELL_PATCH = new ResourceLocation(ADDON_ID, "shellPatch");
    private static final ResourceLocation KEY_SHIPWRECK = new ResourceLocation(ADDON_ID, "shipwreck");
    private static final ResourceLocation KEY_FROZEN_OCEAN = new ResourceLocation(ADDON_ID, "frozenOcean");
    private static final ResourceLocation KEY_WARM_OCEAN = new ResourceLocation(ADDON_ID, "warmOcean");
    
    private static final Biome BETA_SWAMPLAND = ModernBetaBiomeHolders.BETA_SWAMPLAND;
    private static final Biome BETA_BEACH = ModernBetaBiomeHolders.BETA_BEACH;
    private static final Biome BETA_OCEAN = ModernBetaBiomeHolders.BETA_OCEAN;
    private static final Biome BETA_FROZEN_OCEAN = ModernBetaBiomeHolders.BETA_FROZEN_OCEAN;
    private static final Biome[] BETA_OCEANS = new Biome[]{ BETA_OCEAN, BETA_FROZEN_OCEAN };

    @Override
    public void load() {
        configWorldGen configWorld = ConfigHandler.worldGen;
        configBlock configBlock = ConfigHandler.block;

        ModernBetaRegistries.PROPERTY.register(KEY_USE_COQUINA_OUTCROPS, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_KELP, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_PALM_TREES, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_PRISMARINE_POTS, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_SEAGRASS_PATCHES, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_SEA_OATS_PATCHES, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_SHELL_PATCHES, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_SHIPWRECKS, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_FROZEN_OCEANS, new BooleanProperty(false));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_WARM_OCEANS, new BooleanProperty(false));
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_COQUINA_OUTCROP, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_COQUINA_OUTCROPS) && configWorld.coquinaOutcrop.enableCoquinaOutcrops && configBlock.coquina.enableCoquina) {
                int count = configWorld.coquinaOutcrop.coquinaOutcropTriesPerChunk;
                int chance = configWorld.coquinaOutcrop.coquinaOutcropChancePerChunk;
                
                return new GeneratorCoquinaOutcrop(count, chance, BETA_BEACH);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_KELP, (chunkSource, settings) -> {
            // Only generate in non-Frozen ocean, per vanilla implementation
            if (settings.getBooleanProperty(KEY_USE_KELP) && configBlock.enableKelp) {
                return new WorldGenKelpForest(BETA_OCEAN);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_PALM_TREE, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_PALM_TREES) && configWorld.palmTree.enablePalmTrees) {
                int count = configWorld.palmTree.palmTreeTriesPerChunk;
                int chance = configWorld.palmTree.palmTreeChancePerChunk;
                
                return new WorldGenCoconutTree(count, chance, BETA_BEACH);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_PRISMARINE_POT_0, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_PRISMARINE_POTS)) {
                int count = 2;
                int chance = 2;
                int amount = 48;
                int spreadXZ = 8;
                int spreadY = 8;
                
                return new WorldGenPrismarinePot(count, chance, amount, spreadXZ, spreadY, false, BETA_OCEAN);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_PRISMARINE_POT_1, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_PRISMARINE_POTS)) {
                int count = 2;
                int chance = 2;
                int amount = 48;
                int spreadXZ = 8;
                int spreadY = 8;
                
                return new WorldGenPrismarinePot(count, chance, amount, spreadXZ, spreadY, true, BETA_OCEAN);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_SEAGRASS_PATCH_OCEAN, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_SEAGRASS_PATCHES) && configWorld.enableSeagrassPatches && configBlock.seagrass.enableSeagrass) {
                IBlockState seaGrass = OEBlocks.SEAGRASS.getDefaultState();
                int count = 6;
                int chance = 2;
                int amount = 48;
                int spreadXZ = 8;
                int spreadY = 4;
                double tall = 0.3;
                
                new WorldGenOceanPatch(seaGrass, count, chance, amount, spreadXZ, spreadY, tall, false, BETA_OCEAN);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_SEAGRASS_PATCH_SWAMP, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_SEAGRASS_PATCHES) && configWorld.enableSeagrassPatches && configBlock.seagrass.enableSeagrass) {
                IBlockState seaGrass = OEBlocks.SEAGRASS.getDefaultState();
                int count = 2;
                int chance = 2;
                int amount = 48;
                int spreadXZ = 8;
                int spreadY = 4;
                double tall = 0.6;

                return new WorldGenOceanPatch(seaGrass, count, chance, amount, spreadXZ, spreadY, tall, false, BETA_SWAMPLAND);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_SEA_OATS_PATCH, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_SEA_OATS_PATCHES) && configWorld.seaOatsPatch.enableSeaOatsPatch && configBlock.seaOats.enableSeaOats) {
                int count = configWorld.seaOatsPatch.seaOatsPatchTriesPerChunk;
                int chance = configWorld.seaOatsPatch.seaOatsPatchChancePerChunk;
                int amount = 48;
                
                return new WorldGenSeaOats(count, chance, amount, BETA_BEACH);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_SHELL_PATCH, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_SHELL_PATCHES) && configWorld.shellPatch.enableShellPatch) {
                int count = configWorld.shellPatch.shellPatchTriesPerChunk;
                int chance = configWorld.shellPatch.shellPatchChancePerChunk;
                int amount = 25;
                
                return new WorldGenShellSand(count, chance, amount, BETA_BEACH);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_SHIPWRECK, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_SHIPWRECKS) && configWorld.shipwreck.enableShipwrecks) {
                int count = 1;
                int chance = configWorld.shipwreck.shipwreckChancePerChunk;
                
                return new GeneratorShipwreck(count, chance, BETA_OCEANS);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });

        ModernBetaRegistries.FORGE_FEATURE.register(KEY_FROZEN_OCEAN, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_FROZEN_OCEANS)) {
                return new GeneratorFrozenOcean(BETA_OCEANS);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
        
        ModernBetaRegistries.FORGE_FEATURE.register(KEY_WARM_OCEAN, (chunkSource, settings) -> {
            if (settings.getBooleanProperty(KEY_USE_WARM_OCEANS)) {
                return new GeneratorWarmOcean(BETA_OCEANS);
            }
            
            return WorldGenNoOpForge.INSTANCE;
        });
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public void loadClient() {
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_COQUINA_OUTCROPS, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_KELP, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_PALM_TREES, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_PRISMARINE_POTS, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_SEAGRASS_PATCHES, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_SEA_OATS_PATCHES, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_SHELL_PATCHES, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_SHIPWRECKS, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_FROZEN_OCEANS, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_WARM_OCEANS, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
    }

}
