package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.DnaUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

public class DnaExtractor extends Item {
    public DnaExtractor(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.OFF_HAND || pPlayer.level().isClientSide()) return InteractionResult.PASS;

        var baseGenes = DnaUtils.generateBaseGenes(pInteractionTarget);
        var mutatedGenes = DnaUtils.mutateGenes(baseGenes, pInteractionTarget);

        pPlayer.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            dna.setSource(pInteractionTarget.getType());
            dna.setStability(dna.stability() - dna.calculateInstabilityChange(mutatedGenes));
            dna.setGenes(mutatedGenes);
            dna.apply(pPlayer);
        });

        AtomicReference<Float> stability = new AtomicReference<>(0f);
        pPlayer.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> stability.set(dna.stability()));

        mutatedGenes.forEach((trait, gene) -> {
            System.out.println(
                    "Modified[" +
                            "stat=" + trait.name() +
                            ", value=" + gene.value() +
                            ", stabilityCost=" + gene.stabilityCost() +
                            ", currentStability=" + stability.get() +
                            "]"
            );
        });

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }
}
