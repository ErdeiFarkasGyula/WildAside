package net.farkas.wildaside.item;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.entity.custom.ModBoatEntity;
import net.farkas.wildaside.item.custom.*;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WildAside.MOD_ID);

    public static final RegistryObject<Item> VIBRION = ITEMS.register("vibrion",
            () ->  new Vibrion(new Item.Properties().food(ModFoods.VIBRION)));
    public static final RegistryObject<Item> ENTORIUM = ITEMS.register("entorium",
            () ->  new Item(new Item.Properties()));

    public static final RegistryObject<Item> MUCELLITH_SPAWN_EGG = ITEMS.register("mucellith_spawn_egg",
            () ->  new ForgeSpawnEggItem(ModEntities.MUCELLITH, 0xc8e3ff, 0xfff571, new Item.Properties()));
    public static final RegistryObject<Item> MUCELLITH_JAW = ITEMS.register("mucellith_jaw",
            () ->  new Item(new Item.Properties()));
    public static final RegistryObject<Item> CONTAMINATED_CREEPER_SPAWN_EGG = ITEMS.register("contaminated_creeper_spawn_egg",
            () ->  new ForgeSpawnEggItem(ModEntities.CONTAMINATED_CREEPER, 0x0db50d, 0xfff571, new Item.Properties()));

    public static final RegistryObject<Item> ENTORIUM_PILL = ITEMS.register("entorium_pill",
            () ->  new EntoriumPill(new Item.Properties().food(ModFoods.ENTORIUM_PILL).stacksTo(16)));
    public static final RegistryObject<Item> SPORE_ARROW = ITEMS.register("spore_arrow",
            () ->  new SporeArrow(new Item.Properties()));
    public static final RegistryObject<Item> SPORE_BOMB = ITEMS.register("spore_bomb",
            () ->  new SporeBomb(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> FERTILISER_BOMB = ITEMS.register("fertiliser_bomb",
            () ->  new FertiliserBomb(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> SUBSTILIUM_SIGN = ITEMS.register("substilium_sign",
            () ->  new SignItem(new Item.Properties().stacksTo(16), ModBlocks.SUBSTILIUM_SIGN.get(), ModBlocks.SUBSTILIUM_WALL_SIGN.get()));
    public static final RegistryObject<Item> SUBSTILIUM_HANGING_SIGN = ITEMS.register("substilium_hanging_sign",
            () ->  new HangingSignItem(ModBlocks.SUBSTILIUM_HANGING_SIGN.get(), ModBlocks.SUBSTILIUM_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> SUBSTILIUM_BOAT = ITEMS.register("substilium_boat",
            () ->  new ModBoatItem(false, ModBoatEntity.Type.SUBSTILIUM, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SUBSTILIUM_CHEST_BOAT = ITEMS.register("substilium_chest_boat",
            () ->  new ModBoatItem(true, ModBoatEntity.Type.SUBSTILIUM, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HICKORY_SIGN = ITEMS.register("hickory_sign",
            () ->  new SignItem(new Item.Properties().stacksTo(16), ModBlocks.HICKORY_SIGN.get(), ModBlocks.HICKORY_WALL_SIGN.get()));
    public static final RegistryObject<Item> HICKORY_HANGING_SIGN = ITEMS.register("hickory_hanging_sign",
            () ->  new HangingSignItem(ModBlocks.HICKORY_HANGING_SIGN.get(), ModBlocks.HICKORY_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> HICKORY_BOAT = ITEMS.register("hickory_boat",
            () ->  new ModBoatItem(false, ModBoatEntity.Type.HICKORY, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HICKORY_CHEST_BOAT = ITEMS.register("hickory_chest_boat",
            () ->  new ModBoatItem(true, ModBoatEntity.Type.HICKORY, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HICKORY_NUT = ITEMS.register("hickory_nut",
            () ->  new FuelItem(new Item.Properties().food(ModFoods.HICKORY_NUT), 100));

    public static final RegistryObject<Item> HICKORY_LEAF = ITEMS.register("hickory_leaf",
            () ->  new HickoryLeafItem(new Item.Properties(), 50, HickoryColour.HICKORY));
    public static final RegistryObject<Item> RED_GLOWING_HICKORY_LEAF = ITEMS.register("red_glowing_hickory_leaf",
            () ->  new HickoryLeafItem(new Item.Properties(), 50, HickoryColour.RED_GLOWING));
    public static final RegistryObject<Item> BROWN_GLOWING_HICKORY_LEAF = ITEMS.register("brown_glowing_hickory_leaf",
            () ->  new HickoryLeafItem(new Item.Properties(), 50, HickoryColour.BROWN_GLOWING));
    public static final RegistryObject<Item> YELLOW_GLOWING_HICKORY_LEAF = ITEMS.register("yellow_glowing_hickory_leaf",
            () ->  new HickoryLeafItem(new Item.Properties(), 50, HickoryColour.YELLOW_GLOWING));
    public static final RegistryObject<Item> GREEN_GLOWING_HICKORY_LEAF = ITEMS.register("green_glowing_hickory_leaf",
            () ->  new HickoryLeafItem(new Item.Properties(), 50, HickoryColour.GREEN_GLOWING));

    public static final EnumMap<HickoryColour, RegistryObject<Item>> LEAF_ITEMS = new EnumMap<>(HickoryColour.class);
    static {
        LEAF_ITEMS.put(HickoryColour.HICKORY, HICKORY_LEAF);
        LEAF_ITEMS.put(HickoryColour.RED_GLOWING, RED_GLOWING_HICKORY_LEAF);
        LEAF_ITEMS.put(HickoryColour.BROWN_GLOWING, BROWN_GLOWING_HICKORY_LEAF);
        LEAF_ITEMS.put(HickoryColour.YELLOW_GLOWING, YELLOW_GLOWING_HICKORY_LEAF);
        LEAF_ITEMS.put(HickoryColour.GREEN_GLOWING, GREEN_GLOWING_HICKORY_LEAF);
    }

    public static final RegistryObject<Item> HICKORY_NUT_TRAIL_MIX = ITEMS.register("hickory_nut_trail_mix",
            () ->  new HickoryNutTrailMix(new Item.Properties().stacksTo(1).food(ModFoods.HICKORY_NUT_TRAIL_MIX), HickoryColour.HICKORY));
    public static final RegistryObject<Item> RED_HICKORY_NUT_TRAIL_MIX = ITEMS.register("red_hickory_nut_trail_mix",
            () ->  new HickoryNutTrailMix(new Item.Properties().stacksTo(1).food(ModFoods.HICKORY_NUT_TRAIL_MIX), HickoryColour.RED_GLOWING));
    public static final RegistryObject<Item> BROWN_HICKORY_NUT_TRAIL_MIX = ITEMS.register("brown_hickory_nut_trail_mix",
            () ->  new HickoryNutTrailMix(new Item.Properties().stacksTo(1).food(ModFoods.HICKORY_NUT_TRAIL_MIX), HickoryColour.BROWN_GLOWING));
    public static final RegistryObject<Item> YELLOW_HICKORY_NUT_TRAIL_MIX = ITEMS.register("yellow_hickory_nut_trail_mix",
            () ->  new HickoryNutTrailMix(new Item.Properties().stacksTo(1).food(ModFoods.HICKORY_NUT_TRAIL_MIX), HickoryColour.YELLOW_GLOWING));
    public static final RegistryObject<Item> GREEN_HICKORY_NUT_TRAIL_MIX = ITEMS.register("green_hickory_nut_trail_mix",
            () ->  new HickoryNutTrailMix(new Item.Properties().stacksTo(1).food(ModFoods.HICKORY_NUT_TRAIL_MIX), HickoryColour.GREEN_GLOWING));

    public static final EnumMap<HickoryColour, RegistryObject<Item>> TRAIL_MIX_ITEMS = new EnumMap<>(HickoryColour.class);
    static {
        TRAIL_MIX_ITEMS.put(HickoryColour.HICKORY, HICKORY_NUT_TRAIL_MIX);
        TRAIL_MIX_ITEMS.put(HickoryColour.RED_GLOWING, RED_HICKORY_NUT_TRAIL_MIX);
        TRAIL_MIX_ITEMS.put(HickoryColour.BROWN_GLOWING, BROWN_HICKORY_NUT_TRAIL_MIX);
        TRAIL_MIX_ITEMS.put(HickoryColour.YELLOW_GLOWING, YELLOW_HICKORY_NUT_TRAIL_MIX);
        TRAIL_MIX_ITEMS.put(HickoryColour.GREEN_GLOWING, GREEN_HICKORY_NUT_TRAIL_MIX);
    }

    public static final RegistryObject<Item> HICKORY_TREANT_SPAWN_EGG = ITEMS.register("hickory_treant_spawn_egg",
            () ->  new ForgeSpawnEggItem(ModEntities.HICKORY_TREANT, 0x704626, 0x409312, new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
