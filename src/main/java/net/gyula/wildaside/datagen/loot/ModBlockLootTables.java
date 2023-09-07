package net.gyula.wildaside.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.gyula.wildaside.block.ModBlocks;
import net.gyula.wildaside.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.VIBRION_GEL.get());

        //silktoutch
        this.add(ModBlocks.VIBRION_BLOCK.get(),
                block -> createCopperLikeOreDrops(ModBlocks.VIBRION_BLOCK.get(), ModItems.VIBRION.get()));
        this.add(ModBlocks.VIBRION_GLASS.get(),
                block -> createCopperLikeOreDrops(ModBlocks.VIBRION_GLASS.get(), Item.byBlock(Blocks.AIR)));
        this.add(ModBlocks.LIT_VIBRION_GLASS.get(),
                block -> createCopperLikeOreDrops(ModBlocks.LIT_VIBRION_GLASS.get(), Item.byBlock(Blocks.AIR)));
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}