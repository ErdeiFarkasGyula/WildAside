package net.farkas.wildaside.network;

import net.farkas.wildaside.capability.syringe.SyringeDataCapability;
import net.farkas.wildaside.item.custom.Syringe;
import net.farkas.wildaside.network.packets.SyringeDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class SyringeClientData {
    public static void handleSyringeProgress(SyringeDataPacket pkt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int slot = pkt.getSlot();
        if (slot < 0 || slot >= mc.player.getInventory().items.size()) return;
        ItemStack s = mc.player.getInventory().getItem(slot);
        if (!(s.getItem() instanceof Syringe)) return;

        s.getCapability(SyringeDataCapability.INSTANCE).ifPresent(cap -> {
            cap.setSlot(pkt.getSlot());
            cap.setProgress(pkt.getProgress());
            cap.setAnimating(pkt.isAnimating());
            cap.setInwards(pkt.isInwards());
        });
    }
}
