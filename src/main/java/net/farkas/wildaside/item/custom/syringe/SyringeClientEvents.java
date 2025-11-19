package net.farkas.wildaside.item.custom.syringe;

import com.mojang.blaze3d.vertex.PoseStack;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.syringe.SyringeAnimCapability;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SyringeClientEvents {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END) return;
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        player.getCapability(SyringeAnimCapability.INSTANCE).ifPresent(anim -> {
            if (!anim.getActive()) return;
            anim.tickClient(player);
        });
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        player.getCapability(SyringeAnimCapability.INSTANCE).ifPresent(anim -> {
            if (!anim.getActive()) return;

            PoseStack pose = event.getPoseStack();
            float p = anim.getProgress();
            float offset = anim.getInwards() ? p * -0.35f : p * 0.35f;
            pose.translate(0, offset * 0.5, offset);
            pose.scale(1.0f - p * 0.05f, 1.0f - p * 0.05f, 1.0f - p * 0.05f);
        });
    }
}