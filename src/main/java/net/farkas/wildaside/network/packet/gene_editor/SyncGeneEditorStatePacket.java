package net.farkas.wildaside.network.packet.gene_editor;

import net.farkas.wildaside.capability.gene_editor.GeneEditorStateCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGeneEditorStatePacket {
    private final CompoundTag stateTag;

    public SyncGeneEditorStatePacket(CompoundTag stateTag) {
        this.stateTag = stateTag;
    }

    public static SyncGeneEditorStatePacket decode(FriendlyByteBuf buf) {
        return new SyncGeneEditorStatePacket(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(stateTag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;

                player.getCapability(GeneEditorStateCapability.INSTANCE).ifPresent(state -> {
                    state.deserializeNBT(stateTag);
                });
            } else {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    if (net.minecraft.client.Minecraft.getInstance().player != null) {
                        net.minecraft.client.Minecraft.getInstance().player.getCapability(GeneEditorStateCapability.INSTANCE).ifPresent(state -> {
                            state.deserializeNBT(stateTag);
                        });
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}