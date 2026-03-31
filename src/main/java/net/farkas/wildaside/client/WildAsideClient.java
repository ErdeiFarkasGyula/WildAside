package net.farkas.wildaside.client;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.entity.ModEntityTypes;
import net.farkas.wildaside.entity.client.ModBoatRenderer;
import net.farkas.wildaside.entity.client.vibrion.BacillusBlobEntityRenderer;
import net.farkas.wildaside.entity.client.vibrion.ContaminatedCreeperRenderer;
import net.farkas.wildaside.entity.client.vibrion.MucellithRenderer;
import net.farkas.wildaside.entity.custom.vibrion.SporeArrowEntity;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.custom.DnaHolderItem;
import net.farkas.wildaside.item.custom.SyringeItem;
import net.farkas.wildaside.screen.ModMenuTypes;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationScreen;
import net.farkas.wildaside.screen.biofreezer.BiofreezerScreen;
import net.farkas.wildaside.screen.incubator.IncubatorScreen;
import net.farkas.wildaside.screen.potion_blaster.PotionBlasterScreen;
import net.farkas.wildaside.util.ModWoodTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.farkas.wildaside.dna.DnaConstants.*;
import static net.farkas.wildaside.dna.DnaConstants.FLUID_LEVEL;

public class WildAsideClient {
    public static void init(FMLClientSetupEvent event) {
        registerWoodTypes();
        registerEntityRenderers();
        registerScreens();
        setRenderTypes();
        registerItemProperties();
    }

    private static void registerWoodTypes() {
        Sheets.addWoodType(ModWoodTypes.SUBSTILIUM);
        Sheets.addWoodType(ModWoodTypes.HICKORY);
        Sheets.addWoodType(ModWoodTypes.CYPRESS);
    }

    private static void registerEntityRenderers() {
        EntityRenderers.register(ModEntityTypes.MOD_BOAT.get(), pContext -> new ModBoatRenderer(pContext, false));
        EntityRenderers.register(ModEntityTypes.MOD_CHEST_BOAT.get(), pContext -> new ModBoatRenderer(pContext, true));
        EntityRenderers.register(ModEntityTypes.SPORE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntityTypes.FERTILISER_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(ModEntityTypes.SPORE_ARROW.get(), pContext -> new ArrowRenderer<SporeArrowEntity>(pContext) {
            @Override
            public ResourceLocation getTextureLocation(SporeArrowEntity pEntity) {
                return new ResourceLocation(WildAside.MOD_ID, "textures/entity/projectiles/spore_arrow.png");
            }
        });
        EntityRenderers.register(ModEntityTypes.MUCELLITH.get(), MucellithRenderer::new);
        EntityRenderers.register(ModEntityTypes.CONTAMINATED_CREEPER.get(), ContaminatedCreeperRenderer::new);
        EntityRenderers.register(ModEntityTypes.BACILLUS_BLOB.get(), BacillusBlobEntityRenderer::new);
    }

    private static void registerScreens() {
        MenuScreens.register(ModMenuTypes.BIOENGINEERING_WORKSTATION.get(), BioengineeringWorkstationScreen::new);
        MenuScreens.register(ModMenuTypes.BIOFREEZER.get(), BiofreezerScreen::new);
        MenuScreens.register(ModMenuTypes.INCUBATOR.get(), IncubatorScreen::new);
        MenuScreens.register(ModMenuTypes.POTION_BLASTER.get(), PotionBlasterScreen::new);
    }

    private static void setRenderTypes() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIBRION_GLASS_PANE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.LIT_VIBRION_GLASS_PANE.get(), RenderType.translucent());

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.FALLEN_HICKORY_LEAVES.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.HICKORY_ROOT_BUSH.get(), RenderType.cutout());
    }

    private static void registerItemProperties() {
        ItemProperties.register(
                ModItems.DNA_HOLDER.get(),
                new ResourceLocation(WildAside.MOD_ID, SAMPLE_PROGRESS),
                (stack, level, entity, seed) -> {
                    if (!stack.hasTag()) return 0f;
                    CompoundTag tag = stack.getTag();
                    if (tag.contains(SAMPLE_PROGRESS)) {
                        int progress = tag.getInt(SAMPLE_PROGRESS);
                        return Mth.clamp((float) progress / DnaHolderItem.DEFAULT_MAX_SAMPLES, 0f, 1f);
                    }
                    return 0f;
                }
        );

        ItemProperties.register(
                ModItems.SYRINGE.get(),
                new ResourceLocation(WildAside.MOD_ID, SYRINGE_PROGRESS),
                (stack, level, entity, seed) -> {
                    if (!stack.hasTag()) return 0f;
                    float p = stack.getTag().getFloat(SYRINGE_PROGRESS);
                    return Mth.clamp(p / SyringeItem.DEFAULT_MAX_LOAD, 0f, 1f);
                }
        );

        ItemProperties.register(
                ModItems.SYRINGE.get(),
                new ResourceLocation(WildAside.MOD_ID, FLUID_LEVEL),
                (stack, level, entity, seed) -> {
                    if (!stack.hasTag()) return 0f;
                    float p = stack.getTag().getFloat(FLUID_LEVEL);
                    return Mth.clamp(p / SyringeItem.DEFAULT_MAX_LOAD, 0f, 1f);
                }
        );

        ItemProperties.register(
                ModItems.GENE.get(),
                new ResourceLocation(WildAside.MOD_ID, "flipped"),
                (stack, level, entity, seed) -> {
                    if (!stack.hasTag()) return 0f;
                    return stack.getTag().getBoolean("flipped") ? 1f : 0f;
                }
        );
    }
}