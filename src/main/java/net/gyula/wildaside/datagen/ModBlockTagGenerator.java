package net.gyula.wildaside.datagen;

import net.gyula.wildaside.WildAside;
import net.gyula.wildaside.block.ModBlocks;
import net.gyula.wildaside.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
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
        this.tag(ModTags.Blocks.WILDASIDE_ITEMS)
                .add(ModBlocks.VIBRION_BLOCK.get(),
                        ModBlocks.VIBRION_GEL.get(),
                        ModBlocks.VIBRION_GLASS.get(),
                        ModBlocks.LIT_VIBRION_GLASS.get(),
                        ModBlocks.SUBSTILIUM_SOIL.get())
                .addTag(ModTags.Blocks.WILDASIDE_ITEMS);

        this.tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.VIBRION_BLOCK.get(),
                        ModBlocks.VIBRION_GEL.get());

    }
}