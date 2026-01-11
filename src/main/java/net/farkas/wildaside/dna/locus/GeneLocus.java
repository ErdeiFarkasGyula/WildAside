package net.farkas.wildaside.dna.locus;

import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.trait.TraitExpression;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumSet;
import java.util.Set;

public class GeneLocus {
    private final String id;
    private final Allele alleleA;
    private final Allele alleleB;
    private final Set<LocusFlag> flags;
    private final float stability;
    private final LocusSource source;
    private final long integrationTick;
    private float degradation;

    public GeneLocus(String id, Allele a, Allele b, Set<LocusFlag> flags, float stability) {
        this(id, a, b, flags, stability, LocusSource.NATIVE, 0L, 0f);
    }

    public GeneLocus(String id, Allele a, Allele b, Set<LocusFlag> flags, float stability, LocusSource source, long integrationTick, float degradation) {
        this.id = id;
        this.alleleA = a;
        this.alleleB = b;
        this.flags = (flags == null || flags.isEmpty())
                ? EnumSet.noneOf(LocusFlag.class)
                : EnumSet.copyOf(flags);
        this.stability = stability;
        this.source = source;
        this.integrationTick = integrationTick;
        this.degradation = Math.max(0f, Math.min(1f, degradation));
    }

    public String getId() {
        return id;
    }

    public Allele getAlleleA() {
        return alleleA;
    }

    public Allele getAlleleB() {
        return alleleB;
    }

    public Set<LocusFlag> getFlags() {
        return flags;
    }

    public float getStability() {
        return stability;
    }

    public LocusSource getSource() {
        return source;
    }

    public long getIntegrationTick() {
        return integrationTick;
    }

    public float getDegradation() {
        return degradation;
    }

    public void addDegradation(float amount) {
        this.degradation = Math.min(1f, this.degradation + amount);
    }

    public boolean isFullyDegraded() {
        return degradation >= 1f;
    }

    public AlleleValue getExpressedValue() {
        return TraitExpression.evaluate(alleleA, alleleB);
    }

    public float getExpressionWeight() {
        float baseWeight = switch (source) {
            case NATIVE -> 1.0f;
            case INTEGRATED -> 0.85f;
            case TRANSIENT -> 0.5f;
            case REJECTED -> 0.1f;
        };
        return baseWeight * (1f - degradation * 0.8f);
    }

    public GeneLocus withSource(LocusSource newSource, long tick) {
        return new GeneLocus(id, alleleA, alleleB, flags, stability, newSource, tick, degradation);
    }

    public GeneLocus copyWithNewAlleles(Allele newA, Allele newB) {
        return new GeneLocus(id, newA, newB, flags, stability, source, integrationTick, degradation);
    }

    public GeneLocus withAlleleA(Allele newAlleleA) {
        return new GeneLocus(id, newAlleleA, alleleB, flags, stability, source, integrationTick, degradation);
    }

    public GeneLocus withAlleleB(Allele newAlleleB) {
        return new GeneLocus(id, alleleA, newAlleleB, flags, stability, source, integrationTick, degradation);
    }

    public GeneLocus withStability(float newStability) {
        return new GeneLocus(id, alleleA, alleleB, flags, Math.max(0f, Math.min(1f, newStability)), source, integrationTick, degradation);
    }

    public GeneLocus withSource(LocusSource newSource) {
        return new GeneLocus(id, alleleA, alleleB, flags, stability, newSource, integrationTick, degradation);
    }

    public GeneLocus withDegradation(float newDegradation) {
        return new GeneLocus(id, alleleA, alleleB, flags, stability, source, integrationTick, Math.max(0f, Math.min(1f, newDegradation)));
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.put("AlleleA", alleleA.serializeNBT());
        tag.put("AlleleB", alleleB.serializeNBT());
        tag.putFloat("Stability", stability);
        tag.putString("Source", source.name());
        tag.putLong("IntegrationTick", integrationTick);
        tag.putFloat("Degradation", degradation);

        int bits = 0;
        for (LocusFlag f : flags) bits |= (1 << f.ordinal());
        tag.putInt("Flags", bits);
        return tag;
    }

    public static GeneLocus deserializeNBT(CompoundTag tag) {
        String id = tag.getString("Id");
        Allele a = Allele.deserializeNBT(tag.getCompound("AlleleA"));
        Allele b = Allele.deserializeNBT(tag.getCompound("AlleleB"));
        float stab = tag.getFloat("Stability");

        LocusSource source = LocusSource.NATIVE;
        if (tag.contains("Source")) {
            try {
                source = LocusSource.valueOf(tag.getString("Source"));
            }
            catch (IllegalArgumentException ignored) {
            }
        }

        long integrationTick = tag.getLong("IntegrationTick");
        float degradation = tag.getFloat("Degradation");

        int bits = tag.getInt("Flags");
        Set<LocusFlag> flags = EnumSet.noneOf(LocusFlag.class);
        for (LocusFlag f : LocusFlag.values()) {
            if ((bits & (1 << f.ordinal())) != 0) flags.add(f);
        }

        return new GeneLocus(id, a, b, flags, stab, source, integrationTick, degradation);
    }
}