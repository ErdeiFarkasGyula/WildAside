package net.farkas.wildaside;

import com.mojang.logging.LogUtils;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.entity.ModBlockEntities;
import net.farkas.wildaside.client.WildAsideClient;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.enchantment.ModEnchantments;
import net.farkas.wildaside.entity.ModEntityTypes;
import net.farkas.wildaside.entity.custom.vibrion.FertiliserBombEntity;
import net.farkas.wildaside.entity.custom.vibrion.SporeArrowEntity;
import net.farkas.wildaside.entity.custom.vibrion.SporeBombEntity;
import net.farkas.wildaside.item.ModCreativeModeTabs;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.VanillaCreativeTabs;
import net.farkas.wildaside.network.NetworkHandler;
import net.farkas.wildaside.particle.ModParticles;
import net.farkas.wildaside.potion.BetterBrewingRecipe;
import net.farkas.wildaside.potion.ModPotions;
import net.farkas.wildaside.recipe.ModRecipes;
import net.farkas.wildaside.screen.ModMenuTypes;
import net.farkas.wildaside.sound.ModSounds;
import net.farkas.wildaside.worldgen.biome.surface.ModSurfaceRules;
import net.farkas.wildaside.worldgen.feature.ModFeatures;
import net.farkas.wildaside.worldgen.feature.ModFoliagePlacers;
import net.farkas.wildaside.worldgen.feature.decorator.ModTreeDecorators;
import net.farkas.wildaside.worldgen.modifier.ModPlacementModifiers;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

@Mod(WildAside.MOD_ID)
public class WildAside {
    public static final String MOD_ID = "wildaside";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WildAside(FMLJavaModLoadingContext context) {
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, ModConfig.SERVER_SPEC);
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, ModConfig.CLIENT_SPEC);

        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);
        ModSounds.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntityTypes.register(modEventBus);
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
            addPottableBlocks();
            addDispenserBehaviours();
            addCompostables();
            addBrewingRecipes();
        });

        event.enqueueWork(() -> {
            addSurfaceRules();
        });
    }

    private static void addPottableBlocks() {
        if (ModBlocks.VIBRION_GROWTH.getId() != null) {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.VIBRION_GROWTH.getId(), ModBlocks.POTTED_VIBRION_GROWTH);
        }
        if (ModBlocks.SPOTTED_WINTERGREEN.getId() != null) {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.SPOTTED_WINTERGREEN.getId(), ModBlocks.POTTED_SPOTTED_WINTERGREEN);
        }
        if (ModBlocks.PINKSTER_FLOWER.getId() != null) {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.PINKSTER_FLOWER.getId(), ModBlocks.POTTED_PINKSTER_FLOWER);
        }
    }

    private static void addDispenserBehaviours() {
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
    }

    private static void addCompostables() {
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

    private static void addBrewingRecipes() {
        BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(Potions.AWKWARD, ModItems.MUCELLITH_JAW.get(), ModPotions.LIFESTEAL_POTION.get()));
        BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(ModPotions.LIFESTEAL_POTION.get(), Items.REDSTONE, ModPotions.LIFESTEAL_POTION_2.get()));
    }

    private static void addSurfaceRules() {
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeRules());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            WildAsideClient.init(event);
        }
    }
}