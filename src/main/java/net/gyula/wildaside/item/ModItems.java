package net.gyula.wildaside.item;

import net.gyula.wildaside.WildAside;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WildAside.MOD_ID);

    public static final RegistryObject<Item> VIBRION = ITEMS.register("vibrion",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENTORIUM = ITEMS.register("entorium",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENTORIUM_PILL = ITEMS.register("entorium_pill",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENTORIUM_SPOREBOMB = ITEMS.register("entorium_sporebomb",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENTORIUM_GAUNTLET = ITEMS.register("entorium_gauntlet",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RED_GLOWING_ESSENCE = ITEMS.register("red_glowing_essence",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BROWN_GLOWING_ESSENCE = ITEMS.register("brown_glowing_essence",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_GLOWING_ESSENCE = ITEMS.register("green_glowing_essence",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_GLOWING_ESSENCE = ITEMS.register("yellow_glowing_essence",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RUTILLION = ITEMS.register("rutillion",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HICKORY_NUT = ITEMS.register("hickory_nut",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}