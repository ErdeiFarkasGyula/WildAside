package net.farkas.wildaside.capability.contamination;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ContaminationCapability {
    public static final Capability<IContamination> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public ContaminationCapability() {

    }
}
