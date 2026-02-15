package mod.bespectacled.modernbetaforge.compat.bettermineshafts;

import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventMineshaftGenOptimized {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMineshaftGen(InitMapGenEvent event) {
        if (event.getType() == InitMapGenEvent.EventType.MINESHAFT) {
            event.setNewGen(new MapGenBetterMineshaftOptimized());
        }
    }
}
