package net.farkas.wildaside.capability.bioengineering;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.contamination.ContaminationImplementation;
import net.farkas.wildaside.capability.contamination.IContamination;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BioengineeringSkillsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(WildAside.MOD_ID, "bioengineering_skills");

    private final IBioengineeringSkills backend = new BioengineeringSkillsImplementation() {};
    private final LazyOptional<IBioengineeringSkills> optionalData = LazyOptional.of(() -> backend);

    @NotNull
    @Override
    public  <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return BioengineeringSkillsCapability.INSTANCE.orEmpty(cap, this.optionalData);
    }

    void invalidate() { this.optionalData.invalidate(); }

    @Override
    public CompoundTag serializeNBT() {
        return this.backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.backend.deserializeNBT(nbt);
    }
}
