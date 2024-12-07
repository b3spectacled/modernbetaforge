package mod.bespectacled.modernbetaforge.world.structure;

import java.util.List;

import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class StructureWeightSampler {
    private static final float[] STRUCTURE_WEIGHTS = new float[13824];
    
    private final List<StructureComponent> structureComponents;
    
    public StructureWeightSampler(List<StructureComponent> structureComponents) {
        this.structureComponents = structureComponents;
    }
    
    public double sample(BlockPos blockPos, ChunkSource chunkSource) {
        int posX = blockPos.getX();
        int posY = blockPos.getY();
        int posZ = blockPos.getZ();
        int padding = 0;
        
        double density = 0.0;
        
        for (StructureComponent component : this.structureComponents) {
            StructureBoundingBox box = component.getBoundingBox();
            
            int height = 
                chunkSource.getHeight(box.minX, box.minZ, Type.STRUCTURE) +
                chunkSource.getHeight(box.minX, box.maxZ, Type.STRUCTURE) +
                chunkSource.getHeight(box.maxX, box.minZ, Type.STRUCTURE) +
                chunkSource.getHeight(box.maxX, box.maxZ, Type.STRUCTURE);
            height /= 4;
            
            int x = Math.max(0, Math.max(box.minX - padding - posX, posX - box.maxX + padding));
            int z = Math.max(0, Math.max(box.minZ - padding - posZ, posZ - box.maxZ + padding));
            int y = posY - height - 1;
            
            density += getStructureWeight(x, y, z) * 0.8;
        }
        
        return density;
    }
    
    private static double getStructureWeight(int x, int y, int z) {
        x += 12;
        y += 12;
        z += 12;
        
        if (!(validIndex(x) && validIndex(y) && validIndex(z)))
            return 0.0;
        
        return (double)STRUCTURE_WEIGHTS[z * 24 * 24 + x * 24 + y];
    }
    
    private static float calculateStructureWeight(double x, double y, double z) {
        double magnitude = x * x + y * y + z * z;
        double weight = Math.pow(Math.E, -magnitude / 16.0);
         
        return (float)(weight * (-y * MathHelper.fastInvSqrt(magnitude / 2.0) / 2.0));
    }
    
    private static boolean validIndex(int index) {
        return index >= 0 && index < 24;
    }
    
    static {
        for (int z = 0; z < 24; ++z) {
            for (int x = 0; x < 24; ++x) {
                for (int y = 0; y < 24; ++y) {
                    STRUCTURE_WEIGHTS[z * 24 * 24 + x * 24 + y] = calculateStructureWeight(x - 12, y - 11.5, z - 12);
                }
            }
        }
    }
}
