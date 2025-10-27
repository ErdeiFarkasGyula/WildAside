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

        var base = DnaUtils.generateBaseCoreGenes(pInteractionTarget);
        var mutateCoreGenes = DnaUtils.mutateCoreGenes(base, pInteractionTarget);

        pPlayer.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            dna.setSource(pInteractionTarget.getType());
            dna.setStability(dna.stability() - dna.calculateInstabilityChange(mutateCoreGenes));
            dna.setGenes(mutateCoreGenes);
            dna.apply(pPlayer);
        });

        AtomicReference<Float> stability = new AtomicReference<>((float) 0);

        pPlayer.getCapability(DnaCapability.INSTANCE).ifPresent(iDna -> stability.set(iDna.stability()));

        mutateCoreGenes.stream().forEach(coreGene ->
                System.out.println(
                        "Modified[" +
                                "stat=" + coreGene.trait() +
                                ", value=" + coreGene.value() +
                                ", stabilityCost=" + coreGene.stabilityCost() +
                                ", currentStability:" + stability +
                                "]"
                ));


//        MinecraftServer server = pPlayer.getServer();
//        ServerLevel level = server.getLevel(ModDimensions.TEST_LEVEL);
//        MobSpeedTesting.runBatchTest(level, new BlockPos(0, 5, 0));

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }
}
