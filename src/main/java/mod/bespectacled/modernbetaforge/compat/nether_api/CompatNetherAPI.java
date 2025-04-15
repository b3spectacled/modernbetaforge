package mod.bespectacled.modernbetaforge.compat.nether_api;

import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.NetherCompat;

public class CompatNetherAPI implements Compat, NetherCompat {
    public static final String MOD_ID = "nether_api";
    
    @Override
    public void load() { }
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
}
