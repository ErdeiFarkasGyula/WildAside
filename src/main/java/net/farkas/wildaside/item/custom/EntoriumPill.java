package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.util.AdvancementHandler;
import net.minecraft.server.level.ServerPlayer;
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

            if (player.hasEffect(ModMobEffects.CONTAMINATION.get())) {
                MobEffectInstance cont = pLivingEntity.getEffect(ModMobEffects.CONTAMINATION.get());
                amplifier = cont.getAmplifier() + 1;
                if (amplifier >= 5) {
                    AdvancementHandler.givePlayerAdvancement(player, "purification_pill");
                }
            }

            player.addEffect(new MobEffectInstance(ModMobEffects.IMMUNITY.get(), (amplifier + 1) * 10 * 20, amplifier));
            player.removeEffect(MobEffects.POISON);

        }

        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}
