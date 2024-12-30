package mod.bespectacled.modernbetaforge.compat;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;

public class CompatGC implements Compat {
    @Override
    public void load() {
        ModernBeta.log(Level.WARN, "Galacticraft has been loaded, sky/fog visual features may not work correctly due to incompatibilties!");
    }
}
