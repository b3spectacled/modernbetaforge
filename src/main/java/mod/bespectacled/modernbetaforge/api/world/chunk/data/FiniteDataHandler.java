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

public class FiniteDataHandler {
    public static final String FILE_NAME = "mblevel.dat";
    
    private static final int FINITE_VERSION_V1_3_1_0 = 1310;
    
    private final File worldDirectory;
    private FiniteData finiteData;
    
    /**
     * Constructs a new finite data handler.
     * 
     * @param world
     * @param finiteChunkSource
     */
    public FiniteDataHandler(World world, FiniteChunkSource finiteChunkSource) {
        this.worldDirectory = world.getSaveHandler().getWorldDirectory();
        this.finiteData = new FiniteData(
            finiteChunkSource.getLevelWidth(),
            finiteChunkSource.getLevelHeight(),
            finiteChunkSource.getLevelLength()
        );
    }
    
    /**
     * Sets given byte level data array into the finite data container.
     * 
     * @param levelData Level byte array provided by FiniteChunkSource object.
     * @param levelMap Level byte-string id map.
     */
    public void setLevelData(byte[] levelData, BiMap<Byte, String> levelMap) {
        this.finiteData.setLevelData(levelData, levelMap);
    }
    
    /**
     * Gets a new LevelDataContainer populated from the loaded finite data.
     * 
     * @param levelWidth The level width.
     * @param levelHeight The level height.
     * @param levelLength The level length
     * @return A new LevelDataContainer populated from the loaded finite data.
     */
    public LevelDataContainer getLevelData(int levelWidth, int levelHeight, int levelLength) {
        return this.finiteData.getLevelData(levelWidth, levelHeight, levelLength);
    }
    
    /**
     * Gets the data version for the data handler.
     * 
     * @return The current data version.
     */
    private static int getFiniteVersion() {
        return FINITE_VERSION_V1_3_1_0;
    }
    
    /**
     * Attempts to write the current finite data to disk. Uses GZIP compression.
     * 
     * @throws IOException if the finite data can't be written to disk
     */
    public void writeToDisk() throws IOException {
        File file = new File(this.worldDirectory, FILE_NAME);

        try(FileOutputStream fos = new FileOutputStream(file);
            GZIPOutputStream gos = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gos)
        ) {
            oos.writeObject(this.finiteData);
        }
    }
    
    /**
     * Attempts to read finite data from disk. Uses GZIP compression.
     * 
     * @throws IOException if the finite data can't be read from disk
     * @throws ClassNotFoundException if the finite data class is not found (when would this happen?)
     */
    public void readFromDisk() throws IOException, ClassNotFoundException {
        File file = new File(this.worldDirectory, FILE_NAME);
        
        try(FileInputStream fis = new FileInputStream(file);
            GZIPInputStream gis = new GZIPInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(gis)
        ) {
            this.finiteData = (FiniteData)ois.readObject();
        }
    }
    
    private static class FiniteData implements Serializable {
        private static final long serialVersionUID = 4163598858586006355L;
        
        private final int levelVersion = FiniteDataHandler.getFiniteVersion();
        
        private final int levelWidth;
        private final int levelHeight;
        private final int levelLength;
        
        private byte[] levelData; 
        private BiMap<Byte, String> levelMap;
        
        /**
         * Constructs a new FiniteData container with level dimensions.
         * 
         * @param levelWidth The level width.
         * @param levelHeight The level height.
         * @param levelLength The level length.
         */
        private FiniteData(int levelWidth, int levelHeight, int levelLength) {
            this.levelWidth = levelWidth;
            this.levelHeight = levelHeight;
            this.levelLength = levelLength;
        }
        
        /**
         * Sets given byte level data array into the finite data container.
         * 
         * @param levelData Level byte array provided by FiniteChunkSource object.
         * @param levelMap Level byte-string id map.
         */
        private void setLevelData(byte[] levelData, BiMap<Byte, String> levelMap) {
            this.levelData = levelData;
            this.levelMap = levelMap;
            
            ModernBeta.log(Level.DEBUG, String.format("Packed byte array of size %d", this.levelData.length));
            ModernBeta.log(Level.DEBUG, String.format("Packed block map of size %d", this.levelMap.size()));
        }

        /**
         * Gets a new LevelDataContainer populated from the loaded finite data.
         * 
         * @param levelWidth The level width.
         * @param levelHeight The level height.
         * @param levelLength The level length
         * @return A new LevelDataContainer populated from the loaded finite data.
         * @throws IllegalStateException if the finite data information was somehow corrupted.
         * @throws NullPointerException if the loaded block data does not have corresponding Forge registry entries.
         */
        private LevelDataContainer getLevelData(int levelWidth, int levelHeight, int levelLength) throws IllegalStateException, NullPointerException {
            if (this.levelVersion < FiniteDataHandler.getFiniteVersion()) {
                String errorStr = String.format(
                    "Stored level version %d is older than current version %d!",
                    this.levelVersion,
                    FiniteDataHandler.getFiniteVersion()
                );
                
                throw new IllegalStateException(errorStr);
            }
            
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
