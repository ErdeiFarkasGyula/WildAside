package net.farkas.wildaside.worldgen.feature.tree;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.worldgen.ModConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class ModTreeGrowers {
    static RandomSource random = RandomSource.create();

    private static ResourceKey<ConfiguredFeature<?, ?>> substiliumMushroom = random.nextBoolean() ? ModConfiguredFeatures.REDLIKE_SUBSTILIUM_MUSHROOM : ModConfiguredFeatures.BROWNLIKE_SUBSTILIUM_MUSHROOM;

    public static final TreeGrower SUBSTILIUM_MUSHROOM = new TreeGrower(WildAside.MOD_ID + ":substilium_mushroom",
            Optional.empty(), Optional.of(substiliumMushroom), Optional.empty());

    public static final TreeGrower HICKORY_TREE_GROWER = new TreeGrower(WildAside.MOD_ID + ":hickory_tree_grower",
            Optional.empty(), Optional.of(ModConfiguredFeatures.HICKORY_TREE), Optional.empty());
    public static final TreeGrower RED_GLOWING_HICKORY_TREE_GROWER = new TreeGrower(WildAside.MOD_ID + ":red_glowing_hickory_tree_grower",
            Optional.empty(), Optional.of(ModConfiguredFeatures.RED_GLOWING_HICKORY_TREE), Optional.empty());
    public static final TreeGrower BROWN_GLOWING_HICKORY_TREE_GROWER = new TreeGrower(WildAside.MOD_ID + ":brown_glowing_hickory_tree_grower",
            Optional.empty(), Optional.of(ModConfiguredFeatures.BROWN_GLOWING_HICKORY_TREE), Optional.empty());
    public static final TreeGrower YELLOW_GLOWING_HICKORY_TREE_GROWER = new TreeGrower(WildAside.MOD_ID + ":yellow_glowing_hickory_tree_grower",
            Optional.empty(), Optional.of(ModConfiguredFeatures.YELLOW_GLOWING_HICKORY_TREE), Optional.empty());
    public static final TreeGrower GREEN_GLOWING_HICKORY_TREE_GROWER = new TreeGrower(WildAside.MOD_ID + ":green_glowing_hickory_tree_grower",
            Optional.empty(), Optional.of(ModConfiguredFeatures.GREEN_GLOWING_HICKORY_TREE), Optional.empty());
}
