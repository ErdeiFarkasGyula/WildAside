package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
        this.genes = enforceAbilityLimit(genes);
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

    public void setGene(LivingEntity entity, Trait trait, float value, float stabilityCost) {
        Gene existing = genes.get(trait);
        if (existing != null) {
            existing.remove(entity);
        }

        Gene newGene = new Gene(trait, value, stabilityCost);
        genes.put(trait, newGene);
        newGene.apply(entity);
    }

    @Override
    public float calculateInstabilityChange(Map<Trait, Gene> newGenes) {
        float totalCost = 0.0f;

        for (Map.Entry<Trait, Gene> entry : newGenes.entrySet()) {
            Trait trait = entry.getKey();
            Gene newGene = entry.getValue();

            Gene currentGene = genes.get(trait);
            if (currentGene == null) {
                totalCost += newGene.trait.baseInstability();
                continue;
            }

            float oldValue = currentGene.value();
            float newValue = newGene.value();

            if (Math.abs(oldValue - newValue) <= 0.001f)
                continue;

            float base = oldValue == 0.0f ? 1.0f : Math.abs(oldValue);
            float relativeChange = Math.abs(newValue - oldValue) / base;

            relativeChange = Math.min(relativeChange, 2.0f);

            float baseInstability = newGene.trait.baseInstability();
            float instabilityCost = baseInstability * (1.0f + relativeChange * 2.0f);

            if (newValue > oldValue)
                instabilityCost *= 1.25f;

            totalCost += instabilityCost;
        }

        return totalCost;
    }

    private static Map<Trait, Gene> enforceAbilityLimit(Map<Trait, Gene> input) {
        Trait chosenAbility = null;
        float lowestValue = Float.POSITIVE_INFINITY;

        for (Trait trait : new ArrayList<>(input.keySet())) {
            if (trait.traitType() == TraitTypes.ABILITY) {
                Gene gene = input.get(trait);
                if (chosenAbility == null || gene.value() < lowestValue) {
                    chosenAbility = trait;
                    lowestValue = gene.value();
                } else {
                    input.remove(trait);
                }
            }
        }

        return input;
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
            CompoundTag geneTag = new CompoundTag();
            geneTag.putString("trait", gene.trait().name());
            geneTag.putFloat("value", gene.value());
            geneTag.putFloat("stabilityCost", gene.stabilityCost());
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
            String traitName = geneTag.getString("trait");
            Trait trait = Traits.getByName(traitName);
            if (trait == null) continue;
            float value = geneTag.getFloat("value");
            float cost = geneTag.getFloat("stabilityCost");
            genes.put(trait, new Gene(trait, value, cost));
        }
    }
}