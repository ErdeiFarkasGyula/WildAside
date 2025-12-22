package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusExpression;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
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
    private Map<Trait, List<GeneLocus>> loci = new HashMap<>();
    private Map<Trait, AlleleValue> expressedCache = new HashMap<>();
    private float stress = 0f;

    @Override public @Nullable EntityType<?> getSource() { return source; }
    @Override public void setSource(EntityType<?> source) { this.source = source; }

    @Override public Map<Trait, List<GeneLocus>> getLoci() { return loci; }
    @Override public void setLoci(Map<Trait, List<GeneLocus>> loci) { this.loci = loci; recomputeCache(); }

    @Override public float getStress() { return stress; }
    @Override public void setStress(float stress) { this.stress = Math.max(0f, Math.min(100f, stress)); }

    @Override
    public void applyGenes(LivingEntity entity) {
        for (Map.Entry<Trait, AlleleValue> e : expressedCache.entrySet()) {
            e.getKey().apply(entity, e.getValue());
        }
    }

    @Override
    public void removeGenes(LivingEntity entity) {
        for (Trait t : loci.keySet()) t.remove(entity);
    }

    @Override
    public void recomputeAndApply(LivingEntity entity) {
        recomputeCache();
        removeGenes(entity);
        applyGenes(entity);
    }

    private void recomputeCache() {
        expressedCache.clear();
        for (Map.Entry<Trait, List<GeneLocus>> e : loci.entrySet()) {
            expressedCache.put(e.getKey(), LocusExpression.express(e.getKey(), e.getValue()));
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("stress", stress);
        tag.putString("source", source == null ? "" : Objects.toString(ForgeRegistries.ENTITY_TYPES.getKey(source), ""));
        ListTag listTag = new ListTag();
        for (Map.Entry<Trait, List<GeneLocus>> e : loci.entrySet()) {
            for (GeneLocus gl : e.getValue()) {
                CompoundTag ct = gl.serializeNBT();
                ct.putString("Trait", e.getKey().getName());
                listTag.add(ct);
            }
        }
        tag.put("loci", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        loci.clear();
        stress = tag.getFloat("stress");
        String s = tag.getString("source");
        if (!s.isEmpty()) source = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(s));

        ListTag listtag = tag.getList("loci", Tag.TAG_COMPOUND);
        for (Tag t : listtag) {
            CompoundTag ct = (CompoundTag) t;
            Trait trait = TraitRegistry.getByName(ct.getString("Trait"));
            GeneLocus gl = GeneLocus.deserializeNBT(ct);
            loci.computeIfAbsent(trait, k -> new ArrayList<>()).add(gl);
        }
        recomputeCache();
    }
}