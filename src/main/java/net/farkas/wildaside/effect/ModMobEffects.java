package net.farkas.wildaside.effect;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.effect.custom.ContaminationEffect;
import net.farkas.wildaside.effect.custom.ImmunityEffect;
import net.farkas.wildaside.effect.custom.OmnivampEffect;
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
            () -> new ContaminationEffect(MobEffectCategory.HARMFUL, -6184663)
                    .addAttributeModifier(Attributes.ARMOR, "FA233E1C-6969-4200-B01B-BCCE9785ACA4", -0.25 , AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "FA233E1C-6969-4200-B01B-BCCE9785ACA4", -0.25 , AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.SPAWN_REINFORCEMENTS_CHANCE, "FB233E1C-6969-4200-B01B-BCCE9785ACA4", 0.1, AttributeModifier.Operation.ADDITION)
    );

    public static final RegistryObject<MobEffect> IMMUNITY = MOB_EFFECTS.register("immunity",
            () -> new ImmunityEffect(MobEffectCategory.BENEFICIAL, -3579177)
    );

    public static final RegistryObject<MobEffect> OMNIVAMP = MOB_EFFECTS.register("omnivamp",
            () -> new OmnivampEffect(MobEffectCategory.BENEFICIAL, -3579177)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
