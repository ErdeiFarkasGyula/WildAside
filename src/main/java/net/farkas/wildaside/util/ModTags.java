package net.farkas.wildaside.util;

import net.farkas.wildaside.WildAside;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> VIBRION_GELS = tag("vibrion_gels");
        public static final TagKey<Block> VIBRION_FULL_GLASSES = tag("vibrion_full_glasses");
        public static final TagKey<Block> NEEDS_ENTORIUM_TOOL = tag("needs_entorium_tool");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> SHEARS = tag("shears");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, name));
        }
    }
}
