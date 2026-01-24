package net.farkas.wildaside.network.packet.gene_editor;

import net.farkas.wildaside.block.entity.custom.BioengineeringWorkstationBlockEntity;
import net.farkas.wildaside.dna.chromosome.Genome;
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
    private final CompoundTag genomeTag;

    public GeneEditorUpdatePacket(BlockPos pos, Genome genome) {
        this.pos = pos;
        this.genomeTag = genome != null ? genome.serializeNBT() : new CompoundTag();
    }

    public GeneEditorUpdatePacket(BlockPos pos, CompoundTag genomeTag) {
        this.pos = pos;
        this.genomeTag = genomeTag;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(genomeTag);
    }

    public static GeneEditorUpdatePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        CompoundTag genomeTag = buf.readNbt();
        return new GeneEditorUpdatePacket(pos, genomeTag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            BlockEntity be = player.level().getBlockEntity(pos);
            if (!(be instanceof BioengineeringWorkstationBlockEntity workstation)) return;

            double distanceSq = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (distanceSq > 64.0) {
                return;
            }

            workstation.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                ItemStack stack = handler.getStackInSlot(EDITOR_INPUT_1);

                if (stack.getItem() instanceof DnaHolderItem && !genomeTag.isEmpty()) {
                    CompoundTag existingTag = stack.getOrCreateTag();
                    CompoundTag existingGenome = existingTag.getCompound(DNA_DATA);

                    if (validateGenomeChanges(existingGenome, genomeTag, player)) {
                        existingTag.put(DNA_DATA, genomeTag);
                        stack.setTag(existingTag);
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

    private boolean validateGenomeChanges(CompoundTag oldGenome, CompoundTag newGenome, ServerPlayer player) {
        if (oldGenome.isEmpty()) {
            return true;
        }

        try {
            Genome oldGen = Genome.deserializeNBT(oldGenome);
            Genome newGen = Genome.deserializeNBT(newGenome);

            if (!oldGen.getEntityType().equals(newGen.getEntityType())) {
                return false;
            }

            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}