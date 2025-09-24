package net.farkas.wildaside.sound;

import net.farkas.wildaside.WildAside;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WildAside.MOD_ID);

    public static final RegistryObject<SoundEvent> VIBRION_HIVE_MUSIC = registerSoundEvent("music.vibrion_hive");

    public static final RegistryObject<SoundEvent> MUCELLITH_AMBIENT_1 = registerSoundEvent("mucellith.ambient_1");
    public static final RegistryObject<SoundEvent> MUCELLITH_AMBIENT_2 = registerSoundEvent("mucellith.ambient_2");
    public static final RegistryObject<SoundEvent> MUCELLITH_AMBIENT_3 = registerSoundEvent("mucellith.ambient_3");
    public static final RegistryObject<SoundEvent> MUCELLITH_AMBIENT_4 = registerSoundEvent("mucellith.ambient_4");

    public static final RegistryObject<SoundEvent> MUCELLITH_HURT_1 = registerSoundEvent("mucellith.hurt_1");
    public static final RegistryObject<SoundEvent> MUCELLITH_HURT_2 = registerSoundEvent("mucellith.hurt_2");
    public static final RegistryObject<SoundEvent> MUCELLITH_HURT_3 = registerSoundEvent("mucellith.hurt_3");

    public static final RegistryObject<SoundEvent> MUCELLITH_DEATH = registerSoundEvent("mucellith.death");



    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WildAside.MOD_ID, name)));
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}