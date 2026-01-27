package net.farkas.wildaside.dna.breeding;

import net.farkas.wildaside.WildAside;
import net.minecraft.world.entity.AgeableMob;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BreedingEventHandler {

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (event.getChild() == null) return;

        AgeableMob child = event.getChild();

        AgeableMob parentA = event.getParentA() instanceof AgeableMob a ? a : null;
        AgeableMob parentB = event.getParentB() instanceof AgeableMob b ? b : null;

        if (parentA == null && parentB == null) {
            WildAside.LOGGER.debug("BabyEntitySpawnEvent: No valid parents found for {}", child.getName().getString());
            return;
        }

        WildAside.LOGGER.debug("BabyEntitySpawnEvent triggered for child: {}", child.getName().getString());

        if (parentA != null && parentB != null) {
            BreedingHandler.applyInheritance(child, parentA, parentB);
        } else if (parentA != null) {
            WildAside.LOGGER.info("Single parent A detected, applying self-inheritance.");
            BreedingHandler.applyInheritance(child, parentA, parentA);
        } else {
            WildAside.LOGGER.info("Single parent B detected, applying self-inheritance.");
            BreedingHandler.applyInheritance(child, parentB, parentB);
        }
    }
}