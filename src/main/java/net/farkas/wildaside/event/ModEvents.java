package net.farkas.wildaside.event;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.capability.contamination.ContaminationAttacher;
import net.farkas.wildaside.capability.contamination.ContaminationCapability;
import net.farkas.wildaside.capability.contamination.IContamination;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.enchantment.ModEnchantments;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.potion.BetterBrewingRecipe;
import net.farkas.wildaside.potion.ModPotions;
import net.farkas.wildaside.util.AdvancementHandler;
import net.farkas.wildaside.util.ContaminationHandler;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {
    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        final ContaminationAttacher.ContaminationProvider provider = new ContaminationAttacher.ContaminationProvider();
        event.addCapability(ContaminationAttacher.ContaminationProvider.IDENTIFIER, provider);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AdvancementHolder advancement = player.server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID,"wild_wilder_wildest"));
            if (advancement == null) return;
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criteria);
                }
            }
        }
    }

    @SubscribeEvent
    public static void brewingRecipesEvent(BrewingRecipeRegisterEvent event) {
//        event.addRecipe(new BetterBrewingRecipe(Potions.AWKWARD, ModItems.VIBRION.get(), ModPotions.CONTAMINATION_POTION.get()));
//        event.addRecipe(new BetterBrewingRecipe(ModPotions.CONTAMINATION_POTION.get(), Items.REDSTONE, ModPotions.CONTAMINATION_POTION_2.get()));
//        event.addRecipe(new BetterBrewingRecipe(ModPotions.IMMUNITY_POTION.get(), Items.FERMENTED_SPIDER_EYE, ModPotions.IMMUNITY_POTION.get()));

//        event.addRecipe(new BetterBrewingRecipe(Potions.AWKWARD, ModItems.ENTORIUM.get(), ModPotions.IMMUNITY_POTION.get()));
//        event.addRecipe(new BetterBrewingRecipe(ModPotions.CONTAMINATION_POTION.get(), Items.FERMENTED_SPIDER_EYE, ModPotions.IMMUNITY_POTION.get()));
//        event.addRecipe(new BetterBrewingRecipe(ModPotions.IMMUNITY_POTION.get(), Items.REDSTONE, ModPotions.IMMUNITY_POTION_2.get()));
//        event.addRecipe(new BetterBrewingRecipe(ModPotions.CONTAMINATION_POTION_2.get(), Items.FERMENTED_SPIDER_EYE, ModPotions.IMMUNITY_POTION_2.get()));

        event.addRecipe(new BetterBrewingRecipe(Potions.AWKWARD.get(), ModItems.MUCELLITH_JAW.get(), ModPotions.LIFESTEAL_POTION.get()));
        event.addRecipe(new BetterBrewingRecipe(ModPotions.LIFESTEAL_POTION.get(), Items.REDSTONE, ModPotions.LIFESTEAL_POTION_2.get()));
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FARMER) {
            int villagerLevel = 1;
            ItemStack emerald = new ItemStack(Items.EMERALD);
            event.getTrades().get(villagerLevel).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(ModItems.HICKORY_NUT.get(), 16), emerald, 20, 2, 0.05f
            ));
            for (HickoryColour colour : HickoryColour.values()) {
                event.getTrades().get(villagerLevel).add((pTrader, pRandom) -> new MerchantOffer(
                        new ItemCost(ModItems.LEAF_ITEMS.get(colour).get(), 32), emerald, 20, 2, 0.05f
                ));
            }
        }

        if (event.getType() == VillagerProfession.TOOLSMITH) {
            int villagerLevel = 3;
            event.getTrades().get(villagerLevel).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 6), new ItemStack(ModItems.SPORE_BOMB.get()), 2, 5, 0.06f
            ));
        }
    }

    @SubscribeEvent
    public static void addCustomWanderingTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        //List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 4), new ItemStack(ModBlocks.HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 5), new ItemStack(ModBlocks.RED_GLOWING_HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 5), new ItemStack(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 5), new ItemStack(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemCost(Items.EMERALD, 5), new ItemStack(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        glowUpAdvancement(event);
        itsShearingTimeAdvancement(event);
        bacteriaBarrierAdvancement(event);
    }

    private static final ResourceLocation GLOWING_FOREST = ResourceLocation.fromNamespaceAndPath(WildAside.MOD_ID, "glowing_hickory_forest");

    private static void glowUpAdvancement(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.player;
            ServerLevel world = player.serverLevel();

            long time = world.getDayTime();

            if (time >= 14000 && time <= 22000) {
                Holder<Biome> biomeHolder = world.getBiome(player.blockPosition());
                ResourceKey<Biome> biomeKey = biomeHolder.unwrapKey().orElse(null);

                if (biomeKey != null && biomeKey.location().equals(GLOWING_FOREST)) {
                    AdvancementHandler.givePlayerAdvancement(player, "glow_up");
                }
            }
        }
    }

    private static void itsShearingTimeAdvancement(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.player;
            ServerLevel level = player.serverLevel();

            ClipContext clipContext = new ClipContext(player.getEyePosition(1f),
                    player.getEyePosition(1f).add(player.getViewVector(1f).scale(5)),
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    player);

            BlockPos blockPos = level.clip(clipContext).getBlockPos();

            if (level.getBlockState(blockPos).getBlock() == ModBlocks.OVERGROWN_ENTORIUM_ORE.get()) {
                AdvancementHandler.givePlayerAdvancement(player, "its_shearing_time");
            }
        }
    }

    private static void bacteriaBarrierAdvancement(TickEvent.PlayerTickEvent event) {
        if (!event.player.level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.player;
            ServerLevel level = player.serverLevel();

            ClipContext clipContext = new ClipContext(player.getEyePosition(1f),
                    player.getEyePosition(1f).add(player.getViewVector(1f).scale(5)),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);

            BlockPos blockPos = level.clip(clipContext).getBlockPos();

            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.getBlock() == ModBlocks.SPORE_BLASTER.get() && level.getBestNeighborSignal(blockPos) > 0) {
                AdvancementHandler.givePlayerAdvancement(player, "bacteria_barrier");
            }
        }
    }

    @SubscribeEvent
    public static void blasterBustedAdvancement(BlockEvent.BreakEvent event) {
        if (!event.getPlayer().level().isClientSide) {
            if (event.getLevel().getBlockState(event.getPos()).getBlock() == ModBlocks.NATURAL_SPORE_BLASTER.get()) {
                AdvancementHandler.givePlayerAdvancement((ServerPlayer)event.getPlayer(), "blaster_busted");
            }
        }
    }

    @SubscribeEvent
    public static void onContaminationEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            MobEffectInstance mobEffectInstance = event.getEffectInstance();
            if (mobEffectInstance != null && mobEffectInstance.getEffect() == ModMobEffects.CONTAMINATION.get()) {
                entity.addEffect(new MobEffectInstance(ModMobEffects.IMMUNITY.getHolder().get(), (mobEffectInstance.getAmplifier() + 1 ) * 5 * 20, mobEffectInstance.getAmplifier()));
            }
        }
    }

    @SubscribeEvent
    public static void playerDoesCriticalStrike(CriticalHitEvent event) {
        if (event.isCanceled()) return;

        ModMobEffects.CONTAMINATION.getHolder().ifPresent(contamEffect -> {
            Player attacker = event.getEntity();
            if (attacker == null) return;
            if (attacker.hasEffect(contamEffect)) {
                RandomSource random = attacker.getRandom();
                if ((float)attacker.getEffect(contamEffect).getAmplifier() / 5 > random.nextFloat()) {
                    if (event.getTarget() instanceof LivingEntity target) {
                        attacker.getCapability(ContaminationCapability.INSTANCE).ifPresent(data -> {
                            ContaminationHandler.giveContaminationDose(target, data.getDose() / 5);
                        });
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void livingEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        entity.getCapability(ContaminationCapability.INSTANCE).ifPresent(data -> {
            data.addDose(-10);
        });
    }
}
