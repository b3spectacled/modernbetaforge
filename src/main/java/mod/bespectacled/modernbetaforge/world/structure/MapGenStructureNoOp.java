package mod.bespectacled.modernbetaforge.world.structure;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

public class MapGenStructureNoOp extends MapGenStructure {
    public static final MapGenStructureNoOp INSTANCE = new MapGenStructureNoOp();
    
    private MapGenStructureNoOp() { }

    @Override
    public String getStructureName() {
        return null;
    }

    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        return null;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return false;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return null;
    }
}
