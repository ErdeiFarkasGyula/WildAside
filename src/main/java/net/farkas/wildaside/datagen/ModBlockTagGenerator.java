package net.farkas.wildaside.datagen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WildAside.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.Blocks.VIBRION_GELS)
                .add(ModBlocks.VIBRION_GEL.get())
                .add(ModBlocks.LIT_VIBRION_GEL.get());

        this.tag(ModTags.Blocks.VIBRION_FULL_GLASSES)
                .add(ModBlocks.VIBRION_GLASS.get())
                .add(ModBlocks.LIT_VIBRION_GLASS.get());

        //

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SPORE_BLASTER.get())
                .add(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get())
                .add(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get())
                .add(ModBlocks.CHISELED_SUBSTILIUM_SOIL.get())
                .add(ModBlocks.SUBSTILIUM_TILES.get())
                .add(ModBlocks.CRACKED_SUBSTILIUM_TILES.get())
                .add(ModBlocks.SUBSTILIUM_TILE_STAIRS.get())
                .add(ModBlocks.SUBSTILIUM_TILE_SLAB.get())
                .add(ModBlocks.SUBSTILIUM_TILE_BUTTON.get())
                .add(ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE.get())
                .add(ModBlocks.SUBSTILIUM_TILE_WALLS.get());

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.SPORE_BLASTER.get())
                .add(ModBlocks.SUBSTILIUM_SOIL.get())
                .add(ModBlocks.OVERGROWN_ENTORIUM_ORE.get())
                .add(ModBlocks.ENTORIUM_ORE.get());

        this.tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.VIBRION_GEL.get())
                .add(ModBlocks.LIT_VIBRION_GEL.get())
                .add(ModBlocks.VIBRION_GLASS.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.ENTORIUM_SHROOM.get())
                .add(ModBlocks.SUBSTILIUM_STEM.get())
                .add(ModBlocks.SUBSTILIUM_WOOD.get())
                .add(ModBlocks.STRIPPED_SUBSTILIUM_STEM.get())
                .add(ModBlocks.STRIPPED_SUBSTILIUM_WOOD.get())
                .add(ModBlocks.SUBSTILIUM_PLANKS.get())
                .add(ModBlocks.SUBSTILIUM_STAIRS.get())
                .add(ModBlocks.SUBSTILIUM_SLAB.get())
                .add(ModBlocks.SUBSTILIUM_PRESSURE_PLATE.get())
                .add(ModBlocks.SUBSTILIUM_BUTTON.get())
                .add(ModBlocks.SUBSTILIUM_DOOR.get())
                .add(ModBlocks.SUBSTILIUM_TRAPDOOR.get())
                .add(ModBlocks.SUBSTILIUM_FENCE.get())
                .add(ModBlocks.SUBSTILIUM_FENCE_GATE.get())
                .add(ModBlocks.SUBSTILIUM_SIGN.get())
                .add(ModBlocks.SUBSTILIUM_HANGING_SIGN.get())
                .add(ModBlocks.HICKORY_LOG.get())
                .add(ModBlocks.HICKORY_WOOD.get())
                .add(ModBlocks.STRIPPED_HICKORY_LOG.get())
                .add(ModBlocks.STRIPPED_HICKORY_WOOD.get())
                .add(ModBlocks.HICKORY_PLANKS.get())
                .add(ModBlocks.HICKORY_STAIRS.get())
                .add(ModBlocks.HICKORY_SLAB.get())
                .add(ModBlocks.HICKORY_PRESSURE_PLATE.get())
                .add(ModBlocks.HICKORY_BUTTON.get())
                .add(ModBlocks.HICKORY_DOOR.get())
                .add(ModBlocks.HICKORY_TRAPDOOR.get())
                .add(ModBlocks.HICKORY_FENCE.get())
                .add(ModBlocks.HICKORY_FENCE_GATE.get())
                .add(ModBlocks.HICKORY_SIGN.get())
                .add(ModBlocks.HICKORY_HANGING_SIGN.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.ENTORIUM_ORE.get())
                .add(ModBlocks.OVERGROWN_ENTORIUM_ORE.get());

        this.tag(ModTags.Blocks.SUBSTILIUM_WOODSET)
                .add(ModBlocks.SUBSTILIUM_STEM.get())
                .add(ModBlocks.SUBSTILIUM_WOOD.get())
                .add(ModBlocks.STRIPPED_SUBSTILIUM_STEM.get())
                .add(ModBlocks.STRIPPED_SUBSTILIUM_WOOD.get())
                .add(ModBlocks.SUBSTILIUM_WOOD.get())
                .add(ModBlocks.SUBSTILIUM_PLANKS.get())
                .add(ModBlocks.SUBSTILIUM_STAIRS.get())
                .add(ModBlocks.SUBSTILIUM_SLAB.get())
                .add(ModBlocks.SUBSTILIUM_PRESSURE_PLATE.get())
                .add(ModBlocks.SUBSTILIUM_BUTTON.get())
                .add(ModBlocks.SUBSTILIUM_DOOR.get())
                .add(ModBlocks.SUBSTILIUM_TRAPDOOR.get())
                .add(ModBlocks.SUBSTILIUM_FENCE.get())
                .add(ModBlocks.SUBSTILIUM_FENCE_GATE.get())
                .add(ModBlocks.SUBSTILIUM_SIGN.get())
                .add(ModBlocks.SUBSTILIUM_HANGING_SIGN.get());

        //

        this.tag(BlockTags.FENCES)
                .add(ModBlocks.SUBSTILIUM_FENCE.get())
                .add(ModBlocks.HICKORY_FENCE.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.SUBSTILIUM_FENCE_GATE.get())
                .add(ModBlocks.HICKORY_FENCE_GATE.get());
        this.tag(BlockTags.WALLS)
                .add(ModBlocks.SUBSTILIUM_TILE_WALLS.get());

        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.SUBSTILIUM_STEM.get())
                .add(ModBlocks.STRIPPED_SUBSTILIUM_STEM.get())
                .add(ModBlocks.SUBSTILIUM_WOOD.get())
                .add(ModBlocks.STRIPPED_SUBSTILIUM_WOOD.get())
                .add(ModBlocks.HICKORY_LOG.get())
                .add(ModBlocks.STRIPPED_HICKORY_LOG.get())
                .add(ModBlocks.HICKORY_WOOD.get())
                .add(ModBlocks.STRIPPED_HICKORY_WOOD.get());

        this.tag(BlockTags.OVERWORLD_NATURAL_LOGS)
                .add(ModBlocks.HICKORY_LOG.get())
                .add(ModBlocks.STRIPPED_HICKORY_LOG.get())
                .add(ModBlocks.HICKORY_WOOD.get())
                .add(ModBlocks.STRIPPED_HICKORY_WOOD.get());

        this.tag(BlockTags.PLANKS)
                .add(ModBlocks.SUBSTILIUM_PLANKS.get())
                .add(ModBlocks.HICKORY_PLANKS.get());

        this.tag(BlockTags.LEAVES)
                .add(ModBlocks.HICKORY_LEAVES.get())
                .add(ModBlocks.RED_GLOWING_HICKORY_LEAVES.get())
                .add(ModBlocks.BROWN_GLOWING_HICKORY_LEAVES.get())
                .add(ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES.get())
                .add(ModBlocks.GREEN_GLOWING_HICKORY_LEAVES.get());

        this.tag(BlockTags.MUSHROOM_GROW_BLOCK)
                .add(ModBlocks.SUBSTILIUM_SOIL.get());
    }
}