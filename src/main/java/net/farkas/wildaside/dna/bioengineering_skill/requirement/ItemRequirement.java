package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemRequirement extends IBioengineeringSkillRequirement {
    private final Item item;
    private final @Nullable CompoundTag requiredTag;
    private final boolean consume;

    public ItemRequirement(Item item, @Nullable CompoundTag requiredTag, boolean consume) {
        this.item = item;
        this.requiredTag = requiredTag;
        this.consume = consume;
    }

    @Override
    public boolean isSatisfied(ServerPlayer player) {
        return findMatchingStack(player) != null;
    }

    @Override
    public void unlock(ServerPlayer player) {
        if (!consume) return;

        ItemStack match = findMatchingStack(player);
        if (match != null) {
            match.shrink(1);
        }
    }

    private ItemStack findMatchingStack(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() != item) continue;

            if (requiredTag == null) {
                if (!stack.isEmpty()) return stack;
            } else if (stack.hasTag() && stack.getTag().equals(requiredTag)) {
                return stack;
            }
        }
        return null;
    }
}
