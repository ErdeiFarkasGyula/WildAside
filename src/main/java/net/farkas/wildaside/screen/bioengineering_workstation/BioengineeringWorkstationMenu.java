package net.farkas.wildaside.screen.bioengineering_workstation;

import net.farkas.wildaside.block.ModBlocks;
import net.farkas.wildaside.block.entity.BioengineeringWorkstationBlockEntity;
import net.farkas.wildaside.capability.dna.DnaImplementation;
import net.farkas.wildaside.dna.Gene;
import net.farkas.wildaside.dna.traits.Trait;
import net.farkas.wildaside.dna.traits.TraitTypes;
import net.farkas.wildaside.dna.traits.Traits;
import net.farkas.wildaside.item.ModItems;
import net.farkas.wildaside.item.custom.DnaHolder;
import net.farkas.wildaside.screen.ModMenuTypes;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BioengineeringWorkstationMenu extends AbstractContainerMenu {
    public final BioengineeringWorkstationBlockEntity blockEntity;
    public final Inventory inventory;
    public final Player player;
    private final Level level;
    private final ContainerData data;

    private BioengineeringWorkstationTab tab;

    private int teFirstSlotIndex;
    private int teSlotCount;

    public List<Slot> topGeneSlots = new ArrayList<>();
    public List<Slot> botGeneSlots = new ArrayList<>();

    public static final int TOP_GENE_INDEX_START = 11;
    public static final int BOT_GENE_INDEX_START = TOP_GENE_INDEX_START + 13;

    public BioengineeringWorkstationMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(70));
    }

    public BioengineeringWorkstationMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.BIOENGINEERING_WORKSTATION_MENU.get(), pContainerId);

        this.blockEntity = (BioengineeringWorkstationBlockEntity) entity;
        this.inventory = inv;
        this.player = inv.player;
        this.level = player.level();
        this.data = data;
        this.tab = blockEntity.getTab();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addMenuSlots(tab);

        addDataSlots(data);
    }

    private void addMenuSlots(BioengineeringWorkstationTab tab) {
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            int before = slots.size();

            switch (tab) {
                case ASSEMBLER -> {
                    addSlot(new SlotItemHandler(iItemHandler, 0, 84, 34));
                    addSlot(new SlotItemHandler(iItemHandler, 1, 84, 16));
                    addSlot(new SlotItemHandler(iItemHandler, 2, 102, 34));
                    addSlot(new SlotItemHandler(iItemHandler, 3, 84, 52));
                    addSlot(new SlotItemHandler(iItemHandler, 4, 66, 34));
                    addSlot(new BioengineeringWorkstationResultSlot(iItemHandler, 5, 170, 34, player));
                }
                case DNA_ANALYZER -> {
                    addSlot(new SlotItemHandler(iItemHandler, 5, 80, 30));
                    addSlot(new SlotItemHandler(iItemHandler, 6, 100, 30));
                }
                case DNA_SEQUENCER -> {
                    addSlot(new DnaInputSlot(this, iItemHandler, 7, 15, 8));
                    addSlot(new DnaInputSlot(this, iItemHandler, 8, 15, 30));
                    addSlot(new BioengineeringWorkstationResultSlot(iItemHandler, 9, 191, 57, player));
                    addSlot(new BioengineeringWorkstationResultSlot(iItemHandler, 10, 221, 57, player));

                    for (int i = 0; i <= 12; i++) {
                        topGeneSlots.add(addSlot(new GeneSlot(iItemHandler, TOP_GENE_INDEX_START + i, 33 + i * 16, 8)));
                    }
                    for (int i = 0; i <= 12; i++) {
                        botGeneSlots.add(addSlot(new GeneSlot(iItemHandler, BOT_GENE_INDEX_START + i, 33 + i * 16, 8 + 22)));
                    }
                }
            }

            this.teFirstSlotIndex = before;
            this.teSlotCount = slots.size() - before;
        });
    }

    public void rebuildSlots(Inventory inv) {
        slots.clear();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addMenuSlots(tab);
    }

    public void clearGenes() {
        topGeneSlots.forEach(slot -> slot.set(ItemStack.EMPTY));
        botGeneSlots.forEach(slot -> slot.set(ItemStack.EMPTY));
    }

    public void loadGenes(int i) {
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            ItemStack stack = iItemHandler.getStackInSlot(i);

            if (stack.getItem() instanceof DnaHolder dnaHolder) {
                CompoundTag dnaDataTag = stack.getOrCreateTagElement("dna_data");
                DnaImplementation dna = new DnaImplementation();
                dna.deserializeNBT(dnaDataTag);
                Map<Trait, Gene> genes = BioengineeringWorkstationBlockEntity.orderGenes(dna);

                for (int x = 0; x < Math.min(Traits.TRAITS.size(), topGeneSlots.size()); x++) {
                    Trait trait = Traits.TRAITS.get(x);
                    Gene gene = genes.getOrDefault(trait, new Gene(trait, 0, trait.baseInstability()));
                    if (trait == Traits.FIRE_ABILITY && gene.value == 0.0) {
                        continue;
                    }
                    ItemStack geneStack = new ItemStack(ModItems.GENE.get());
                    CompoundTag tag = geneStack.getOrCreateTag();
                    tag.putString("trait", gene.trait.name());
                    tag.putFloat("value", gene.value);
                    geneStack.setTag(tag);

                    if (i == 7) {
                        topGeneSlots.get(x).set(geneStack);
                    } else if (i == 8) {
                        botGeneSlots.get(x).set(geneStack);
                    }
                }
            }
        });
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 27;

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public void setTab(BioengineeringWorkstationTab tab) {
        this.tab = tab;
        rebuildSlots(inventory);
    }

    public void setTab(int index) {
        this.tab = BioengineeringWorkstationTab.values()[index];
        rebuildSlots(inventory);
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
        } else if (pIndex < end) {
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
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18 + 40, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18 + 40, 142));
        }
    }
}