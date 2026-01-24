package net.farkas.wildaside.screen.slot;

import net.farkas.wildaside.advancement.AdvancementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class AdvancementGivingVisibleResultSlotItemHandler extends VisibleSlotItemHandler {
    public Player player;
    public ResourceLocation advancement;

    public AdvancementGivingVisibleResultSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, Player player, ResourceLocation advancement) {
        super(itemHandler, index, xPosition, yPosition);
        this.player = player;
        this.advancement = advancement;
    }

    public AdvancementGivingVisibleResultSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
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
            AdvancementUtils.givePlayerAdvancement(serverPlayer, advancement);
        }
    }
}