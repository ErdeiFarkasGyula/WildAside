package net.gyula.wildaside.datagen;

import net.gyula.wildaside.WildAside;
import net.gyula.wildaside.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WildAside.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.VIBRION);

        simpleItem(ModItems.ENTORIUM);
        simpleItem(ModItems.ENTORIUM_GAUNTLET);
        simpleItem(ModItems.ENTORIUM_PILL);
        simpleItem(ModItems.ENTORIUM_SPOREBOMB);

        simpleItem(ModItems.RED_GLOWING_ESSENCE);
        simpleItem(ModItems.BROWN_GLOWING_ESSENCE);
        simpleItem(ModItems.GREEN_GLOWING_ESSENCE);
        simpleItem(ModItems.YELLOW_GLOWING_ESSENCE);

        simpleItem(ModItems.HICKORY_NUT);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(WildAside.MOD_ID,"item/" + item.getId().getPath()));
    }

}