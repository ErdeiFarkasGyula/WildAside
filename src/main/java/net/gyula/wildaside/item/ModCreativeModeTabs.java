package net.gyula.wildaside.item;

import net.gyula.wildaside.WildAside;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WildAside.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS.register("wildaside_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.VIBRION.get()))
                    .title(Component.translatable("creativetab.wildaside_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModItems.VIBRION.get());
                        pOutput.accept(ModItems.HICKORY_NUT.get());

                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}