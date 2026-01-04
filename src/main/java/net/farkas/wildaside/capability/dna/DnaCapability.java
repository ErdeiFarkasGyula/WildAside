package net.farkas.wildaside.capability.dna;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class DnaCapability {
    public static final Capability<IDna> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public DnaCapability() {

    }
}
