package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DnaUtils {
    public static final String DNA_PREFIX = WildAside.MOD_ID + "_dna_";

    public static void clearDnaEffects(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            List<MobEffectInstance> toRemoveEffects = new ArrayList<>();
            for (MobEffectInstance instance : livingEntity.getActiveEffects()) {
                String name = instance.getDescriptionId();
                String dataName = DNA_PREFIX + name.split("\\.")[name.split("\\.").length - 1].toUpperCase();

                if (livingEntity.getPersistentData().contains(dataName)) {
                    toRemoveEffects.add(instance);
                }

            }

            toRemoveEffects.forEach(effect -> livingEntity.removeEffect(effect.getEffect()));

            for (AttributeInstance attr : livingEntity.getAttributes().getSyncableAttributes()) {
                List<AttributeModifier> toRemove = new ArrayList<>();
                for (AttributeModifier mod : attr.getModifiers()) {
                    if (mod.getName().startsWith(DNA_PREFIX)) {
                        toRemove.add(mod);
                    }
                }
                toRemove.forEach(attr::removeModifier);
            }

            CompoundTag tag = livingEntity.getPersistentData();
            List<String> keysToRemove = tag.getAllKeys().stream().filter(k -> k.startsWith(DNA_PREFIX)).toList();
            keysToRemove.forEach(tag::remove);
        }
    }

    public static UUID getUuid(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }
}