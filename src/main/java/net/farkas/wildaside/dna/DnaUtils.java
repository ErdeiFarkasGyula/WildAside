package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.appearance.AppearanceGeneRegistry;
import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.locus.LocusExpression;
import net.farkas.wildaside.dna.locus.LocusFlag;
import net.farkas.wildaside.dna.speed.MobSpeedResultStorage;
import net.farkas.wildaside.dna.speed.MobSpeedTesting;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitType;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class DnaUtils {
    private static final float LATENT_CHANCE = 0.35f;
    private static final float ACTIVATOR_MIN = 0.05f;

    public static String fullName(String id) {
        return WildAside.MOD_ID + "_dna_" + id;
    }

    public static ResourceLocation getAttributeRes(String name) {
        return new ResourceLocation("minecraft", "generic." + name);
    }

    public static UUID generateUuid(String name) {
        return java.util.UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

    public static float getAttributeValue(LivingEntity entity, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return 0.0f;

        return (float) instance.getBaseValue();
    }

//    public static Map<Trait, Gene> generateBaseGenes(LivingEntity entity, boolean preGen) {
//        Map<Trait, Gene> genes = new HashMap<>();
//        long seed = entity.getUUID().getLeastSignificantBits();
//
//        for (Trait trait : TraitRegistry.TRAITS) {
//            if (trait.getTraitType() == TraitType.CORE || trait == TraitRegistry.KNOCKBACK_RESISTANCE) {
//                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(DnaUtils.getAttributeRes(trait.getName()));
//                if (attribute != null) {
//                    float baseValue = DnaUtils.getAttributeValue(entity, attribute);
//
//                    if (trait == TraitRegistry.MOVEMENT_SPEED && ModConfig.ACCURATE_DNA_MOVEMENT_SPEEDS.get() && !preGen) {
//                        baseValue = (float) MobSpeedResultStorage.getSpeed(entity.getType(), "ground");
//                        if (MobSpeedTesting.EXCLUDED_MOBS.contains(entity.getType())) {
//                            baseValue = (float) entity.getAttributeBaseValue(attribute);
//                        }
//                    }
//
//                    Allele alleleA = createFloatAllele(trait, baseValue, seed, 0);
//                    Allele alleleB = createFloatAllele(trait, baseValue, seed, 1);
//
//                    genes.put(trait, new Gene(trait, alleleA, alleleB));
//                }
//            }
//        }
//
//        generateResistanceGene(genes, TraitRegistry.FIRE_RESISTANCE, seed, entity.fireImmune());
//        generateResistanceGene(genes, TraitRegistry.FREEZE_RESISTANCE, seed, entity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES));
//        generateResistanceGene(genes, TraitRegistry.FALL_RESISTANCE, seed, entity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE));
//
//        if (entity.getType() == EntityType.BLAZE) {
//            generateAbilityGene(genes, TraitRegistry.FIRE_ABILITY, seed);
//        }
//        else if (entity.getType() == EntityType.ENDERMAN) {
//            generateAbilityGene(genes, TraitRegistry.TELEPORT_ABILITY, seed);
//        }
//
//        AppearanceGeneRegistry.extract(entity, genes, seed);
//
//        return genes;
//    }

    private static Allele createFloatAllele(Trait trait, float baseValue, long seed, int index) {
        float gaussian = DnaUtils.deterministicGaussian(seed, trait.getName() + index);

        Dominance dominance = trait.getTraitType().getDominanceExpression().chooseDominance(trait, seed, index);

        float mutationRate = 0.05f * (trait.getTraitType() == TraitType.ABILITY ? 2f : 1f);

        float value = baseValue * (1 + 0.2f * gaussian);

        return new Allele(new FloatAlleleValue(value), mutationRate, dominance);
    }

    private static void generateResistanceGene(Map<Trait, Gene> genes, Trait trait, long seed, boolean condition) {
        Allele alleleA, alleleB;

        if (condition) {
            alleleA = createFloatAllele(trait, 1f, seed, 0);
            alleleB = createFloatAllele(trait, 1f, seed, 1);
        }
        else {
            alleleA = maybeMutateZeroAllele(trait, seed, 0);
            alleleB = maybeMutateZeroAllele(trait, seed, 1);
        }
        genes.put(trait, new Gene(trait, alleleA, alleleB));
    }

    private static void generateAbilityGene(Map<Trait, Gene> genes, Trait trait, long seed) {
        Allele alleleA = createFloatAllele(trait, trait.getInstabilityModifier() * 100f, seed, 0);
        Allele alleleB = createFloatAllele(trait, trait.getInstabilityModifier() * 100f, seed, 1);
        genes.put(trait, new Gene(trait, alleleA, alleleB));
    }

    private static Allele maybeMutateZeroAllele(Trait trait, long seed, int index) {
        float chance = trait.getInstabilityModifier() / 100;

        float roll = hashToFloat(seed, trait.getName() + "_mutate_zero", index);

        if (roll < chance) {
            float mutatedValue = 0.02f + 0.13f * Math.abs(deterministicGaussian(seed, trait.getName() + "_zero_val" + index));

            float mutationRate = trait.getInstabilityModifier() * 0.5f;
            Dominance dom = deterministicDominancePick(seed, trait.getName() + "_zero_dom" + index);

            return new Allele(new FloatAlleleValue(mutatedValue), mutationRate, dom);
        }

        return new Allele(new FloatAlleleValue(0.0f), 0f, Dominance.RECESSIVE);
    }

    public static long mix64(long x) {
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

        return (h >>> 40) / (float) (1L << 24);
    }

    public static float deterministicGaussian(long seed, String salt) {
        float u1 = hashToFloat(seed, salt, 0);
        float u2 = hashToFloat(seed, salt, 1);

        u1 = Math.max(u1, 1e-12f);

        return (float) (Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2));
    }

    public static Dominance generateNewDominance(Allele allele, float mutationRate, long seed, String salt) {
        Dominance newDom = allele.getDominance();
        float dominanceFlipChance = mutationRate * 0.02f;
        float domNoise = deterministicGaussian(seed, salt + "_DOMINANCE");
        float domProb = (domNoise + 1f) * 0.5f;

        if (domProb < dominanceFlipChance) {
            newDom = deterministicDominancePick(seed, salt);
        }

        return newDom;
    }

    public static Dominance deterministicDominancePick(long seed, String salt) {
        float g = deterministicGaussian(seed, salt + "_DOMINANCE_PICK");
        float p = (g + 1f) * 0.5f;

        if (p < 0.25f) return Dominance.DOMINANT;
        if (p < 0.50f) return Dominance.RECESSIVE;
        if (p < 0.75f) return Dominance.INCOMPLETE;
        return Dominance.CO_DOMINANT;
    }

    public static String getFormattedFloatString(float value) {
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

        if (tag.getBoolean(REVEAL_TRAITS)) return 0;

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

    public static Map<Trait, List<GeneLocus>> generateBaseLoci(LivingEntity entity) {
        Map<Trait, List<GeneLocus>> out = new HashMap<>();
        long seed = entity.getUUID().getLeastSignificantBits();

        AppearanceGeneRegistry.extract(entity, out, seed);

        if (TraitRegistry.MOVEMENT_SPEED != null) {
            float baseValue = getSafeBaseAttributeValue(entity, ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes("movement_speed")));

            if (ModConfig.ACCURATE_DNA_MOVEMENT_SPEEDS.get()) {
                baseValue = (float) MobSpeedResultStorage.getSpeed(entity.getType(), "ground");
                if (MobSpeedTesting.EXCLUDED_MOBS.contains(entity.getType())) {
                    baseValue = getSafeBaseAttributeValue(entity, ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes("movement_speed")));
                }
            }

            out.put(TraitRegistry.MOVEMENT_SPEED, List.of(
                    locus("move_base", TraitRegistry.MOVEMENT_SPEED, baseValue, seed, 0),
                    locus("move_tendon", TraitRegistry.MOVEMENT_SPEED, baseValue, seed, 2),
                    locus("move_aero", TraitRegistry.MOVEMENT_SPEED, baseValue, seed, 4)
            ));
        }

        out.put(TraitRegistry.FIRE_RESISTANCE,
                resistanceLoci(entity.fireImmune() ? 1f : 0f, TraitRegistry.FIRE_RESISTANCE, "fire", seed, true));
        out.put(TraitRegistry.FREEZE_RESISTANCE,
                resistanceLoci(entity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES) ? 1f : 0f, TraitRegistry.FREEZE_RESISTANCE, "freeze", seed, false));
        out.put(TraitRegistry.FALL_RESISTANCE,
                resistanceLoci(entity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE) ? 1f : 0f, TraitRegistry.FALL_RESISTANCE, "fall", seed, false));
        out.put(TraitRegistry.EXPLOSION_RESISTANCE,
                resistanceLoci(0f, TraitRegistry.EXPLOSION_RESISTANCE, "explosion", seed, false));

        if (!out.containsKey(TraitRegistry.KNOCKBACK_RESISTANCE)) {
            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes("knockback_resistance"));
            float baseValue = attr != null ? getSafeBaseAttributeValue(entity, attr) : 0f;
            out.put(TraitRegistry.KNOCKBACK_RESISTANCE, List.of(
                    locus("knockback_resistance", TraitRegistry.KNOCKBACK_RESISTANCE, baseValue, seed, 0)
            ));
        }

        if (entity.getType() == EntityType.BLAZE) {
            float potBase = TraitRegistry.FIRE_ABILITY.getInstabilityModifier() * 100f;
            out.put(TraitRegistry.FIRE_ABILITY, List.of(
                    locus("fire_act", TraitRegistry.FIRE_ABILITY, 1f, seed, 0, Set.of(LocusFlag.ACTIVATOR)),
                    locus("fire_pot", TraitRegistry.FIRE_ABILITY, potBase, seed, 2, Set.of(LocusFlag.POTENCY)),
                    locus("fire_side", TraitRegistry.FIRE_ABILITY, 0.1f, seed, 4, Set.of(LocusFlag.SIDE_EFFECT))
            ));
        }

        if (entity.getType() == EntityType.ENDERMAN) {
            float potBase = TraitRegistry.TELEPORT_ABILITY.getInstabilityModifier() * 100f;
            out.put(TraitRegistry.TELEPORT_ABILITY, List.of(
                    locus("tele_act", TraitRegistry.TELEPORT_ABILITY, 1f, seed, 0, Set.of(LocusFlag.ACTIVATOR)),
                    locus("tele_pot", TraitRegistry.TELEPORT_ABILITY, potBase, seed, 2, Set.of(LocusFlag.POTENCY)),
                    locus("tele_side", TraitRegistry.TELEPORT_ABILITY, 0.1f, seed, 4, Set.of(LocusFlag.SIDE_EFFECT))
            ));
        }

        for (Trait trait : TraitRegistry.TRAITS) {
            if (out.containsKey(trait)) continue;
            if (trait.getTraitType() == TraitType.CORE || trait == TraitRegistry.KNOCKBACK_RESISTANCE) {
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes(trait.getName()));
                float baseValue = attribute != null ? getSafeBaseAttributeValue(entity, attribute) : 0f;
                out.put(trait, List.of(locus(trait.getName(), trait, baseValue, seed, 0)));
            }
        }

        return out;
    }

    private static List<GeneLocus> resistanceLoci(float baseVal, Trait trait, String prefix, long seed, boolean includeRegulator) {
        List<GeneLocus> list = new ArrayList<>();
        list.add(locus(prefix + "_base", trait, baseVal, seed, 0));
        if (includeRegulator) {
            list.add(locus(prefix + "_reg", trait, 0.25f, seed, 2, Set.of(LocusFlag.REGULATOR)));
        }
        boolean latent = hashToFloat(seed, prefix + "_latent", 3) < LATENT_CHANCE;
        float actVal = latent ? 0f : ACTIVATOR_MIN + 0.1f * hashToFloat(seed, prefix + "_actv_val", 4);
        list.add(locus(prefix + "_act", trait, actVal, seed, 4, Set.of(LocusFlag.ACTIVATOR)));
        return list;
    }

    private static GeneLocus locus(String id, Trait trait, float baseValue, long seed, int index) {
        return locus(id, trait, baseValue, seed, index, Set.of());
    }

    private static GeneLocus locus(String id, Trait trait, float baseValue, long seed, int index, Set<LocusFlag> flags) {
        Allele a = createFloatAllele(trait, baseValue, seed, index);
        Allele b = createFloatAllele(trait, baseValue, seed, index + 1);
        return new GeneLocus(id, a, b, flags, trait.getInstabilityModifier());
    }

    public static AlleleValue getExpressed(Map<Trait, List<GeneLocus>> loci, Trait trait) {
        return LocusExpression.express(trait, loci.get(trait));
    }

    public static Gene asGene(Trait trait, List<GeneLocus> loci) {
        if (loci == null || loci.isEmpty()) return null;
        GeneLocus l = loci.get(0);
        return new Gene(trait, l.getAlleleA(), l.getAlleleB());
    }

    public static float getSafeBaseAttributeValue(LivingEntity entity, Attribute attribute) {
        var inst = entity.getAttribute(attribute);
        return inst == null ? 0f : (float) inst.getBaseValue();
    }
}