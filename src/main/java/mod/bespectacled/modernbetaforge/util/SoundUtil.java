package mod.bespectacled.modernbetaforge.util;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.init.SoundEvents;

public class SoundUtil {
    public static void playClickSound(SoundHandler soundHandler) {
        soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
