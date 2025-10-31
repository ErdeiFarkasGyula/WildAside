package net.farkas.wildaside.dna.ability;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AbilityEvents {
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        LivingEntity entity = event.getEntity();

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            float cooldown = entity.getPersistentData().getFloat(IAbility.COOLDOWN);
            if (cooldown > 0) {
                entity.getPersistentData().putFloat(IAbility.COOLDOWN, cooldown - 1);
            }
            for (Gene gene : dna.genes().values()) {
                if (gene.trait().traitType() == TraitTypes.ABILITY) {
                    IAbility behavior = Abilities.get(gene.trait());
                    if (behavior != null) behavior.onTick(entity, gene);
                }
            }
        });
    }
}
