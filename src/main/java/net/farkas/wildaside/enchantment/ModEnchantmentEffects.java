package net.farkas.wildaside.enchantment;

import com.mojang.serialization.MapCodec;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.enchantment.custom.CushioningEnchantmentEffect;
import net.farkas.wildaside.enchantment.custom.ExtensiveResearchEnchantmentEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantmentEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, WildAside.MOD_ID);

    public static final RegistryObject<MapCodec<? extends EnchantmentEntityEffect>> CUSHIONING =
            ENTITY_ENCHANTMENT_EFFECTS.register("cushioning", () -> CushioningEnchantmentEffect.CODEC);
    public static final RegistryObject<MapCodec<? extends EnchantmentEntityEffect>> EXTENSIVE_RESEARCH =
            ENTITY_ENCHANTMENT_EFFECTS.register("extensive_research", () -> ExtensiveResearchEnchantmentEffect.CODEC);


    public static void register(IEventBus eventBus) {
        ENTITY_ENCHANTMENT_EFFECTS.register(eventBus);
    }
}
