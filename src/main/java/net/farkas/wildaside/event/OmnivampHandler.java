package net.farkas.wildaside.event;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.particle.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "wildaside", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OmnivampHandler {
    private static class Task {
        final ServerLevel level;
        final UUID attackerId, targetId;
        final float healAmount;
        final int totalTicks;
        int ticksElapsed = 0;

        Task(ServerLevel level, UUID attackerId, UUID targetId, float healAmount, int totalTicks) {
            this.level = level;
            this.attackerId = attackerId;
            this.targetId = targetId;
            this.healAmount = healAmount;
            this.totalTicks = totalTicks;
        }
    }

    private static final List<Task> TASKS = new LinkedList<>();

    @SubscribeEvent
    public static void applyOmnivamp(LivingHurtEvent event) {
        DamageSource src = event.getSource();
        if (!(src.getEntity() instanceof Player attacker)) return;

        MobEffectInstance omni = attacker.getEffect(ModMobEffects.OMNIVAMP.get());
        if (omni == null) return;

        float perc = (omni.getAmplifier() + 1) / 10f;
        float max = (omni.getAmplifier() + 1) * 3f;
        float heal = event.getAmount() * perc;
        if (heal > max) heal = max;

        LivingEntity target = event.getEntity();
        if (attacker.level() instanceof ServerLevel lvl) {
            double dist = attacker.distanceTo(target);
            int delay = Mth.ceil(dist * 4);
            TASKS.add(new Task(lvl, attacker.getUUID(), target.getUUID(), heal, delay));
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.ServerTickEvent.Phase.END) return;

        Iterator<Task> it = TASKS.iterator();
        while (it.hasNext()) {
            Task t = it.next();

            var attacker = t.level.getEntity(t.attackerId);
            var target   = t.level.getEntity(t.targetId);
            if (!(attacker instanceof LivingEntity a) || !(target instanceof LivingEntity b)) {
                it.remove();
                continue;
            }

            double ax = a.getX(), ay = a.getY() + a.getBbHeight() * 0.5, az = a.getZ();
            double tx = b.getX(), ty = b.getY() + b.getBbHeight() * 0.5, tz = b.getZ();

            double prog = (double)t.ticksElapsed / t.totalTicks;
            double px = Mth.lerp(prog, tx, ax);
            double py = Mth.lerp(prog, ty, ay);
            double pz = Mth.lerp(prog, tz, az);

            t.level.sendParticles(ModParticles.OMNIVAMP_PARTICLE.get(), px, py, pz, 1, 0, 0, 0, 0);

            t.ticksElapsed++;
            if (t.ticksElapsed >= t.totalTicks) {
                a.heal(t.healAmount);
                it.remove();
            }
        }
    }
}