package net.farkas.wildaside.dna;

import com.google.common.collect.Multimap;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.config.Config;
import net.farkas.wildaside.dna.speed.MobSpeedResultStorage;
import net.farkas.wildaside.dna.speed.MobSpeedTesting;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
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

        float result = (float) ((base * multBase + add) * multTotal);
        if (result == -1) return 0;
        return result;
    }

    public static float getAttributeValue(LivingEntity entity, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return 0.0f;

        return (float) instance.getBaseValue();
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

    public static Map<Trait, Gene> generateBaseGenes(LivingEntity entity, boolean preGen) {
        Map<Trait, Gene> genes = new HashMap<>();

        for (Trait trait : Traits.getByType(TraitTypes.CORE)) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(getAttribute(trait.name()));
            if (attribute != null) {
                float traitValue = getAttributeValue(entity, attribute);
                if (trait == Traits.MOVEMENT_SPEED) {
                    if (Config.ACCURATE_DNA_MOVEMENT_SPEEDS.get() && !preGen) {
                        traitValue = (float) (MobSpeedResultStorage.getSpeed(entity.getType(), "ground"));
                        System.out.println("MOVESPEEEDA: " + traitValue);
                        if (MobSpeedTesting.EXCLUDED_MOBS.contains(entity.getType())) {
                            traitValue = (float) entity.getAttributeBaseValue(attribute);
                        }
                    }
                }
                genes.put(trait, new Gene(trait, traitValue, trait.baseInstability()));
            }
        }

        long seed = entity.getUUID().getLeastSignificantBits();

        if (entity.fireImmune()) {
            putResistanceWithGaussian(genes, Traits.FIRE_RESISTANCE, seed);
        }
        if (entity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)) {
            putResistanceWithGaussian(genes, Traits.FREEZE_RESISTANCE, seed);
        }
        if (entity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
            putResistanceWithGaussian(genes, Traits.FALL_RESISTANCE, seed);
        }

        if (entity.getType() == EntityType.BLAZE) {
            putAbilityWithGaussian(genes, Traits.FIRE_ABILITY, seed);
        }

        return genes;
    }

    private static void putResistanceWithGaussian(Map<Trait, Gene> genes, Trait trait, long seed) {
        genes.put(trait, new Gene(trait, deterministicGaussian(seed, trait.name()) / 2 + 0.5f, trait.baseInstability()));
    }

    private static void putAbilityWithGaussian(Map<Trait, Gene> genes, Trait trait, long seed) {
        genes.put(trait, new Gene(trait, (deterministicGaussian(seed, trait.name()) / 2 + trait.baseInstability()) * 100, trait.baseInstability()));
    }

    public static Map<Trait, Gene> mutateGenes(Map<Trait, Gene> baseGenes, LivingEntity entity) {
        Map<Trait, Gene> mutated = new HashMap<>();
        for (var entry : baseGenes.entrySet()) {
            mutated.put(entry.getKey(), mutateGene(entry.getValue(), entity));
        }
        return mutated;
    }

    public static Gene mutateGene(Gene gene, LivingEntity entity) {
        long seed = entity.getUUID().getLeastSignificantBits();
        String salt = gene.trait().name();

        float gaussian = deterministicGaussian(seed, salt);

        float averageMutation = -0.1f;
        float baseVariance = 0.5f;

        float mutation = gaussian * baseVariance + averageMutation;
        float newValue = gene.value() * (1.0f + mutation);

        System.out.printf("Trait:%s  Value:%.3f  Mutation:%.3f  →  %.3f%n", gene.trait(), gene.value(), mutation, newValue);

        return new Gene(gene.trait(), newValue, gene.stabilityCost());
    }

    private static float hashToFloat(long seed, String salt, int index) {
        long hash = seed ^ (index * 0x9E3779B97F4A7C15L);
        for (char c : salt.toCharArray()) {
            hash = hash * 31 + c;
        }

        hash ^= (hash >>> 33);
        hash *= 0xff51afd7ed558ccdL;
        hash ^= (hash >>> 33);
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= (hash >>> 33);

        return (float) ((hash & 0xFFFFFFFFL) / (double) 0xFFFFFFFFL);
    }

    private static float deterministicGaussian(long seed, String salt) {
        float sum = 0f;
        for (int i = 0; i < 6; i++) {
            sum += hashToFloat(seed, salt, i);
        }
        return (sum / 6f - 0.5f) * 2f;
    }

    public static ResourceLocation getAttribute(String name) {
        return new ResourceLocation("minecraft", "generic." + name);
    }

    public static UUID getUuid(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }
}