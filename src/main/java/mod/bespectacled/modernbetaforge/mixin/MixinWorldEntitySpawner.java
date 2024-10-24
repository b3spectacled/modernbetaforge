package mod.bespectacled.modernbetaforge.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeMobs;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

@Mixin(WorldEntitySpawner.class)
public class MixinWorldEntitySpawner {
    @Unique private static World modernBeta_world;
    @Unique private static Biome modernBeta_biome;
    
    @Inject(method = "performWorldGenSpawning", at = @At("HEAD"))
    private static void captureWorldGenSpawningInfo(
        World world,
        Biome biome,
        int centerX,
        int centerZ,
        int diameterX,
        int diameterZ,
        Random random,
        CallbackInfo info
    ) {
        modernBeta_world = world;
        modernBeta_biome = biome;
    }
    
    @ModifyVariable(
        method = "performWorldGenSpawning",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/biome/Biome;getSpawnableList(Lnet/minecraft/entity/EnumCreatureType;)Ljava/util/List;")
    )
    private static List<SpawnListEntry> injectPerformWorldGenSpawning(List<SpawnListEntry> spawnEntries) {
        if (modernBeta_world != null && modernBeta_biome != null) {
            if (modernBeta_world.getWorldInfo().getTerrainType() instanceof ModernBetaWorldType) {
                ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.buildSettings(modernBeta_world.getWorldInfo().getGeneratorOptions());         
                return ModernBetaBiomeMobs.modifySpawnList(new ArrayList<>(spawnEntries), EnumCreatureType.CREATURE, modernBeta_biome, settings);
            }
        }
        
        return spawnEntries;
    }
}
