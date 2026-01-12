package net.farkas.wildaside.dna.chromosome;

import net.farkas.wildaside.dna.expression.ExpressionContext;
import net.farkas.wildaside.dna.expression.GeneExpressionPair;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class Genome {
    private final EntityType<?> entityType;
    private final ChromosomeSet maternalSet;
    private final ChromosomeSet paternalSet;

    public Genome(EntityType<?> entityType) {
        this.entityType = entityType;
        this.maternalSet = new ChromosomeSet();
        this.paternalSet = new ChromosomeSet();
    }

    public Genome(EntityType<?> entityType, ChromosomeSet maternalSet, ChromosomeSet paternalSet) {
        this.entityType = entityType;
        this.maternalSet = maternalSet;
        this.paternalSet = paternalSet;
    }

    public ChromosomeSet getMaternal() {
        return maternalSet;
    }

    public ChromosomeSet getPaternal() {
        return paternalSet;
    }

    public GeneExpressionPair getGeneExpression(Trait trait) {
        ChromosomeType chromoType = trait.getTraitType().getChromosomeType();

        GeneSequence maternal = maternalSet
                .getChromosome(chromoType)
                .getGeneSequence(trait);

        GeneSequence paternal = paternalSet
                .getChromosome(chromoType)
                .getGeneSequence(trait);

        return new GeneExpressionPair(trait, maternal, paternal);
    }

    public float getExpressedValue(Trait trait, ExpressionContext context) {
        return getGeneExpression(trait).express(context);
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        if (entityType != null) {
            tag.putString("EntityType", EntityType.getKey(entityType).toString());
        }

        tag.put("Maternal", maternalSet.serializeNBT());
        tag.put("Paternal", paternalSet.serializeNBT());

        return tag;
    }

    public static Genome deserializeNBT(CompoundTag tag) {
        EntityType<?> entityType = null;
        if (tag.contains("EntityType")) {
            String entityTypeStr = tag.getString("EntityType");
            ResourceLocation resourceLocation = ResourceLocation.parse(entityTypeStr);
            if (ForgeRegistries.ENTITY_TYPES.containsKey(resourceLocation)) {
                entityType = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
            }
        }

        ChromosomeSet maternal = ChromosomeSet.deserializeNBT(tag.getCompound("Maternal"));
        ChromosomeSet paternal = ChromosomeSet.deserializeNBT(tag.getCompound("Paternal"));

        return new Genome(entityType, maternal, paternal);
    }
}
