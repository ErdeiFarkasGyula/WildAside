package net.farkas.wildaside.item;

import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class VanillaCreativeTabs {
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        var tab = event.getTabKey();
        if (tab == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.VIBRION);
            event.accept(ModItems.MUCELLITH_JAW);
            event.accept(ModItems.ENTORIUM);
        }
        if (tab == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.COMPRESSED_VIBRION_BLOCK);
            event.accept(ModBlocks.VIBRION_GLASS);
            event.accept(ModBlocks.LIT_VIBRION_GLASS);
            event.accept(ModBlocks.VIBRION_GLASS_PANE);
            event.accept(ModBlocks.LIT_VIBRION_GLASS_PANE);
            event.accept(ModBlocks.SMOOTH_SUBSTILIUM_SOIL);
            event.accept(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_STAIRS);
            event.accept(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_SLAB);
            event.accept(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_WALLS);
            event.accept(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_PRESSURE_PLATE);
            event.accept(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_BUTTON);
            event.accept(ModBlocks.SUBSTILIUM_TILES);
            event.accept(ModBlocks.SUBSTILIUM_TILE_STAIRS);
            event.accept(ModBlocks.SUBSTILIUM_TILE_SLAB);
            event.accept(ModBlocks.SUBSTILIUM_TILE_WALLS);
            event.accept(ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE);
            event.accept(ModBlocks.SUBSTILIUM_TILE_BUTTON);
            event.accept(ModBlocks.CHISELED_SUBSTILIUM_SOIL);
            event.accept(ModBlocks.SUBSTILIUM_STEM);
            event.accept(ModBlocks.SUBSTILIUM_WOOD);
            event.accept(ModBlocks.STRIPPED_SUBSTILIUM_STEM);
            event.accept(ModBlocks.STRIPPED_SUBSTILIUM_WOOD);
            event.accept(ModBlocks.SUBSTILIUM_PLANKS);
            event.accept(ModBlocks.SUBSTILIUM_STAIRS);
            event.accept(ModBlocks.SUBSTILIUM_SLAB);
            event.accept(ModBlocks.SUBSTILIUM_FENCE);
            event.accept(ModBlocks.SUBSTILIUM_FENCE_GATE);
            event.accept(ModBlocks.SUBSTILIUM_DOOR);
            event.accept(ModBlocks.SUBSTILIUM_TRAPDOOR);
            event.accept(ModBlocks.SUBSTILIUM_PRESSURE_PLATE);
            event.accept(ModBlocks.SUBSTILIUM_BUTTON);
            event.accept(ModBlocks.HICKORY_LOG);
            event.accept(ModBlocks.HICKORY_WOOD);
            event.accept(ModBlocks.STRIPPED_HICKORY_LOG);
            event.accept(ModBlocks.STRIPPED_HICKORY_WOOD);
            event.accept(ModBlocks.HICKORY_STAIRS);
            event.accept(ModBlocks.HICKORY_SLAB);
            event.accept(ModBlocks.HICKORY_FENCE);
            event.accept(ModBlocks.HICKORY_FENCE_GATE);
            event.accept(ModBlocks.HICKORY_DOOR);
            event.accept(ModBlocks.HICKORY_TRAPDOOR);
            event.accept(ModBlocks.HICKORY_PRESSURE_PLATE);
            event.accept(ModBlocks.HICKORY_BUTTON);
        }
        if (tab == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.SPORE_BOMB);
            event.accept(ModItems.SPORE_ARROW);
        }
        if (tab == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.HICKORY_NUT);
            event.accept(ModItems.HICKORY_NUT_TRAIL_MIX);
            event.accept(ModItems.RED_HICKORY_NUT_TRAIL_MIX);
            event.accept(ModItems.BROWN_HICKORY_NUT_TRAIL_MIX);
            event.accept(ModItems.YELLOW_HICKORY_NUT_TRAIL_MIX);
            event.accept(ModItems.GREEN_HICKORY_NUT_TRAIL_MIX);
        }
        if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.BIOENGINEERING_WORKSTATION);
            event.accept(ModBlocks.SUBSTILIUM_SIGN);
            event.accept(ModBlocks.SUBSTILIUM_HANGING_SIGN);
            event.accept(ModBlocks.HICKORY_SIGN);
            event.accept(ModBlocks.HICKORY_HANGING_SIGN);
        }
        if (tab == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModBlocks.VIBRION_BLOCK);
            event.accept(ModBlocks.VIBRION_GEL);
            event.accept(ModBlocks.LIT_VIBRION_GEL);
            event.accept(ModBlocks.VIBRION_GROWTH);
            event.accept(ModBlocks.VIBRION_SPOREHOLDER);
            event.accept(ModBlocks.HANGING_VIBRION_VINES);
            event.accept(ModBlocks.SUBSTILIUM_SPROUTS);
            event.accept(ModBlocks.SUBSTILIUM_SOIL);
            event.accept(ModBlocks.SUBSTILIUM_COAL_ORE);
            event.accept(ModBlocks.SUBSTILIUM_IRON_ORE);
            event.accept(ModBlocks.SUBSTILIUM_COPPER_ORE);
            event.accept(ModBlocks.SUBSTILIUM_GOLD_ORE);
            event.accept(ModBlocks.SUBSTILIUM_REDSTONE_ORE);
            event.accept(ModBlocks.SUBSTILIUM_EMERALD_ORE);
            event.accept(ModBlocks.SUBSTILIUM_LAPIS_ORE);
            event.accept(ModBlocks.SUBSTILIUM_DIAMOND_ORE);
            event.accept(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL);
            event.accept(ModBlocks.OVERGROWN_ENTORIUM_ORE);
            event.accept(ModBlocks.ENTORIUM_ORE);
            event.accept(ModBlocks.NATURAL_SPORE_BLASTER);
            event.accept(ModBlocks.ENTORIUM_SHROOM);
            event.accept(ModBlocks.SUBSTILIUM_STEM);
            event.accept(ModBlocks.HICKORY_LOG);
            event.accept(ModBlocks.HICKORY_LEAVES);
            event.accept(ModBlocks.RED_GLOWING_HICKORY_LEAVES);
            event.accept(ModBlocks.BROWN_GLOWING_HICKORY_LEAVES);
            event.accept(ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES);
            event.accept(ModBlocks.GREEN_GLOWING_HICKORY_LEAVES);
            event.accept(ModItems.HICKORY_LEAF);
            event.accept(ModItems.RED_GLOWING_HICKORY_LEAF);
            event.accept(ModItems.BROWN_GLOWING_HICKORY_LEAF);
            event.accept(ModItems.YELLOW_GLOWING_HICKORY_LEAF);
            event.accept(ModItems.GREEN_GLOWING_HICKORY_LEAF);
            event.accept(ModBlocks.HICKORY_SAPLING);
            event.accept(ModBlocks.RED_GLOWING_HICKORY_SAPLING);
            event.accept(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING);
            event.accept(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING);
            event.accept(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING);
            event.accept(ModBlocks.SPOTTED_WINTERGREEN);
            event.accept(ModBlocks.PINKSTER_FLOWER);
        }
        if (tab == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModBlocks.SPORE_BLASTER);
            event.accept(ModBlocks.POTION_BLASTER);
//            event.accept(ModBlocks.WIND_BLASTER);
        }
        if (tab == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SUBSTILIUM_BOAT);
            event.accept(ModItems.SUBSTILIUM_CHEST_BOAT);
            event.accept(ModItems.HICKORY_BOAT);
            event.accept(ModItems.HICKORY_CHEST_BOAT);
            event.accept(ModItems.ENTORIUM_PILL);
            event.accept(ModItems.FERTILISER_BOMB);
            event.accept(ModItems.SPORE_BOMB);
            event.accept(ModItems.SPORE_ARROW);
            event.accept(ModItems.DNA_HOLDER);
        }
        if (tab == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.MUCELLITH_SPAWN_EGG);
//            event.accept(ModItems.CONTAMINATED_CREEPER_SPAWN_EGG);
//            event.accept(ModItems.HICKORY_TREANT_SPAWN_EGG);
        }
    }
}
