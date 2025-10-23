package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.dna.Dna;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.farkas.wildaside.dna.genes.AbilityGene;
import net.farkas.wildaside.dna.genes.CoreGene;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.util.AdvancementHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntoriumPill extends Item {
    public EntoriumPill(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide && pLivingEntity instanceof ServerPlayer player) {
            int amplifier = 0;

            if (player.hasEffect(ModMobEffects.CONTAMINATION.get())) {
                MobEffectInstance cont = pLivingEntity.getEffect(ModMobEffects.CONTAMINATION.get());
                amplifier = cont.getAmplifier() + 1;
                if (amplifier >= 5) {
                    AdvancementHandler.givePlayerAdvancement(player, "purification_pill");
                }
            }

            player.addEffect(new MobEffectInstance(ModMobEffects.IMMUNITY.get(), (amplifier + 1) * 10 * 20, amplifier));
            player.removeEffect(MobEffects.POISON);

            Dna dna = new Dna(pLivingEntity,
                    List.of(new CoreGene(Traits.Core.ARMOR, 0.25f, 3)),
                    List.of(),
                    List.of(new AbilityGene(Traits.Ability.REGENERATION, 1, 6)), 75);

            dna.applyTo(pLivingEntity);

        }

        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.wildaside.entorium_pill.tooltip"));
    }
}
