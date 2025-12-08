package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.screen.ModVisibleSlotItemHandler;
import net.farkas.wildaside.util.AdvancementHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class AdvancementGivingVisibleResultSlot extends ModVisibleSlotItemHandler {
    private final Player player;
    private final String advancement;

    public AdvancementGivingVisibleResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Player player, String advancement) {
        super(itemHandler, index, xPosition, yPosition);
        this.player = player;
        this.advancement = advancement;
    }

    public AdvancementGivingVisibleResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.player = null;
        this.advancement = null;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        super.onTake(pPlayer, pStack);

        if (advancement != null && player instanceof ServerPlayer serverPlayer) {
            AdvancementHandler.givePlayerAdvancement(serverPlayer, advancement);
        }
    }
}