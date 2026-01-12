package net.farkas.wildaside.dna.sequence;

import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.expression.*;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneSequence {
    private final Trait trait;
    private final List<CodingRegion> codingRegions;
    private final List<Activator> activators;
    private final List<Enhancer> enhancers;
    private final List<Silencer> silencers;
    private final List<Regulator> regulators;

    private final Dominance dominance;
    private final float mutationRate;
    private final float stability;
    private final GeneSource source;

    private GeneSequence(Builder builder) {
        this.trait = builder.trait;
        this.codingRegions = new ArrayList<>(builder.codingRegions);
        this.activators = new ArrayList<>(builder.activators);
        this.enhancers = new ArrayList<>(builder.enhancers);
        this.silencers = new ArrayList<>(builder.silencers);
        this.regulators = new ArrayList<>(builder.regulators);
        this.dominance = builder.dominance;
        this.mutationRate = builder.mutationRate;
        this.stability = builder.stability;
        this.source = builder.source;
    }

    public float calculateBaseValue() {
        float total = 0f;
        for (CodingRegion region : codingRegions) {
            total = region.combine(total);
        }
        return total;
    }

    public float express(ExpressionContext context) {
        if (!isActive(context)) {
            return 0f;
        }

        float value = calculateBaseValue();

        value = applyEnhancers(value, context);
        value = applySilencers(value, context);
        value = applyRegulators(value, context);

        return value;
    }

    private boolean isActive(ExpressionContext context) {
        if (activators.isEmpty()) {
            return true;
        }

        for (Activator activator : activators) {
            if (activator.isActive(context)) {
                return true;
            }
        }
        return false;
    }

    private float applyEnhancers(float value, ExpressionContext context) {
        for (Enhancer enhancer : enhancers) {
            if (enhancer.shouldApply(context)) {
                value = enhancer.apply(value);
            }
        }
        return value;
    }

    private float applySilencers(float value, ExpressionContext context) {
        for (Silencer silencer : silencers) {
            if (silencer.shouldApply(context)) {
                value = silencer.apply(value);
            }
        }
        return value;
    }

    private float applyRegulators(float value, ExpressionContext context) {
        for (Regulator regulator : regulators) {
            value = regulator.regulate(value, context);
        }
        return value;
    }

    public Trait getTrait() {
        return trait;
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

    public List<CodingRegion> getCodingRegions() {
        return Collections.unmodifiableList(codingRegions);
    }

    public List<Activator> getActivators() {
        return Collections.unmodifiableList(activators);
    }

    public List<Enhancer> getEnhancers() {
        return Collections.unmodifiableList(enhancers);
    }

    public List<Silencer> getSilencers() {
        return Collections.unmodifiableList(silencers);
    }

    public List<Regulator> getRegulators() {
        return Collections.unmodifiableList(regulators);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Trait trait;
        private List<CodingRegion> codingRegions = new ArrayList<>();
        private List<Activator> activators = new ArrayList<>();
        private List<Enhancer> enhancers = new ArrayList<>();
        private List<Silencer> silencers = new ArrayList<>();
        private List<Regulator> regulators = new ArrayList<>();
        private Dominance dominance = Dominance.DOMINANT;
        private float mutationRate = 0.01f;
        private float stability = 1.0f;
        private GeneSource source = GeneSource.NATURAL;

        public Builder trait(Trait trait) {
            this.trait = trait;
            return this;
        }

        public Builder codingRegion(CodingRegion region) {
            this.codingRegions.add(region);
            return this;
        }

        public Builder activator(Activator activator) {
            this.activators.add(activator);
            return this;
        }

        public Builder enhancer(Enhancer enhancer) {
            this.enhancers.add(enhancer);
            return this;
        }

        public Builder silencer(Silencer silencer) {
            this.silencers.add(silencer);
            return this;
        }

        public Builder regulator(Regulator regulator) {
            this.regulators.add(regulator);
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
            if (trait == null) {
                throw new IllegalStateException("Trait must be set");
            }
            return new GeneSequence(this);
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Trait", trait.getName());

        ListTag codingTag = new ListTag();
        for (CodingRegion region : codingRegions) {
            codingTag.add(region.serializeNBT());
        }
        tag.put("CodingRegions", codingTag);

        ListTag activatorsTag = new ListTag();
        for (Activator activator : activators) {
            activatorsTag.add(activator.serializeNBT());
        }
        tag.put("Activators", activatorsTag);

        ListTag enhancersTag = new ListTag();
        for (Enhancer enhancer : enhancers) {
            enhancersTag.add(enhancer.serializeNBT());
        }
        tag.put("Enhancers", enhancersTag);

        ListTag silencersTag = new ListTag();
        for (Silencer silencer : silencers) {
            silencersTag.add(silencer.serializeNBT());
        }
        tag.put("Silencers", silencersTag);

        ListTag regulatorsTag = new ListTag();
        for (Regulator regulator : regulators) {
            regulatorsTag.add(regulator.serializeNBT());
        }
        tag.put("Regulators", regulatorsTag);

        tag.putString("Dominance", dominance.name());
        tag.putFloat("MutationRate", mutationRate);
        tag.putFloat("Stability", stability);
        tag.putString("Source", source.name());

        return tag;
    }

    public static GeneSequence deserializeNBT(CompoundTag tag, Trait trait) {
        Builder builder = builder().trait(trait);

        ListTag codingTag = tag.getList("CodingRegions", Tag.TAG_COMPOUND);
        for (Tag t : codingTag) {
            builder.codingRegion(CodingRegion.deserializeNBT((CompoundTag) t));
        }

        ListTag activatorsTag = tag.getList("Activators", Tag.TAG_COMPOUND);
        for (Tag t : activatorsTag) {
            builder.activator(Activator.deserializeNBT((CompoundTag) t));
        }

        ListTag enhancersTag = tag.getList("Enhancers", Tag.TAG_COMPOUND);
        for (Tag t : enhancersTag) {
            builder.enhancer(Enhancer.deserializeNBT((CompoundTag) t));
        }

        ListTag silencersTag = tag.getList("Silencers", Tag.TAG_COMPOUND);
        for (Tag t : silencersTag) {
            builder.silencer(Silencer.deserializeNBT((CompoundTag) t));
        }

        ListTag regulatorsTag = tag.getList("Regulators", Tag.TAG_COMPOUND);
        for (Tag t : regulatorsTag) {
            builder.regulator(Regulator.deserializeNBT((CompoundTag) t));
        }

        builder.dominance(Dominance.valueOf(tag.getString("Dominance")));
        builder.mutationRate(tag.getFloat("MutationRate"));
        builder.stability(tag.getFloat("Stability"));
        builder.source(GeneSource.valueOf(tag.getString("Source")));

        return builder.build();
    }
}
