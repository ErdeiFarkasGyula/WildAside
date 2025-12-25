package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillPointOperation;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas.wildaside.screen.AdvancementGivingVisibleResultSlotItemHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class PointGivingRewardSlotItemHandler extends AdvancementGivingVisibleResultSlotItemHandler {
    private final int points;

    public PointGivingRewardSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, Player player, ResourceLocation advancement, int points) {
        super(itemHandler, index, xPosition, yPosition, player, advancement);
        this.points = points;
    }

    public PointGivingRewardSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, Player player, int points) {
        super(itemHandler, index, xPosition, yPosition);
        this.points = points;
        this.player = player;
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        super.onTake(pPlayer, pStack);
        if (player instanceof ServerPlayer serverPlayer && shouldGivePoint(player, pStack) && points > 0) {
            BioengineeringSkillUtils.handlePointsAndSyncToClient(player, points, BioengineeringSkillPointOperation.ADD);
        }
    }

    public boolean shouldGivePoint(Player pPlayer, ItemStack pStack) {
        return true;
    }
}
