package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.config.ModConfig;
import net.farkas.wildaside.dna.appearance.AppearanceGeneRegistry;
import net.farkas.wildaside.dna.chromosome.Genome;
import net.farkas.wildaside.dna.expression.*;
import net.farkas.wildaside.dna.sequence.components.CodingRegion;
import net.farkas.wildaside.dna.sequence.CombineMethod;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.sequence.GeneSource;
import net.farkas.wildaside.dna.sequence.components.Activator;
import net.farkas.wildaside.dna.sequence.components.Enhancer;
import net.farkas.wildaside.dna.sequence.components.Regulator;
import net.farkas.wildaside.dna.sequence.components.Silencer;
import net.farkas.wildaside.dna.speed.MobSpeedResultStorage;
import net.farkas.wildaside.dna.speed.MobSpeedTesting;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.farkas.wildaside.dna.trait.TraitType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class DnaUtils {
    public static String fullName(String id) {
        return WildAside.MOD_ID + "_dna_" + id;
    }

    public static ResourceLocation getAttributeRes(String name) {
        return new ResourceLocation("minecraft", "generic." + name);
    }

    public static UUID generateUuid(String name) {
        return java.util.UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

    public static Genome generateBaseGenome(LivingEntity entity) {
        long seed = entity.getUUID().getLeastSignificantBits();
        Genome genome = new Genome(entity.getType());

        AppearanceGeneRegistry.extract(entity, genome, seed);

        if (TraitRegistry.MOVEMENT_SPEED != null) {
            float baseValue = getSafeBaseAttributeValue(entity, ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes("movement_speed")));

            if (ModConfig.accurateDnaMovementSpeeds) {
                baseValue = (float) MobSpeedResultStorage.getSpeed(entity.getType(), "ground");
                if (MobSpeedTesting.EXCLUDED_MOBS.contains(entity.getType())) {
                    baseValue = getSafeBaseAttributeValue(entity, ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes("movement_speed")));
                }
            }

            GeneSequence matSeq = createMovementSequence(TraitRegistry.MOVEMENT_SPEED, baseValue, seed, "mat");
            GeneSequence patSeq = createMovementSequence(TraitRegistry.MOVEMENT_SPEED, baseValue, seed, "pat");

            genome.getMaternal().getChromosome(TraitRegistry.MOVEMENT_SPEED.getTraitType().getChromosomeType())
                    .setGeneSequence(TraitRegistry.MOVEMENT_SPEED, matSeq);
            genome.getPaternal().getChromosome(TraitRegistry.MOVEMENT_SPEED.getTraitType().getChromosomeType())
                    .setGeneSequence(TraitRegistry.MOVEMENT_SPEED, patSeq);
        }

        addResistance(genome, entity, TraitRegistry.FIRE_RESISTANCE, entity.fireImmune(), seed);
        addResistance(genome, entity, TraitRegistry.FREEZE_RESISTANCE, entity.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES), seed);
        addResistance(genome, entity, TraitRegistry.FALL_RESISTANCE, entity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE), seed);
        addResistance(genome, entity, TraitRegistry.EXPLOSION_RESISTANCE, false, seed);

        if (TraitRegistry.KNOCKBACK_RESISTANCE != null) {
            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes("knockback_resistance"));
            float baseValue = attr != null ? getSafeBaseAttributeValue(entity, attr) : 0f;

            GeneSequence matSeq = createKnockbackSequence(TraitRegistry.KNOCKBACK_RESISTANCE, baseValue, seed, "mat");
            GeneSequence patSeq = createKnockbackSequence(TraitRegistry.KNOCKBACK_RESISTANCE, baseValue, seed, "pat");

            genome.getMaternal().getChromosome(TraitRegistry.KNOCKBACK_RESISTANCE.getTraitType().getChromosomeType())
                    .setGeneSequence(TraitRegistry.KNOCKBACK_RESISTANCE, matSeq);
            genome.getPaternal().getChromosome(TraitRegistry.KNOCKBACK_RESISTANCE.getTraitType().getChromosomeType())
                    .setGeneSequence(TraitRegistry.KNOCKBACK_RESISTANCE, patSeq);
        }

        if (entity.getType() == EntityType.BLAZE && TraitRegistry.FIRE_ABILITY != null) {
            float potBase = TraitRegistry.FIRE_ABILITY.getInstabilityModifier() * 100f;
            GeneSequence matSeq = createFireAbilitySequence(TraitRegistry.FIRE_ABILITY, potBase, seed, "mat");
            GeneSequence patSeq = createFireAbilitySequence(TraitRegistry.FIRE_ABILITY, potBase, seed, "pat");

            genome.getMaternal().getChromosome(TraitRegistry.FIRE_ABILITY.getTraitType().getChromosomeType())
                    .setGeneSequence(TraitRegistry.FIRE_ABILITY, matSeq);
            genome.getPaternal().getChromosome(TraitRegistry.FIRE_ABILITY.getTraitType().getChromosomeType())
                    .setGeneSequence(TraitRegistry.FIRE_ABILITY, patSeq);
        }

        if (entity.getType() == EntityType.ENDERMAN && TraitRegistry.TELEPORT_ABILITY != null) {
            float potBase = TraitRegistry.TELEPORT_ABILITY.getInstabilityModifier() * 100f;
            GeneSequence matSeq = createTeleportAbilitySequence(TraitRegistry.TELEPORT_ABILITY, potBase, seed, "mat");
            GeneSequence patSeq = createTeleportAbilitySequence(TraitRegistry.TELEPORT_ABILITY, potBase, seed, "pat");

            genome.getMaternal().getChromosome(TraitRegistry.TELEPORT_ABILITY.getTraitType().getChromosomeType())
                    .setGeneSequence(TraitRegistry.TELEPORT_ABILITY, matSeq);
            genome.getPaternal().getChromosome(TraitRegistry.TELEPORT_ABILITY.getTraitType().getChromosomeType())
                    .setGeneSequence(TraitRegistry.TELEPORT_ABILITY, patSeq);
        }

        for (Trait trait : TraitRegistry.TRAITS) {
            if (trait.getTraitType() == TraitType.CORE && trait != TraitRegistry.MOVEMENT_SPEED && trait != TraitRegistry.KNOCKBACK_RESISTANCE) {
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(getAttributeRes(trait.getName()));
                float baseValue = attribute != null ? getSafeBaseAttributeValue(entity, attribute) : 0f;

                GeneSequence matSeq = createCoreSequence(trait, baseValue, seed, "mat");
                GeneSequence patSeq = createCoreSequence(trait, baseValue, seed, "pat");

                genome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType())
                        .setGeneSequence(trait, matSeq);
                genome.getPaternal().getChromosome(trait.getTraitType().getChromosomeType())
                        .setGeneSequence(trait, patSeq);
            }
        }

        return genome;
    }

    private static final float DOMINANCE_CORRECTION = 0.9f;

    private static GeneSequence createMovementSequence(Trait trait, float baseValue, long seed, String salt) {
        float target = baseValue * DOMINANCE_CORRECTION;

        float var1 = deterministicGaussian(seed, "move_1_" + salt) * 0.15f;
        float var2 = deterministicGaussian(seed, "move_2_" + salt) * 0.15f;
        float var3 = deterministicGaussian(seed, "move_3_" + salt) * 0.15f;

        GeneSequence.Builder builder = GeneSequence.builder()
                .trait(trait);

        builder.activator(new Activator("move_act", ActivationCondition.ALWAYS, 1f))
                .codingRegion(new CodingRegion("move_base", target * 0.6f * (1f + var1), CombineMethod.ADD))
                .codingRegion(new CodingRegion("move_tendon", target * 0.3f * (1f + var2), CombineMethod.ADD))
                .codingRegion(new CodingRegion("move_aero", target * 0.1f * (1f + var3), CombineMethod.ADD))
                .source(GeneSource.NATURAL);

        float envRoll = hashToFloat(seed, "move_env_" + salt, 2);
        if (envRoll > 0.9f) {
            builder.activator(new Activator("move_night_act", ActivationCondition.IS_NIGHT, 0f))
                    .codingRegion(new CodingRegion("move_night", target * 0.15f, CombineMethod.ADD))
                    .activator(new Activator("move_night_reset", ActivationCondition.ALWAYS, 1f));
        } else if (envRoll > 0.8f) {
            builder.activator(new Activator("move_day_act", ActivationCondition.IS_DAY, 0f))
                    .codingRegion(new CodingRegion("move_sun", target * 0.15f, CombineMethod.ADD))
                    .activator(new Activator("move_day_reset", ActivationCondition.ALWAYS, 1f));
        }

//        float regVar = hashToFloat(seed, "move_reg_" + salt, 0);
//        builder.regulator(new Regulator("move_reg", RegulationType.CAP_MAX, 0.15f + regVar * 0.05f));

        if (hashToFloat(seed, "move_enh_" + salt, 1) > 0.5f) {
            float boost = 1.05f + hashToFloat(seed, "move_enh_val_" + salt, 2) * 0.15f;
            builder.enhancer(new Enhancer("move_boost", ActivationCondition.SPRINTING, 0f, boost, 0f));
        }

        if (hashToFloat(seed, "move_sil_" + salt, 3) > 0.8f) {
            float damp = 0.7f + hashToFloat(seed, "move_sil_val_" + salt, 4) * 0.2f;
            builder.silencer(new Silencer("move_damp", ActivationCondition.IN_WATER, 0f, damp, 0f));
        }

        if (hashToFloat(seed, "move_adrenaline_" + salt, 1) > 0.95f) {
            builder.activator(new Activator("move_adrenaline_act", ActivationCondition.IN_COMBAT, 1))
                    .codingRegion(new CodingRegion("move_panic", target * 0.2f, CombineMethod.ADD))
                    .activator(new Activator("move_adrenaline_reset", ActivationCondition.ALWAYS, 1f));
        }

        return builder.build();
    }

    private static GeneSequence createKnockbackSequence(Trait trait, float baseValue, long seed, String salt) {
        float target = baseValue * DOMINANCE_CORRECTION;

        float var1 = deterministicGaussian(seed, "kb_1_" + salt) * 0.2f;
        float var2 = deterministicGaussian(seed, "kb_2_" + salt) * 0.2f;

        GeneSequence.Builder builder = GeneSequence.builder()
                .trait(trait);

        builder.activator(new Activator("kb_act", ActivationCondition.ALWAYS, 1f))
                .codingRegion(new CodingRegion("knockback_base", target * 0.6f * (1f + var1), CombineMethod.ADD))
                .codingRegion(new CodingRegion("knockback_mass", target * 0.4f * (1f + var2), CombineMethod.ADD))
                .source(GeneSource.NATURAL);

        if (hashToFloat(seed, "kb_iron_" + salt, 0) > 0.9f) {
            builder.activator(new Activator("kb_iron_act", ActivationCondition.HEALTH_BELOW, 0.25f))
                    .codingRegion(new CodingRegion("kb_iron_will", 0.3f, CombineMethod.ADD))
                    .activator(new Activator("kb_iron_reset", ActivationCondition.ALWAYS, 1f));
        }

        if (hashToFloat(seed, "kb_sil_" + salt, 2) > 0.6f) {
            float damp = 0.6f + hashToFloat(seed, "kb_sil_val_" + salt, 3) * 0.3f;
            builder.silencer(new Silencer("kb_dampen", ActivationCondition.IN_WATER, 0f, damp, 0f));
        }

        return builder.build();
    }

    private static GeneSequence createFireAbilitySequence(Trait trait, float potBase, long seed, String salt) {
        float baseCooldown = 200f;
        float reduction = potBase * DOMINANCE_CORRECTION;
        float target = Math.max(40f, baseCooldown - reduction);

        float var1 = deterministicGaussian(seed, "fire_1_" + salt) * 0.2f;

        GeneSequence.Builder builder = GeneSequence.builder()
                .trait(trait)
                .activator(new Activator("fire_act", ActivationCondition.ALWAYS, 1f))
                .codingRegion(new CodingRegion("fire_charge", target * 0.6f * (1f - var1), CombineMethod.ADD));

        if (hashToFloat(seed, "fire_nether_enh_" + salt, 0) > 0.5f) {
            builder.addComponent(new Silencer("fire_nether", ActivationCondition.IN_NETHER, 0f, 0.80f, 0f));
        }

        builder.source(GeneSource.NATURAL);

        float regVal = 20f + hashToFloat(seed, "fire_reg_" + salt, 0) * 20f;
        builder.regulator(new Regulator("fire_reg", RegulationType.CAP_MIN, regVal));

        if (hashToFloat(seed, "fire_enh_" + salt, 1) > 0.4f) {
            float reductionMult = 0.9f - hashToFloat(seed, "fire_enh_val_" + salt, 2) * 0.1f;
            builder.silencer(new Silencer("fire_rage", ActivationCondition.IN_COMBAT, 0f, reductionMult, 0f));
        }

        builder.enhancer(new Enhancer("fire_water_damp", ActivationCondition.IN_WATER, 0f, 1.5f, 0f));

        return builder.build();
    }

    private static GeneSequence createTeleportAbilitySequence(Trait trait, float potBase, long seed, String salt) {
        float baseCooldown = 200f;
        float reduction = potBase * DOMINANCE_CORRECTION;
        float target = Math.max(40f, baseCooldown - reduction);

        float var1 = deterministicGaussian(seed, "tele_1_" + salt) * 0.2f;
        float var2 = deterministicGaussian(seed, "tele_2_" + salt) * 0.2f;

        GeneSequence.Builder builder = GeneSequence.builder()
                .trait(trait)
                .activator(new Activator("tele_act", ActivationCondition.ALWAYS, 1f))
                .codingRegion(new CodingRegion("tele_charge", target * 0.6f * (1f - var1), CombineMethod.ADD))
                .codingRegion(new CodingRegion("tele_warp", target * 0.4f * (1f - var2), CombineMethod.ADD))
                .source(GeneSource.NATURAL);

        float regVal = 20f + hashToFloat(seed, "tele_reg_" + salt, 0) * 20f;
        builder.regulator(new Regulator("tele_range", RegulationType.CAP_MIN, regVal));

        if (hashToFloat(seed, "tele_enh_" + salt, 1) > 0.4f) {
            builder.silencer(new Silencer("tele_end", ActivationCondition.IN_END, 0f, 0.8f, 0f));
        }

        if (hashToFloat(seed, "tele_sil_" + salt, 1) > 0.7f) {
            float damp = 1.5f + hashToFloat(seed, "tele_sil_val_" + salt, 2) * 0.5f;
            builder.enhancer(new Enhancer("tele_water_damp", ActivationCondition.IN_WATER, 0f, damp, 0f));
        }

        if (hashToFloat(seed, "tele_panic_" + salt, 0) > 0.6f) {
            builder.silencer(new Silencer("tele_panic", ActivationCondition.HEALTH_BELOW, 0.4f, 0.7f, 0f));
        }

        return builder.build();
    }

    private static GeneSequence createCoreSequence(Trait trait, float baseValue, long seed, String salt) {
        float target = baseValue * DOMINANCE_CORRECTION;

        float var1 = deterministicGaussian(seed, trait.getName() + "_1_" + salt) * 0.15f;
        float var2 = deterministicGaussian(seed, trait.getName() + "_2_" + salt) * 0.15f;

        GeneSequence.Builder builder = GeneSequence.builder()
                .trait(trait);

        builder.activator(new Activator(trait.getName() + "_act", ActivationCondition.ALWAYS, 1f))
                .codingRegion(new CodingRegion(trait.getName() + "_primary", target * 0.6f * (1f + var1), CombineMethod.ADD))
                .codingRegion(new CodingRegion(trait.getName() + "_secondary", target * 0.4f * (1f + var2), CombineMethod.ADD))
                .source(GeneSource.NATURAL);

        if (hashToFloat(seed, trait.getName() + "_enh_" + salt, 1) > 0.9f) {
            float boost = 1.05f + hashToFloat(seed, trait.getName() + "_enh_val_" + salt, 2) * 0.1f;
            builder.enhancer(new Enhancer(trait.getName() + "_boost", ActivationCondition.HEALTH_ABOVE, 0.8f, boost, 0f));
        }

        if (hashToFloat(seed, trait.getName() + "_surv_" + salt, 3) > 0.9f) {
            float boost = 1.05f + hashToFloat(seed, trait.getName() + "_surv_val_" + salt, 2) * 0.1f;
            builder.enhancer(new Enhancer(trait.getName() + "_survival", ActivationCondition.HEALTH_BELOW, 0.3f, boost, 0f));
        }

        if (hashToFloat(seed, trait.getName() + "_reg_" + salt, 6) > 0.8f) {
            float cap = baseValue * (1.5f + hashToFloat(seed, trait.getName() + "_reg_val_" + salt, 7));
            builder.regulator(new Regulator(trait.getName() + "_cap", RegulationType.CAP_MAX, cap));
        }

        return builder.build();
    }

    private static void addResistance(Genome genome, LivingEntity entity, Trait trait, boolean immune, long seed) {
        if (trait == null) return;

        genome.getMaternal().getChromosome(trait.getTraitType().getChromosomeType())
                .setGeneSequence(trait, createResistanceSequence(trait, immune, seed, "mat"));
        genome.getPaternal().getChromosome(trait.getTraitType().getChromosomeType())
                .setGeneSequence(trait, createResistanceSequence(trait, immune, seed, "pat"));
    }

    private static GeneSequence createResistanceSequence(Trait trait, boolean immune, long seed, String salt) {
        float val = immune ? 1f : 0f;

        if (!immune && hashToFloat(seed, trait.getName() + "_dormant_chance_" + salt, 0) > 0.7f) {
            val = 0.2f + hashToFloat(seed, trait.getName() + "_dormant_val_" + salt, 1) * 0.2f;
        }

        GeneSequence.Builder builder = GeneSequence.builder()
                .trait(trait);

        if (immune) {
            builder.activator(new Activator(trait.getName() + "_act", ActivationCondition.ALWAYS, 1f));
        } else {
            float threshold = val * (1.5f + hashToFloat(seed, trait.getName() + "_dormant_thresh_" + salt, 3));
            builder.activator(new Activator(trait.getName() + "_dormant", ActivationCondition.GENE_VALUE_ABOVE, threshold));
        }

        builder.codingRegion(new CodingRegion(trait.getName() + "_base", val, CombineMethod.SET));

        builder.source(GeneSource.NATURAL);

        return builder.build();
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

    public static boolean hasGenomeSequences(Genome genome) {
        return !genome.getMaternal().getAllSequences().isEmpty() || !genome.getPaternal().getAllSequences().isEmpty();
    }

    public static float getSafeBaseAttributeValue(LivingEntity entity, Attribute attribute) {
        var inst = entity.getAttribute(attribute);
        return inst == null ? 0f : (float) inst.getBaseValue();
    }

    public static float hashToFloat(long seed, String salt, long tick) {
        long h = seed ^ salt.hashCode() ^ (tick * 31);
        h ^= (h >>> 33);
        h *= 0xff51afd7ed558ccdL;
        h ^= (h >>> 33);
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= (h >>> 33);
        return (h & 0xFFFFFF) / (float) 0xFFFFFF;
    }

    public static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }

    public static float deterministicGaussian(long seed, String salt) {
        float u1 = hashToFloat(seed, salt, 0);
        float u2 = hashToFloat(seed, salt, 1);

        u1 = Math.max(u1, 1e-12f);

        return (float) (Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2));
    }
}