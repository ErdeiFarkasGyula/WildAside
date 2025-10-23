package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class DnaUtils {
    public static final String DNA_PREFIX = WildAside.MOD_ID + "_dna_";

    public static void clearDnaEffects(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
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
}