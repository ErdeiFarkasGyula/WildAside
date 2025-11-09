package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.screen.ModVisibleSlot;
import net.farkas.wildaside.util.AdvancementHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BioengineeringWorkstationResultSlot extends ModVisibleSlot {
    private final Player player;

    public BioengineeringWorkstationResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Player player) {
        super(itemHandler, index, xPosition, yPosition);
        this.player = player;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        super.onTake(pPlayer, pStack);

        if (player instanceof ServerPlayer serverPlayer) {
            AdvancementHandler.givePlayerAdvancement(serverPlayer, "we_need_to_cook");
        }
    }
}