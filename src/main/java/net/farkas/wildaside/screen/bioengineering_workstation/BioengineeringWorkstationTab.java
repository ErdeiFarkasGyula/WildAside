package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.WildAside;
import net.minecraft.resources.ResourceLocation;

public enum BioengineeringWorkstationTab {
    ASSEMBLER("textures/gui/bioengineering_workstation.png"),
    DNA_ANALYZER("textures/gui/bioengineering_workstation_analyzer.png"),
    DNA_SEQUENCER("textures/gui/bioengineering_workstation_sequencer.png");

    private final ResourceLocation texture;

    BioengineeringWorkstationTab(String texturePath) {
        this.texture = new ResourceLocation(WildAside.MOD_ID, texturePath);
    }

    public ResourceLocation getTexture() {
        return texture;
    }
}
