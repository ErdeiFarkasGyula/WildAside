package net.farkas.wildaside.event;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.entity.custom.MucellithEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvent {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MUCELLITH.get(), MucellithEntity.createAttributes().build());
    }

//    @SubscribeEvent
//    public static void onSpawnPlacementsRegister(SpawnPlacementRegisterEvent event) {
//        System.out.println("MEOW");
//        event.register(ModEntities.MUCELLITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ((pEntityType, pServerLevel, pSpawnType, pPos, pRandom) ->
//                pServerLevel.getBrightness(LightLayer.BLOCK, pPos) <= 7), SpawnPlacementRegisterEvent.Operation.REPLACE);
//    }
}
