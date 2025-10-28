package net.farkas.wildaside.event;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.client.ModKeyMappings;
import net.farkas.wildaside.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModEventsForgeClientEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (ModKeyMappings.USE_ABILITY.consumeClick()) {
                NetworkHandler.sendAbilityKeyUpdate();
                System.out.println("PRESSING G");
            }
        }
    }
}
