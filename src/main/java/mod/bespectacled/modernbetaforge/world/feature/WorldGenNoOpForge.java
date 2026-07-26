package mod.bespectacled.modernbetaforge.world.feature;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenNoOpForge implements IWorldGenerator {
    public static final WorldGenNoOpForge INSTANCE = new WorldGenNoOpForge();
    
    private WorldGenNoOpForge() { }
    
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) { }
    
}
