package net.farkas.wildaside.dna;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import static net.farkas.wildaside.dna.DnaConstants.*;

public class BacillusBlobPayload {
    public static boolean copyDnaFromHolder(ItemStack blob, ItemStack dnaHolder) {
        if (!dnaHolder.hasTag()) return false;
        CompoundTag src = dnaHolder.getOrCreateTag();
        if (!src.contains(DNA_DATA)) return false;

        CompoundTag tag = blob.getOrCreateTag();
        tag.put(DNA_DATA, src.getCompound(DNA_DATA).copy());
        return true;
    }

    public static boolean copyDnaFromHolderInternal(CompoundTag target, ItemStack dnaHolder) {
        if (!dnaHolder.hasTag()) return false;
        CompoundTag src = dnaHolder.getOrCreateTag();
        if (!src.contains(DNA_DATA)) return false;
        target.put(DNA_DATA, src.getCompound(DNA_DATA).copy());
        return true;
    }

    public static boolean hasDna(ItemStack blob) {
        return blob.hasTag() && blob.getTag().contains(DNA_DATA);
    }

    public static CompoundTag getDna(ItemStack blob) {
        return blob.hasTag() ? blob.getTag().getCompound(DNA_DATA) : new CompoundTag();
    }
}