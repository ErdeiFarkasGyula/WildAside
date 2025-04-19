package net.farkas.wildaside.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HickoryNutTrailMix extends Item {
    public HickoryNutTrailMix(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide && pLivingEntity instanceof Player player) {
            MobEffectInstance speed = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 800, 0);
            MobEffectInstance jump = new MobEffectInstance(MobEffects.JUMP, 800, 0);
            MobEffectInstance saturation = new MobEffectInstance(MobEffects.SATURATION, 800, 0);

            player.addEffect(speed);
            player.addEffect(jump);
            player.addEffect(saturation);
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}
