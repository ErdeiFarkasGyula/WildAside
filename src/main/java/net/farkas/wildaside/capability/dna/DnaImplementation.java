package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.Traits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class DnaImplementation implements IDna {
    private @Nullable EntityType<?> source;
    private float stability = 100;
    private Map<Trait, Gene> genes = new HashMap<>();

    @Override
    public void applyGenes(LivingEntity entity) {
        for (Gene gene : genes.values()) {
            gene.apply(entity);
        }
    }

    @Override
    public void removeGenes(LivingEntity entity) {
        for (Gene gene : genes.values()) {
            gene.remove(entity);
        }
    }

    @Override
    public void setGenes(Map<Trait, Gene> genes) {
        this.genes = genes;
    }

    @Override
    public void setStability(float stability) {
        this.stability = stability;
    }

    @Override
    public float stability() {
        return stability;
    }

    @Override
    public void setSource(@Nullable EntityType<?> source) {
        this.source = source;
    }

    @Nullable
    @Override
    public EntityType<?> source() {
        return source;
    }

    @Override
    public Map<Trait, Gene> genes() {
        return genes;
    }

    @Override
    public float calculateInstabilityChange(Map<Trait, Gene> newGenes) {
//        float totalCost = 0.0f;
//
//        for (Map.Entry<Trait, Gene> entry : newGenes.entrySet()) {
//            Trait trait = entry.getKey();
//            Gene newGene = entry.getValue();
//
//            Gene currentGene = genes.get(trait);
//            if (currentGene == null) {
//                totalCost += newGene.getTrait().baseInstability();
//                continue;
//            }
//
//            float oldValue = currentGene.getExpressedValue();
//            float newValue = newGene.getExpressedValue();
//
//            if (Math.abs(oldValue - newValue) <= 0.001f)
//                continue;
//
//            float base = oldValue == 0.0f ? 1.0f : Math.abs(oldValue);
//            float relativeChange = Math.abs(newValue - oldValue) / base;
//
//            relativeChange = Math.min(relativeChange, 2.0f);
//
//            float baseInstability = newGene.getTrait().baseInstability();
//            float instabilityCost = baseInstability * (1.0f + relativeChange * 2.0f);
//
//            if (newValue > oldValue)
//                instabilityCost *= 1.25f;
//
//            totalCost += instabilityCost;
//        }
//
//        return totalCost;
        return 0.0f;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (source != null) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(source);
            tag.putString("source", id.toString());
        } else {
            tag.putString("source", "");
        }
        tag.putFloat("stability", stability);

        ListTag list = new ListTag();
        for (Gene gene : genes.values()) {
            CompoundTag geneTag = gene.serializeNBT();
            list.add(geneTag);
        }
        tag.put("genes", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        String sourceString = tag.getString("source");
        if (!sourceString.isEmpty()) {
            source = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(sourceString));
        }

        stability = tag.getFloat("stability");
        genes.clear();
        ListTag list = tag.getList("genes", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag geneTag = (CompoundTag) t;
            Gene gene = Gene.deserializeNBT(geneTag);
            genes.put(gene.getTrait(), gene);
        }
    }
}