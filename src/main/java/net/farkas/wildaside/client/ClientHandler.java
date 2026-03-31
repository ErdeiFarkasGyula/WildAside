package net.farkas.wildaside.client;

import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.network.packet.gene_editor.OpenAdvancedGeneEditorPacket;
import net.farkas.wildaside.screen.gene_editor.AdvancedGeneEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class ClientHandler {
    public static void openGeneEditor(BlockPos pos, DnaImplementation dnaA, DnaImplementation dnaB) {
        Minecraft.getInstance().setScreen(new AdvancedGeneEditorScreen(pos, dnaA, dnaB));
    }
}