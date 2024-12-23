package mod.bespectacled.modernbetaforge.api.client.gui;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.client.gui.GuiIdentifiers;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class GuiPredicate {
    private final int guiId;
    private Predicate<ModernBetaGeneratorSettings.Factory> predicate;
    
    /**
     * Constructs a new GuiPredicate with an associated GUI integer id and its predicate consuming a settings Factory.
     * 
     * @param guiIdentifier The integer id that is associate the GUI button, can be found in {@link GuiIdentifiers}.
     * @param predicate The predicate used to test if the GUI button associated with the id should be enabled.
     */
    public GuiPredicate(int guiIdentifier, Predicate<ModernBetaGeneratorSettings.Factory> predicate) {
        this.guiId = guiIdentifier;
        this.predicate = predicate;
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
    public int getId() {
        return this.guiId;
    }
}
