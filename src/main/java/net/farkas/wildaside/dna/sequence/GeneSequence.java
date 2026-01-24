package net.farkas.wildaside.dna.sequence;

import net.farkas.wildaside.dna.dominance.Dominance;
import net.farkas.wildaside.dna.expression.*;
import net.farkas.wildaside.dna.sequence.components.*;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneSequence {
    private final List<GeneComponent> components;
    private final Dominance dominance;
    private final float mutationRate;
    private final float stability;
    private final GeneSource source;

    private GeneSequence(Builder builder) {
        this.components = new ArrayList<>(builder.components);
        this.dominance = builder.dominance;
        this.mutationRate = builder.mutationRate;
        this.stability = builder.stability;
        this.source = builder.source;
    }

    public float calculateBaseValue() {
        float total = 0f;
        for (GeneComponent component : components) {
            if (component instanceof CodingRegion region) {
                total = region.combine(total);
            }
        }
        return total;
    }

    public float express(ExpressionContext context) {
        if (components.isEmpty() || !(components.get(0) instanceof TraitDefiner)) {
            return 0f;
        }

        float value = 0f;
        boolean active = false;

        for (GeneComponent component : components) {
            if (component instanceof TraitDefiner) {
                continue;
            }
            else if (component instanceof CodingRegion codingRegion) {
                if (active) {
                    value = codingRegion.combine(value);
                }
            }
            else if (component instanceof Activator activator) {
                active = activator.isActive(context, value);
            }
            else if (component instanceof Enhancer enhancer) {
                if (active && enhancer.shouldApply(context, value)) {
                    value = enhancer.apply(value);
                }
            }
            else if (component instanceof Silencer silencer) {
                if (active && silencer.shouldApply(context, value)) {
                    value = silencer.apply(value);
                }
            }
            else if (component instanceof Regulator regulator) {
                if (active) {
                    value = regulator.regulate(value, context);
                }
            }
        }

        if (!active) {
            return 0f;
        }

        return value;
    }

    public Trait getTrait() {
        if (!components.isEmpty() && components.get(0) instanceof TraitDefiner definer) {
            return definer.getTrait();
        }
        return null;
    }

    public Dominance getDominance() {
        return dominance;
    }

    public float getMutationRate() {
        return mutationRate;
    }

    public float getStability() {
        return stability;
    }

    public GeneSource getSource() {
        return source;
    }

    public List<GeneComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public Dominance calculateDominance() {
        float transcriptionPotential = 0f;
        boolean hasActivator = false;

        for (GeneComponent component : components) {
            if (component instanceof Activator activator) {
                hasActivator = true;
                float conditionScore = getConditionScore(activator.getCondition());
                transcriptionPotential += conditionScore;
            }
        }

        if (!hasActivator) {
            return Dominance.RECESSIVE;
        }

        for (GeneComponent component : components) {
            if (component instanceof Enhancer enhancer) {
                transcriptionPotential *= (0.5f + 0.5f * enhancer.getMultiplier());
            }
            else if (component instanceof Silencer silencer) {
                transcriptionPotential *= silencer.getMultiplier();
            }
        }

        boolean producesProtein = false;
        for (GeneComponent component : components) {
            if (component instanceof CodingRegion region) {
                if (Math.abs(region.getValue()) > 0.0001f) {
                    producesProtein = true;
                    break;
                }
            }
        }

        if (!producesProtein) {
            return Dominance.RECESSIVE;
        }

        float finalExpression = transcriptionPotential * stability;

        System.out.println("Dominance Calc for " + (getTrait() != null ? getTrait().getName() : "null") +
                " | Transcription: " + String.format("%.2f", transcriptionPotential) +
                " | Protein: " + producesProtein +
                " | Stability: " + stability +
                " | Final Score: " + String.format("%.2f", finalExpression));

        if (finalExpression >= 1.5f) return Dominance.DOMINANT;
        if (finalExpression >= 0.8f) return Dominance.CO_DOMINANT;
        if (finalExpression >= 0.4f) return Dominance.INCOMPLETE;
        return Dominance.RECESSIVE;
    }

    private float getConditionScore(ActivationCondition condition) {
        return switch (condition) {
            case ALWAYS -> 1.0f;
            case HEALTH_ABOVE, HEALTH_BELOW, SPRINTING, IN_COMBAT, IS_DAY, IS_NIGHT, UNDER_SKY, UNDERGROUND -> 0.8f;
            case ON_FIRE, IN_WATER, IN_NETHER, IN_END -> 0.5f;
            case GENE_VALUE_ABOVE, GENE_VALUE_BELOW -> 0.6f;
            default -> 0.7f;
        };
    }

    public float calculateMutationRate(Trait trait) {
        float baseMutationRate = trait.getInstabilityModifier();
        float volatility = 1.0f;

        volatility += components.size() * 0.05f;

        for (GeneComponent component : components) {
            if (component instanceof CodingRegion region) {
                float absValue = Math.abs(region.getValue());
                if (absValue > 10.0f || absValue < 0.01f) {
                    volatility += 0.15f;
                }
            }
        }

        volatility *= (2.0f - stability);

        volatility *= switch (source) {
            case NATURAL -> 1.0f;
            case MUTATED -> 1.5f;
            case ENGINEERED -> 0.8f;
            case INTEGRATED -> 1.2f;
            default -> 1.0f;
        };

        return Math.min(1.0f, baseMutationRate * volatility);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<GeneComponent> components = new ArrayList<>();
        private Dominance dominance = null;
        private float mutationRate = 0.01f;
        private float stability = 1.0f;
        private GeneSource source = GeneSource.NATURAL;

        public Builder trait(Trait trait) {
            if (components.isEmpty() || !(components.get(0) instanceof TraitDefiner)) {
                components.add(0, new TraitDefiner(trait));
            }
            else {
                components.set(0, new TraitDefiner(trait));
            }
            return this;
        }

        public Builder addComponent(GeneComponent component) {
            this.components.add(component);
            return this;
        }

        public Builder addComponents(List<GeneComponent> components) {
            this.components.addAll(components);
            return this;
        }

        public Builder codingRegion(CodingRegion region) {
            return addComponent(region);
        }

        public Builder codingRegions(List<CodingRegion> regions) {
            this.components.addAll(regions);
            return this;
        }

        public Builder activator(Activator activator) {
            return addComponent(activator);
        }

        public Builder activators(List<Activator> activators) {
            this.components.addAll(activators);
            return this;
        }

        public Builder enhancer(Enhancer enhancer) {
            return addComponent(enhancer);
        }

        public Builder enhancers(List<Enhancer> enhancers) {
            this.components.addAll(enhancers);
            return this;
        }

        public Builder silencer(Silencer silencer) {
            return addComponent(silencer);
        }

        public Builder silencers(List<Silencer> silencers) {
            this.components.addAll(silencers);
            return this;
        }

        public Builder regulator(Regulator regulator) {
            return addComponent(regulator);
        }

        public Builder regulators(List<Regulator> regulators) {
            this.components.addAll(regulators);
            return this;
        }

        public Builder dominance(Dominance dominance) {
            this.dominance = dominance;
            return this;
        }

        public Builder mutationRate(float mutationRate) {
            this.mutationRate = mutationRate;
            return this;
        }

        public Builder stability(float stability) {
            this.stability = stability;
            return this;
        }

        public Builder source(GeneSource source) {
            this.source = source;
            return this;
        }

        public GeneSequence build() {
            if (components.isEmpty() || !(components.get(0) instanceof TraitDefiner)) {
                throw new IllegalStateException("GeneSequence must start with a TraitDefiner");
            }

            if (source == null) {
                source = GeneSource.NATURAL;
            }

            if (this.dominance == null) {
                GeneSequence tempSeq = new GeneSequence(this);
                this.dominance = tempSeq.calculateDominance();
            }

            return new GeneSequence(this);
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag componentsTag = new ListTag();
        for (GeneComponent component : components) {
            CompoundTag compTag = component.serializeNBT();
            compTag.putString("ComponentType", component.getType().name());
            componentsTag.add(compTag);
        }
        tag.put("Components", componentsTag);

        tag.putString("Dominance", dominance.name());
        tag.putFloat("MutationRate", mutationRate);
        tag.putFloat("Stability", stability);
        tag.putString("Source", source.name());

        return tag;
    }

    public static GeneSequence deserializeNBT(CompoundTag tag, Trait trait) {
        Builder builder = builder();

        ListTag componentsTag = tag.getList("Components", Tag.TAG_COMPOUND);
        for (Tag t : componentsTag) {
            CompoundTag compTag = (CompoundTag) t;
            GeneComponent.ComponentType type = GeneComponent.ComponentType.valueOf(compTag.getString("ComponentType"));
            GeneComponent component = GeneComponent.deserializeNBT(type, compTag);

            builder.addComponent(component);
        }

        if (tag.contains("Dominance")) {
            builder.dominance(Dominance.valueOf(tag.getString("Dominance")));
        }
        builder.mutationRate(tag.getFloat("MutationRate"));
        builder.stability(tag.getFloat("Stability"));
        builder.source(GeneSource.valueOf(tag.getString("Source")));

        return builder.build();
    }

    public List<CodingRegion> getCodingRegions() {
        List<CodingRegion> regions = new ArrayList<>();
        for (GeneComponent component : components) {
            if (component instanceof CodingRegion region) {
                regions.add(region);
            }
        }
        return regions;
    }

    public List<Activator> getActivators() {
        List<Activator> activators = new ArrayList<>();
        for (GeneComponent component : components) {
            if (component instanceof Activator activator) {
                activators.add(activator);
            }
        }
        return activators;
    }

    public List<Enhancer> getEnhancers() {
        List<Enhancer> enhancers = new ArrayList<>();
        for (GeneComponent component : components) {
            if (component instanceof Enhancer enhancer) {
                enhancers.add(enhancer);
            }
        }
        return enhancers;
    }

    public List<Silencer> getSilencers() {
        List<Silencer> silencers = new ArrayList<>();
        for (GeneComponent component : components) {
            if (component instanceof Silencer silencer) {
                silencers.add(silencer);
            }
        }
        return silencers;
    }

    public List<Regulator> getRegulators() {
        List<Regulator> regulators = new ArrayList<>();
        for (GeneComponent component : components) {
            if (component instanceof Regulator regulator) {
                regulators.add(regulator);
            }
        }
        return regulators;
    }
}
