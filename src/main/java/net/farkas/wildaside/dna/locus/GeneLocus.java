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

    public GeneLocus(String id, Allele a, Allele b, Set<LocusFlag> flags, float stability) {
        this.id = id;
        this.alleleA = a;
        this.alleleB = b;
        if (flags == null || flags.isEmpty()) {
            this.flags = EnumSet.noneOf(LocusFlag.class);
        } else {
            this.flags = EnumSet.copyOf(flags);
        }
        this.stability = stability;
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

    public AlleleValue getExpressedValue() {
        return TraitExpression.evaluate(alleleA, alleleB);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.put("AlleleA", alleleA.serializeNBT());
        tag.put("AlleleB", alleleB.serializeNBT());
        tag.putFloat("Stability", stability);
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
        int bits = tag.getInt("Flags");
        Set<LocusFlag> flags = EnumSet.noneOf(LocusFlag.class);
        for (LocusFlag f : LocusFlag.values()) if ((bits & (1 << f.ordinal())) != 0) flags.add(f);
        return new GeneLocus(id, a, b, flags, stab);
    }
}