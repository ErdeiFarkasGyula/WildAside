package net.farkas.wildaside.dna.genes;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public record CoreGene(Traits.Core stat, float value, float stabilityCost) implements Gene {
    @Override
    public void apply(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (stat.attribute != null) {
                var instance = livingEntity.getAttribute(stat.attribute);
                if (instance != null) {
                    UUID id = DnaUtils.getUuid(fullName());
                    instance.removeModifier(id);
                    instance.removePermanentModifier(id);
                }
            }

            livingEntity.getAttribute(stat.attribute).addPermanentModifier(
                    new AttributeModifier(DnaUtils.getUuid(fullName()), fullName(), value, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    public String id() { return stat.name(); }

    @Override
    public float stabilityCost() { return stabilityCost; }

    public static String fullName(String id) {
        return DnaUtils.DNA_PREFIX + id;
    }

    public String fullName() {
        return DnaUtils.DNA_PREFIX + id();
    }
}