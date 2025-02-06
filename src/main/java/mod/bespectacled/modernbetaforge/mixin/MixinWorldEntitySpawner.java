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

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeMobs;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.IChunkGenerator;

@Mixin(WorldEntitySpawner.class)
public abstract class MixinWorldEntitySpawner {
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
            WorldServer worldServer = (WorldServer)modernBeta_world;
            IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
            
            if (chunkGenerator instanceof ModernBetaChunkGenerator) {
                ModernBetaGeneratorSettings settings = ((ModernBetaChunkGenerator)chunkGenerator).getGeneratorSettings();
                return ModernBetaBiomeMobs.modifySpawnList(new ArrayList<>(spawnEntries), EnumCreatureType.CREATURE, modernBeta_biome, settings);
            }
        }
        
        return spawnEntries;
    }
}
