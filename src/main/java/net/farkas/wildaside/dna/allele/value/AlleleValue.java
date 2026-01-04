package net.farkas.wildaside.dna.allele.value;

import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public interface AlleleValue {
    AlleleValueType getType();

    AlleleValue expressWith(AlleleValue other, Dominance selfDom, Dominance otherDom);

    void serialize(CompoundTag tag);
    void deserialize(CompoundTag tag);

    Component format();

    AlleleValue parse(String input);
}
