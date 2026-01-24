package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.merge.PendingDnaIntegration;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitTransition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDna extends INBTSerializable<CompoundTag> {
    @Nullable EntityType<?> getSource();
    void setSource(EntityType<?> source);

    Genome getGenome();
    void setGenome(Genome genome);

    float getStress();
    void setStress(float stress);

    boolean isActive();
    void setActive(boolean active);

    List<PendingDnaIntegration> getPendingIntegrations();
    void addPendingIntegration(PendingDnaIntegration integration);
    void removePendingIntegration(PendingDnaIntegration integration);
    void clearPendingIntegrations();

    Map<Trait, TraitTransition> getActiveTransitions();
    void addTransition(TraitTransition transition);
    void removeTransition(Trait trait);
    void clearTransitions();
    boolean hasActiveTransition(Trait trait);

    Map<Trait, Float> getCurrentAppliedValues();
    void setCurrentAppliedValue(Trait trait, float value);
    float getCurrentAppliedValue(Trait trait);

    Set<Trait> getDynamicTraits();
    void updateDynamicTraits(LivingEntity entity);

    void applyGenes(LivingEntity entity);
    void removeGenes(LivingEntity entity);
    void recomputeAndApply(LivingEntity entity);
    void tickTransitions(LivingEntity entity, long currentTick);
}