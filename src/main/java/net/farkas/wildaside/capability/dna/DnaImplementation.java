package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.merge.PendingDnaIntegration;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.farkas.wildaside.dna.trait.TraitTransition;
import net.farkas.wildaside.dna.trait.TraitType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class DnaImplementation implements IDna {
    private @Nullable EntityType<?> source;
    
    private Genome genome;
    
    private Map<Trait, List<GeneLocus>> invadingLoci = new HashMap<>();
    
    private Map<Trait, TraitTransition> activeTransitions = new HashMap<>();
    private Map<Trait, Float> currentAppliedValues = new HashMap<>();
    private List<PendingDnaIntegration> pendingIntegrations = new ArrayList<>();
    private float stress = 0f;

    public DnaImplementation() {
        this.genome = new Genome(null);
    }

    @Override
    public @Nullable EntityType<?> getSource() {
        return source;
    }

    @Override
    public void setSource(EntityType<?> source) {
        this.source = source;
    }

    @Override
    public Genome getGenome() {
        return genome;
    }

    @Override
    public void setGenome(Genome genome) {
        this.genome = genome;
        if (genome != null && genome.getEntityType() != null) {
            this.source = genome.getEntityType();
        }
    }

    @Override
    public Map<Trait, List<GeneLocus>> getGenomeLociView() {
        return net.farkas.wildaside.dna.DnaUtils.convertGenomeToLoci(genome);
    }

    @Override
    public void setGenomeFromLoci(Map<Trait, List<GeneLocus>> loci) {
        if (loci == null || loci.isEmpty()) {
            genome = new Genome(source);
            return;
        }
        genome = net.farkas.wildaside.dna.DnaUtils.convertLociToGenome(source, loci);
    }

    @Override
    public Map<Trait, List<GeneLocus>> getInvadingLoci() {
        return invadingLoci;
    }

    @Override
    public void setInvadingLoci(Map<Trait, List<GeneLocus>> loci) {
        this.invadingLoci = loci != null ? new HashMap<>(loci) : new HashMap<>();
    }

    @Override
    public void addInvadingLoci(Map<Trait, List<GeneLocus>> newLoci) {
        if (newLoci == null || newLoci.isEmpty()) return;

        for (Map.Entry<Trait, List<GeneLocus>> entry : newLoci.entrySet()) {
            Trait trait = entry.getKey();
            List<GeneLocus> lociToAdd = entry.getValue();
            invadingLoci.computeIfAbsent(trait, k -> new ArrayList<>()).addAll(lociToAdd);
        }
    }

    @Override
    public void clearInvadingLoci() {
        invadingLoci.clear();
    }

    @Override
    public boolean hasInvadingLoci() {
        return !invadingLoci.isEmpty();
    }

    @Override
    public float getStress() {
        return stress;
    }

    @Override
    public void setStress(float stress) {
        this.stress = Math.max(0f, Math.min(100f, stress));
    }

    @Override
    public List<PendingDnaIntegration> getPendingIntegrations() {
        return pendingIntegrations;
    }

    @Override
    public void addPendingIntegration(PendingDnaIntegration integration) {
        pendingIntegrations.add(integration);
    }

    @Override
    public void removePendingIntegration(PendingDnaIntegration integration) {
        pendingIntegrations.remove(integration);
    }

    @Override
    public void clearPendingIntegrations() {
        pendingIntegrations.clear();
    }

    @Override
    public Map<Trait, TraitTransition> getActiveTransitions() {
        return activeTransitions;
    }

    @Override
    public void addTransition(TraitTransition transition) {
        activeTransitions.put(transition.getTrait(), transition);
    }

    @Override
    public void removeTransition(Trait trait) {
        activeTransitions.remove(trait);
    }

    @Override
    public void clearTransitions() {
        activeTransitions.clear();
    }

    @Override
    public boolean hasActiveTransition(Trait trait) {
        return activeTransitions.containsKey(trait) && !activeTransitions.get(trait).isComplete();
    }

    @Override
    public Map<Trait, Float> getCurrentAppliedValues() {
        return currentAppliedValues;
    }

    @Override
    public void setCurrentAppliedValue(Trait trait, float value) {
        currentAppliedValues.put(trait, value);
    }

    @Override
    public float getCurrentAppliedValue(Trait trait) {
        return currentAppliedValues.getOrDefault(trait, 0f);
    }

    @Override
    public void applyGenes(LivingEntity entity) {
        long currentTick = entity.level().getGameTime();
        if (genome == null) return;
        
        ExpressionContext context = new ExpressionContext(entity);

        for (Trait trait : TraitRegistry.getAllTraits()) {
            float targetValue = genome.getExpressedValue(trait, context);

            if (!currentAppliedValues.containsKey(trait)) {
                trait.applyRaw(entity, targetValue);
                currentAppliedValues.put(trait, targetValue);
                continue;
            }

            float current = currentAppliedValues.get(trait);

            if (Math.abs(targetValue - current) < 0.001f) {
                continue;
            }

            if (!hasActiveTransition(trait)) {
                int duration = getTransitionDuration(trait);
                TraitTransition transition = new TraitTransition(trait, current, targetValue, currentTick, duration);
                addTransition(transition);
                WildAside.LOGGER.info("Started transition for [{}]: {} -> {} over {}t",
                        trait.getName(), current, targetValue, duration);
            } else {
                TraitTransition existing = activeTransitions.get(trait);
                if (Math.abs(existing.getTargetValue() - targetValue) > 0.001f) {
                    float currentTransitionValue = existing.getCurrentValue();
                    int duration = getTransitionDuration(trait);
                    TraitTransition newTransition = new TraitTransition(trait, currentTransitionValue, targetValue, currentTick, duration);
                    addTransition(newTransition);
                    WildAside.LOGGER.info("Updated transition for [{}]: {} -> {} over {}t",
                            trait.getName(), currentTransitionValue, targetValue, duration);
                }
            }
        }
    }

    private int getTransitionDuration(Trait trait) {
        TraitType type = trait.getTraitType();
        return switch (type) {
            case CORE -> TraitTransition.DEFAULT_DURATION;
            case RESISTANCE -> TraitTransition.FAST_DURATION;
            case ABILITY -> TraitTransition.SLOW_DURATION;
            case APPEARANCE -> TraitTransition.FAST_DURATION;
        };
    }

    @Override
    public void tickTransitions(LivingEntity entity, long currentTick) {
        if (activeTransitions.isEmpty()) return;

        List<Trait> completed = new ArrayList<>();

        for (Map.Entry<Trait, TraitTransition> entry : activeTransitions.entrySet()) {
            Trait trait = entry.getKey();
            TraitTransition transition = entry.getValue();

            float newValue = transition.tick(currentTick);
            trait.applyRaw(entity, newValue);
            currentAppliedValues.put(trait, newValue);

            if (transition.isComplete()) {
                completed.add(trait);
                WildAside.LOGGER.info("Transition complete for [{}]: final value = {}",
                        trait.getName(), newValue);
            }
        }

        for (Trait trait : completed) {
            activeTransitions.remove(trait);
        }
    }

    @Override
    public void removeGenes(LivingEntity entity) {
        for (Trait t : TraitRegistry.getAllTraits()) {
            t.remove(entity);
        }
    }

    @Override
    public void recomputeAndApply(LivingEntity entity) {
        applyGenes(entity);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("stress", stress);
        tag.putString("source", source == null ? "" : Objects.toString(ForgeRegistries.ENTITY_TYPES.getKey(source), ""));

        if (genome != null) {
            tag.put("genome", genome.serializeNBT());
        }

        ListTag invadingTag = new ListTag();
        for (Map.Entry<Trait, List<GeneLocus>> e : invadingLoci.entrySet()) {
            for (GeneLocus gl : e.getValue()) {
                CompoundTag ct = gl.serializeNBT();
                ct.putString("Trait", e.getKey().getName());
                invadingTag.add(ct);
            }
        }
        tag.put("invadingLoci", invadingTag);

        ListTag pendingTag = new ListTag();
        for (PendingDnaIntegration pending : pendingIntegrations) {
            pendingTag.add(pending.serializeNBT());
        }
        tag.put("pendingIntegrations", pendingTag);

        ListTag transitionsTag = new ListTag();
        for (TraitTransition transition : activeTransitions.values()) {
            transitionsTag.add(transition.serializeNBT());
        }
        tag.put("activeTransitions", transitionsTag);

        CompoundTag appliedValuesTag = new CompoundTag();
        for (Map.Entry<Trait, Float> entry : currentAppliedValues.entrySet()) {
            appliedValuesTag.putFloat(entry.getKey().getName(), entry.getValue());
        }
        tag.put("currentAppliedValues", appliedValuesTag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        invadingLoci.clear();
        pendingIntegrations.clear();
        activeTransitions.clear();
        currentAppliedValues.clear();

        stress = tag.getFloat("stress");
        String s = tag.getString("source");
        if (!s.isEmpty()) source = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(s));

        if (tag.contains("genome")) {
            genome = Genome.deserializeNBT(tag.getCompound("genome"));
            if (genome.getEntityType() != null) {
                source = genome.getEntityType();
            }
        } else if (tag.contains("loci")) {
            Map<Trait, List<GeneLocus>> loadedLoci = new HashMap<>();
            ListTag lociTag = tag.getList("loci", Tag.TAG_COMPOUND);
            for (Tag t : lociTag) {
                CompoundTag ct = (CompoundTag) t;
                Trait trait = TraitRegistry.getByName(ct.getString("Trait"));
                if (trait == null) continue;
                GeneLocus gl = GeneLocus.deserializeNBT(ct);
                loadedLoci.computeIfAbsent(trait, k -> new ArrayList<>()).add(gl);
            }
            
            if (!loadedLoci.isEmpty()) {
                genome = net.farkas.wildaside.dna.DnaUtils.convertLociToGenome(source, loadedLoci);
            } else {
                genome = new Genome(source);
            }
        } else {
            genome = new Genome(source);
        }

        if (tag.contains("invadingLoci")) {
            ListTag invadingTag = tag.getList("invadingLoci", Tag.TAG_COMPOUND);
            for (Tag t : invadingTag) {
                CompoundTag ct = (CompoundTag) t;
                Trait trait = TraitRegistry.getByName(ct.getString("Trait"));
                if (trait == null) continue;
                GeneLocus gl = GeneLocus.deserializeNBT(ct);
                invadingLoci.computeIfAbsent(trait, k -> new ArrayList<>()).add(gl);
            }
        }

        if (tag.contains("pendingIntegrations")) {
            ListTag pendingTag = tag.getList("pendingIntegrations", Tag.TAG_COMPOUND);
            for (Tag t : pendingTag) {
                PendingDnaIntegration pending = PendingDnaIntegration.deserializeNBT((CompoundTag) t);
                if (pending != null && pending.getTrait() != null) {
                    pendingIntegrations.add(pending);
                }
            }
        }

        if (tag.contains("activeTransitions")) {
            ListTag transitionsTag = tag.getList("activeTransitions", Tag.TAG_COMPOUND);
            for (Tag t : transitionsTag) {
                TraitTransition transition = TraitTransition.deserializeNBT((CompoundTag) t);
                if (transition != null) {
                    activeTransitions.put(transition.getTrait(), transition);
                }
            }
        }

        if (tag.contains("currentAppliedValues")) {
            CompoundTag appliedValuesTag = tag.getCompound("currentAppliedValues");
            for (String key : appliedValuesTag.getAllKeys()) {
                Trait trait = TraitRegistry.getByName(key);
                if (trait != null) {
                    currentAppliedValues.put(trait, appliedValuesTag.getFloat(key));
                }
            }
        }
    }
}