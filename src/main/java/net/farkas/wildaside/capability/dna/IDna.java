package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.dna.Gene;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.List;

public interface IDna extends INBTSerializable<CompoundTag> {
    @Nullable EntityType<?> source();
    List<Gene> genes();
    float stability();
    void setSource(EntityType<?> source);
    void setGenes(List<Gene> genes);
    void setStability(float stability);
    void apply(LivingEntity entity);
    void remove(LivingEntity entity);
    float calculateInstabilityChange(List<Gene> genes);
}
