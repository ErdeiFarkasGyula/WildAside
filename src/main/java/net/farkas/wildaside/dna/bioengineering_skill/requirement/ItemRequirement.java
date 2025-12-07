package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemRequirement extends IBioengineeringSkillRequirement {
    private final ItemStack itemStack;
    private final boolean consume;

    public ItemRequirement(ItemStack stack, boolean consume) {
        this.itemStack = stack;
        this.consume = consume;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player) {
        return getTotalMatching(player) >= itemStack.getCount();
    }

    @Override
    public void unlock(ServerPlayer player) {
        if (!consume) return;

        int remaining = itemStack.getCount();

        for (ItemStack stack : player.getInventory().items) {
            if (remaining <= 0) break;
            if (!matches(stack)) continue;

            int removable = Math.min(stack.getCount(), remaining);
            stack.shrink(removable);
            remaining -= removable;
        }
    }

    private int getTotalMatching(Player player) {
        int total = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (matches(stack)) {
                total += stack.getCount();
            }
        }

        return total;
    }

    private boolean matches(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != itemStack.getItem()) return false;

        if (itemStack.getTag() == null) return true;

        return stack.hasTag() && stack.getTag().equals(itemStack.getTag());
    }
}
