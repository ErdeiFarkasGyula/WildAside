package net.farkas.wildaside.dna.bioengineering_skill.requirement;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemRequirement extends IBioengineeringSkillRequirement {
    private final ItemStack itemStack;
    private final boolean consume;

    public ItemRequirement(ItemStack stack, boolean consume) {
        this.itemStack = stack;
        this.consume = consume;
    }

    @Override
    public boolean isSatisfied(Player player) {
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

    @Override
    protected List<Component> getTooltip(Player player, int depth) {
        boolean has = isClientSatisfied(player);

        Component consumeTag = consume
                ? Component.translatable("skill.wildaside.consumes")
                .withStyle(ChatFormatting.DARK_RED)
//                : Component.translatable("skill.wildaside.not_consumed")
//                .withStyle(ChatFormatting.GRAY);
                : Component.empty();

        return List.of(
                indent(depth)
                        .append(bullet(has))
                        .append(Component.literal("x" + itemStack.getCount() + " "))
                        .append(itemStack.getHoverName())
                        .append(Component.literal(" "))
                        .append(consumeTag)
        );
    }
}
