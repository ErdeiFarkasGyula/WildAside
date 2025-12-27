package net.farkas.wildaside.datagen;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
    protected static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();
    protected static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
    private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
    private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }


    @Override
    protected void generate() {
        //DROP-SELF
        this.dropSelf(ModBlocks.COMPRESSED_VIBRION_BLOCK.get());
        this.dropSelf(ModBlocks.VIBRION_GEL.get());
        this.dropSelf(ModBlocks.LIT_VIBRION_GEL.get());
        this.dropSelf(ModBlocks.VIBRION_GROWTH.get());
        this.dropSelf(ModBlocks.HANGING_VIBRION_VINES.get());
        this.dropSelf(ModBlocks.SPORE_BLASTER.get());
        this.dropSelf(ModBlocks.POTION_BLASTER.get());
        this.dropSelf(ModBlocks.WIND_BLASTER.get());
        this.dropSelf(ModBlocks.ENTORIUM_SHROOM.get());
        this.dropSelf(ModBlocks.BIOENGINEERING_WORKSTATION.get());
        this.dropSelf(ModBlocks.BIOFREEZER.get());
        this.dropSelf(ModBlocks.INCUBATOR.get());

        this.dropSelf(ModBlocks.SUBSTILIUM_SOIL.get());
        this.dropSelf(ModBlocks.COMPRESSED_SUBSTILIUM_SOIL.get());
        this.dropSelf(ModBlocks.SMOOTH_SUBSTILIUM_SOIL.get());
        this.dropSelf(ModBlocks.SUBSTILIUM_TILES.get());
        this.dropSelf(ModBlocks.CRACKED_SUBSTILIUM_TILES.get());
        this.dropSelf(ModBlocks.CHISELED_SUBSTILIUM_SOIL.get());
        this.dropSelf(ModBlocks.SUBSTILIUM_STEM.get());
        this.dropSelf(ModBlocks.STRIPPED_SUBSTILIUM_STEM.get());
        this.dropSelf(ModBlocks.SUBSTILIUM_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_SUBSTILIUM_WOOD.get());
        this.dropSelf(ModBlocks.SUBSTILIUM_PLANKS.get());

        this.dropSelf(ModBlocks.SUBSTILIUM_SPROUTS.get());

        this.dropSelf(ModBlocks.HICKORY_LOG.get());
        this.dropSelf(ModBlocks.STRIPPED_HICKORY_LOG.get());
        this.dropSelf(ModBlocks.HICKORY_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_HICKORY_WOOD.get());
        this.dropSelf(ModBlocks.HICKORY_PLANKS.get());

        this.dropSelf(ModBlocks.HICKORY_ROOT_BUSH.get());
        this.dropSelf(ModBlocks.SPOTTED_WINTERGREEN.get());
        this.dropSelf(ModBlocks.PINKSTER_FLOWER.get());

        //STAIRS
        this.dropSelf(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_STAIRS.get());
        this.dropSelf(ModBlocks.SUBSTILIUM_TILE_STAIRS.get());
        this.dropSelf(ModBlocks.SUBSTILIUM_STAIRS.get());
        this.dropSelf(ModBlocks.HICKORY_STAIRS.get());

        //SLAB
        this.add(ModBlocks.SUBSTILIUM_TILE_SLAB.get(), block -> createSlabItemTable(ModBlocks.SUBSTILIUM_TILE_SLAB.get()));
        this.add(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_SLAB.get(), block -> createSlabItemTable(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_SLAB.get()));
        this.add(ModBlocks.SUBSTILIUM_SLAB.get(), block -> createSlabItemTable(ModBlocks.SUBSTILIUM_SLAB.get()));
        this.add(ModBlocks.HICKORY_SLAB.get(), block -> createSlabItemTable(ModBlocks.HICKORY_SLAB.get()));

        //WALLS
        this.dropSelf(ModBlocks.SUBSTILIUM_TILE_WALLS.get());
        this.dropSelf(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_WALLS.get());

        //FENCE
        this.dropSelf(ModBlocks.SUBSTILIUM_FENCE.get());
        this.dropSelf(ModBlocks.HICKORY_FENCE.get());

        //FENCEGATE
        this.dropSelf(ModBlocks.SUBSTILIUM_FENCE_GATE.get());
        this.dropSelf(ModBlocks.HICKORY_FENCE_GATE.get());

        //PRESSUREPLATE
        this.dropSelf(ModBlocks.SUBSTILIUM_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.SUBSTILIUM_TILE_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.HICKORY_PRESSURE_PLATE.get());

        //BUTTON
        this.dropSelf(ModBlocks.SUBSTILIUM_BUTTON.get());
        this.dropSelf(ModBlocks.SMOOTH_SUBSTILIUM_SOIL_BUTTON.get());
        this.dropSelf(ModBlocks.SUBSTILIUM_TILE_BUTTON.get());
        this.dropSelf(ModBlocks.HICKORY_BUTTON.get());

        //DOOR
        this.add(ModBlocks.SUBSTILIUM_DOOR.get(), block -> createDoorTable(ModBlocks.SUBSTILIUM_DOOR.get()));
        this.add(ModBlocks.HICKORY_DOOR.get(), block -> createDoorTable(ModBlocks.HICKORY_DOOR.get()));

        //TRAPDOOR
        this.dropSelf(ModBlocks.SUBSTILIUM_TRAPDOOR.get());
        this.dropSelf(ModBlocks.HICKORY_TRAPDOOR.get());

        //SIGN
        this.add(ModBlocks.SUBSTILIUM_SIGN.get(), block -> createSingleItemTable(ModBlocks.SUBSTILIUM_SIGN.get()));
        this.add(ModBlocks.SUBSTILIUM_WALL_SIGN.get(), block -> createSingleItemTable(ModBlocks.SUBSTILIUM_SIGN.get()));
        this.add(ModBlocks.SUBSTILIUM_HANGING_SIGN.get(), block -> createSingleItemTable(ModBlocks.SUBSTILIUM_HANGING_SIGN.get()));
        this.add(ModBlocks.SUBSTILIUM_WALL_HANGING_SIGN.get(), block -> createSingleItemTable(ModBlocks.SUBSTILIUM_HANGING_SIGN.get()));

        this.add(ModBlocks.HICKORY_SIGN.get(), block -> createSingleItemTable(ModBlocks.HICKORY_SIGN.get()));
        this.add(ModBlocks.HICKORY_WALL_SIGN.get(), block -> createSingleItemTable(ModBlocks.HICKORY_SIGN.get()));
        this.add(ModBlocks.HICKORY_HANGING_SIGN.get(), block -> createSingleItemTable(ModBlocks.HICKORY_HANGING_SIGN.get()));
        this.add(ModBlocks.HICKORY_WALL_HANGING_SIGN.get(), block -> createSingleItemTable(ModBlocks.HICKORY_HANGING_SIGN.get()));

        //SILKTOUCH
        this.add(ModBlocks.VIBRION_BLOCK.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.VIBRION_BLOCK.get(), ModItems.VIBRION.get(), 1, 3));
        this.add(ModBlocks.VIBRION_SPOREHOLDER.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.VIBRION_SPOREHOLDER.get(), ModItems.VIBRION.get(), 1, 2));

        this.add(ModBlocks.VIBRION_GLASS.get(), block -> createSilkTouchOnlyTable(ModBlocks.VIBRION_GLASS.get()));
        this.add(ModBlocks.LIT_VIBRION_GLASS.get(), block -> createSilkTouchOnlyTable(ModBlocks.LIT_VIBRION_GLASS.get()));
        this.add(ModBlocks.VIBRION_GLASS_PANE.get(), block -> createSilkTouchOnlyTable(ModBlocks.VIBRION_GLASS_PANE.get()));
        this.add(ModBlocks.LIT_VIBRION_GLASS_PANE.get(), block -> createSilkTouchOnlyTable(ModBlocks.LIT_VIBRION_GLASS_PANE.get()));
        this.add(ModBlocks.NATURAL_SPORE_BLASTER.get(), block -> createSilkTouchOnlyTable(ModBlocks.NATURAL_SPORE_BLASTER.get()));
        this.add(ModBlocks.OVERGROWN_ENTORIUM_ORE.get(), block -> createSilkTouchOnlyTable(ModBlocks.OVERGROWN_ENTORIUM_ORE.get()));

        //ORE
        this.add(ModBlocks.ENTORIUM_ORE.get(), block -> createOreDrop(ModBlocks.ENTORIUM_ORE.get(), ModItems.ENTORIUM.get()));

        this.add(ModBlocks.SUBSTILIUM_COAL_ORE.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.SUBSTILIUM_COAL_ORE.get(), Items.COAL, 1, 2));
        this.add(ModBlocks.SUBSTILIUM_COPPER_ORE.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.SUBSTILIUM_COPPER_ORE.get(), Items.RAW_COPPER, 2, 6));
        this.add(ModBlocks.SUBSTILIUM_LAPIS_ORE.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.SUBSTILIUM_LAPIS_ORE.get(), Items.LAPIS_LAZULI, 4, 10));
        this.add(ModBlocks.SUBSTILIUM_IRON_ORE.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.SUBSTILIUM_IRON_ORE.get(), Items.RAW_IRON, 1, 2));
        this.add(ModBlocks.SUBSTILIUM_GOLD_ORE.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.SUBSTILIUM_GOLD_ORE.get(), Items.RAW_GOLD, 1, 2));
        this.add(ModBlocks.SUBSTILIUM_REDSTONE_ORE.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.SUBSTILIUM_REDSTONE_ORE.get(), Items.REDSTONE, 4, 6));
        this.add(ModBlocks.SUBSTILIUM_DIAMOND_ORE.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.SUBSTILIUM_DIAMOND_ORE.get(), Items.DIAMOND, 1, 2));
        this.add(ModBlocks.SUBSTILIUM_EMERALD_ORE.get(),
                block -> createSilktouchedFortuneDrops(ModBlocks.SUBSTILIUM_EMERALD_ORE.get(), Items.EMERALD, 1, 2));

        //FLOWERPOT
        this.add(ModBlocks.POTTED_VIBRION_GROWTH.get(), createPotFlowerItemTable(ModBlocks.VIBRION_GROWTH.get()));
        this.add(ModBlocks.POTTED_SPOTTED_WINTERGREEN.get(), createPotFlowerItemTable(ModBlocks.SPOTTED_WINTERGREEN.get()));
        this.add(ModBlocks.POTTED_PINKSTER_FLOWER.get(), createPotFlowerItemTable(ModBlocks.PINKSTER_FLOWER.get()));

        //SINGLE
        this.add(ModBlocks.HANGING_VIBRION_VINES_PLANT.get(), block -> createSingleItemTable(ModBlocks.HANGING_VIBRION_VINES.get()));

        //HICKORY
        for (HickoryColour colour : HickoryColour.values()) {
            this.add(ModBlocks.HICKORY_LEAVES_BLOCKS.get(colour).get(),
                    block -> createBiggerLeavesDrops(block, ModBlocks.HICKORY_SAPLINGS.get(colour).get(), ModItems.HICKORY_NUT.get()));
            this.dropSelf(ModBlocks.HICKORY_SAPLINGS.get(colour).get());
        }

        this.add(ModBlocks.FALLEN_HICKORY_LEAVES.get(), block -> createBiggerLeavesDrops(Blocks.AIR, Blocks.AIR, Items.AIR));
    }

    protected LootTable.Builder createSilktouchedFortuneDrops(Block pBlock, Item item, int min, int max) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));

    }

    protected LootTable.Builder createSilktouchedFortuneDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    protected LootTable.Builder createSilktouchedDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    protected LootTable.Builder createBiggerLeavesDrops(Block leaves, Block sapling, Item otherDrop) {
        return createLeavesDrops(leaves, sapling, NORMAL_LEAVES_SAPLING_CHANCES)
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
                        .add(this.applyExplosionCondition(leaves, LootItem.lootTableItem(otherDrop))
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.15f, 0.2f, 0.3f, 0.35f))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}