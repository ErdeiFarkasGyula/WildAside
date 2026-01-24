package net.farkas.wildaside.event;

import com.mojang.brigadier.CommandDispatcher;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.advancement.ModAdvancements;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.capability.bioengineering_skill.BioengineeringSkillsProvider;
import net.farkas.wildaside.capability.contamination.ContaminationCapability;
import net.farkas.wildaside.capability.contamination.ContaminationProvider;
import net.farkas.wildaside.capability.dna.DnaProvider;
import net.farkas.wildaside.command.ModCommands;
import net.farkas.wildaside.dna.bioengineering_skill.BioengineeringSkillUtils;
import net.farkas.wildaside.effect.ModMobEffects;
import net.farkas.wildaside.network.WindSavedData;
import net.farkas.wildaside.advancement.AdvancementUtils;
import net.farkas.wildaside.util.ContaminationHandler;
import net.farkas.wildaside.util.WindManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WildAside.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModForgeEvents {
    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity livingEntity) {
            event.addCapability(ContaminationProvider.IDENTIFIER, new ContaminationProvider());
            event.addCapability(DnaProvider.IDENTIFIER, new DnaProvider());
        }
        if (event.getObject() instanceof Player player) {
            event.addCapability(BioengineeringSkillsProvider.IDENTIFIER, new BioengineeringSkillsProvider());
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
            AdvancementUtils.givePlayerAdvancement(player, ModAdvancements.WILD_WILDER_WILDEST);
            BioengineeringSkillUtils.syncToClient(player);
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

    private static final int REGULAR_TIME = 60 * 20;
    private static int irregularTime = 1467;

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

        int tickCount = server.getTickCount();;

        if (server.getTickCount() % REGULAR_TIME == 0) {
            WindManager.calculateAndSetWind(overworld, true);
            int minTime = raining || thundering ? 40 : 100;
            irregularTime = overworld.random.nextInt(minTime, 300);
        }
        else if (tickCount % irregularTime == 0) {
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
                AdvancementUtils.givePlayerAdvancement(player, ModAdvancements.GLOW_UP);
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
            AdvancementUtils.givePlayerAdvancement(player, ModAdvancements.ITS_SHEARING_TIME);
        } else if (block.equals(ModBlocks.SPORE_BLASTER.get()) && level.getBestNeighborSignal(blockPos) > 0) {
            AdvancementUtils.givePlayerAdvancement(player, ModAdvancements.BACTERIA_BEACON);
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getPlayer().level().isClientSide) return;
        blasterBustedAdvancement(event);
    }

    public static void blasterBustedAdvancement(BlockEvent.BreakEvent event) {
        if (event.getLevel().getBlockState(event.getPos()).getBlock() == ModBlocks.NATURAL_SPORE_BLASTER.get()) {
            AdvancementUtils.givePlayerAdvancement(event.getPlayer(), ModAdvancements.BLASTER_BUSTED);
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
