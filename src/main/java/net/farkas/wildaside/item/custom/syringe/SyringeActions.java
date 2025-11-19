package net.farkas.wildaside.item.custom.syringe;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.capability.syringe.SyringeAnimCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SyringeActions {
    private static final Map<Player, Deque<SyringeAction>> QUEUES = new HashMap<>();

    public static void queue(Player player, SyringeAction action) {
        QUEUES.computeIfAbsent(player, p -> new LinkedList<>()).add(action);
    }

    public static SyringeAction pop(Player player) {
        Deque<SyringeAction> q = QUEUES.get(player);
        if (q == null || q.isEmpty()) return null;
        return q.poll();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            player.getCapability(SyringeAnimCapability.INSTANCE).ifPresent(anim -> {
                if (!anim.getActive()) return;
                anim.tickClient(player);
                if (!anim.getActive()) {
                    SyringeAction action = pop(player);
                    if (action != null) action.execute(player);
                }
            });
        }
    }
}