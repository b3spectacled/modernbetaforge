package mod.bespectacled.modernbetaforge.world.carver;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public class MapGenNoOp extends MapGenBase {
    public MapGenNoOp(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) { }
    
    @Override
    public void generate(World world, int x, int z, ChunkPrimer chunkPrimer) { }
}
