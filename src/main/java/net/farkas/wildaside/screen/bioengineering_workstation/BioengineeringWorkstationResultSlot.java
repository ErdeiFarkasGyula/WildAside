package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.WildAside;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BioengineeringWorkstationResultSlot extends SlotItemHandler {
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

        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            ResourceLocation id = new ResourceLocation(WildAside.MOD_ID, "we_need_to_cook");
            Advancement adv = serverPlayer.server.getAdvancements().getAdvancement(id);
            if (adv != null) {
                AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(adv);
                if (!progress.isDone()) {
                    for (String criterion : progress.getRemainingCriteria()) {
                        serverPlayer.getAdvancements().award(adv, criterion);
                    }
                }
            }
        }
    }
}
