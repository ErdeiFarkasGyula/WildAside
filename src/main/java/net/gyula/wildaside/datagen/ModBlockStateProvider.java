package net.gyula.wildaside.datagen;

import net.gyula.wildaside.WildAside;
import net.gyula.wildaside.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WildAside.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.VIBRION_BLOCK);
        blockWithItem(ModBlocks.VIBRION_GEL);
        blockWithItem(ModBlocks.VIBRION_GLASS);
        blockWithItem(ModBlocks.LIT_VIBRION_GLASS);
        blockWithItem(ModBlocks.SUBSTILIUM_SOIL);

//        customPaneBlock(ModBlocks.VIBRION_GLASS_PANE);
//        customPaneBlock(ModBlocks.LIT_VIBRION_GLASS_PANE);
    }

//    private void customPaneBlock(RegistryObject<Block> blockRegistryObject) {
//        paneBlock((IronBarsBlock) blockRegistryObject.get(), String.valueOf(blockRegistryObject.get().getName()), blockRegistryObject.getId(), blockRegistryObject.getId());
//    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void customPaneBlock(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge) {
        paneBlock(block, String.valueOf(block.getName()), pane, edge);
    }
}