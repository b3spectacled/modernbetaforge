package mod.bespectacled.modernbetaforge.world.spawn;

import java.util.Random;

import net.minecraft.util.math.BlockPos.MutableBlockPos;

public class PEWorldSpawner extends BetaWorldSpawner {
    @Override
    protected void setRandomPosition(MutableBlockPos mutablePos, Random random) {
        int x = mutablePos.getX();
        int z = mutablePos.getZ();
        
        x += random.nextInt(32) - random.nextInt(32);
        z += random.nextInt(32) - random.nextInt(32);
        
        // Keep spawn pos within bounds of original PE world size
        if (x < 4) x += 32;
        if (x >= 251) x -= 32;
        
        if (z < 4) z += 32;
        if (z >= 251) z -= 32;
        
        mutablePos.setPos(x, mutablePos.getY(), z);
    }
}
