package mod.bespectacled.modernbetaforge.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Predicate;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.feature.OreType;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenClayOre;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenMinableMutable;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenNoOp;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

@Mixin(BiomeDecorator.class)
public class MixinBiomeDecorator {
    @Shadow private BlockPos chunkPos;
    @Shadow private ChunkGeneratorSettings chunkProviderSettings;
    @Shadow private WorldGenerator clayGen;
    @Shadow private WorldGenerator sandGen;
    @Shadow private WorldGenerator gravelGen;
    @Shadow private WorldGenerator dirtGen;
    @Shadow private WorldGenerator gravelOreGen;
    @Shadow private WorldGenerator graniteGen;
    @Shadow private WorldGenerator dioriteGen;
    @Shadow private WorldGenerator andesiteGen;
    @Shadow private WorldGenerator coalGen;
    @Shadow private WorldGenerator ironGen;
    @Shadow private WorldGenerator goldGen;
    @Shadow private WorldGenerator redstoneGen;
    @Shadow private WorldGenerator diamondGen;
    @Shadow private WorldGenerator lapisGen;
    @Shadow private boolean decorating;
    @Shadow private boolean generateFalls;
    
    @Unique private WorldGenerator initClayGen;
    @Unique private WorldGenerator initSandGen;
    @Unique private WorldGenerator initGravelGen;
    @Unique private boolean initGenerateFalls;
    @Unique private WorldGenerator clayOreGen;
    
    @Shadow protected void genDecorations(Biome biome, World world, Random random) { }
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectConstructor(CallbackInfo info) {
        this.initClayGen = this.clayGen;
        this.initSandGen = this.sandGen;
        this.initGravelGen = this.gravelGen;
        this.initGenerateFalls = this.generateFalls;
    }
    
    @Inject(method = "decorate", at = @At("HEAD"), cancellable = true)
    private void injectDecorate(World world, Random random, Biome biome, BlockPos pos, CallbackInfo info) {
        WorldServer worldServer = (WorldServer)world;
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        
        // Reset world generators that are only initialized with the new decorator.
        this.clayGen = this.initClayGen;
        this.sandGen = this.initSandGen;
        this.gravelGen = this.initGravelGen;
        this.generateFalls = this.initGenerateFalls;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            if (this.decorating) {
                throw new RuntimeException("Already decorating");
            } else {
                ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(worldServer);
                OreType oreType = OreType.fromIdOrElse(settings.oreType, OreType.VANILLA);
                
                this.chunkPos = pos;
                this.chunkProviderSettings = ChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();
                this.dirtGen = createMinable(BlockStates.DIRT, this.chunkProviderSettings.dirtSize, oreType);
                this.gravelOreGen = createMinable(BlockStates.GRAVEL, this.chunkProviderSettings.gravelSize, oreType);
                this.graniteGen = createMinable(BlockStates.GRANITE, this.chunkProviderSettings.graniteSize, oreType);
                this.dioriteGen = createMinable(BlockStates.DIORITE, this.chunkProviderSettings.dioriteSize, oreType);
                this.andesiteGen = createMinable(BlockStates.ANDESITE, this.chunkProviderSettings.andesiteSize, oreType);
                this.coalGen = createMinable(BlockStates.COAL_ORE, this.chunkProviderSettings.coalSize, oreType);
                this.ironGen = createMinable(BlockStates.IRON_ORE, this.chunkProviderSettings.ironSize, oreType);
                this.goldGen = createMinable(BlockStates.GOLD_ORE, this.chunkProviderSettings.goldSize, oreType);
                this.redstoneGen = createMinable(BlockStates.REDSTONE_ORE, this.chunkProviderSettings.redstoneSize, oreType);
                this.diamondGen = createMinable(BlockStates.DIAMOND_ORE, this.chunkProviderSettings.diamondSize, oreType);
                this.lapisGen = createMinable(BlockStates.LAPIS_ORE, this.chunkProviderSettings.lapisSize, oreType);
                this.clayOreGen = new WorldGenClayOre(settings.claySize);
                this.clayGen = settings.useClayDisks ? new WorldGenClay(4) : WorldGenNoOp.INSTANCE;
                this.sandGen = settings.useSandDisks ? new WorldGenSand(Blocks.SAND, 7) : WorldGenNoOp.INSTANCE;
                this.gravelGen = settings.useGravelDisks ? new WorldGenSand(Blocks.GRAVEL, 6) : WorldGenNoOp.INSTANCE;
                this.generateFalls = settings.useSprings;
                
                this.genDecorations(biome, world, random);
                this.decorating = false;
            }
            
            info.cancel();
        }
    }
    
    @Inject(
        method = "generateOres",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/biome/BiomeDecorator;genStandardOre2(Lnet/minecraft/world/World;Ljava/util/Random;ILnet/minecraft/world/gen/feature/WorldGenerator;II)V",
            shift = At.Shift.AFTER
        )
    )
    private void injectGenerateOres(World world, Random random, CallbackInfo info) {
        WorldServer worldServer = (WorldServer)world;
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            MutableBlockPos mutablePos = new MutableBlockPos();
            ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(worldServer);
            
            if (TerrainGen.generateOre(world, random, this.clayOreGen, this.chunkPos, EventType.CUSTOM)) {
                ModernBetaBiomeDecorator.populateOreStandard(world, random, this.chunkPos, this.clayOreGen, mutablePos, settings.clayCount, settings.clayMinHeight, settings.clayMaxHeight);
            }
        }
    }
    
    @Unique
    private static WorldGenerator createMinable(IBlockState blockState, int size, OreType oreType) {
        return createMinable(blockState, size, WorldGenMinableMutable.STONE_PREDICATE, oreType);
    }

    @Unique
    private static WorldGenerator createMinable(IBlockState blockState, int size, Predicate<IBlockState> predicate, OreType oreType) {
        if (oreType == OreType.VANILLA) {
            return new WorldGenMinable(blockState, size, predicate);
        }
        
        return new WorldGenMinableMutable(blockState, size, predicate, oreType);
    }
}
