package net.farkas.wildaside.network.packet.gene_editor;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.screen.gene_editor.AdvancedGeneEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenAdvancedGeneEditorPacket {
    private final BlockPos pos;
    private final CompoundTag dnaATag;
    private final CompoundTag dnaBTag;

    public OpenAdvancedGeneEditorPacket(BlockPos pos, DnaImplementation dnaA, DnaImplementation dnaB) {
        this.pos = pos;
        this.dnaATag = dnaA != null ? dnaA.serializeNBT() : new CompoundTag();
        this.dnaBTag = dnaB != null ? dnaB.serializeNBT() : new CompoundTag();
    }

    public OpenAdvancedGeneEditorPacket(BlockPos pos, CompoundTag dnaA, CompoundTag dnaB) {
        this.pos = pos;
        this.dnaATag = dnaA;
        this.dnaBTag = dnaB;
    }

    public static OpenAdvancedGeneEditorPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundTag dnaATag = buf.readNbt();
        CompoundTag dnaBTag = buf.readNbt();
        return new OpenAdvancedGeneEditorPacket(pos, dnaATag, dnaBTag);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(dnaATag);
        buf.writeNbt(dnaBTag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                DnaImplementation dnaA = new DnaImplementation();
                DnaImplementation dnaB = new DnaImplementation();

                if (!dnaATag.isEmpty()) dnaA.deserializeNBT(dnaATag);
                if (!dnaBTag.isEmpty()) dnaB.deserializeNBT(dnaBTag);

                Minecraft.getInstance().setScreen(new AdvancedGeneEditorScreen(pos, dnaA, dnaB));
            });
        });
        ctx.get().setPacketHandled(true);
    }
}