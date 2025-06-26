package net.farkas.wildaside;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.entity.ModBlockEntities;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.enchantment.ModEnchantmentEffects;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.entity.client.ModBoatRenderer;
import net.farkas.wildaside.entity.client.vibrion.MucellithRenderer;
import net.farkas.wildaside.entity.custom.vibrion.SporeArrowEntity;
import net.farkas.wildaside.item.ModCreativeModeTabs;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.VanillaCreativeTabs;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.potion.ModPotions;
import net.farkas.wildaside.recipe.ModRecipes;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationScreen;
import net.farkas.wildaside.screen.ModMenuTypes;
import net.farkas.wildaside.screen.potion_blaster.PotionBlasterScreen;
import net.farkas.wildaside.sound.ModSounds;
import net.farkas.wildaside.util.ModWoodTypes;
import net.farkas.wildaside.worldgen.biome.ModTerraBlenderAPI;
import net.farkas.wildaside.worldgen.biome.surface.ModSurfaceRules;
import net.farkas.wildaside.worldgen.feature.ModFeatures;
import net.farkas.wildaside.worldgen.feature.ModFoliagePlacers;
import net.farkas.wildaside.worldgen.feature.decorator.ModTreeDecorators;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import terrablender.api.SurfaceRuleManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WildAside.MOD_ID)
public class WildAside
{
    public static final String MOD_ID = "wildaside";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public WildAside(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModSounds.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        ModMobEffects.register(modEventBus);
        ModPotions.register(modEventBus);

        ModParticles.register(modEventBus);

        ModTreeDecorators.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModTerraBlenderAPI.registerRegions();

        ModFoliagePlacers.register(modEventBus);

        ModEnchantmentEffects.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(VanillaCreativeTabs::addCreative);

        //context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.VIBRION_GROWTH.getId(), ModBlocks.POTTED_VIBRION_GROWTH);
        });

        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeRules());

        ComposterBlock.COMPOSTABLES.put(ModItems.VIBRION.get(), 0.25f);
        ComposterBlock.COMPOSTABLES.put(ModItems.ENTORIUM.get(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.VIBRION_BLOCK.get().asItem(), 1);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.COMPRESSED_VIBRION_BLOCK.get().asItem(), 1);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.VIBRION_GEL.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.LIT_VIBRION_GEL.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.VIBRION_GROWTH.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.SUBSTILIUM_SPROUTS.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.ENTORIUM_SHROOM.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.VIBRION_SPOREHOLDER.get().asItem(), 1f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.HANGING_VIBRION_VINES.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.HICKORY_LEAVES.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.RED_GLOWING_HICKORY_LEAVES.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.BROWN_GLOWING_HICKORY_LEAVES.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.YELLOW_GLOWING_HICKORY_LEAVES.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.GREEN_GLOWING_HICKORY_LEAVES.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModItems.HICKORY_LEAF.get(), 0.1f);
        ComposterBlock.COMPOSTABLES.put(ModItems.RED_GLOWING_HICKORY_LEAF.get(), 0.15f);
        ComposterBlock.COMPOSTABLES.put(ModItems.BROWN_GLOWING_HICKORY_LEAF.get(), 0.15f);
        ComposterBlock.COMPOSTABLES.put(ModItems.YELLOW_GLOWING_HICKORY_LEAF.get(), 0.15f);
        ComposterBlock.COMPOSTABLES.put(ModItems.GREEN_GLOWING_HICKORY_LEAF.get(), 0.15f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.HICKORY_SAPLING.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.RED_GLOWING_HICKORY_SAPLING.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.SPOTTED_WINTERGREEN.get().asItem(), 0.65f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.PINKSTER_FLOWER.get().asItem(), 0.65f);
        ComposterBlock.COMPOSTABLES.put(ModItems.HICKORY_NUT.get(), 0.65f);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            Sheets.addWoodType(ModWoodTypes.SUBSTILIUM);
            Sheets.addWoodType(ModWoodTypes.HICKORY);
            Sheets.addWoodType(ModWoodTypes.CYPRESS);

            EntityRenderers.register(ModEntities.MOD_BOAT.get(), pContext -> new ModBoatRenderer(pContext, false));
            EntityRenderers.register(ModEntities.MOD_CHEST_BOAT.get(), pContext -> new ModBoatRenderer(pContext, true));
            EntityRenderers.register(ModEntities.SPORE_BOMB.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.FERTILISER_BOMB.get(), ThrownItemRenderer::new);
            EntityRenderers.register(ModEntities.SPORE_ARROW.get(),pContext -> new ArrowRenderer<SporeArrowEntity>(pContext) {
                @Override
                public ResourceLocation getTextureLocation(SporeArrowEntity pEntity) {
                    return ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "textures/entity/projectiles/spore_arrow.png");
                }
            });
            EntityRenderers.register(ModEntities.MUCELLITH.get(), MucellithRenderer::new);

            MenuScreens.register(ModMenuTypes.BIOENGINEERING_WORKSTATION_MENU.get(), BioengineeringWorkstationScreen::new);
            MenuScreens.register(ModMenuTypes.POTION_BLASTER_MENU.get(), PotionBlasterScreen::new);

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIBRION_GLASS_PANE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LIT_VIBRION_GLASS_PANE.get(), RenderType.translucent());

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.FALLEN_HICKORY_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.HICKORY_ROOT_BUSH.get(), RenderType.cutout());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class ServerModEvents
    {

    }
}