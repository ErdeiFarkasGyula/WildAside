package net.farkas.wildaside.util;

import net.farkas.wildaside.effect.ModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class ContaminationHandler {
    public static void givePlayerContamination(Player player, int sec) {
        if (!(player.hasEffect(ModMobEffects.IMMUNITY.get()))) {
            player.addEffect(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), sec * 20));
        }
    }
}
