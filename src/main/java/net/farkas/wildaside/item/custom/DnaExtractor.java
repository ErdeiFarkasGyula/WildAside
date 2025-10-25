package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.dna.Dna;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Traits;
import net.farkas.wildaside.dna.genes.AbilityGene;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DnaExtractor extends Item {
    public DnaExtractor(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.OFF_HAND || pPlayer.level().isClientSide()) return InteractionResult.PASS;

        var base = DnaUtils.generateBaseCoreGenes(pInteractionTarget);
        var core = DnaUtils.mutateCoreGenes(base, pInteractionTarget);

        Dna dna = new Dna(pInteractionTarget,
                core,
                List.of(),
                List.of(new AbilityGene(Traits.Ability.REGENERATION, 1, 6)), 75);

        dna.applyTo(pPlayer);

        base.stream().forEach(baseGene ->
                System.out.println(
                        "BaseGene[" +
                                "stat=" + baseGene.trait() +
                                ", value=" + baseGene.value() +
                                ", stabilityCost=" + baseGene.stabilityCost() +
                                "]"
                ));

        core.stream().forEach(coreGene ->
                System.out.println(
                        "Modified[" +
                                "stat=" + coreGene.trait() +
                                ", value=" + coreGene.value() +
                                ", stabilityCost=" + coreGene.stabilityCost() +
                                "]"
                ));


        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }
}
