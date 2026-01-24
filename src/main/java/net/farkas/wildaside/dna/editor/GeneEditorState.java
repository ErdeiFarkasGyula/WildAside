package net.farkas.wildaside.dna.editor;

import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.dna.trait.TraitRegistry;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public class GeneEditorState {
    private Trait selectedTrait = null;
    private boolean selectedIsMaternal = true;
    private GeneSequence selectedSequence = null;
    private String selectedComponentId = null;
    private ComponentType selectedComponentType = null;
    private GeneEditorOperation currentOperation = GeneEditorOperation.ADD_CODING_REGION;
    private int scrollOffsetTraits = 0;
    private int scrollOffsetComponents = 0;
    private boolean sequenceDetailOpen = false;
    
    public enum ComponentType {
        CODING_REGION,
        ACTIVATOR,
        ENHANCER,
        SILENCER,
        REGULATOR
    }

    public void selectTrait(Trait trait, boolean isMaternal, GeneSequence sequence) {
        this.selectedTrait = trait;
        this.selectedIsMaternal = isMaternal;
        this.selectedSequence = sequence;
        clearComponentSelection();
    }

    public void selectComponent(String componentId, ComponentType componentType) {
        this.selectedComponentId = componentId;
        this.selectedComponentType = componentType;
    }

    public void clearComponentSelection() {
        this.selectedComponentId = null;
        this.selectedComponentType = null;
    }

    public void clearSelection() {
        this.selectedTrait = null;
        this.selectedSequence = null;
        this.selectedComponentId = null;
        this.selectedComponentType = null;
        this.sequenceDetailOpen = false;
    }

    public void openSequenceDetail() {
        this.sequenceDetailOpen = true;
    }

    public void closeSequenceDetail() {
        this.sequenceDetailOpen = false;
    }

    public Trait getSelectedTrait() {
        return selectedTrait;
    }

    public boolean isSelectedMaternal() {
        return selectedIsMaternal;
    }

    public GeneSequence getSelectedSequence() {
        return selectedSequence;
    }

    public String getSelectedComponentId() {
        return selectedComponentId;
    }

    public ComponentType getSelectedComponentType() {
        return selectedComponentType;
    }

    public GeneEditorOperation getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(GeneEditorOperation op) {
        this.currentOperation = op;
    }

    public int getScrollOffsetTraits() {
        return scrollOffsetTraits;
    }

    public int getScrollOffsetComponents() {
        return scrollOffsetComponents;
    }

    public void setScrollOffsetTraits(int offset) {
        this.scrollOffsetTraits = Math.max(0, offset);
    }

    public void setScrollOffsetComponents(int offset) {
        this.scrollOffsetComponents = Math.max(0, offset);
    }

    public boolean isSequenceDetailOpen() {
        return sequenceDetailOpen;
    }

    public boolean hasValidSelection() {
        return selectedTrait != null && selectedSequence != null;
    }

    public boolean hasValidComponentSelection() {
        return hasValidSelection() && selectedComponentId != null && selectedComponentType != null;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        if (selectedTrait != null) {
            tag.putString("trait", selectedTrait.getName());
        }
        tag.putBoolean("isMaternal", selectedIsMaternal);
        if (selectedComponentId != null) {
            tag.putString("componentId", selectedComponentId);
        }
        if (selectedComponentType != null) {
            tag.putString("componentType", selectedComponentType.name());
        }
        tag.putString("operation", currentOperation.name());
        tag.putInt("scrollTraits", scrollOffsetTraits);
        tag.putInt("scrollComponents", scrollOffsetComponents);
        tag.putBoolean("detailOpen", sequenceDetailOpen);
        return tag;
    }

    public static GeneEditorState deserialize(CompoundTag tag) {
        GeneEditorState state = new GeneEditorState();
        if (tag.contains("trait")) {
            state.selectedTrait = TraitRegistry.getByName(tag.getString("trait"));
        }
        state.selectedIsMaternal = tag.getBoolean("isMaternal");
        if (tag.contains("componentId")) {
            state.selectedComponentId = tag.getString("componentId");
        }
        if (tag.contains("componentType")) {
            state.selectedComponentType = ComponentType.valueOf(tag.getString("componentType"));
        }
        if (tag.contains("operation")) {
            state.currentOperation = GeneEditorOperation.valueOf(tag.getString("operation"));
        }
        state.scrollOffsetTraits = tag.getInt("scrollTraits");
        state.scrollOffsetComponents = tag.getInt("scrollComponents");
        state.sequenceDetailOpen = tag.getBoolean("detailOpen");
        return state;
    }
}