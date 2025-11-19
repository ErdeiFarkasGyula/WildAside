package net.farkas.wildaside.item.custom.syringe;

import net.farkas.wildaside.capability.dna.DnaCapability;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.item.custom.DnaHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;


public class SyringeLogic {
    public static boolean shouldPull(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int prog = tag.getInt(SyringeItem.NEEDLE_PROGRESS);
        return prog < SyringeItem.MAX_PROGRESS;
    }

    public static void extractFromEntity(ItemStack stack, LivingEntity target) {
        CompoundTag tag = stack.getOrCreateTag();
        int prog = Math.min(SyringeItem.MAX_PROGRESS, tag.getInt(SyringeItem.NEEDLE_PROGRESS) + 1);
        tag.putInt(SyringeItem.NEEDLE_PROGRESS, prog);

        tag.putBoolean(SyringeItem.TAG_WATER, false);
        ResourceLocation sourceId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
        tag.putString(SyringeItem.TAG_SOURCE, sourceId.toString());
        tag.putInt(SyringeItem.TAG_CONTAM, 0);
        tag.putInt(SyringeItem.TAG_BLOOD_AGE, 0);

        DnaImplementation toStore = new DnaImplementation();
        target.getCapability(DnaCapability.INSTANCE).ifPresent(dna -> {
            toStore.setSource(dna.source());
            toStore.setGenes(dna.genes());
            toStore.setStability(dna.stability());
        });

        tag.put(SyringeItem.TAG_DNA, toStore.serializeNBT());
    }

    public static void extractFromWater(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int prog = Math.min(SyringeItem.MAX_PROGRESS, tag.getInt(SyringeItem.NEEDLE_PROGRESS) + 1);
        tag.putInt(SyringeItem.NEEDLE_PROGRESS, prog);

        tag.putBoolean(SyringeItem.TAG_WATER, true);
        tag.remove(SyringeItem.TAG_SOURCE);
        tag.remove(SyringeItem.TAG_DNA);
        tag.putInt(SyringeItem.TAG_CONTAM, 0);
        tag.putInt(SyringeItem.TAG_BLOOD_AGE, 0);
    }

    public static void push(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        boolean water = tag.getBoolean(SyringeItem.TAG_WATER);
        boolean hasBlood = tag.contains(SyringeItem.TAG_DNA);

        if (water) {
            tag.putInt(SyringeItem.TAG_CONTAM, 0);
            tag.remove(SyringeItem.TAG_DNA);
            tag.remove(SyringeItem.TAG_SOURCE);
        } else if (hasBlood) {
            int c = tag.getInt(SyringeItem.TAG_CONTAM);
            tag.putInt(SyringeItem.TAG_CONTAM, Math.min(SyringeItem.MAX_CONTAM, c + 1));
        }

        int prog = Math.max(0, tag.getInt(SyringeItem.NEEDLE_PROGRESS) - 1);
        tag.putInt(SyringeItem.NEEDLE_PROGRESS, prog);

        if (prog == 0) {
            if (!tag.contains(SyringeItem.TAG_DNA)) {
                tag.remove(SyringeItem.TAG_SOURCE);
                tag.remove(SyringeItem.TAG_BLOOD_AGE);
            }
        }
    }

    public static void transferToHolder(ItemStack syringeStack, ItemStack holderStack) {
        if (!syringeStack.hasTag()) return;
        CompoundTag sTag = syringeStack.getTag();
        if (!sTag.contains(SyringeItem.TAG_DNA)) return;

        CompoundTag dna = sTag.getCompound(SyringeItem.TAG_DNA);

        CompoundTag hTag = holderStack.getOrCreateTag();
        hTag.put(SyringeItem.TAG_DNA, dna.copy());

        int progress = hTag.getInt("sample_progress");
        progress = Math.min(progress + 1, DnaHolder.DEFAULT_MAX_SAMPLES);
        hTag.putInt("sample_progress", progress);
        holderStack.setTag(hTag);

        sTag.remove(SyringeItem.TAG_DNA);
        sTag.remove(SyringeItem.TAG_SOURCE);
        syringeStack.setTag(sTag);
    }

    public static void tickClotting(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(SyringeItem.TAG_DNA)) return;
        int age = tag.getInt(SyringeItem.TAG_BLOOD_AGE);
        age++;
        tag.putInt(SyringeItem.TAG_BLOOD_AGE, age);
    }
}