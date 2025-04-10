package net.farkas.wildaside.util;

import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class ContaminationHandler {
    public static void givePlayerContamination(Player player, int sec, int amplifier) {
        if (player.getEffect(ModMobEffects.IMMUNITY.get()).getAmplifier() < amplifier) {
            player.addEffect(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), sec * 20, amplifier));
        }
    }
}
