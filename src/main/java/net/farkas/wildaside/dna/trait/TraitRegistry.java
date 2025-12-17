package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Cat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TraitRegistry {
    public static final List<Trait> TRAITS = new ArrayList<>();

    public static final Trait MAX_HEALTH = register(new AttributeTrait("max_health", 1.4f, DnaUtils.getAttributeRes("max_health")));
    public static final Trait MOVEMENT_SPEED = register(new AttributeTrait("movement_speed", 1.4f, DnaUtils.getAttributeRes("movement_speed")));
    public static final Trait ATTACK_DAMAGE = register(new AttributeTrait("attack_damage", 1.5f, DnaUtils.getAttributeRes("attack_damage")));
    public static final Trait ATTACK_SPEED = register(new AttributeTrait("attack_speed", 1.1f, DnaUtils.getAttributeRes("attack_speed")));
    public static final Trait ATTACK_KNOCKBACK = register(new AttributeTrait("attack_knockback", 1.0f, DnaUtils.getAttributeRes("attack_knockback"), AttributeModifier.Operation.ADDITION));
    public static final Trait ARMOR = register(new AttributeTrait("armor", 1.5f, DnaUtils.getAttributeRes("armor"), AttributeModifier.Operation.ADDITION));
    public static final Trait ARMOR_TOUGHNESS = register(new AttributeTrait("armor_toughness", 0.8f, DnaUtils.getAttributeRes("armor_toughness"), AttributeModifier.Operation.ADDITION));

    public static final Trait KNOCKBACK_RESISTANCE = register(new AttributeTrait("knockback_resistance", 1.6f, TraitType.RESISTANCE, DnaUtils.getAttributeRes("knockback_resistance")));
    public static final Trait FIRE_RESISTANCE = register(new ResistanceTrait("fire_resistance", 2.3f));
    public static final Trait FALL_RESISTANCE = register(new ResistanceTrait("fall_resistance", 2.1f));
    public static final Trait EXPLOSION_RESISTANCE = register(new ResistanceTrait("explosion_resistance", 1.7f));
    public static final Trait FREEZE_RESISTANCE = register(new ResistanceTrait("freeze_resistance", 1.1f));

    public static final Trait FIRE_ABILITY = register(new AbilityTrait("fire_ability", 3f));
    public static final Trait TELEPORT_ABILITY = register(new AbilityTrait("teleport_ability", 3f));

    public static final Trait CAT_VARIANT =
            register(new AppearanceTrait<ResourceLocation>("cat_variant", 1.3f) {
                @Override
                public void applyAppearance(LivingEntity entity, ResourceLocation value) {
                    if (entity instanceof Cat cat) {
                        if (BuiltInRegistries.CAT_VARIANT.containsKey(value)) {
                            cat.setVariant(BuiltInRegistries.CAT_VARIANT.get(value));
                        }
                    }
                }
            });


    private static <T extends Trait> T register(T trait) {
        TRAITS.add(trait);
        return trait;
    }

    public static List<Trait> getByType(TraitType type) {
        List<Trait> out = new ArrayList<>();
        for (Trait t : TRAITS) if (t.getTraitType() == type) out.add(t);
        return out;
    }

    public static Trait getByName(String name) {
        for (Trait t : TRAITS) if (t.getName().equalsIgnoreCase(name)) return t;
        return MAX_HEALTH;
    }

    public static float getTraitValue(Map<Trait, Gene> genes, Trait trait) {
        if (trait == null || genes == null) return 0.0f;
        Gene gene = genes.get(trait);
        if (gene == null) return 0.0f;
        return gene.getExpressedValue();
    }

    public static Component translatableTrait(Trait trait) {
        return Component.translatable("trait.wildaside." + trait.getName());
    }
}