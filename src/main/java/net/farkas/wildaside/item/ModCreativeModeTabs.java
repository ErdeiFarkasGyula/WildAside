package net.farkas.wildaside.item;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WildAside.MOD_ID);

    public static final RegistryObject<CreativeModeTab> WILDASIDE_TAB = CREATIVE_MODE_TABS.register(WildAside.MOD_ID,
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.VIBRION_BLOCK.get()))
                    .title(Component.translatable("creativetab.wildaside_tab"))
                    .displayItems((pParamateres, pOutput) -> {
                        pOutput.accept(ModItems.VIBRION.get());
                        pOutput.accept(ModBlocks.VIBRION_BLOCK.get());
                        pOutput.accept(ModBlocks.COMPRESSED_VIBRION_BLOCK.get());
                        pOutput.accept(ModBlocks.VIBRION_GEL.get());
                        pOutput.accept(ModBlocks.LIT_VIBRION_GEL.get());
                        pOutput.accept(ModBlocks.VIBRION_GROWTH.get());
                        pOutput.accept(ModBlocks.VIBRION_SPOREHOLDER.get());
                        pOutput.accept(ModBlocks.HANGING_VIBRION_VINES.get());
                        pOutput.accept(ModBlocks.VIBRION_GLASS.get());
                        pOutput.accept(ModBlocks.LIT_VIBRION_GLASS.get());
                        pOutput.accept(ModBlocks.VIBRION_GLASS_PANE.get());
                        pOutput.accept(ModBlocks.LIT_VIBRION_GLASS_PANE.get());
                        pOutput.accept(ModItems.MUCELLITH_SPAWN_EGG.get());
                        pOutput.accept(ModItems.MUCELLITH_JAW.get());

                        pOutput.accept(ModBlocks.SUBSTILIUM_SPROUTS.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_SOIL.get());
                        pOutput.accept(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get());
                        pOutput.accept(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get());
                        pOutput.accept(ModBlocks.CHISELED_SUBSTILIUM_SOIL.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_TILES.get());
                        pOutput.accept(ModBlocks.CRACKED_SUBSTILIUM_TILES.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_TILE_STAIRS.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_TILE_SLAB.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_TILE_WALLS.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_TILE_BUTTON.get());

                        pOutput.accept(ModBlocks.SUBSTILIUM_COAL_ORE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_IRON_ORE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_COPPER_ORE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_GOLD_ORE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_REDSTONE_ORE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_EMERALD_ORE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_LAPIS_ORE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_DIAMOND_ORE.get());
                        pOutput.accept(ModBlocks.OVERGROWN_ENTORIUM_ORE.get());
                        pOutput.accept(ModBlocks.ENTORIUM_ORE.get());
                        pOutput.accept(ModItems.ENTORIUM.get());
                        pOutput.accept(ModBlocks.BIOENGINEERING_WORKSTATION.get());
                        pOutput.accept(ModItems.ENTORIUM_PILL.get());
                        pOutput.accept(ModItems.SPORE_ARROW.get());
                        pOutput.accept(ModItems.SPORE_BOMB.get());
                        pOutput.accept(ModItems.FERTILISER_BOMB.get());

                        pOutput.accept(ModBlocks.NATURAL_SPORE_BLASTER.get());
                        pOutput.accept(ModBlocks.SPORE_BLASTER.get());
                        pOutput.accept(ModBlocks.POTION_BLASTER.get());

                        pOutput.accept(ModBlocks.ENTORIUM_SHROOM.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_STEM.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_WOOD.get());
                        pOutput.accept(ModBlocks.STRIPPED_SUBSTILIUM_STEM.get());
                        pOutput.accept(ModBlocks.STRIPPED_SUBSTILIUM_WOOD.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_PLANKS.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_STAIRS.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_SLAB.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_FENCE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_FENCE_GATE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_DOOR.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_TRAPDOOR.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_PRESSURE_PLATE.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_BUTTON.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_SIGN.get());
                        pOutput.accept(ModBlocks.SUBSTILIUM_HANGING_SIGN.get());
                        pOutput.accept(ModItems.SUBSTILIUM_BOAT.get());
                        pOutput.accept(ModItems.SUBSTILIUM_CHEST_BOAT.get());

                        pOutput.accept(ModBlocks.HICKORY_LOG.get());
                        pOutput.accept(ModBlocks.HICKORY_WOOD.get());
                        pOutput.accept(ModBlocks.STRIPPED_HICKORY_LOG.get());
                        pOutput.accept(ModBlocks.STRIPPED_HICKORY_WOOD.get());
                        pOutput.accept(ModBlocks.HICKORY_PLANKS.get());
                        pOutput.accept(ModBlocks.HICKORY_STAIRS.get());
                        pOutput.accept(ModBlocks.HICKORY_SLAB.get());
                        pOutput.accept(ModBlocks.HICKORY_FENCE.get());
                        pOutput.accept(ModBlocks.HICKORY_FENCE_GATE.get());
                        pOutput.accept(ModBlocks.HICKORY_DOOR.get());
                        pOutput.accept(ModBlocks.HICKORY_TRAPDOOR.get());
                        pOutput.accept(ModBlocks.HICKORY_PRESSURE_PLATE.get());
                        pOutput.accept(ModBlocks.HICKORY_BUTTON.get());
                        pOutput.accept(ModBlocks.HICKORY_SIGN.get());
                        pOutput.accept(ModBlocks.HICKORY_HANGING_SIGN.get());
                        pOutput.accept(ModItems.HICKORY_BOAT.get());
                        pOutput.accept(ModItems.HICKORY_CHEST_BOAT.get());
                        pOutput.accept(ModItems.HICKORY_NUT.get());
                        pOutput.accept(ModBlocks.HICKORY_LEAVES.get());
                        pOutput.accept(ModBlocks.RED_GLOWING_HICKORY_LEAVES.get());
                        pOutput.accept(ModBlocks.BROWN_GLOWING_HICKORY_LEAVES.get());
                        pOutput.accept(ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES.get());
                        pOutput.accept(ModBlocks.GREEN_GLOWING_HICKORY_LEAVES.get());
                        pOutput.accept(ModItems.HICKORY_LEAF.get());
                        pOutput.accept(ModItems.RED_GLOWING_HICKORY_LEAF.get());
                        pOutput.accept(ModItems.BROWN_GLOWING_HICKORY_LEAF.get());
                        pOutput.accept(ModItems.YELLOW_GLOWING_HICKORY_LEAF.get());
                        pOutput.accept(ModItems.GREEN_GLOWING_HICKORY_LEAF.get());
                        pOutput.accept(ModBlocks.HICKORY_SAPLING.get());
                        pOutput.accept(ModBlocks.RED_GLOWING_HICKORY_SAPLING.get());
                        pOutput.accept(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING.get());
                        pOutput.accept(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING.get());
                        pOutput.accept(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING.get());
                        pOutput.accept(ModItems.HICKORY_NUT_TRAIL_MIX.get());
                        pOutput.accept(ModItems.RED_HICKORY_NUT_TRAIL_MIX.get());
                        pOutput.accept(ModItems.BROWN_HICKORY_NUT_TRAIL_MIX.get());
                        pOutput.accept(ModItems.YELLOW_HICKORY_NUT_TRAIL_MIX.get());
                        pOutput.accept(ModItems.GREEN_HICKORY_NUT_TRAIL_MIX.get());
                        pOutput.accept(ModBlocks.SPOTTED_WINTERGREEN.get());
                        pOutput.accept(ModBlocks.PINKSTER_FLOWER.get());
                        pOutput.accept(ModItems.HICKORY_TREANT_SPAWN_EGG.get());

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
