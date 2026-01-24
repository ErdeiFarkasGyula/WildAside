package net.farkas.wildaside.dna.editor;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum GeneEditorOperation {
    ADD_CODING_REGION("add_coding_region", "component_editing_basic", 10),
    REMOVE_CODING_REGION("remove_coding_region", "component_editing_basic", 12),
    MODIFY_CODING_REGION("modify_coding_region", "component_editing_basic", 8),
    
    ADD_ACTIVATOR("add_activator", "component_editing_advanced", 15),
    REMOVE_ACTIVATOR("remove_activator", "component_editing_advanced", 18),
    MODIFY_ACTIVATOR("modify_activator", "component_editing_advanced", 12),
    
    ADD_ENHANCER("add_enhancer", "component_editing_advanced", 20),
    REMOVE_ENHANCER("remove_enhancer", "component_editing_advanced", 15),
    MODIFY_ENHANCER("modify_enhancer", "component_editing_advanced", 15),
    
    ADD_SILENCER("add_silencer", "component_editing_master", 25),
    REMOVE_SILENCER("remove_silencer", "component_editing_master", 20),
    MODIFY_SILENCER("modify_silencer", "component_editing_master", 18),
    
    ADD_REGULATOR("add_regulator", "component_editing_master", 30),
    REMOVE_REGULATOR("remove_regulator", "component_editing_master", 25),
    MODIFY_REGULATOR("modify_regulator", "component_editing_master", 20),
    
    COPY_SEQUENCE("copy_sequence", "sequence_manipulation", 35),
    SWAP_COMPONENT("swap_component", "sequence_manipulation", 28),
    
    STABILIZE_SEQUENCE("stabilize_sequence", "genetic_stabilization", 40),
    INTEGRATE_INVADING_SEQUENCE("integrate_invading_sequence", "genetic_stabilization", 45),
    REJECT_INVADING_SEQUENCE("reject_invading_sequence", null, 15);

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
        return Component.translatable("gene_editor.wildaside.operation." + id + ".desc");
    }

    public boolean requiresSkill() {
        return requiredSkill != null;
    }
}
