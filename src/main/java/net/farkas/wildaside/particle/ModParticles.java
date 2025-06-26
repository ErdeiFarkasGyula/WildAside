package net.farkas.wildaside.particle;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.util.HickoryColour;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, WildAside.MOD_ID);

    public static final RegistryObject<SimpleParticleType> VIBRION_PARTICLE =
            PARTICLES.register("vibrion_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> ENTORIUM_PARTICLE =
            PARTICLES.register("entorium_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SUBSTILIUM_PARTICLE =
            PARTICLES.register("substilium_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> STILL_SUBSTILIUM_PARTICLE =
            PARTICLES.register("still_substilium_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> LIFESTEAL_PARTICLE =
            PARTICLES.register("lifesteal_particle", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> HICKORY_LEAF_PARTICLE =
            PARTICLES.register("hickory_leaf_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> RED_GLOWING_HICKORY_LEAF_PARTICLE =
            PARTICLES.register("red_glowing_hickory_leaf_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BROWN_GLOWING_HICKORY_LEAF_PARTICLE =
            PARTICLES.register("brown_glowing_hickory_leaf_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> YELLOW_GLOWING_HICKORY_LEAF_PARTICLE =
            PARTICLES.register("yellow_glowing_hickory_leaf_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> GREEN_GLOWING_HICKORY_LEAF_PARTICLE =
            PARTICLES.register("green_glowing_hickory_leaf_particle", () -> new SimpleParticleType(true));

    public static final EnumMap<HickoryColour, RegistryObject<SimpleParticleType>> HICKORY_PARTICLES = new EnumMap<>(HickoryColour.class);
    static {
        HICKORY_PARTICLES.put(HickoryColour.HICKORY, HICKORY_LEAF_PARTICLE);
        HICKORY_PARTICLES.put(HickoryColour.RED_GLOWING, RED_GLOWING_HICKORY_LEAF_PARTICLE);
        HICKORY_PARTICLES.put(HickoryColour.BROWN_GLOWING, BROWN_GLOWING_HICKORY_LEAF_PARTICLE);
        HICKORY_PARTICLES.put(HickoryColour.YELLOW_GLOWING, YELLOW_GLOWING_HICKORY_LEAF_PARTICLE);
        HICKORY_PARTICLES.put(HickoryColour.GREEN_GLOWING, GREEN_GLOWING_HICKORY_LEAF_PARTICLE);
    }

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}