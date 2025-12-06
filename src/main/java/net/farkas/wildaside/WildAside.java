package net.farkas.wildaside;

import com.mojang.logging.LogUtils;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.entity.ModBlockEntities;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.enchantment.ModEnchantments;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.entity.client.ModBoatRenderer;
import net.farkas.wildaside.entity.client.vibrion.ContaminatedCreeperRenderer;
import net.farkas.wildaside.entity.client.vibrion.MucellithRenderer;
import net.farkas.wildaside.entity.custom.vibrion.FertiliserBombEntity;
import net.farkas.wildaside.entity.custom.vibrion.SporeArrowEntity;
import net.farkas.wildaside.entity.custom.vibrion.SporeBombEntity;
import net.farkas.wildaside.item.ModCreativeModeTabs;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.VanillaCreativeTabs;
import net.farkas.wildaside.item.custom.DnaHolderItem;
import net.farkas.wildaside.item.custom.SyringeItem;
import net.farkas.wildaside.network.NetworkHandler;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.potion.BetterBrewingRecipe;
import net.farkas.wildaside.potion.ModPotions;
import net.farkas.wildaside.recipe.ModRecipes;
import net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationScreen;
import net.farkas.wildaside.screen.ModMenuTypes;
import net.farkas.wildaside.screen.biofreezer.BiofreezerScreen;
import net.farkas.wildaside.screen.potion_blaster.PotionBlasterScreen;
import net.farkas.wildaside.sound.ModSounds;
import net.farkas.wildaside.util.ModWoodTypes;
import net.farkas.wildaside.worldgen.biome.ModTerraBlenderAPI;
import net.farkas.wildaside.worldgen.biome.surface.ModSurfaceRules;
import net.farkas.wildaside.worldgen.feature.ModFeatures;
import net.farkas.wildaside.worldgen.feature.ModFoliagePlacers;
import net.farkas.wildaside.worldgen.feature.decorator.ModTreeDecorators;
import net.farkas.wildaside.worldgen.modifier.ModPlacementModifiers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

import static net.farkas.wildaside.dna.DnaConstants.*;

@Mod(WildAside.MOD_ID)
public class WildAside
{
    public static final String MOD_ID = "wildaside";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WildAside(FMLJavaModLoadingContext context)
    {
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.COMMON_SPEC);
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, ModConfig.CLIENT_SPEC);

        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);
        ModSounds.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEnchantments.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMobEffects.register(modEventBus);
        ModPotions.register(modEventBus);
        ModParticles.register(modEventBus);
        ModPlacementModifiers.register(modEventBus);
        ModTreeDecorators.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModFoliagePlacers.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(VanillaCreativeTabs::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.init(event);

        event.enqueueWork(() -> {
            ModTerraBlenderAPI.registerRegions();

            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.VIBRION_GROWTH.getId(), ModBlocks.POTTED_VIBRION_GROWTH);
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.SPOTTED_WINTERGREEN.getId(), ModBlocks.POTTED_SPOTTED_WINTERGREEN);
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.PINKSTER_FLOWER.getId(), ModBlocks.POTTED_PINKSTER_FLOWER);

            DispenserBlock.registerBehavior(ModItems.FERTILISER_BOMB.get(), new AbstractProjectileDispenseBehavior() {
                @Override
                protected Projectile getProjectile(Level pLevel, Position pPosition, ItemStack pStack) {
                    return new FertiliserBombEntity(pLevel, pPosition.x(), pPosition.y(), pPosition.z());
                }
            });
            DispenserBlock.registerBehavior(ModItems.SPORE_BOMB.get(), new AbstractProjectileDispenseBehavior() {
                @Override
                protected Projectile getProjectile(Level pLevel, Position pPosition, ItemStack pStack) {
                    return new SporeBombEntity(pLevel, pPosition.x(), pPosition.y(), pPosition.z());
                }
            });
            DispenserBlock.registerBehavior(ModItems.SPORE_ARROW.get(), new AbstractProjectileDispenseBehavior() {
                @Override
                protected Projectile getProjectile(Level pLevel, Position pPosition, ItemStack pStack) {
                    return new SporeArrowEntity(pLevel, pPosition.x(), pPosition.y(), pPosition.z());
                }
            });
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

        BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(Potions.AWKWARD, ModItems.MUCELLITH_JAW.get(), ModPotions.LIFESTEAL_POTION.get()));
        BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(ModPotions.LIFESTEAL_POTION.get(), Items.REDSTONE, ModPotions.LIFESTEAL_POTION_2.get()));
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
                    return new ResourceLocation(WildAside.MOD_ID, "textures/entity/projectiles/spore_arrow.png");
                }
            });
            EntityRenderers.register(ModEntities.MUCELLITH.get(), MucellithRenderer::new);
            EntityRenderers.register(ModEntities.CONTAMINATED_CREEPER.get(), ContaminatedCreeperRenderer::new);

            MenuScreens.register(ModMenuTypes.BIOENGINEERING_WORKSTATION_MENU.get(), BioengineeringWorkstationScreen::new);
            MenuScreens.register(ModMenuTypes.BIOFREEZER_MENU.get(), BiofreezerScreen::new);
            MenuScreens.register(ModMenuTypes.POTION_BLASTER_MENU.get(), PotionBlasterScreen::new);

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIBRION_GLASS_PANE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.LIT_VIBRION_GLASS_PANE.get(), RenderType.translucent());

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.FALLEN_HICKORY_LEAVES.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.HICKORY_ROOT_BUSH.get(), RenderType.cutout());

            ItemProperties.register(
                    ModItems.DNA_HOLDER.get(),
                    new ResourceLocation(MOD_ID, SAMPLE_PROGRESS),
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
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class ServerModEvents
    {

    }
}