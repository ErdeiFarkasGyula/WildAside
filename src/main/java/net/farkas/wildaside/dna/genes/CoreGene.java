package net.farkas.wildaside.dna.genes;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.Traits;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record CoreGene(Traits.Core stat, float value, float stabilityCost) implements Gene {
    @Override
    public void apply(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getAttribute(stat.attribute).addPermanentModifier(new AttributeModifier(uuid(), value, AttributeModifier.Operation.MULTIPLY_TOTAL));
            entity.getPersistentData().putFloat("dna_modifier_" + stat.name(), value);
        }
    }

    @Override
    public String id() { return stat.name(); }

    @Override
    public float stabilityCost() { return stabilityCost; }

    public String uuid() {
        return WildAside.MOD_ID + "_dna_" + id();
    }
}