package net.gyula.wildaside.block;

import net.gyula.wildaside.WildAside;
import net.gyula.wildaside.block.custom.VibrionGel;
import net.gyula.wildaside.block.custom.VibrionGlass;
import net.gyula.wildaside.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, WildAside.MOD_ID);

    public static final RegistryObject<Block> VIBRION_BLOCK = registerBlock("vibrion_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.SHROOMLIGHT).strength(2.3f).explosionResistance(1f).lightLevel(s -> 7).mapColor(MapColor.COLOR_YELLOW)));

    public static final RegistryObject<Block> VIBRION_GEL = registerBlock("vibrion_gel",
            () -> new VibrionGel(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.SHROOMLIGHT).strength(0.1f).explosionResistance(0f).jumpFactor(0.6f).speedFactor(0.2f).mapColor(MapColor.COLOR_YELLOW).noCollission().noOcclusion()));

    public static final RegistryObject<Block> VIBRION_GLASS = registerBlock("vibrion_glass",
            () -> new VibrionGlass(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.SHROOMLIGHT).strength(0.4f).explosionResistance(0.4f).mapColor(MapColor.COLOR_YELLOW).noOcclusion()));
    public static final RegistryObject<Block> LIT_VIBRION_GLASS = registerBlock("lit_vibrion_glass",
            () -> new VibrionGlass(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.SHROOMLIGHT).strength(0.4f).explosionResistance(0.4f).mapColor(MapColor.COLOR_YELLOW).noOcclusion().lightLevel(s -> 7)));

    public static final RegistryObject<Block> SUBSTILIUM_SOIL = registerBlock("substilium_soil",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.SOUL_SOIL).noLootTable()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}