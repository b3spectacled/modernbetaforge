package mod.bespectacled.modernbetaforge.world.setting.visitor;

import mod.bespectacled.modernbetaforge.api.world.property.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.world.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.world.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.world.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.world.property.ListProperty;
import mod.bespectacled.modernbetaforge.api.world.property.StringProperty;
import net.minecraft.client.gui.GuiPageButtonList;

public interface GuiPropertyVisitor {
    GuiPageButtonList.GuiListEntry visit(BooleanProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(FloatProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(IntProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(StringProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(ListProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(BiomeProperty property, int guiIdentifier);
}
