package net.farkas.wildaside.dna.ability.custom;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.ability.IAbility;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;

public class FireAbility implements IAbility {
    @Override
    public void onTick(LivingEntity entity, Gene gene) {

    }

    @Override
    public void onDamage(LivingEntity entity, DamageSource source, float amount) {

    }

    @Override
    public void onUse(LivingEntity entity, float cooldown) {
        if (entity.getPersistentData().getFloat(DATA_NAME) > 0) return;

        if (entity.level() instanceof ServerLevel level) {
            SmallFireball fireball = new SmallFireball(level, entity, entity.getLookAngle().x, entity.getLookAngle().y, entity.getLookAngle().z);
            fireball.setPos(entity.getEyePosition());
            level.addFreshEntity(fireball);
            entity.getPersistentData().putFloat(DATA_NAME, cooldown);
        }
    }
}
