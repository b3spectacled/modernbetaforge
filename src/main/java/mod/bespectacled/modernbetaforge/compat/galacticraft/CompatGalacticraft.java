package mod.bespectacled.modernbetaforge.compat.galacticraft;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.Compat;

public class CompatGalacticraft implements Compat {
    public static final String MOD_ID = "galacticraftcore";
    
    @Override
    public void load() {
        ModernBeta.log(Level.WARN, "Galacticraft has been detected, sky/fog visual features may not work correctly due to incompatibilties!");
    }
    
    @Override
    public String getModId() {
        return MOD_ID;
    }
}
