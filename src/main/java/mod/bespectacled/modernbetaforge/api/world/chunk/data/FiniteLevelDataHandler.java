package mod.bespectacled.modernbetaforge.api.world.chunk.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.Level;

import com.google.common.collect.BiMap;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.FiniteChunkSource.LevelDataContainer;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class FiniteLevelDataHandler {
    public static final String FILE_NAME = "mblevel.dat";
    
    private final File worldDirectory;
    private FiniteLevelData levelData;
    
    public FiniteLevelDataHandler(World world, FiniteChunkSource chunkSource) {
        this.worldDirectory = world.getSaveHandler().getWorldDirectory();
        this.levelData = new FiniteLevelData(
            chunkSource.getLevelWidth(),
            chunkSource.getLevelHeight(),
            chunkSource.getLevelLength()
        );
    }
    
    public void setLevelData(byte[] levelData, BiMap<Byte, String> levelMap) {
        this.levelData.setLevelData(levelData, levelMap);
    }
    
    public LevelDataContainer getLevelData(int levelWidth, int levelHeight, int levelLength) {
        return this.levelData.getLevelData(levelWidth, levelHeight, levelLength);
    }
    
    public void writeToDisk() throws IOException {
        File file = new File(this.worldDirectory, FILE_NAME);

        try(FileOutputStream fos = new FileOutputStream(file);
            GZIPOutputStream gos = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gos)
        ) {
            oos.writeObject(this.levelData);
        }
    }
    
    public void readFromDisk() throws IOException, ClassNotFoundException {
        File file = new File(this.worldDirectory, FILE_NAME);
        
        try(FileInputStream fis = new FileInputStream(file);
            GZIPInputStream gis = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gis)
        ) {
            this.levelData = (FiniteLevelData)ois.readObject();
        }
    }
    
    private static class FiniteLevelData implements Serializable {
        private static final long serialVersionUID = 4163598858586006355L;
        
        private final int levelWidth;
        private final int levelHeight;
        private final int levelLength;
        
        private byte[] levelData; 
        private BiMap<Byte, String> levelMap;
        
        private FiniteLevelData(int levelWidth, int levelHeight, int levelLength) {
            this.levelWidth = levelWidth;
            this.levelHeight = levelHeight;
            this.levelLength = levelLength;
        }
        
        private void setLevelData(byte[] levelData, BiMap<Byte, String> levelMap) {
            this.levelData = levelData;
            this.levelMap = levelMap;
            
            ModernBeta.log(Level.DEBUG, String.format("Packed byte array of size %d", this.levelData.length));
            ModernBeta.log(Level.DEBUG, String.format("Packed block map of size %d", this.levelMap.size()));
        }
        
        private LevelDataContainer getLevelData(int levelWidth, int levelHeight, int levelLength) {
            if (this.levelWidth != levelWidth || this.levelHeight != levelHeight || this.levelLength != levelLength) {
                throw new IllegalStateException("Stored level dimensions were somehow corrupted!");
            }
            
            if (this.levelData.length != this.levelWidth * this.levelHeight * this.levelLength) {
                throw new IllegalStateException("Stored level size from file was somehow corrupted!");
            }
            
            for (String registryName : this.levelMap.values()) {
                if (registryName == null) {
                    throw new NullPointerException("Stored block registry name is null!");
                }
                
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
                
                if (block == null) {
                    throw new NullPointerException("Stored block was not found in Forge registry!");
                }
            }
            
            ModernBeta.log(Level.DEBUG, String.format("Unpacked byte array of size %d", this.levelData.length));
            ModernBeta.log(Level.DEBUG, String.format("Unpacked block map of size %d", this.levelMap.size()));
            
            return new LevelDataContainer(this.levelData, this.levelMap);
        }
    }
}
