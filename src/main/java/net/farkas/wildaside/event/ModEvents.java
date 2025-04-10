package net.farkas.wildaside.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.util.AdvancementHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Advancement advancement = player.server.getAdvancements().getAdvancement(new ResourceLocation("wildaside:wild_wilder_wildest"));
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criteria);
                }
            }
        }
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FARMER) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            int villagerLevel = 1;
            ItemStack emerald = new ItemStack(Items.EMERALD);

            trades.get(villagerLevel).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(ModItems.HICKORY_NUT.get(), 16), emerald, 20, 2, 0.05f
            ));
        }

        if (event.getType() == VillagerProfession.TOOLSMITH) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            int villagerLevel = 3;

            trades.get(villagerLevel).add((pTrader, pRandom) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 6), new ItemStack(ModItems.SPORE_BOMB.get()), 2, 5, 0.06f
            ));
        }
    }

    @SubscribeEvent
    public static void addCustomWanderingTrades(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> genericTrades = event.getGenericTrades();
        //List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 4), new ItemStack(ModBlocks.HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 5), new ItemStack(ModBlocks.RED_GLOWING_HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 5), new ItemStack(ModBlocks.BROWN_GLOWING_HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 5), new ItemStack(ModBlocks.YELLOW_GLOWING_HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
        genericTrades.add((pTrader, pRandom) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 5), new ItemStack(ModBlocks.GREEN_GLOWING_HICKORY_SAPLING.get()), 8, 2, 0.03f
        ));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        glowUpAdvancement(event);
        itsShearingTimeAdvancement(event);
        bacteriaBarrierAdvancement(event);
    }

    private static final ResourceLocation GLOWING_FOREST = new ResourceLocation("wildaside", "glowing_hickory_forest");

    private static void glowUpAdvancement(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            ServerPlayer player = (ServerPlayer) event.player;
            ServerLevel world = player.serverLevel();

            long time = world.getDayTime();

            if (time >= 18000 && time <= 22000) {

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
                AdvancementHandler.givePlayerAdvancement(player, "bacteria_bricks");
            }
        }
    }

    @SubscribeEvent
    public static void onContaminationEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            MobEffectInstance mobEffectInstance = event.getEffectInstance();
            if (mobEffectInstance.getEffect() == ModMobEffects.CONTAMINATION.get()) {
                entity.addEffect(new MobEffectInstance(ModMobEffects.IMMUNITY.get(), 100, mobEffectInstance.getAmplifier()));
            }
        }
    }

    @SubscribeEvent
    public static void playerDoesCriticalStrike(CriticalHitEvent event) {
        if (!event.isCanceled()) {
            if (event.getEntity() != null) {
                Player attacker = event.getEntity();
                if (attacker.hasEffect(ModMobEffects.CONTAMINATION.get())) {
                    if (event.getTarget() instanceof LivingEntity target) {
                        int amplifier = attacker.getEffect(ModMobEffects.CONTAMINATION.get()).getAmplifier() + 1;
                        target.addEffect(new MobEffectInstance(ModMobEffects.CONTAMINATION.get(), 200, amplifier));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void applyLifeSteal(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        System.out.println("Applying Life Steal!");

        if (source.getEntity() instanceof Player attacker) {
            MobEffectInstance effectInstance  = attacker.getEffect(ModMobEffects.LIFE_STEAL.get());
            if (effectInstance != null) {
                int amplifier = effectInstance.getAmplifier();
                float lifeStealPercentage = (float) (amplifier + 1) / 10;
                float maxLifeStealAmount = (amplifier + 1) * 3;

                float healAmount = event.getAmount() * lifeStealPercentage;

                if (healAmount > maxLifeStealAmount) {
                    healAmount = maxLifeStealAmount;
                }

                attacker.heal(healAmount);
            }
        }
    }
}
