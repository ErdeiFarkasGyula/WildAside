package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class DnaExtractor extends Item {
    public DnaExtractor(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND || player.level().isClientSide()) return InteractionResult.PASS;

        if (stack.getCount() > 1) {
            ItemStack single = stack.split(1);
            ItemStack result = applyDna(player, target, single);

            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide());
        } else {
            ItemStack result = applyDna(player, target, stack);
            player.setItemInHand(hand, result);
        }

        return InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    private ItemStack applyDna(Player player, LivingEntity target, ItemStack stack) {
        var baseGenes = DnaUtils.generateBaseGenes(target);
        var mutatedGenes = DnaUtils.mutateGenes(baseGenes, target);

        DnaImplementation extractedDna = new DnaImplementation();
        extractedDna.setSource(target.getType());
        extractedDna.setGenes(mutatedGenes);

        target.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            extractedDna.setStability(dna.stability());
        });

        player.getCapability(DnaCapability.INSTANCE).ifPresent(playerDna -> {
            playerDna.setSource(extractedDna.source());
            float stability = playerDna.stability() - playerDna.calculateInstabilityChange(extractedDna.genes());
            playerDna.setStability(stability);
            System.out.println(stability);
            playerDna.setGenes(extractedDna.genes());
            playerDna.apply(player);
        });

        CompoundTag tag = stack.getOrCreateTag();
        tag.put("dna_data", extractedDna.serializeNBT());

        tag.putBoolean("reveal_source", false);
        tag.putBoolean("reveal_stability", false);
        tag.putBoolean("reveal_traits", false);

        stack.setTag(tag);
        return stack;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        CompoundTag tag = item.getOrCreateTag();
        tag.putBoolean("reveal_source", true);
        tag.putBoolean("reveal_stability", true);
        tag.putBoolean("reveal_traits", true);
        item.setTag(tag);
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);

        if (!stack.hasTag() || !stack.getTag().contains("dna_data")) {
            tooltip.add(Component.literal("No DNA stored").withStyle(ChatFormatting.GRAY));
            return;
        }

        CompoundTag tag = stack.getTag();
        DnaImplementation dna = new DnaImplementation();
        dna.deserializeNBT(tag.getCompound("dna_data"));

        boolean revealSource = tag.getBoolean("reveal_source");
        boolean revealStability = tag.getBoolean("reveal_stability");
        boolean revealTraits = tag.getBoolean("reveal_traits");

        if (!(revealSource || revealStability || revealTraits)) {
            tooltip.add(Component.literal("DNA data hidden").withStyle(ChatFormatting.STRIKETHROUGH));
        } else {
            if (revealSource) {
                String sourceName = dna.source() != null
                        ? ForgeRegistries.ENTITY_TYPES.getKey(dna.source()).getPath()
                        : "Unknown";
                tooltip.add(Component.literal("Source: " + sourceName).withStyle(ChatFormatting.AQUA));
            }

            if (revealStability) {
                tooltip.add(Component.literal("Stability: " + String.format("%.2f", dna.stability())).withStyle(ChatFormatting.GREEN));
            }

            if (revealTraits) {
                BiConsumer<String, TraitTypes> displayGenes = (title, type) -> {
                    Map<Trait, Gene> filtered = dna.genes().entrySet().stream()
                            .filter(e -> e.getKey().traitType() == type)
                            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Trait::name)))
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (a, b) -> a,
                                    LinkedHashMap::new
                            ));
                    if (!filtered.isEmpty()) {
                        ChatFormatting headerColor = switch (type) {
                            case CORE -> ChatFormatting.YELLOW;
                            case RESISTANCE -> ChatFormatting.BLUE;
                            case ABILITY -> ChatFormatting.RED;
                        };
                        tooltip.add(Component.literal(title).withStyle(headerColor));
                        filtered.values().forEach(gene -> {
                            String valueStr = String.format("%.2f", gene.value());
                            ChatFormatting valueColor = (type == TraitTypes.ABILITY) ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.WHITE;
                            tooltip.add(Component.literal("- " + gene.trait().name() + ": " + valueStr).withStyle(valueColor));
                        });
                    }
                };

                displayGenes.accept("Core Stats:", TraitTypes.CORE);
                displayGenes.accept("Resistances:", TraitTypes.RESISTANCE);
                displayGenes.accept("Abilities:", TraitTypes.ABILITY);
            }
        }
    }
}
