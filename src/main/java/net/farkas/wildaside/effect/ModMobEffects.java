package net.farkas.wildaside.effect;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.effect.custom.Contamination;
import net.farkas.wildaside.effect.custom.Immunity;
import net.farkas.wildaside.effect.custom.Lifesteal;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WildAside.MOD_ID);

    public static final RegistryObject<MobEffect> CONTAMINATION = MOB_EFFECTS.register("contamination",
            () -> new Contamination(MobEffectCategory.HARMFUL, 0xefca73)
                    .addAttributeModifier(Attributes.ARMOR, "FA233E1C-6969-4200-B01B-BCCE9785ACA4", -0.10 , AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "FA233E1C-6969-4200-B01B-BCCE9785ACA4", -0.10 , AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.SPAWN_REINFORCEMENTS_CHANCE, "FB233E1C-6969-4200-B01B-BCCE9785ACA4", 0.01, AttributeModifier.Operation.ADDITION)
    );

    public static final RegistryObject<MobEffect> IMMUNITY = MOB_EFFECTS.register("immunity",
            () -> new Immunity(MobEffectCategory.BENEFICIAL, -3579177)
    );

    public static final RegistryObject<MobEffect> LIFESTEAL = MOB_EFFECTS.register("lifesteal",
            () -> new Lifesteal(MobEffectCategory.BENEFICIAL, 11141120)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
