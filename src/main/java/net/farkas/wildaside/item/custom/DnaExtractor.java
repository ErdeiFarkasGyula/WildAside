package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.dna.Dna;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.farkas.wildaside.dna.genes.AbilityGene;
import net.farkas.wildaside.dna.genes.CoreGene;
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

        var trait = Traits.Core.getRandom(pPlayer.getRandom());
        System.out.println(trait);

        Dna dna = new Dna(pInteractionTarget,
                List.of(new CoreGene(trait, 0.5f, 3)),
                List.of(),
                List.of(new AbilityGene(Traits.Ability.REGENERATION, 1, 6)), 75);

        dna.applyTo(pPlayer);

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }
}
