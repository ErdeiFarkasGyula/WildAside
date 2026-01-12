package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.advancement.ModAdvancements;
import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.entity.custom.BioengineeringWorkstationBlockEntity;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.DnaUtils;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.allele.value.FloatAlleleValue;
import net.farkas.wildaside.dna.trait.Trait;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.custom.DnaHolderItem;
import net.farkas.wildaside.item.custom.GeneItem;
import net.farkas.wildaside.screen.*;
import net.farkas.wildaside.screen.slot.AdvancementGivingVisibleResultSlotItemHandler;
import net.farkas.wildaside.screen.slot.ItemSpecificVisibleSlotItemHandler;
import net.farkas.wildaside.screen.slot.VisibleSlot;
import net.farkas.wildaside.screen.slot.VisibleSlotItemHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.farkas.wildaside.dna.DnaConstants.*;
import static net.farkas.wildaside.screen.bioengineering_workstation.BioengineeringWorkstationSlots.*;

public class BioengineeringWorkstationMenu extends AbstractContainerMenu {
    public final BioengineeringWorkstationBlockEntity blockEntity;
    public final Inventory inventory;
    public final Player player;
    private final Level level;
    private final ContainerData data;

    private BioengineeringWorkstationTab tab;

    private int teFirstSlotIndex;
    private int teSlotCount;

    public List<VisibleSlot> playerInventorySlots = new ArrayList<>();
    public List<VisibleSlot> hotbarSlots = new ArrayList<>();

    public List<GeneSlotItemHandler> topGeneSlots = new ArrayList<>();
    public List<GeneSlotItemHandler> botGeneSlots = new ArrayList<>();

    private VisibleSlotItemHandler assemblerSlot0;
    private VisibleSlotItemHandler assemblerSlot1;
    private VisibleSlotItemHandler assemblerSlot2;
    private VisibleSlotItemHandler assemblerSlot3;
    private VisibleSlotItemHandler assemblerSlot4;
    private AdvancementGivingVisibleResultSlotItemHandler assemblerResult;

    private VisibleSlotItemHandler analyserSlotA;
    private VisibleSlotItemHandler analyserSlotB;
    private VisibleSlotItemHandler analyserSlotC;
    private VisibleSlotItemHandler analyserResult;

    private DnaEditorInputSlotItemHandler editorInputA;
    private DnaEditorInputSlotItemHandler editorInputB;
    private AdvancementGivingVisibleResultSlotItemHandler editorOutA;
    private AdvancementGivingVisibleResultSlotItemHandler editorOutB;

    public static final int yOffset = 28;

    public BioengineeringWorkstationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public BioengineeringWorkstationMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.BIOENGINEERING_WORKSTATION.get(), pContainerId);

        this.blockEntity = (BioengineeringWorkstationBlockEntity) entity;
        this.inventory = inv;
        this.player = inv.player;
        this.level = player.level();
        this.data = data;
        this.tab = blockEntity.getTab();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addMenuSlots();

        applyTabVisibility();

        addDataSlots(data);
    }

    private void addMenuSlots() {
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            int before = slots.size();

            assemblerSlot0 = new VisibleSlotItemHandler(iItemHandler, ASSE_INPUT_1, 84, 34 + yOffset);
            assemblerSlot1 = new VisibleSlotItemHandler(iItemHandler, ASSE_INPUT_2, 84, 16 + yOffset);
            assemblerSlot2 = new VisibleSlotItemHandler(iItemHandler, ASSE_INPUT_3, 102, 34 + yOffset);
            assemblerSlot3 = new VisibleSlotItemHandler(iItemHandler, ASSE_INPUT_4, 84, 52 + yOffset);
            assemblerSlot4 = new VisibleSlotItemHandler(iItemHandler, ASSE_INPUT_5, 66, 34 + yOffset);
            assemblerResult = new AdvancementGivingVisibleResultSlotItemHandler(iItemHandler, ASSE_OUTPUT_1, 170, 34 + yOffset, player, ModAdvancements.WE_NEED_TO_COOK);

            addSlot(assemblerSlot0);
            addSlot(assemblerSlot1);
            addSlot(assemblerSlot2);
            addSlot(assemblerSlot3);
            addSlot(assemblerSlot4);
            addSlot(assemblerResult);

            analyserSlotA = new ItemSpecificVisibleSlotItemHandler(iItemHandler, ANA_INPUT_1, 66, 34 + yOffset, List.of(ModItems.DNA_HOLDER.get()));
            analyserSlotB = new ItemSpecificVisibleSlotItemHandler(iItemHandler, ANA_INPUT_2, 84, 34 + yOffset, List.of(ModItems.ENTORIUM.get()));
            analyserSlotC = new ItemSpecificVisibleSlotItemHandler(iItemHandler, ANA_INPUT_3, 102, 34 + yOffset, List.of(ModItems.VIBRION.get()));
            analyserResult = new PointGivingRewardSlotItemHandler(iItemHandler, ANA_OUTPUT_1, 170, 34 + yOffset, player, 10) {
                @Override
                public boolean shouldGivePoint(Player pPlayer, ItemStack pStack) {
                    CompoundTag tag = pStack.getOrCreateTag();
                    DnaImplementation dna = new DnaImplementation();
                    dna.deserializeNBT(tag.getCompound(DNA_DATA));

                    if (dna.getGenome() != null && hasGenomeSequences(dna.getGenome())) {
                        return super.shouldGivePoint(pPlayer, pStack);
                    }

                    if (dna.getGenomeLociView().isEmpty()) {
                        return false;
                    }

                    return super.shouldGivePoint(pPlayer, pStack);
                }

                private boolean hasGenomeSequences(net.farkas.wildaside.dna.chromosome.Genome genome) {
                    return DnaUtils.hasGenomeSequences(genome);
                }
            };

            addSlot(analyserSlotA);
            addSlot(analyserSlotB);
            addSlot(analyserSlotC);
            addSlot(analyserResult);

            editorInputA = new DnaEditorInputSlotItemHandler(this, iItemHandler, EDITOR_INPUT_1, 15, 8 + yOffset);
            editorInputB = new DnaEditorInputSlotItemHandler(this, iItemHandler, EDITOR_INPUT_2, 15, 30 + yOffset);
            editorOutA = new AdvancementGivingVisibleResultSlotItemHandler(iItemHandler, EDITOR_OUTPUT_1, 191, 57 + yOffset);
            editorOutB = new AdvancementGivingVisibleResultSlotItemHandler(iItemHandler, EDITOR_OUTPUT_2, 221, 57 + yOffset);

            addSlot(editorInputA);
            addSlot(editorInputB);
            addSlot(editorOutA);
            addSlot(editorOutB);

            for (int i = 0; i <= 12; i++) {
                GeneSlotItemHandler currentSlot = new GeneSlotItemHandler(iItemHandler, EDITOR_TOP_GENE_START_INDEX + i, 33 + i * 16, 8 + yOffset);
                addSlot(currentSlot);
                topGeneSlots.add(currentSlot);
            }
            for (int i = 0; i <= 12; i++) {
                GeneSlotItemHandler currentSlot = new GeneSlotItemHandler(iItemHandler, EDITOR_BOTTOM_GENE_START_INDEX + i, 33 + i * 16, 30 + yOffset);
                addSlot(currentSlot);
                botGeneSlots.add(currentSlot);
            }

            this.teFirstSlotIndex = before;
            this.teSlotCount = slots.size() - before;
        });
    }

    private void applyTabVisibility() {
        switch (tab) {
            case ASSEMBLER -> {
                setVisibleAsm(true);
                setVisibleAnalyser(false);
                setVisibleSequencer(false);

                setVisiblePlayerInventorySlots(true);
                setVisibleHotbar(true);
            }
            case DNA_ANALYSER -> {
                setVisibleAsm(false);
                setVisibleAnalyser(true);
                setVisibleSequencer(false);

                setVisiblePlayerInventorySlots(true);
                setVisibleHotbar(true);
            }
            case DNA_EDITOR -> {
                setVisibleAsm(false);
                setVisibleAnalyser(false);
                setVisibleSequencer(true);

                setVisiblePlayerInventorySlots(true);
                setVisibleHotbar(true);
            }
            case SKILL_TAB -> {
                setVisibleAsm(false);
                setVisibleAnalyser(false);
                setVisibleSequencer(false);

                setVisiblePlayerInventorySlots(false);
                setVisibleHotbar(false);
            }
        }
    }

    private void setVisibleAsm(boolean v) {
        assemblerSlot0.setVisible(v);
        assemblerSlot1.setVisible(v);
        assemblerSlot2.setVisible(v);
        assemblerSlot3.setVisible(v);
        assemblerSlot4.setVisible(v);
        assemblerResult.setVisible(v);
    }

    private void setVisibleAnalyser(boolean v) {
        analyserSlotA.setVisible(v);
        analyserSlotB.setVisible(v);
        analyserSlotC.setVisible(v);
        analyserResult.setVisible(v);
    }

    private void setVisibleSequencer(boolean v) {
        editorInputA.setVisible(v);
        editorInputB.setVisible(v);
        editorOutA.setVisible(v);
        editorOutB.setVisible(v);

        topGeneSlots.forEach(s -> s.setVisible(v));
        botGeneSlots.forEach(s -> s.setVisible(v));
    }

    private void setVisiblePlayerInventorySlots(boolean v) {
        playerInventorySlots.forEach(s -> s.setVisible(v));
    }

    private void setVisibleHotbar(boolean v) {
        hotbarSlots.forEach(s -> s.setVisible(v));
    }

    public void clearGenes() {
        topGeneSlots.forEach(slot -> slot.set(ItemStack.EMPTY));
        botGeneSlots.forEach(slot -> slot.set(ItemStack.EMPTY));
        broadcastChanges();
    }

    public void loadGenes(int i) {
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            ItemStack stack = iItemHandler.getStackInSlot(i);

            if (stack.getItem() instanceof DnaHolderItem dnaHolder) {
                CompoundTag tag = stack.getOrCreateTag();

                if (!tag.getBoolean(REVEAL_TRAITS)) return;

                CompoundTag dnaDataTag = tag.getCompound(DNA_DATA);
                DnaImplementation dna = new DnaImplementation();
                dna.deserializeNBT(dnaDataTag);
                Map<Trait, Gene> genes = BioengineeringWorkstationBlockEntity.orderGenes(dna);

                int slotIndex = 0;

                for (Gene gene : genes.values()) {
                    if (slotIndex >= 13) break;

                    ItemStack geneStack = GeneItem.createFromTag(gene);

                    if (isLatentGene(gene)) {
                        geneStack.getOrCreateTag().putBoolean("latent", true);
                    }

                    if (i == EDITOR_INPUT_1) {
                        if (topGeneSlots.size() > slotIndex) {
                            topGeneSlots.get(slotIndex).set(geneStack);
                        }
                    }
                    else if (i == EDITOR_INPUT_2) {
                        if (botGeneSlots.size() > slotIndex) {
                            botGeneSlots.get(slotIndex).set(geneStack);
                        }
                    }
                    slotIndex++;
                }
            }
        });
    }

    public boolean isLatentGene(Gene gene) {
        if (gene.getExpressedValueHolder() instanceof FloatAlleleValue floatAlleleValue) {
            return floatAlleleValue.get() == 0.0f;
        }
        return false;
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public boolean isAnalysing() {
        return data.get(2) > 0;
    }

    public int getScaledCraftingProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 27;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getScaledAnalysingProgress() {
        int progress = this.data.get(2);
        int maxProgress = this.data.get(3);
        int progressArrowSize = 27;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public void setTab(BioengineeringWorkstationTab tab) {
        this.tab = tab;
        applyTabVisibility();
    }

    public void setTab(int index) {
        this.tab = BioengineeringWorkstationTab.values()[index];
        applyTabVisibility();
    }

    public BioengineeringWorkstationTab getTab() {
        return tab;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // MODIFIED VERSION BY FARKAS!
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        int start = teFirstSlotIndex;
        int end = teFirstSlotIndex + teSlotCount;

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, start, end, false)) return ItemStack.EMPTY;
        }
        else if (pIndex < end) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false))
                return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();

        sourceSlot.onTake(playerIn, sourceStack);
        return copy;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.BIOENGINEERING_WORKSTATION.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                VisibleSlot slot = new VisibleSlot(playerInventory, l + i * 9 + 9, 8 + l * 18 + 40, 84 + yOffset + i * 18);
                this.addSlot(slot);
                playerInventorySlots.add(slot);
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            VisibleSlot slot = new VisibleSlot(playerInventory, i, 8 + i * 18 + 40, 142 + yOffset);
            this.addSlot(slot);
            hotbarSlots.add(slot);
        }
    }
}