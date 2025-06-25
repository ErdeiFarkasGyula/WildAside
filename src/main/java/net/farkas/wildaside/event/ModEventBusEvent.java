package net.farkas.wildaside.event;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.contamination.IContamination;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.entity.custom.hickory.HickoryTreantEntity;
import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvent {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MUCELLITH.get(), MucellithEntity.createAttributes().build());
        event.put(ModEntities.HICKORY_TREANT.get(), HickoryTreantEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IContamination.class);
    }
}
