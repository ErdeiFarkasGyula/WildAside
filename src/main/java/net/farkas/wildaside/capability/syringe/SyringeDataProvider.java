package net.farkas.wildaside.capability.syringe;

import net.farkas.wildaside.WildAside;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SyringeDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation ID = new ResourceLocation(WildAside.MOD_ID, "syringe_data");

    private final ISyringeData backend = new SyringeDataImplementation();
    private final LazyOptional<ISyringeData> optional = LazyOptional.of(() -> backend);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return SyringeDataCapability.INSTANCE.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        backend.deserializeNBT(nbt);
    }

    public void invalidate() { optional.invalidate(); }
}