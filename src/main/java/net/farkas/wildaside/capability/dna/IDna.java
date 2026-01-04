package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface IDna extends INBTSerializable<CompoundTag> {
    @Nullable EntityType<?> getSource();
    void setSource(EntityType<?> source);

    Map<Trait, List<GeneLocus>> getLoci();
    void setLoci(Map<Trait, List<GeneLocus>> loci);

    float getStress();
    void setStress(float stress);

    void applyGenes(LivingEntity entity);
    void removeGenes(LivingEntity entity);
    void recomputeAndApply(LivingEntity entity);
}