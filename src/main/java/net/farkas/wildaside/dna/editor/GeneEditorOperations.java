package net.farkas.wildaside.dna. editor;

import net.farkas.wildaside.WildAside;
import net.farkas. wildaside.capability. dna.DnaImplementation;
import net.farkas.wildaside.dna. Gene;
import net.farkas.wildaside.dna. allele.Allele;
import net.farkas.wildaside.dna.allele.dominance. Dominance;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas. wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus. LocusSource;
import net.farkas. wildaside.dna.trait.Trait;
import net. minecraft.resources.ResourceLocation;
import net. minecraft.world.entity.player. Player;

import java.util. ArrayList;
import java.util. List;
import java.util.Map;
import java.util.Set;

public class GeneEditorOperations {
    public static boolean canPerformOperation(Player player, GeneEditorOperation operation) {
        ResourceLocation requiredSkill = operation.getRequiredSkillId();
        if (requiredSkill == null) return true;
        return BioengineeringSkillUtils.hasSkill(player, requiredSkill);
    }

    public static GeneEditorResult swapTrait(DnaImplementation dnaA, int geneIndexA, DnaImplementation dnaB, int geneIndexB, List<Gene> genesA, List<Gene> genesB) {
        if (geneIndexA < 0 || geneIndexA >= genesA.size()) {
            return GeneEditorResult.failure("Invalid gene index A");
        }
        if (geneIndexB < 0 || geneIndexB >= genesB.size()) {
            return GeneEditorResult.failure("Invalid gene index B");
        }

        Gene geneA = genesA.get(geneIndexA);
        Gene geneB = genesB. get(geneIndexB);

        if (!geneA.getTrait().equals(geneB.getTrait())) {
            return GeneEditorResult.failure("Traits must match for swap");
        }

        Trait trait = geneA.getTrait();

        List<GeneLocus> lociA = dnaA.getLoci().get(trait);
        List<GeneLocus> lociB = dnaB.getLoci().get(trait);

        if (lociA == null || lociB == null) {
            return GeneEditorResult.failure("Missing loci data");
        }

        dnaA.getLoci().put(trait, new ArrayList<>(lociB));
        dnaB.getLoci().put(trait, new ArrayList<>(lociA));

        WildAside. LOGGER.info("Swapped trait [{}] between DNAs", trait.getName());

        return GeneEditorResult.success("Trait swapped successfully", 0f);
    }

    public static GeneEditorResult swapAllele(DnaImplementation dnaA, Gene geneA, boolean alleleAFromA, DnaImplementation dnaB, Gene geneB, boolean alleleAFromB) {
        if (!geneA. getTrait().equals(geneB.getTrait())) {
            return GeneEditorResult.failure("Traits must match for allele swap");
        }

        Trait trait = geneA. getTrait();

        List<GeneLocus> lociA = dnaA.getLoci().get(trait);
        List<GeneLocus> lociB = dnaB.getLoci().get(trait);

        if (lociA == null || lociA.isEmpty() || lociB == null || lociB.isEmpty()) {
            return GeneEditorResult.failure("Missing loci data");
        }

        GeneLocus locusA = lociA.get(0);
        GeneLocus locusB = lociB.get(0);

        Allele fromA = alleleAFromA ? locusA.getAlleleA() : locusA.getAlleleB();
        Allele fromB = alleleAFromB ? locusB.getAlleleA() : locusB.getAlleleB();

        GeneLocus newLocusA;
        GeneLocus newLocusB;

        if (alleleAFromA) {
            newLocusA = locusA.withAlleleA(fromB);
        } else {
            newLocusA = locusA.withAlleleB(fromB);
        }

        if (alleleAFromB) {
            newLocusB = locusB.withAlleleA(fromA);
        } else {
            newLocusB = locusB.withAlleleB(fromA);
        }

        lociA.set(0, newLocusA);
        lociB.set(0, newLocusB);

        float stabilityLoss = 0.05f;
        lociA.set(0, newLocusA.withStability(newLocusA.getStability() - stabilityLoss));
        lociB.set(0, newLocusB.withStability(newLocusB.getStability() - stabilityLoss));

        WildAside.LOGGER.info("Swapped alleles for trait [{}]", trait.getName());

        return GeneEditorResult.success("Alleles swapped", stabilityLoss * 10f);
    }

    public static GeneEditorResult modifyDominance(
            DnaImplementation dna, Gene gene, boolean modifyAlleleA, Dominance newDominance) {

        Trait trait = gene.getTrait();
        List<GeneLocus> loci = dna.getLoci().get(trait);

        if (loci == null || loci.isEmpty()) {
            return GeneEditorResult.failure("Missing loci data");
        }

        GeneLocus locus = loci.get(0);
        Allele target = modifyAlleleA ? locus. getAlleleA() : locus.getAlleleB();
        Allele modified = target.copyWithDominance(newDominance);

        GeneLocus newLocus;
        if (modifyAlleleA) {
            newLocus = locus.withAlleleA(modified);
        } else {
            newLocus = locus.withAlleleB(modified);
        }

        float stabilityLoss = 0.1f;
        newLocus = newLocus.withStability(newLocus.getStability() - stabilityLoss);

        loci.set(0, newLocus);

        WildAside.LOGGER.info("Modified dominance for trait [{}] allele {} to {}",
                trait.getName(), modifyAlleleA ? "A" : "B", newDominance);

        return GeneEditorResult.success("Dominance modified", stabilityLoss * 15f);
    }

    public static GeneEditorResult stabilize(DnaImplementation dna, Gene gene, float amount) {
        Trait trait = gene.getTrait();
        List<GeneLocus> loci = dna.getLoci().get(trait);

        if (loci == null || loci.isEmpty()) {
            return GeneEditorResult.failure("Missing loci data");
        }

        GeneLocus locus = loci.get(0);
        float newStability = Math.min(1.0f, locus.getStability() + amount);
        GeneLocus newLocus = locus.withStability(newStability);

        if (locus.getSource() == LocusSource.TRANSIENT && newStability > 0.8f) {
            newLocus = newLocus.withSource(LocusSource.INTEGRATED);
            WildAside.LOGGER.info("Transient locus stabilized to Integrated for trait [{}]", trait.getName());
        }

        loci.set(0, newLocus);

        WildAside.LOGGER.info("Stabilized trait [{}] by {} to {}", trait.getName(), amount, newStability);

        return GeneEditorResult.success("Gene stabilized", 0f);
    }

    public static GeneEditorResult amplify(DnaImplementation dna, Gene gene, float multiplier) {
        Trait trait = gene.getTrait();
        List<GeneLocus> loci = dna.getLoci().get(trait);

        if (loci == null || loci.isEmpty()) {
            return GeneEditorResult.failure("Missing loci data");
        }

        GeneLocus locus = loci. get(0);

        Allele alleleA = locus.getAlleleA();
        Allele alleleB = locus.getAlleleB();

        AlleleValue valueA = alleleA.getValueHolder();
        AlleleValue valueB = alleleB.getValueHolder();

        if (!(valueA instanceof FloatAlleleValue) || !(valueB instanceof FloatAlleleValue)) {
            return GeneEditorResult.failure("Cannot amplify non-numeric genes");
        }

        float newValueA = ((FloatAlleleValue) valueA).get() * multiplier;
        float newValueB = ((FloatAlleleValue) valueB).get() * multiplier;

        Allele newAlleleA = alleleA.copyWithValue(new FloatAlleleValue(newValueA));
        Allele newAlleleB = alleleB.copyWithValue(new FloatAlleleValue(newValueB));

        GeneLocus newLocus = locus.withAlleleA(newAlleleA).withAlleleB(newAlleleB);

        float stabilityLoss = (multiplier - 1.0f) * 0.2f;
        newLocus = newLocus.withStability(Math.max(0.1f, newLocus.getStability() - stabilityLoss));

        loci.set(0, newLocus);

        WildAside.LOGGER.info("Amplified trait [{}] by {}x", trait.getName(), multiplier);

        return GeneEditorResult. success("Gene amplified", stabilityLoss * 20f);
    }

    public static GeneEditorResult suppress(DnaImplementation dna, Gene gene, float multiplier) {
        return amplify(dna, gene, 1.0f / multiplier);
    }

    public static GeneEditorResult mergeLoci(
            DnaImplementation targetDna, Gene targetGene,
            DnaImplementation sourceDna, Gene sourceGene) {

        if (! targetGene.getTrait().equals(sourceGene.getTrait())) {
            return GeneEditorResult.failure("Traits must match for merge");
        }

        Trait trait = targetGene.getTrait();

        List<GeneLocus> targetLoci = targetDna.getLoci().get(trait);
        List<GeneLocus> sourceLoci = sourceDna.getLoci().get(trait);

        if (targetLoci == null || sourceLoci == null || sourceLoci.isEmpty()) {
            return GeneEditorResult.failure("Missing loci data");
        }

        if (targetLoci.size() >= 4) {
            return GeneEditorResult.failure("Target already has maximum loci");
        }

        GeneLocus sourceLocus = sourceLoci.get(0);
        GeneLocus copiedLocus = new GeneLocus(
                sourceLocus.getId() + "_merged",
                sourceLocus.getAlleleA().copy(),
                sourceLocus.getAlleleB().copy(),
                sourceLocus.getFlags(),
                sourceLocus.getStability() * 0.8f,
                LocusSource.INTEGRATED,
                0,
                0f
        );

        targetLoci.add(copiedLocus);

        WildAside. LOGGER.info("Merged locus into trait [{}], now has {} loci", trait.getName(), targetLoci.size());

        return GeneEditorResult. success("Locus merged", 15f);
    }
}