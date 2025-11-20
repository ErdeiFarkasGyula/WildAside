package net.farkas.wildaside.capability.syringe;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class SyringeDataCapability {
    public static final Capability<ISyringeData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    private SyringeDataCapability() {

    }
}