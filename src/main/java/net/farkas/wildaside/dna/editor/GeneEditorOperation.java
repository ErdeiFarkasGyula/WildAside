package net.farkas.wildaside.dna.editor;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum GeneEditorOperation {
    SWAP_TRAIT("swap_trait", null, 0),
    SWAP_ALLELE("swap_allele", "swap_alleles", 5),
    MODIFY_DOMINANCE("modify_dominance", "modify_dominance", 10),
    STABILIZE("stabilize", "stabilize_gene", 15),
    AMPLIFY("amplify", "amplify_gene", 20),
    SUPPRESS("suppress", "suppress_gene", 15),
    ISOLATE_ALLELE("isolate_allele", "isolate_allele", 25),
    MERGE_LOCI("merge_loci", "splice_locus", 30);

    private final String id;
    private final String requiredSkill;
    private final int stressCost;

    GeneEditorOperation(String id, String requiredSkill, int stressCost) {
        this.id = id;
        this.requiredSkill = requiredSkill;
        this.stressCost = stressCost;
    }

    public String getId() {
        return id;
    }

    public ResourceLocation getRequiredSkillId() {
        if (requiredSkill == null) return null;
        return new ResourceLocation("wildaside", requiredSkill);
    }

    public int getStressCost() {
        return stressCost;
    }

    public Component getDisplayName() {
        return Component.translatable("gene_editor.wildaside.operation." + id);
    }

    public Component getDescription() {
        return Component.translatable("gene_editor.wildaside.operation." + id + ". desc");
    }

    public boolean requiresSkill() {
        return requiredSkill != null;
    }
}