package net.farkas.wildaside.dna;

import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class BacillusPayloadUtil {
    public static Map<Trait, List<GeneLocus>> readPayload(ItemStack stack) {
        Map<Trait, List<GeneLocus>> out = new HashMap<>();
        CompoundTag root = stack.getTagElement("bacillus_payload");
        if (root == null) return out;
        ListTag list = root.getList("loci", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag ct = (CompoundTag) t;
            Trait trait = TraitRegistry.getByName(ct.getString("Trait"));
            GeneLocus locus = GeneLocus.deserializeNBT(ct);
            out.computeIfAbsent(trait, k -> new ArrayList<>()).add(locus);
        }
        return out;
    }

    public static CompoundTag writePayload(Map<Trait, List<GeneLocus>> payload) {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        for (Map.Entry<Trait, List<GeneLocus>> e : payload.entrySet()) {
            for (GeneLocus locus : e.getValue()) {
                CompoundTag ct = locus.serializeNBT();
                ct.putString("Trait", e.getKey().getName());
                list.add(ct);
            }
        }
        root.put("loci", list);
        return root;
    }

    public static float computeStress(Map<Trait, List<GeneLocus>> payload) {
        int lociCount = payload.values().stream().mapToInt(List::size).sum();
        return Math.min(15f, 8f + lociCount * 1.5f);
    }
}