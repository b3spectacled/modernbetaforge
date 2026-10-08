package mod.bespectacled.modernbetaforge.compat.defiledlands;

import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import net.minecraft.util.ResourceLocation;

public class CompatDefiledLands implements Compat, ClientCompat {
    public static final String MOD_ID = "defiledlands";
    public static final String ADDON_ID = "compat" + MOD_ID;
    
    public static final ResourceLocation KEY_USE_COMPAT = new ResourceLocation(ADDON_ID, "useCompat");

    @Override
    public void load() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void loadClient() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public String getModId() {
        return MOD_ID;
    }

}
