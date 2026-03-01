package mod.bespectacled.modernbetaforge.client.gui.modal;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModalList extends GuiModal<GuiModalList> {
    public GuiModalList(GuiScreen parent, String title, int modalWidth, int modalHeight, Consumer<GuiModalList> onConfirm, Consumer<GuiModalList> onCancel) {
        super(parent, title, modalWidth, modalHeight, onConfirm, onCancel);
    }

}
