package net.farkas.wildaside.dna;

import com.google.common.collect.Multimap;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.Dominance;
import net.farkas.wildaside.dna.speed.MobSpeedResultStorage;
import net.farkas.wildaside.dna.speed.MobSpeedTesting;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.farkas.wildaside.dna.trait.Traits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
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

    public static ResourceLocation getAttributeRes(String name) {
        return new ResourceLocation("minecraft", "generic." + name);
    }

    public static UUID generateUuid(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
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

        for (Trait trait : Traits.getByType(TraitType.CORE)) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes(trait.getName()));
            if (attribute != null) {
                float traitValue = getAttributeValue(entity, attribute);
                if (trait == Traits.MOVEMENT_SPEED) {
                    if (ModConfig.ACCURATE_DNA_MOVEMENT_SPEEDS.get() && !preGen) {
                        traitValue = (float) (MobSpeedResultStorage.getSpeed(entity.getType(), "ground"));
                        if (MobSpeedTesting.EXCLUDED_MOBS.contains(entity.getType())) {
                            traitValue = (float) entity.getAttributeBaseValue(attribute);
                            System.out.println(traitValue);
                        }
                    }
                }
                genes.put(trait, new Gene(trait, traitValue, trait.baseInstability()));
            }
        }

        long seed = entity.getUUID().getLeastSignificantBits();

        putGaussianOrZero(genes, Traits.FIRE_RESISTANCE, seed, entity.fireImmune());
        putGaussianOrZero(genes, Traits.FREEZE_RESISTANCE, seed, entity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES));
        putGaussianOrZero(genes, Traits.FALL_RESISTANCE, seed, entity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE));

        if (entity.getType() == EntityType.BLAZE) {
            putAbilityWithGaussian(genes, Traits.FIRE_ABILITY, seed);
        }
        else if (entity.getType() == EntityType.ENDERMAN) {
            putAbilityWithGaussian(genes, Traits.TELEPORT_ABILITY, seed);
        }


        return genes;
    }

    private static void putGaussianOrZero(Map<Trait, Gene> genes, Trait trait, long seed, boolean condition) {
        if (condition) {
            putResistanceWithGaussian(genes, trait, seed);
        } else {
            genes.put(trait, new Gene(trait, 0.0f, trait.getInstabilityModifier()));
        }
    }

    private static void putResistanceWithGaussian(Map<Trait, Gene> genes, Trait trait, long seed) {
        genes.put(trait, new Gene(trait, deterministicGaussian(seed, trait.getName()) / 2 + 0.5f, trait.getInstabilityModifier()));
    }

    private static void putAbilityWithGaussian(Map<Trait, Gene> genes, Trait trait, long seed) {
        genes.put(trait, new Gene(trait, (deterministicGaussian(seed, trait.getName()) / 2 + trait.getInstabilityModifier()) * 100, trait.getInstabilityModifier()));
    }

    public static Map<Trait, Gene> mutateGenes(Map<Trait, Gene> baseGenes, LivingEntity entity) {
        Map<Trait, Gene> mutated = new HashMap<>();
        for (var entry : baseGenes.entrySet()) {
            mutated.put(entry.getKey(), mutateGene(entry.getValue(), entity));
        }
        return mutated;
    }

    public static Gene mutateGene(Gene gene, LivingEntity entity) {
        Allele alleleA = mutateAllele(gene.getAlleleA(), entity);
        Allele alleleB = mutateAllele(gene.getAlleleB(), entity);

        return new Gene(gene.getTrait(), alleleA, alleleB);
    }

    public static Allele mutateAllele(Allele allele, LivingEntity entity) {
        long seed = entity.getUUID().getLeastSignificantBits();
        String salt = allele.toString();

        float gaussian = deterministicGaussian(seed, salt);

        float averageMutation = -0.1f;

        float baseMutation = allele.getMutationRate();
        float mutation = gaussian * baseMutation + averageMutation;

        float newValue = allele.getValue() * (1.0f + mutation);

        float traitBaseMutation = 0.03f;

        float newMutationRate = traitBaseMutation * (1f + Math.abs(newValue) * 0.1f);

        float baseStability = allele.getStability();
        float newStability = baseStability - (Math.abs(newValue) * 0.1f) + (gaussian * 0.05f);

        newStability = Mth.clamp(newStability, 0, 100);
        Dominance newDom = generateNewDominance(allele, newMutationRate, seed, salt);

        return new Allele(newValue, newMutationRate, newStability, newDom);
    }

    private static long mix64(long x) {
        x ^= (x >>> 30);
        x *= 0xBF58476D1CE4E5B9L;
        x ^= (x >>> 27);
        x *= 0x94D049BB133111EBL;
        x ^= (x >>> 31);
        return x;
    }

    private static float hashToFloat(long seed, String salt, int index) {
        long h = seed;
        h ^= 0x9E3779B97F4A7C15L * index;
        h ^= mix64(salt.hashCode());
        h = mix64(h);

        return (h >>> 40) / (float)(1L << 24);
    }

    private static float deterministicGaussian(long seed, String salt) {
        float u1 = hashToFloat(seed, salt, 0);
        float u2 = hashToFloat(seed, salt, 1);

        u1 = Math.max(u1, 1e-12f);

        return (float)(Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2));
    }

    private static Dominance generateNewDominance(Allele allele, float mutationRate, long seed, String salt) {
        Dominance newDom = allele.getDominance();
        float dominanceFlipChance = mutationRate * 0.02f;
        float domNoise = deterministicGaussian(seed, salt + "_DOMINANCE");
        float domProb = (domNoise + 1f) * 0.5f;

        if (domProb < dominanceFlipChance) {
            newDom = deterministicDominancePick(seed, salt);
        }

        return newDom;
    }

    private static Dominance deterministicDominancePick(long seed, String salt) {
        float g = deterministicGaussian(seed, salt + "_DOMINANCE_PICK");
        float p = (g + 1f) * 0.5f;

        if (p < 0.25f) return Dominance.DOMINANT;
        if (p < 0.50f) return Dominance.RECESSIVE;
        if (p < 0.75f) return Dominance.INCOMPLETE;
        return Dominance.CO_DOMINANT;
    }
}