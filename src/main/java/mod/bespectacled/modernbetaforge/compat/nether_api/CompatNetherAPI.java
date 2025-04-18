package mod.bespectacled.modernbetaforge.compat.nether_api;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.NetherCompat;

public class CompatNetherAPI implements Compat, NetherCompat {
    public static final String MOD_ID = "nether_api";
    
    @Override
    public void load() {
        ModernBeta.log(Level.WARN, "Nether API has been detected, classic Nether settings will be disabled due to incompatibilties!");
    }
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
}
