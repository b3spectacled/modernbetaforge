package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BiomeBeta extends ModernBetaBiome {
    protected static final SpawnListEntry RABBIT = new SpawnListEntry(EntityRabbit.class, 4, 2, 3);
    protected static final SpawnListEntry RABBIT_TUNDRA = new SpawnListEntry(EntityRabbit.class, 10, 2, 3);
    protected static final SpawnListEntry POLAR_BEAR = new SpawnListEntry(EntityPolarBear.class, 1, 1, 2);
    protected static final SpawnListEntry HORSE_PLAINS = new SpawnListEntry(EntityHorse.class, 5, 2, 6);
    protected static final SpawnListEntry HORSE_SAVANNA = new SpawnListEntry(EntityHorse.class, 1, 2, 6);
    protected static final SpawnListEntry DONKEY_PLAINS = new SpawnListEntry(EntityDonkey.class, 1, 1, 3);
    protected static final SpawnListEntry DONKEY_SAVANNA = new SpawnListEntry(EntityDonkey.class, 1, 1, 1);
    protected static final SpawnListEntry LLAMA = new SpawnListEntry(EntityLlama.class, 1, 4, 4);
    protected static final SpawnListEntry PARROT = new SpawnListEntry(EntityParrot.class, 40, 1, 2);
    protected static final SpawnListEntry OCELOT = new SpawnListEntry(EntityOcelot.class, 2, 1, 1);
    protected static final SpawnListEntry WOLF_TAIGA = new SpawnListEntry(EntityWolf.class, 8, 4, 4);
    
    protected static final SpawnListEntry HUSK = new SpawnListEntry(EntityHusk.class, 80, 4, 4);
    protected static final SpawnListEntry STRAY = new SpawnListEntry(EntityStray.class, 80, 4, 4);
    protected static final SpawnListEntry SLIME_SWAMP = new SpawnListEntry(EntitySlime.class, 1, 1, 1);
    
    public BiomeBeta(BiomeProperties properties) {
        super(properties);
        
        this.populateSpawnableMobs(EnumCreatureType.MONSTER, SPIDER, SKELETON, ZOMBIE, CREEPER, SLIME);
        this.populateSpawnableMobs(EnumCreatureType.CREATURE, SHEEP, PIG, COW, CHICKEN);
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorBeta());
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float temp) {
        BlockPos blockPos = Minecraft.getMinecraft().player.getPosition();
        
        if (BetaColorSampler.INSTANCE.canSampleSkyColor()) {
            return BetaColorSampler.INSTANCE.getSkyColor(blockPos);
        }

        return super.getSkyColorByTemp(temp);
    }
    
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos blockPos) {
        if (BetaColorSampler.INSTANCE.canSampleBiomeColor()) {
            return BetaColorSampler.INSTANCE.getGrassColor(blockPos);
        }
        
        return super.getGrassColorAtPos(blockPos);
    }
    
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos blockPos) {
        if (BetaColorSampler.INSTANCE.canSampleBiomeColor()) {
            return BetaColorSampler.INSTANCE.getFoliageColor(blockPos);
        }
        
        return super.getFoliageColorAtPos(blockPos);
    }
    
    public static boolean canSetIceBeta(World world, BlockPos blockPos, boolean doWaterCheck, double temp) {
        if (temp >= 0.5) {
            return false;
        }
        
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, blockPos) < 10) {
            IBlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            boolean isWater = block == Blocks.WATER || block == Blocks.FLOWING_WATER;
            
            if (isWater && blockState.getValue(BlockLiquid.LEVEL) == 0) {
                if (!doWaterCheck) {
                    return true;
                }
                
                boolean submerged = 
                    isWater(world, blockPos.west()) && 
                    isWater(world, blockPos.east()) && 
                    isWater(world, blockPos.north()) && 
                    isWater(world, blockPos.south());
                
                if (!submerged) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static boolean canSetSnowBeta(World world, BlockPos blockPos, double temp) {
        if (temp >= 0.5) {
            return false;
        }
        
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, blockPos) < 10) {
            IBlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock().isAir(blockState, world, blockPos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, blockPos)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean isWater(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos).getMaterial() == Material.WATER;
    }
}
