package net.farkas.wildaside.capability.contamination;

import net.farkas.wildaside.WildAside;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ContaminationAttacher {
    public static class ContaminationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "contamination");

        private final IContamination backend = new ContaminationImplementation() {};
        private final LazyOptional<IContamination> optionalData = LazyOptional.of(() -> backend);

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return ContaminationCapability.INSTANCE.orEmpty(cap, this.optionalData);
        }

        void invalidate() {
            this.optionalData.invalidate();
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
            return this.backend.serializeNBT(registryAccess);
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
            this.backend.deserializeNBT(registryAccess, nbt);
        }
    }

    private ContaminationAttacher() {

    }
}
