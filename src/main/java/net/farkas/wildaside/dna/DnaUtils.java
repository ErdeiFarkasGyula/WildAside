package net.farkas.wildaside.dna;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.speed.MobSpeedResultStorage;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static List<Gene> generateDefaultCoreGenes(LivingEntity entity) {
        List<Gene> genes = new ArrayList<>();
        for (var trait : Traits.getByType(TraitTypes.CORE)) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(getAttribute(trait.name()));

            float traitValue = entity.getAttribute(attribute) == null ? 0 : (float) entity.getAttribute(attribute).getBaseValue();
            genes.add(new Gene(trait, traitValue, trait.baseInstability()));
        }
        return genes;
    }

    public static List<Gene> generateBaseCoreGenes(LivingEntity entity) {
        List<Gene> genes = new ArrayList<>();
        for (var trait : Traits.getByType(TraitTypes.CORE)) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(getAttribute(trait.name()));

            float traitValue = entity.getAttribute(attribute) == null ? 0 : (float) entity.getAttribute(attribute).getBaseValue();
            if (trait == Traits.MOVEMENT_SPEED) {
                traitValue = (float) (MobSpeedResultStorage.getSpeed(entity.getType(), "ground") / 43.17);
                System.out.println("TRAIT VALUE: " + traitValue);
            }
            genes.add(new Gene(trait, traitValue, trait.baseInstability()));
        }
        return genes;
    }

    public static List<Gene> mutateCoreGenes(List<Gene> baseGenes, LivingEntity entity) {
        RandomSource random = RandomSource.create(entity.getUUID().getLeastSignificantBits());
        return baseGenes.stream().map(gene -> mutateCoreGene(gene, random)).toList();
    }

    private static Gene mutateCoreGene(Gene gene, RandomSource random) {
        float averageMutation = -0.2f;
        float baseVariance = 0.2f;
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