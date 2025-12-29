package net.farkas.wildaside.item.custom;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraft.world.entity.EntityType;
import java.util.List;

import static net.farkas.wildaside.dna.DnaConstants.*;

public abstract class AbstractDnaSampleItem extends net.minecraft.world.item.Item {

    protected AbstractDnaSampleItem(Properties props) { super(props); }

    protected boolean hasDna(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(DNA_DATA);
    }

    protected boolean isAnalyzed(CompoundTag tag) {
        return tag.getBoolean(REVEAL_SOURCE) || tag.getBoolean(REVEAL_STABILITY) || tag.getBoolean(REVEAL_TRAITS);
    }

    protected void applyRevealFlags(CompoundTag target, CompoundTag source) {
        target.putBoolean(REVEAL_SOURCE, source.getBoolean(REVEAL_SOURCE));
        target.putBoolean(REVEAL_STABILITY, source.getBoolean(REVEAL_STABILITY));
        target.putBoolean(REVEAL_TRAITS, source.getBoolean(REVEAL_TRAITS));
    }

    protected int resolveSourceEggColor(CompoundTag dnaTag) {
        DnaImplementation dna = new DnaImplementation();
        dna.deserializeNBT(dnaTag);
        EntityType<?> src = dna.getSource();
        if (src == null) return -1;
        var egg = ForgeSpawnEggItem.fromEntityType(src);
        return egg != null ? egg.getColor(0) : -1;
    }

    protected boolean appendContaminationTooltipIfNeeded(List<Component> tooltip, CompoundTag tag, Level level) {
        if (isAnalyzed(tag)) {
            tag.putBoolean(SAMPLE_UNUSABLE, false);
            return false;
        }
        boolean multiple = tag.getBoolean(MULTIPLE_SOURCES);
        boolean clotted = DnaUtils.getFrozenItemEffectiveAge(tag, level) > tag.getLong(BLOOD_CLOTTING_TIME) || tag.getBoolean(SAMPLE_CLOTTED);
        boolean dirty = tag.getBoolean(SAMPLE_DIRTY);
        return DnaUtils.handleContaminatedSampleTooltip(tooltip, multiple, clotted, dirty);
    }
}