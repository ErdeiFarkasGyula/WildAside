package net.gyula.wildaside.item;

import net.gyula.wildaside.WildAside;
import net.gyula.wildaside.block.ModBlocks;
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

    public static final RegistryObject<CreativeModeTab> WILDASIDE_TAB = CREATIVE_MODE_TABS.register("wildaside_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.VIBRION.get()))
                    .title(Component.translatable("creativetab.wildaside_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModItems.VIBRION.get());

                        pOutput.accept(ModItems.ENTORIUM.get());
                        pOutput.accept(ModItems.ENTORIUM_PILL.get());
                        pOutput.accept(ModItems.ENTORIUM_SPOREBOMB.get());
                        pOutput.accept(ModItems.ENTORIUM_GAUNTLET.get());

                        pOutput.accept(ModItems.HICKORY_NUT.get());

                        pOutput.accept(ModItems.RED_GLOWING_ESSENCE.get());
                        pOutput.accept(ModItems.BROWN_GLOWING_ESSENCE.get());
                        pOutput.accept(ModItems.YELLOW_GLOWING_ESSENCE.get());
                        pOutput.accept(ModItems.GREEN_GLOWING_ESSENCE.get());


                        pOutput.accept(ModBlocks.VIBRION_BLOCK.get());
                        pOutput.accept(ModBlocks.VIBRION_GEL.get());
                        pOutput.accept(ModBlocks.VIBRION_GLASS.get());
                        pOutput.accept(ModBlocks.LIT_VIBRION_GLASS.get());

                        pOutput.accept(ModBlocks.SUBSTILIUM_SOIL.get());

                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}