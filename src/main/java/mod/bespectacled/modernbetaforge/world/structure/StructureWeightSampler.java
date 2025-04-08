package mod.bespectacled.modernbetaforge.world.structure;

import java.util.List;
import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorStructureStart;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureWeightSampler {
    private static final float[] STRUCTURE_WEIGHTS = new float[13824];
    
    private final List<StructureComponent> structureComponents;
    
    public StructureWeightSampler(List<StructureComponent> structureComponents) {
        this.structureComponents = structureComponents;
    }
    
    public double sample(ChunkSource chunkSource, BlockPos blockPos) {
        if (this.structureComponents == null) {
            return 0.0;
        }
        
        int posX = blockPos.getX();
        int posY = blockPos.getY();
        int posZ = blockPos.getZ();
        
        double density = 0.0;
        
        for (int i = 0; i < this.structureComponents.size(); ++i) {
            StructureBoundingBox box = this.structureComponents.get(i).getBoundingBox();
            
            int height = getStructureHeight(chunkSource, box);
            int x = Math.max(0, Math.max(box.minX - posX, posX - box.maxX));
            int z = Math.max(0, Math.max(box.minZ - posZ, posZ - box.maxZ));
            int y = posY - height - 1;
            
            density += getStructureWeight(x, y, z) * 0.8;
        }
        
        return density;
    }
    
    public static int getStructureHeight(ChunkSource chunkSource, StructureBoundingBox boundingBox) {
        int centerX = boundingBox.minX + boundingBox.getXSize() / 2;
        int centerZ = boundingBox.minZ + boundingBox.getZSize() / 2;
        
        return chunkSource.getHeight(centerX, centerZ, Type.STRUCTURE);
    }
    
    public static int cacheStructures(ModernBetaChunkGenerator chunkGenerator, StructureStart start, Predicate<StructureComponent> excluder) {
        ChunkSource chunkSource = chunkGenerator.getChunkSource();
        int numComponents = 0;
        
        if (chunkSource instanceof NoiseChunkSource) {
            AccessorStructureStart accessor = (AccessorStructureStart)start;
            
            for (StructureComponent component : accessor.getComponents()) {
                if (excluder.test(component)) {
                    continue;
                }

                StructureBoundingBox box = component.getBoundingBox();
                
                int minChunkX = box.minX >> 4;
                int minChunkZ = box.minZ >> 4;
                int maxChunkX = box.maxX >> 4;
                int maxChunkZ = box.maxZ >> 4;
                
                for (int componentChunkZ = minChunkZ - 1; componentChunkZ <= maxChunkZ + 1; ++componentChunkZ) {
                    for (int componentChunkX = minChunkX - 1; componentChunkX <= maxChunkX + 1; ++componentChunkX) {
                        chunkGenerator.cacheStructureComponent(componentChunkX, componentChunkZ, component);
                    }
                }
                
                numComponents++;
            }
        }
        
        return numComponents;
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
