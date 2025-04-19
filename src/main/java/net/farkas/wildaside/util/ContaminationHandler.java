package net.farkas.wildaside.util;

import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class ContaminationHandler {
    public static void givePlayerContamination(Player player, int sec) {
        MobEffectInstance cont = player.getEffect(ModMobEffects.CONTAMINATION.get());
        MobEffectInstance immunity = player.getEffect(ModMobEffects.IMMUNITY.get());

        int amplifier = cont != null ? cont.getAmplifier() + 1 : 0;
        int duration = cont != null ? cont.getDuration() : 0;
        int cappedAmplifier = Math.min(amplifier, 4);

        if (immunity == null || cappedAmplifier > immunity.getAmplifier()) {
            if (duration <= 15 * 20) {
                player.addEffect(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), sec * 20, cappedAmplifier));
            }
        }
    }
}
