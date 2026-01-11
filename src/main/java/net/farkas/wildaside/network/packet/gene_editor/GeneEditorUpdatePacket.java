package net.farkas.wildaside.network.packet.gene_editor;

import net.farkas.wildaside.block.entity.custom.BioengineeringWorkstationBlockEntity;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.item.custom.DnaHolderItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static net.farkas.wildaside.dna.DnaConstants.DNA_DATA;
import static net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationSlots.*;

public class GeneEditorUpdatePacket {
    private final BlockPos pos;
    private final CompoundTag dnaATag;
    private final CompoundTag dnaBTag;

    public GeneEditorUpdatePacket(BlockPos pos, DnaImplementation dnaA, DnaImplementation dnaB) {
        this.pos = pos;
        this.dnaATag = dnaA != null ? dnaA.serializeNBT() : new CompoundTag();
        this.dnaBTag = dnaB != null ? dnaB.serializeNBT() : new CompoundTag();
    }

    public GeneEditorUpdatePacket(BlockPos pos, CompoundTag dnaA, CompoundTag dnaB) {
        this.pos = pos;
        this.dnaATag = dnaA;
        this.dnaBTag = dnaB;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(dnaATag);
        buf.writeNbt(dnaBTag);
    }

    public static GeneEditorUpdatePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundTag dnaATag = buf.readNbt();
        CompoundTag dnaBTag = buf.readNbt();
        return new GeneEditorUpdatePacket(pos, dnaATag, dnaBTag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            BlockEntity be = player.level().getBlockEntity(pos);
            if (!(be instanceof BioengineeringWorkstationBlockEntity workstation)) return;

            workstation.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                ItemStack stackA = handler.getStackInSlot(EDITOR_INPUT_1);
                ItemStack stackB = handler.getStackInSlot(EDITOR_INPUT_2);

                if (stackA.getItem() instanceof DnaHolderItem && !dnaATag.isEmpty()) {
                    CompoundTag tag = stackA.getOrCreateTag();
                    tag.put(DNA_DATA, dnaATag);
                    stackA.setTag(tag);
                }

                if (stackB.getItem() instanceof DnaHolderItem && !dnaBTag.isEmpty()) {
                    CompoundTag tag = stackB.getOrCreateTag();
                    tag.put(DNA_DATA, dnaBTag);
                    stackB.setTag(tag);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}