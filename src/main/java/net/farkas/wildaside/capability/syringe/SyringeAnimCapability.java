package net.farkas.wildaside.capability.syringe;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class SyringeAnimCapability {
    public static final Capability<ISyringeAnim> INSTANCE = CapabilityManager.get(new CapabilityToken<ISyringeAnim>() {});

    public SyringeAnimCapability() {

    }
}
