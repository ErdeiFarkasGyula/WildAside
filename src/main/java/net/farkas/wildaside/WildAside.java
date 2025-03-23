package net.farkas.wildaside;

import com.mojang.logging.LogUtils;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.entity.ModBlockEntities;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.entity.client.ModBoatRenderer;
import net.farkas.wildaside.item.ModCreativeModeTabs;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.potion.BetterBrewingRecipe;
import net.farkas.wildaside.potion.ModPotions;
import net.farkas.wildaside.recipe.ModRecipes;
import net.farkas.wildaside.screen.BioengineeringWorkstationScreen;
import net.farkas.wildaside.screen.ModMenuTypes;
import net.farkas.wildaside.util.ModWoodTypes;
import net.farkas.wildaside.worldgen.biome.ModTerraBlenderAPI;
import net.farkas.wildaside.worldgen.biome.surface.ModSurfaceRules;
import net.farkas.wildaside.worldgen.feature.ModFeatures;
import net.farkas.wildaside.worldgen.feature.ModFoliagePlacers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

@Mod(WildAside.MOD_ID)
public class WildAside
{
    public static final String MOD_ID = "wildaside";
    private static final Logger LOGGER = LogUtils.getLogger();

    public WildAside(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        ModMobEffects.register(modEventBus);
        ModPotions.register(modEventBus);

        ModParticles.register(modEventBus);

        ModFeatures.register(modEventBus);
        ModTerraBlenderAPI.registerRegions();

        ModFoliagePlacers.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

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
        ComposterBlock.COMPOSTABLES.put(ModBlocks.HICKORY_SAPLING.get().asItem(), 0.3f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.RED_GLOWING_HICKORY_SAPLING.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING.get().asItem(), 0.5f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.SPOTTED_WINTERGREEN.get().asItem(), 0.65f);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.PINKSTER_FLOWER.get().asItem(), 0.65f);
        ComposterBlock.COMPOSTABLES.put(ModItems.HICKORY_NUT.get(), 0.65f);

        BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(Potions.AWKWARD, ModItems.VIBRION.get(), ModPotions.CONTAMINATION_POTION.get()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

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

            MenuScreens.register(ModMenuTypes.BIOENGINEERING_WORKSTATION_MENU.get(), BioengineeringWorkstationScreen::new);

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIBRION_GLASS_PANE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LIT_VIBRION_GLASS_PANE.get(), RenderType.translucent());
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class ServerModEvents
    {

    }
}