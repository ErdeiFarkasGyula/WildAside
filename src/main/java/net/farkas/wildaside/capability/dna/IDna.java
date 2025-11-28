package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Map;

public interface IDna extends INBTSerializable<CompoundTag> {
    @Nullable EntityType<?> getSource();
    Map<Trait, Gene> getGenes();
    float getStability();
    boolean getModified();
    void setSource(EntityType<?> source);
    void setGenes(Map<Trait, Gene> genes);
    void setStability(float stability);
    void setModified(boolean modified);
    void applyGenes(LivingEntity entity);
    void removeGenes(LivingEntity entity);
    float calculateInstabilityChange(Map<Trait, Gene> genes);
}
