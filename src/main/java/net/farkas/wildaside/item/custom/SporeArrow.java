package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.entity.custom.SporeArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SporeArrow extends ArrowItem {
    public SporeArrow(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
        return new SporeArrowEntity(pLevel, pShooter);
    }
}
