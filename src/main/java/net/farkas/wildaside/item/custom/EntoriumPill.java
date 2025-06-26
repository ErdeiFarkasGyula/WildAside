package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.util.AdvancementHandler;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntoriumPill extends Item {
    public EntoriumPill(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide && pLivingEntity instanceof ServerPlayer player) {
            int amplifier = 0;

            Holder<MobEffect> contamination = ModMobEffects.CONTAMINATION.getHolder().get();
            if (player.hasEffect(contamination)) {
                MobEffectInstance cont = pLivingEntity.getEffect(contamination);
                amplifier = cont.getAmplifier() + 1;
                if (amplifier >= 5) {
                    AdvancementHandler.givePlayerAdvancement(player, "purification_pill");
                }
            }

            player.addEffect(new MobEffectInstance(ModMobEffects.IMMUNITY.getHolder().get(), (amplifier + 1) * 10 * 20, amplifier));
            player.removeEffect(MobEffects.POISON);

        }

        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}
