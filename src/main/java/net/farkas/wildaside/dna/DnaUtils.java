package net.farkas.wildaside.dna;

import com.google.common.collect.Multimap;
import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.dominance.Dominance;
import net.farkas.wildaside.dna.speed.MobSpeedResultStorage;
import net.farkas.wildaside.dna.speed.MobSpeedTesting;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.farkas.wildaside.dna.trait.Traits;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class DnaUtils {
    public static final String DNA_PREFIX = WildAside.MOD_ID + "_dna_";

    public static String fullName(String id) {
        return DNA_PREFIX + id;
    }

    public static ResourceLocation getAttributeRes(String name) {
        return new ResourceLocation("minecraft", "generic." + name);
    }

    public static UUID generateUuid(String name) {
        return java.util.UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
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
        long seed = entity.getUUID().getLeastSignificantBits();

        for (Trait trait : Traits.TRAITS) {
            if (trait.getTraitType() == TraitType.CORE || trait == Traits.KNOCKBACK_RESISTANCE) {
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(DnaUtils.getAttributeRes(trait.getName()));
                if (attribute != null) {
                    float baseValue = DnaUtils.getAttributeValue(entity, attribute);

                    if (trait == Traits.MOVEMENT_SPEED && ModConfig.ACCURATE_DNA_MOVEMENT_SPEEDS.get() && !preGen) {
                        baseValue = (float) MobSpeedResultStorage.getSpeed(entity.getType(), "ground");
                        if (MobSpeedTesting.EXCLUDED_MOBS.contains(entity.getType())) {
                            baseValue = (float) entity.getAttributeBaseValue(attribute);
                        }
                    }

                    Allele alleleA = createAllele(trait, baseValue, seed, 0);
                    Allele alleleB = createAllele(trait, baseValue, seed, 1);

                    genes.put(trait, new Gene(trait, alleleA, alleleB));
                }
            }
        }

        generateResistanceGene(genes, Traits.FIRE_RESISTANCE, seed, entity.fireImmune());
        generateResistanceGene(genes, Traits.FREEZE_RESISTANCE, seed, entity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES));
        generateResistanceGene(genes, Traits.FALL_RESISTANCE, seed, entity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE));

        if (entity.getType() == EntityType.BLAZE) {
            generateAbilityGene(genes, Traits.FIRE_ABILITY, seed);
        } else if (entity.getType() == EntityType.ENDERMAN) {
            generateAbilityGene(genes, Traits.TELEPORT_ABILITY, seed);
        }

        return genes;
    }

    private static Allele createAllele(Trait trait, float baseValue, long seed, int index) {
        float gaussian = DnaUtils.deterministicGaussian(seed, trait.getName() + index);

        Dominance dominance = trait.getTraitType().getDominanceExpression().chooseDominance(trait, seed, index);

        float mutationRate = 0.05f * (trait.getTraitType() == TraitType.ABILITY ? 2f : 1f);

        float stability = trait.getInstabilityModifier() * (0.5f + gaussian / 2f);

        float value = baseValue * (1 + 0.2f * gaussian);

        return new Allele(value, mutationRate, stability, dominance);
    }

    private static void generateResistanceGene(Map<Trait, Gene> genes, Trait trait, long seed, boolean condition) {
        Allele alleleA, alleleB;
        if (condition) {
            alleleA = createAllele(trait, 1f, seed, 0);
            alleleB = createAllele(trait, 1f, seed, 1);
        } else {
            alleleA = new Allele(0f, 0f, trait.getInstabilityModifier(), Dominance.RECESSIVE);
            alleleB = new Allele(0f, 0f, trait.getInstabilityModifier(), Dominance.RECESSIVE);
        }
        genes.put(trait, new Gene(trait, alleleA, alleleB));
    }

    private static void generateAbilityGene(Map<Trait, Gene> genes, Trait trait, long seed) {
        Allele alleleA = createAllele(trait, trait.getInstabilityModifier() * 100f, seed, 0);
        Allele alleleB = createAllele(trait, trait.getInstabilityModifier() * 100f, seed, 1);
        genes.put(trait, new Gene(trait, alleleA, alleleB));
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

    public static float hashToFloat(long seed, String salt, int index) {
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

    public static String getFormattedString(float value) {
        return String.format("%.2f", value);
    }

    public static long getBloodSamplingTick(CompoundTag tag) {
        return tag.getLong(BLOOD_CREATION_TICK);
    }

    public static void setBloodSamplingTick(CompoundTag tag, long value) {
        tag.putLong(BLOOD_CREATION_TICK, value);
    }

    public static void resetBloodSamplingTick(CompoundTag tag) {
        setBloodSamplingTick(tag, 0);
    }

    public static void saveBloodSamplingTick(CompoundTag tag, ServerLevel level) {
        setBloodSamplingTick(tag, level.getGameTime());
    }

    public static long getBloodFreezerTicks(CompoundTag tag) {
        return tag.getLong(BLOOD_FREEZER_TICKS);
    }

    public static void setBloodFreezerTicks(CompoundTag tag, long value) {
        tag.putLong(BLOOD_FREEZER_TICKS, value);
    }

    public static void increaseBloodFreezerTicks(CompoundTag tag, long value) {
        setBloodFreezerTicks(tag, getBloodFreezerTicks(tag) + value);
    }

    public static void resetBloodFreezerTicks(CompoundTag tag) {
        setBloodFreezerTicks(tag, 0);
    }

    public static long getFrozenItemEffectiveAge(CompoundTag tag, Level level) {
        if (level == null || tag == null) return 0;

        long currentTime = level.getGameTime();
        long creationTime = getBloodSamplingTick(tag);
        long freezerTicks = getBloodFreezerTicks(tag);

        if (creationTime == 0) {
            return 0;
        }

        return currentTime - creationTime - freezerTicks;
    }

    public static boolean handleContaminatedSampleTooltip(List<Component> tooltip, boolean multipleSources, boolean clotted, boolean dirty) {
        if (multipleSources || clotted || dirty) {
            tooltip.add(
                    Component.translatable("dna.wildaside.sample_unusable")
                            .append(Component.literal(": "))
                            .withStyle(ChatFormatting.RED)
            );

            if (multipleSources) {
                tooltip.add(Component.literal("- ")
                        .append(Component.translatable("dna.wildaside.multiple_sources"))
                );
            }

            if (clotted) {
                tooltip.add(Component.literal("- ")
                        .append(Component.translatable("dna.wildaside.blood_clotted"))
                );
            }

            if (dirty) {
                tooltip.add(Component.literal("- ")
                        .append(Component.translatable("dna.wildaside.blood_contaminated"))
                );
            }
            return true;
        }
        return false;
    }
}