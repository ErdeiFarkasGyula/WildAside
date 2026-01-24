package net.farkas.wildaside.capability.gene_editor;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class GeneEditorStateCapability {
    public static final Capability<IGeneEditorState> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
}