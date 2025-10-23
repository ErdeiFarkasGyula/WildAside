package net.farkas.wildaside.dna.genes;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record CoreGene(Traits.Core stat, float value, float stabilityCost) implements Gene {
    @Override
    public void apply(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getAttribute(stat.attribute).addPermanentModifier(
                    new AttributeModifier(UUID.nameUUIDFromBytes(uuid().getBytes(StandardCharsets.UTF_8)), uuid(), value, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    public String id() { return stat.name(); }

    @Override
    public float stabilityCost() { return stabilityCost; }

    public String uuid() {
        return DnaUtils.DNA_PREFIX + id();
    }
}