package net.farkas.wildaside.dna.editor;

import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GeneEditorState {
    private Gene selectedGeneTop = null;
    private Gene selectedGeneBottom = null;
    private int selectedGeneTopIndex = -1;
    private int selectedGeneBottomIndex = -1;
    private Allele selectedAllele = null;
    private boolean selectedAlleleIsA = true;
    private boolean selectedAlleleFromTop = true;
    private GeneEditorOperation currentOperation = GeneEditorOperation.SWAP_TRAIT;
    private int scrollOffsetTop = 0;
    private int scrollOffsetBottom = 0;
    private boolean detailPanelOpen = false;
    private Gene detailPanelGene = null;
    private boolean detailPanelFromTop = true;

    public void selectGeneTop(Gene gene, int index) {
        this.selectedGeneTop = gene;
        this.selectedGeneTopIndex = index;
        clearAlleleSelection();
    }

    public void selectGeneBottom(Gene gene, int index) {
        this.selectedGeneBottom = gene;
        this.selectedGeneBottomIndex = index;
        clearAlleleSelection();
    }

    public void selectAllele(Allele allele, boolean isAlleleA, boolean fromTop) {
        this.selectedAllele = allele;
        this.selectedAlleleIsA = isAlleleA;
        this.selectedAlleleFromTop = fromTop;
    }

    public void clearAlleleSelection() {
        this.selectedAllele = null;
    }

    public void clearSelection() {
        this.selectedGeneTop = null;
        this.selectedGeneBottom = null;
        this.selectedGeneTopIndex = -1;
        this.selectedGeneBottomIndex = -1;
        this.selectedAllele = null;
        this.detailPanelOpen = false;
        this.detailPanelGene = null;
    }

    public void openDetailPanel(Gene gene, boolean fromTop) {
        this.detailPanelOpen = true;
        this.detailPanelGene = gene;
        this.detailPanelFromTop = fromTop;
    }

    public void closeDetailPanel() {
        this.detailPanelOpen = false;
        this.detailPanelGene = null;
    }

    public Gene getSelectedGeneTop() {
        return selectedGeneTop;
    }

    public Gene getSelectedGeneBottom() {
        return selectedGeneBottom;
    }

    public int getSelectedGeneTopIndex() {
        return selectedGeneTopIndex;
    }

    public int getSelectedGeneBottomIndex() {
        return selectedGeneBottomIndex;
    }

    public Allele getSelectedAllele() {
        return selectedAllele;
    }

    public boolean isSelectedAlleleA() {
        return selectedAlleleIsA;
    }

    public boolean isSelectedAlleleFromTop() {
        return selectedAlleleFromTop;
    }

    public GeneEditorOperation getCurrentOperation() {
        return currentOperation;
    }

    public void setCurrentOperation(GeneEditorOperation op) {
        this.currentOperation = op;
    }

    public int getScrollOffsetTop() {
        return scrollOffsetTop;
    }

    public int getScrollOffsetBottom() {
        return scrollOffsetBottom;
    }

    public void setScrollOffsetTop(int offset) {
        this.scrollOffsetTop = Math.max(0, offset);
    }

    public void setScrollOffsetBottom(int offset) {
        this.scrollOffsetBottom = Math.max(0, offset);
    }

    public boolean isDetailPanelOpen() {
        return detailPanelOpen;
    }

    public Gene getDetailPanelGene() {
        return detailPanelGene;
    }

    public boolean isDetailPanelFromTop() {
        return detailPanelFromTop;
    }

    public boolean hasValidSwapSelection() {
        return selectedGeneTop != null && selectedGeneBottom != null &&
                selectedGeneTop.getTrait().equals(selectedGeneBottom.getTrait());
    }

    public boolean hasValidAlleleSwapSelection() {
        return selectedGeneTop != null && selectedGeneBottom != null && selectedAllele != null;
    }
}