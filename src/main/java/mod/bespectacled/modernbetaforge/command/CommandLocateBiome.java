package mod.bespectacled.modernbetaforge.command;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CommandLocateBiome extends CommandLocateExtended {
    private static final int RANGE = 4096;
    
    public CommandLocateBiome() {
        super("locatebiome", CommandLocateBiome::locate);
    }

    private static BlockPos locate(MinecraftServer server, ICommandSender sender, String[] args) {
        int senderX = sender.getPosition().getX();
        int senderZ = sender.getPosition().getZ();
        
        List<Biome> biomes = ImmutableList.of(ForgeRegistryUtil.get(new ResourceLocation(args[0]), ForgeRegistries.BIOMES));
        Random random = new Random(sender.getEntityWorld().getSeed());
        
        return sender.getEntityWorld().getBiomeProvider().findBiomePosition(senderX, senderZ, RANGE, biomes, random);
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<Biome> biomes = ForgeRegistryUtil.getValues(ForgeRegistries.BIOMES);
        String[] biomeNames = biomes.stream().map(biome -> biome.getRegistryName().toString()).toArray(String[]::new);
        
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, biomeNames) : Collections.emptyList();
    }
}
