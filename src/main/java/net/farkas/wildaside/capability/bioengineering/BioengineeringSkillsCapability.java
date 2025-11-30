package net.farkas.wildaside.capability.bioengineering;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class BioengineeringSkillsCapability {
    public static final Capability<IBioengineeringSkills> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public BioengineeringSkillsCapability() {

    }
}
