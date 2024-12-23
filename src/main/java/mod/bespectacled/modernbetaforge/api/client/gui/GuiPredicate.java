package mod.bespectacled.modernbetaforge.api.client.gui;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.client.gui.GuiIdentifiers;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class GuiPredicate {
    private Predicate<ModernBetaGeneratorSettings.Factory> predicate;
    private final int[] guiIds;
    
    /**
     * Constructs a new GuiPredicate with an associated GUI integer id and its predicate consuming a settings Factory.
     * 
     * @param predicate The predicate used to test if the GUI button associated with the id should be enabled.
     * @param guiIds A list of integer ids that is associate the GUI buttons, can be found in {@link GuiIdentifiers}.
     */
    public GuiPredicate(Predicate<ModernBetaGeneratorSettings.Factory> predicate, int... guiIds) {
        this.predicate = predicate;
        this.guiIds = guiIds;
    }
    
    /**
     * Append to the current predicate with a new OR condition.
     * 
     * @param predicate The new predicate to test in addition to the existing predicate.
     * @return This GuiPredicate object, so the statements can be chained.
     */
    public GuiPredicate or(Predicate<ModernBetaGeneratorSettings.Factory> predicate) {
        this.predicate = this.predicate.or(predicate);
        
        return this;
    }
    
    /**
     * Append to the current predicate with a new AND condition.
     * 
     * @param predicate The new predicate to test in addition to the existing predicate.
     * @return This GuiPredicate object, so the statements can be chained.
     */
    public GuiPredicate and(Predicate<ModernBetaGeneratorSettings.Factory> predicate) {
        this.predicate = this.predicate.and(predicate);
        
        return this;
    }
    
    /**
     * Tests the predicate.
     * 
     * @param factory The settings Factory to use to test.
     * @return Whether the test is passed. This is then used to set the GUI button's enabled state.
     */
    public boolean test(ModernBetaGeneratorSettings.Factory factory) {
        return this.predicate.test(factory);
    }
    
    /**
     * Gets the GUI id associated with the predicate. GUI ids for built-in buttons can be found in {@link GuiIdentifiers}.
     * 
     * @return An integer denoting the GUI button's id.
     */
    public int[] getIds() {
        return this.guiIds;
    }
}
