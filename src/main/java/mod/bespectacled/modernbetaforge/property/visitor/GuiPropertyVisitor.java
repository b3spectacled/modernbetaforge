package mod.bespectacled.modernbetaforge.property.visitor;

import mod.bespectacled.modernbetaforge.api.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.EntityEntryProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.property.StringProperty;
import net.minecraft.client.gui.GuiPageButtonList;

public interface GuiPropertyVisitor {
    GuiPageButtonList.GuiListEntry visit(BooleanProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(FloatProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(IntProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(StringProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(ListProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(BiomeProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(BlockProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(EntityEntryProperty property, int guiIdentifier);
}
