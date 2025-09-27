package net.farkas.wildaside.event;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.contamination.IContamination;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.entity.custom.hickory.HickoryTreantEntity;
import net.farkas.wildaside.entity.custom.vibrion.ContaminatedCreeperEntity;
import net.farkas.wildaside.entity.custom.vibrion.MucellithEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvent {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MUCELLITH.get(), MucellithEntity.createAttributes().build());
        event.put(ModEntities.HICKORY_TREANT.get(), HickoryTreantEntity.createAttributes().build());
        event.put(ModEntities.CONTAMINATED_CREEPER.get(), ContaminatedCreeperEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        String id = "wildaside_ce";
        Path packPath = ModList.get().getModFileById(WildAside.MOD_ID).getFile().findResource("resourcepacks/" + id);

        event.addRepositorySource(consumer -> {
            Pack pack = Pack.readMetaAndCreate(
                    id,
                    Component.literal("Wild Aside CEntertain"),
                    false,
                    (name) -> new PathPackResources(name, packPath, false),
                    event.getPackType(),
                    Pack.Position.TOP,
                    PackSource.DEFAULT
            );

            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IContamination.class);
    }
}
