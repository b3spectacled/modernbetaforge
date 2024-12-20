package mod.bespectacled.modernbetaforge.world.setting.visitor;

import mod.bespectacled.modernbetaforge.api.world.setting.BiomeProperty;
import mod.bespectacled.modernbetaforge.api.world.setting.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.world.setting.FloatProperty;
import mod.bespectacled.modernbetaforge.api.world.setting.IntProperty;
import mod.bespectacled.modernbetaforge.api.world.setting.ListProperty;
import mod.bespectacled.modernbetaforge.api.world.setting.StringProperty;
import net.minecraft.client.gui.GuiPageButtonList;

public interface GuiPropertyVisitor {
    GuiPageButtonList.GuiListEntry visit(BooleanProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(FloatProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(IntProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(StringProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(ListProperty property, int guiIdentifier);
    
    GuiPageButtonList.GuiListEntry visit(BiomeProperty property, int guiIdentifier);
}
