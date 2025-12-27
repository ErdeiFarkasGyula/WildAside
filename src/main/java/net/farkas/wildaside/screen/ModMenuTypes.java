package net.farkas.wildaside.screen;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationMenu;
import net.farkas.wildaside.screen.biofreezer.BiofreezerMenu;
import net.farkas.wildaside.screen.incubator.IncubatorMenu;
import net.farkas.wildaside.screen.potion_blaster.PotionBlasterMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, WildAside.MOD_ID);

    public static final RegistryObject<MenuType<BioengineeringWorkstationMenu>> BIOENGINEERING_WORKSTATION_MENU =
            registerMenuType("bioengineering_workstation_menu", BioengineeringWorkstationMenu::new);

    public static final RegistryObject<MenuType<BiofreezerMenu>> BIOFREEZER_MENU =
            registerMenuType("biofreezer_menu", BiofreezerMenu::new);

    public static final RegistryObject<MenuType<IncubatorMenu>> INCUBATOR =
            registerMenuType("incubator_menu", IncubatorMenu::new);

    public static final RegistryObject<MenuType<PotionBlasterMenu>> POTION_BLASTER_MENU =
            registerMenuType("potion_blaster_menu", PotionBlasterMenu::new);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}