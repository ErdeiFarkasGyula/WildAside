package net.farkas.wildaside.event;

import com.mojang.brigadier.CommandDispatcher;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.capability.contamination.ContaminationCapability;
import net.farkas.wildaside.capability.contamination.ContaminationProvider;
import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaProvider;
import net.farkas.wildaside.capability.syringe.SyringeAnimProvider;
import net.farkas.wildaside.command.ModCommands;
import net.farkas.wildaside.config.Config;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.ability.IAbility;
import net.farkas.wildaside.dna.speed.MobSpeedTestTracker;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.Traits;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.network.WindSavedData;
import net.farkas.wildaside.util.AdvancementHandler;
import net.farkas.wildaside.util.ContaminationHandler;
import net.farkas.wildaside.util.WindManager;
import net.farkas.wildaside.worldgen.dimension.ModDimensions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {
    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity livingEntity) {
            event.addCapability(ContaminationProvider.IDENTIFIER, new ContaminationProvider());
            event.addCapability(DnaProvider.IDENTIFIER, new DnaProvider());
        }
        if (event.getObject() instanceof Player player) {
            event.addCapability(SyringeAnimProvider.IDENTIFIER, new SyringeAnimProvider());
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ModCommands.register(dispatcher);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AdvancementHandler.givePlayerAdvancement(player, "wild_wilder_wildest");
        }
    }

    @SubscribeEvent
    public static void onEntityLeftLevel(EntityLeaveLevelEvent event) {
        checkTestDimensionEntityRemoves(event);
    }

    public static void checkTestDimensionEntityRemoves(EntityLeaveLevelEvent event) {
        if (!Config.ACCURATE_DNA_WATER_MOVEMENT_SPEEDS.get() || event.getEntity().level().isClientSide()) return;

        if (event.getEntity().level().dimension() == ModDimensions.TEST_LEVEL) {
            if (event.getEntity() instanceof Mob mob && (mob.isSensitiveToWater() || mob.getSpeed() == 0.0 || mob.getDeltaMovement() == Vec3.ZERO)) {
                MobSpeedTestTracker.onMobFinished(mob, "water");
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        applyDnaOnJoinLevel(event);
    }

    public static void applyDnaOnJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().dimension() == ModDimensions.TEST_LEVEL || !Config.ACCURATE_DNA_MOVEMENT_SPEEDS.get() || !(event.getLevel() instanceof ServerLevel)) return;

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
                if (dna.genes().isEmpty()) {
                    dna.setSource(livingEntity.getType());
                    Map<Trait, Gene> genes = DnaUtils.generateBaseGenes(livingEntity, true);
                    dna.setGenes(genes);
                    dna.setStability(100);
                }
            });
            livingEntity.getPersistentData().putFloat(IAbility.COOLDOWN, 0);
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        loadWind(event);
    }

    public static void loadWind(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.dimension().equals(ServerLevel.OVERWORLD)) return;

        WindSavedData data = WindSavedData.get(serverLevel);

        Vec3 dir = data.getWindDirection();
        float strength = data.getWindStrength();

        WindManager.setWind(dir, strength);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        manageWeather(event);
    }

    public static void manageWeather(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = event.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        WindSavedData weatherData = WindSavedData.get(overworld);

        boolean raining = overworld.isRaining();
        boolean thundering = overworld.isThundering();

        if (raining != weatherData.wasRaining() || thundering != weatherData.wasThundering()) {
            WindManager.calculateAndSetWind(overworld, false);
        }

        int time = 1200;
        if (server.getTickCount() % time == 0) {
            WindManager.calculateAndSetWind(overworld, true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide) return;
        glowUpAdvancement(event);
        clipContextCheckingTickEvent(event);
    }

    private static final ResourceLocation GLOWING_HICKORY_FOREST = new ResourceLocation(WildAside.MOD_ID, "glowing_hickory_forest");

    private static void glowUpAdvancement(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ServerPlayer player = (ServerPlayer) event.player;
        ServerLevel world = player.serverLevel();

        long time = world.getDayTime();

        if (time >= 14000 && time <= 22000) {
            Holder<Biome> biomeHolder = world.getBiome(player.blockPosition());
            ResourceKey<Biome> biomeKey = biomeHolder.unwrapKey().orElse(null);

            if (biomeKey != null && biomeKey.location().equals(GLOWING_HICKORY_FOREST)) {
                AdvancementHandler.givePlayerAdvancement(player, "glow_up");
            }
        }
    }

    private static void clipContextCheckingTickEvent(TickEvent.PlayerTickEvent event) {
        ServerPlayer player = (ServerPlayer) event.player;
        ServerLevel level = player.serverLevel();

        ClipContext clipContext = new ClipContext(player.getEyePosition(1f),
                player.getEyePosition(1f).add(player.getViewVector(1f).scale(5)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player);

        BlockPos blockPos = level.clip(clipContext).getBlockPos();
        Block block = level.getBlockState(blockPos).getBlock();

        if (block.equals(ModBlocks.OVERGROWN_ENTORIUM_ORE.get())) {
            AdvancementHandler.givePlayerAdvancement(player, "its_shearing_time");
        } else if (block.equals(ModBlocks.SPORE_BLASTER.get()) && level.getBestNeighborSignal(blockPos) > 0) {
            AdvancementHandler.givePlayerAdvancement(player, "bacteria_beacon");
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getPlayer().level().isClientSide) return;
        blasterBustedAdvancement(event);
    }

    public static void blasterBustedAdvancement(BlockEvent.BreakEvent event) {
        if (event.getLevel().getBlockState(event.getPos()).getBlock() == ModBlocks.NATURAL_SPORE_BLASTER.get()) {
            AdvancementHandler.givePlayerAdvancement((ServerPlayer)event.getPlayer(), "blaster_busted");
        }
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEntity().level().isClientSide) return;
        onContaminationEffectExpired(event);
    }

    public static void onContaminationEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance mobEffectInstance = event.getEffectInstance();

        if (mobEffectInstance != null && mobEffectInstance.getEffect() == ModMobEffects.CONTAMINATION.get()) {
            entity.addEffect(new MobEffectInstance(ModMobEffects.IMMUNITY.get(), (mobEffectInstance.getAmplifier() + 1 ) * 5 * 20, mobEffectInstance.getAmplifier()));
        }
    }

    @SubscribeEvent
    public static void livingEntityHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        reduceDnaStabilityFromHurting(event);
        reduceDamageBasedOnDnaResistances(event);
    }

    public static void reduceDnaStabilityFromHurting(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        float stabilityReduction = event.getAmount() * 0.05f;

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            boolean modified = DnaUtils.generateBaseGenes(entity, true) != dna.genes();

            if (modified) {
                dna.setStability(dna.stability() - stabilityReduction);
            }
        });
    }

    public static void reduceDamageBasedOnDnaResistances(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        entity.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
        Trait trait = null;
            if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR)) {
                trait = Traits.FIRE_RESISTANCE;
            }
            else if (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)) {
                trait = Traits.EXPLOSION_RESISTANCE;
            }
            else if (source.is(DamageTypes.FALL)) {
                trait = Traits.FALL_RESISTANCE;
            }
            else if (source.is(DamageTypes.FREEZE)) {
                trait = Traits.FREEZE_RESISTANCE;
            }

            if (trait != null) {
                float value = Traits.getTraitValue(dna.genes(), trait);
                dna.setStability(dna.stability() - (event.getAmount() * 0.25f));
                event.setAmount(event.getAmount() * (1.0f - value));
            }
        });
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        if (event.getEntity().level().isClientSide || !event.isVanillaCritical()) return;
        spreadContaminationOnCriticalHit(event);
    }

    public static void spreadContaminationOnCriticalHit(CriticalHitEvent event) {
        ModMobEffects.CONTAMINATION.getHolder().ifPresent(contamEffect -> {
            var contamination = contamEffect.get();
            Player attacker = event.getEntity();
            if (attacker == null) return;
            if (attacker.hasEffect(contamination)) {
                RandomSource random = attacker.getRandom();
                if ((float)attacker.getEffect(contamination).getAmplifier() / 5 > random.nextFloat()) {
                    if (event.getTarget() instanceof LivingEntity target) {
                        attacker.getCapability(ContaminationCapability.INSTANCE).ifPresent(data -> {
                            ContaminationHandler.addDose(target, data.getDose() / 5);
                        });
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void livingEntityTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide) return;
        passiveContaminationDoseReduction(event);
    }

    public static void passiveContaminationDoseReduction(LivingBreatheEvent.LivingTickEvent event) {
        event.getEntity().getCapability(ContaminationCapability.INSTANCE).ifPresent(data -> {
            data.addDose(-10);
        });
    }
}
