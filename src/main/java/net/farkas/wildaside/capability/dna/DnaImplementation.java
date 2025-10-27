package net.farkas.wildaside.capability.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.Traits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DnaImplementation implements IDna {
    private @Nullable EntityType<?> source;
    private float stability = 100;
    private List<Gene> genes = new ArrayList<>();

    @Override
    public void apply(LivingEntity entity) {
        for (Gene gene : genes) {
            gene.apply(entity);
        }
    }

    @Override
    public void remove(LivingEntity entity) {
        for (Gene gene : genes) {
            gene.remove(entity);
        }
    }

    @Override
    public void setGenes(List<Gene> genes) {
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
        if (source == null) { return null; }
        return ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(source.toString()));
    }

    @Override
    public List<Gene> genes() {
        return genes;
    }

    public float calculateInstabilityChange(List<Gene> newGenes) {
        List<Gene> currentGenes = this.genes;
        float totalCost = 0.0f;

        for (Gene newGene : newGenes) {
            Gene currentGene = currentGenes.stream()
                    .filter(g -> g.trait.equals(newGene.trait))
                    .findFirst()
                    .orElse(null);

            boolean isDifferent = false;

            if (currentGene == null) {
                isDifferent = true;
            } else {
                if (Math.abs(currentGene.value() - newGene.value()) > 0.0001f) {
                    isDifferent = true;
                }
            }

            if (isDifferent) {
                totalCost += newGene.stabilityCost();
            }
        }

        return totalCost;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        String sourceString = "";
        if (source != null) {
            sourceString = source.toString();
        }
        tag.putString("source", sourceString);
        tag.putFloat("stability", stability);

        ListTag list = new ListTag();
        if (genes != null) {
            for (Gene gene : genes) {
                CompoundTag geneTag = new CompoundTag();
                geneTag.putString("trait", gene.trait().name());
                geneTag.putFloat("value", gene.value());
                geneTag.putFloat("stabilityCost", gene.stabilityCost());
                list.add(geneTag);
            }
        }
        tag.put("genes", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        String sourceString = tag.getString("source");
        source = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(sourceString));
        stability = tag.getFloat("stability");

        if (genes != null) {
            genes.clear();
        }
        ListTag list = tag.getList("genes", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag geneTag = (CompoundTag) t;
            String trait = geneTag.getString("trait");
            Trait traitValue = Traits.getByName(trait);
            if (traitValue == null) {
                traitValue = Traits.ARMOR_TOUGHNESS;
                WildAside.LOGGER.warn("Missing Trait: {}", trait);
            }
            float value = geneTag.getFloat("value");
            float cost = geneTag.getFloat("stabilityCost");
            Gene gene = new Gene(traitValue, value, cost);
            genes.add(gene);
        }
    }
}
