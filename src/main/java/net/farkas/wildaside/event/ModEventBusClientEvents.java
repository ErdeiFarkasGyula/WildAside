package net.farkas.wildaside.event;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.entity.ModBlockEntities;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.client.ModKeyMappings;
import net.farkas.wildaside.entity.ModEntities;
import net.farkas.wildaside.entity.client.ModModelLayers;
import net.farkas.wildaside.entity.client.hickory.HickoryTreantRenderer;
import net.farkas.wildaside.entity.client.vibrion.MucellithModel;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.custom.DnaHolder;
import net.farkas.wildaside.item.custom.Syringe;
import net.farkas.wildaside.particle.*;
import net.farkas.wildaside.particle.custom.*;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.farkas.wildaside.item.custom.DnaHolder.DNA_DATA;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeyMappings.USE_ABILITY);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.SUBSTILIUM_BOAT_LAYER, BoatModel::createBodyModel);
        event.registerLayerDefinition(ModModelLayers.SUBSTILIUM_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(ModModelLayers.HICKORY_BOAT_LAYER, BoatModel::createBodyModel);
        event.registerLayerDefinition(ModModelLayers.HICKORY_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel);

        event.registerLayerDefinition(ModModelLayers.MUCELLITH_LAYER, MucellithModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOD_HANGING_SIGN.get(), HangingSignRenderer::new);
        event.registerEntityRenderer(ModEntities.HICKORY_LEAF_PROJECTILE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.HICKORY_TREANT.get(), HickoryTreantRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.ENTORIUM_PARTICLE.get(), EntoriumParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SUBSTILIUM_PARTICLE.get(), SubstiliumParticle.Provider::new);
        event.registerSpriteSet(ModParticles.STILL_SUBSTILIUM_PARTICLE.get(), StillSubstiliumParticle.Provider::new);
        event.registerSpriteSet(ModParticles.LIFESTEAL_PARTICLE.get(), LifestealParticle.Provider::new);
        event.registerSpriteSet(ModParticles.VIBRION_PARTICLE.get(), VibrionParticle.Provider::new);
        event.registerSpriteSet(ModParticles.VIBRION_DRIP_PARTICLE.get(), ModDripParticle.Provider::new);
        event.registerSpriteSet(ModParticles.HICKORY_LEAF_PARTICLE.get(), FallingHickoryLeafParticle.Provider::new);
        event.registerSpriteSet(ModParticles.RED_GLOWING_HICKORY_LEAF_PARTICLE.get(), FallingHickoryLeafParticle.Provider::new);
        event.registerSpriteSet(ModParticles.BROWN_GLOWING_HICKORY_LEAF_PARTICLE.get(), FallingHickoryLeafParticle.Provider::new);
        event.registerSpriteSet(ModParticles.YELLOW_GLOWING_HICKORY_LEAF_PARTICLE.get(), FallingHickoryLeafParticle.Provider::new);
        event.registerSpriteSet(ModParticles.GREEN_GLOWING_HICKORY_LEAF_PARTICLE.get(), FallingHickoryLeafParticle.Provider::new);
        event.registerSpriteSet(ModParticles.WIND_PARTICLE.get(), WindParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerColoredBlocks(RegisterColorHandlersEvent.Block event) {
        event.register((pState, pLevel, pPos, pTintIndex) -> pLevel != null &&
                pPos != null ? BiomeColors.getAverageFoliageColor(pLevel, pPos) : FoliageColor.getDefaultColor(),
                ModBlocks.HICKORY_LEAVES.get(), ModBlocks.FALLEN_HICKORY_LEAVES.get());
    }

    @SubscribeEvent
    public static void registerColoredItems(RegisterColorHandlersEvent.Item event) {
        event.register((pStack, pTintIndex) -> {
            BlockState state = ((BlockItem)pStack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(state, null, null, pTintIndex);
        }, ModBlocks.HICKORY_LEAVES.get());

        event.getItemColors().register((stack, tintIndex) -> {
            if (!stack.hasTag() || !stack.getTag().contains(DNA_DATA)) return 0xFFFFFF;

            CompoundTag dnaTag = stack.getTag().getCompound(DNA_DATA);
            DnaImplementation dna = new DnaImplementation();
            dna.deserializeNBT(dnaTag);

            if (dna.source() == null) return 0xFFFFFF;

            SpawnEggItem egg = ForgeSpawnEggItem.fromEntityType(dna.source());
            if (egg == null) return 0xFFFFFF;

            return switch (tintIndex) {
                case 0 -> egg.getColor(0);
                case 1 -> egg.getColor(1);
                default -> 0xFFFFFF;
            };
        }, ModItems.DNA_HOLDER.get());

        event.getItemColors().register((stack, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFF;

            if (!stack.hasTag()) return 0xFFFFFF;
            CompoundTag tag = stack.getTag();

            float level = tag.contains(Syringe.BLOOD_LEVEL) ? tag.getFloat(Syringe.BLOOD_LEVEL) : 0f;

            if (level <= 0.01f) return 0xFFFFFF;

            int baseColor = Syringe.DEFAULT_BLOOD_COLOR;

            int alpha = (int)(255 * Mth.clamp(level, 0f, 1f));
            return (alpha << 24) | (baseColor & 0xFFFFFF);

        }, ModItems.SYRINGE.get());
    }
}

