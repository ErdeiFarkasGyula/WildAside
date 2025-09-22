package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HickoryNutTrailMix extends Item {
    private final HickoryColour colour;

    public HickoryNutTrailMix(Properties pProperties, HickoryColour colour) {
        super(pProperties);
        this.colour = colour;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (!pLevel.isClientSide) {
            MobEffectInstance saturation = new MobEffectInstance(MobEffects.SATURATION, 400, 0);
            MobEffect effect;

            switch (colour) {
                case RED_GLOWING -> effect = MobEffects.DAMAGE_BOOST;
                case BROWN_GLOWING -> effect = MobEffects.DAMAGE_RESISTANCE;
                case YELLOW_GLOWING -> effect = MobEffects.FIRE_RESISTANCE;
                case GREEN_GLOWING -> effect = MobEffects.REGENERATION;
                default -> effect = MobEffects.SATURATION;
            }

            pLivingEntity.addEffect(saturation);
            pLivingEntity.addEffect(new MobEffectInstance(effect, 400, 0));
        }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }

    public HickoryColour getColour() {
        return this.colour;
    }
}
