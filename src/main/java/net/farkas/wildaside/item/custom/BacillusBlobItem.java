package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.dna.BacillusPayloadUtil;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class BacillusBlobItem extends Item {
    public BacillusBlobItem(Properties props) { super(props); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.pass(stack);

        player.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            Map<Trait, List<GeneLocus>> payload = BacillusPayloadUtil.readPayload(stack);
            if (payload.isEmpty()) return;
            Map<Trait, List<GeneLocus>> loci = dna.getLoci();
            loci.putAll(payload);
            dna.setLoci(loci);
            dna.setStress(dna.getStress() + BacillusPayloadUtil.computeStress(payload));
            dna.recomputeAndApply(player);
            stack.shrink(1);
        });

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}