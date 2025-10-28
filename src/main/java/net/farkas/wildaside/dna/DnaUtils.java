package net.farkas.wildaside.dna;

import com.google.common.collect.Multimap;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.speed.MobSpeedResultStorage;
import net.farkas.wildaside.dna.speed.MobSpeedTesting;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class DnaUtils {
    public static final String DNA_PREFIX = WildAside.MOD_ID + "_dna_";

    public static String fullName(String id) {
        return DNA_PREFIX + id;
    }

//    public static void clearDnaEffects(Entity entity) {
//        if (entity instanceof LivingEntity livingEntity) {
//            for (AttributeInstance attr : livingEntity.getAttributes().getSyncableAttributes()) {
//                for (Traits.Core stat : Traits.Core.values()) {
//                    UUID id = DnaUtils.getUuid(DNA_PREFIX + stat.name());
//                    attr.removeModifier(id);
//                    attr.removePermanentModifier(id);
//                }
//            }
//
//            livingEntity.getActiveEffects().stream()
//                    .filter(e -> e.getDescriptionId().contains(DNA_PREFIX))
//                    .toList()
//                    .forEach(mobEffectInstance -> livingEntity.removeEffect(mobEffectInstance.getEffect()));
//
//            List<MobEffectInstance> toRemoveEffects = new ArrayList<>();
//            for (MobEffectInstance instance : livingEntity.getActiveEffects()) {
//                String name = instance.getDescriptionId();
//                String dataName = DNA_PREFIX + name.split("\\.")[name.split("\\.").length - 1].toUpperCase();
//
//                if (livingEntity.getPersistentData().contains(dataName)) {
//                    toRemoveEffects.add(instance);
//                }
//            }
//
//            toRemoveEffects.forEach(effect -> livingEntity.removeEffect(effect.getEffect()));
//
//            for (AttributeInstance attr : livingEntity.getAttributes().getSyncableAttributes()) {
//                List<AttributeModifier> toRemove = new ArrayList<>();
//                for (AttributeModifier mod : attr.getModifiers()) {
//                    System.out.println("ATTR: " + mod.getId());
//                    if (mod.getId().toString().contains(DNA_PREFIX)) {
//                        toRemove.add(mod);
//                    }
//                }
//                toRemove.forEach(attributeModifier -> attr.removePermanentModifier(attributeModifier.getId()));
//                toRemove.forEach(attributeModifier -> attr.removeModifier(attributeModifier.getId()));
//            }
//
//            CompoundTag tag = livingEntity.getPersistentData();
//            List<String> keysToRemove = tag.getAllKeys().stream().filter(k -> k.startsWith(DNA_PREFIX)).toList();
//            keysToRemove.forEach(tag::remove);
//        }
//
//    }

    public static float getStableAttributeValue(LivingEntity entity, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return 0.0f;

        double base = instance.getBaseValue();
        double add = 0.0;
        double multBase = 1.0;
        double multTotal = 1.0;

        for (AttributeModifier mod : instance.getModifiers()) {
            String name = mod.getName().toLowerCase(Locale.ROOT);

            if (name.contains("potion") || name.contains("effect") || name.contains("temporary")) continue;
            if (isEquipmentModifier(entity, mod)) continue;

            switch (mod.getOperation()) {
                case ADDITION -> add += mod.getAmount();
                case MULTIPLY_BASE -> multBase += mod.getAmount();
                case MULTIPLY_TOTAL -> multTotal += mod.getAmount();
            }
        }

        return (float) ((base * multBase + add) * multTotal);
    }

    private static boolean isEquipmentModifier(LivingEntity entity, AttributeModifier modifier) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                Multimap<Attribute, AttributeModifier> itemModifiers = stack.getAttributeModifiers(slot);
                if (itemModifiers.containsValue(modifier)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Map<Trait, Gene> generateDefaultCoreGenes(LivingEntity entity) {
        Map<Trait, Gene> genes = new HashMap<>();
        for (Trait trait : Traits.getByType(TraitTypes.CORE)) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(getAttribute(trait.name()));
            float traitValue = getStableAttributeValue(entity, attribute);
            genes.put(trait, new Gene(trait, traitValue, trait.baseInstability()));
        }
        return genes;
    }

    public static Map<Trait, Gene> generateBaseGenes(LivingEntity entity) {
        Map<Trait, Gene> genes = new HashMap<>();
        for (Trait trait : Traits.getByType(TraitTypes.CORE)) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(getAttribute(trait.name()));
            if (attribute != null) {
                float traitValue = getStableAttributeValue(entity, attribute);
                if (trait == Traits.MOVEMENT_SPEED) {
                    traitValue = (float) (MobSpeedResultStorage.getSpeed(entity.getType(), "ground") / 43.17f);
                    if (MobSpeedTesting.EXCLUDED_MOBS.contains(entity.getType())) {
                        traitValue = (float) entity.getAttributeBaseValue(attribute);
                    }
                    System.out.println("TRAIT VALUE: " + traitValue);
                }
                genes.put(trait, new Gene(trait, traitValue, trait.baseInstability()));
            }
        }

        RandomSource random = RandomSource.create(entity.getUUID().getLeastSignificantBits());
        if (entity.fireImmune()) {
            Trait trait = Traits.FIRE_RESISTANCE;
            genes.put(trait, new Gene(trait, random.nextFloat(), trait.baseInstability()));
        }
        if (entity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)) {
            Trait trait = Traits.FREEZE_RESISTANCE;
            genes.put(trait, new Gene(trait, random.nextFloat(), trait.baseInstability()));
        }
        if (entity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
            Trait trait = Traits.FALL_RESISTANCE;
            genes.put(trait, new Gene(trait, random.nextFloat(), trait.baseInstability()));
        }

        return genes;
    }

    public static Map<Trait, Gene> mutateGenes(Map<Trait, Gene> baseGenes, LivingEntity entity) {
        RandomSource random = RandomSource.create(entity.getUUID().getLeastSignificantBits());
        Map<Trait, Gene> mutated = new HashMap<>();
        for (var entry : baseGenes.entrySet()) {
            mutated.put(entry.getKey(), mutateGene(entry.getValue(), random));
        }
        return mutated;
    }

    private static Gene mutateGene(Gene gene, RandomSource random) {
        float averageMutation = -0.1f;
        float baseVariance = 0.25f;
        float strongMutationChance = 0.05f;
        float strongMultiplier = 0.5f;

        float mutation = (float) random.nextGaussian() * baseVariance + averageMutation;

        if (random.nextFloat() < strongMutationChance) {
            float direction = random.nextBoolean() ? 1.0f : -1.0f;
            mutation += direction * (random.nextFloat() * strongMultiplier);
        }

        float newValue = gene.value() * (1.0f + mutation);

        return new Gene(gene.trait(), newValue, gene.stabilityCost());
    }

    public static ResourceLocation getAttribute(String name) {
        return new ResourceLocation("minecraft", "generic." + name);
    }

    public static UUID getUuid(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }
}