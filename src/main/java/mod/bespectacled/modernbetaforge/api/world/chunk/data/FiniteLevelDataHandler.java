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
import com.google.common.collect.HashBiMap;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.FiniteChunkSource;
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
    
    public void setLevelData(Block[] blockData) {
        this.levelData.setLevelData(blockData);
    }
    
    public Block[] getLevelData() {
        return this.levelData.buildLevelData();
    }
    
    public void writeToDisk() throws IOException {
        File file = new File(this.worldDirectory, FILE_NAME);

        try(FileOutputStream fos = new FileOutputStream(file);
            GZIPOutputStream gis = new GZIPOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(gis)
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
        private final byte[] levelData; 
        private final BiMap<Byte, String> levelMap;
        
        private FiniteLevelData(int levelWidth, int levelHeight, int levelLength) {
            this.levelWidth = levelWidth;
            this.levelHeight = levelHeight;
            this.levelLength = levelLength;
            this.levelData = new byte[this.levelWidth * this.levelHeight * this.levelLength];
            this.levelMap = HashBiMap.create();
        }
        
        private void setLevelData(Block[] blockData) {
            byte id = 0;
            
            for (int i = 0; i < blockData.length; ++i) {
                Block block = blockData[i];
                String registryName = ForgeRegistries.BLOCKS.getKey(block).toString();
                
                if (!this.levelMap.containsValue(registryName)) {
                    this.levelMap.put(id++, registryName);
                }
                
                if (this.levelMap.size() > 255) {
                    throw new IndexOutOfBoundsException("Level data block map size exceeded 255!");
                }
                
                this.levelData[i] = this.levelMap.inverse().get(registryName);
            }
            
            ModernBeta.log(Level.DEBUG, String.format("Packed byte array of size %d", this.levelData.length));
            ModernBeta.log(Level.DEBUG, String.format("Packed block map of size %d", this.levelMap.size()));
        }
        
        private Block[] buildLevelData() {
            if (this.levelData.length != this.levelWidth * this.levelHeight * this.levelLength) {
                throw new IllegalStateException("Indev level size from file is not correct!");
            }
            
            Block[] blockData = new Block[this.levelData.length];
            for (int i = 0; i < this.levelData.length; ++i) {
                String registryName = this.levelMap.get(this.levelData[i]);
                Block block;
                
                if (registryName == null) {
                    throw new NullPointerException("Block registry name is null!");
                }
                
                block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
                
                if (block == null) {
                    throw new NullPointerException("Block was not found in Forge registry!");
                }
                
                blockData[i] = block;
            }
            
            ModernBeta.log(Level.DEBUG, String.format("Unpacked byte array of size %d", this.levelData.length));
            ModernBeta.log(Level.DEBUG, String.format("Unpacked block map of size %d", this.levelMap.size()));
            
            return blockData;
        }
    }
}
