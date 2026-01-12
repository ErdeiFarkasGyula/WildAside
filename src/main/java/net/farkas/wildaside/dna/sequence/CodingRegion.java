package net.farkas.wildaside.dna.sequence;

import net.minecraft.nbt.CompoundTag;

public class CodingRegion {
    private final String id;
    private final float value;
    private final CombineMethod combineMethod;

    public CodingRegion(String id, float value, CombineMethod combineMethod) {
        this.id = id;
        this.value = value;
        this.combineMethod = combineMethod;
    }

    public float combine(float existing) {
        return switch (combineMethod) {
            case ADD -> existing + value;
            case MULTIPLY -> existing * value;
            case SET -> value;
            case MAX -> Math.max(existing, value);
            case MIN -> Math.min(existing, value);
        };
    }

    public String getId() {
        return id;
    }

    public float getValue() {
        return value;
    }

    public CombineMethod getCombineMethod() {
        return combineMethod;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putFloat("Value", value);
        tag.putString("CombineMethod", combineMethod.name());
        return tag;
    }

    public static CodingRegion deserializeNBT(CompoundTag tag) {
        return new CodingRegion(
                tag.getString("Id"),
                tag.getFloat("Value"),
                CombineMethod.valueOf(tag.getString("CombineMethod"))
        );
    }
}
