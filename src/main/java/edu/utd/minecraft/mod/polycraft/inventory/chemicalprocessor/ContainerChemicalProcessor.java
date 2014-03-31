package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerChemicalProcessor extends Container {

	public static final int SLOT_INDEX_FUEL = 0;
	public static final int SLOT_INDEX_FIRST_MATERIAL = SLOT_INDEX_FUEL + 1;
	public static final int SLOT_INDEX_FLUID_CONTAINER = SLOT_INDEX_FIRST_MATERIAL + ChemicalProcessorRecipe.MAX_INPUTS;
	public static final int SLOT_INDEX_FIRST_RESULT = SLOT_INDEX_FLUID_CONTAINER + 1;

	private final TileEntityChemicalProcessor tileChemicalProcessor;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;

	public ContainerChemicalProcessor(InventoryPlayer playerInventory, TileEntityChemicalProcessor tileEntityChemicalProcessor) {
		this.tileChemicalProcessor = tileEntityChemicalProcessor;
		this.addSlotToContainer(new Slot(tileEntityChemicalProcessor, SLOT_INDEX_FUEL, 26, 54)); // fuel
		this.addSlotToContainer(new Slot(tileEntityChemicalProcessor, SLOT_INDEX_FIRST_MATERIAL, 8, 18));
		this.addSlotToContainer(new Slot(tileEntityChemicalProcessor, SLOT_INDEX_FIRST_MATERIAL + 1, 8 + 18, 18));
		this.addSlotToContainer(new Slot(tileEntityChemicalProcessor, SLOT_INDEX_FIRST_MATERIAL + 2, 8 + 36, 18));
		this.addSlotToContainer(new Slot(tileEntityChemicalProcessor, SLOT_INDEX_FIRST_MATERIAL + 3, 8, 36));
		this.addSlotToContainer(new Slot(tileEntityChemicalProcessor, SLOT_INDEX_FIRST_MATERIAL + 4, 8 + 36, 36));
		this.addSlotToContainer(new Slot(tileEntityChemicalProcessor, SLOT_INDEX_FLUID_CONTAINER, 71, 54));
		
		// outputs
		int slot = SLOT_INDEX_FIRST_RESULT;
		for (int j = 0; j < 3; ++j) {
			for (int i = 0; i < 3; ++i) {
				this.addSlotToContainer(
						new SlotChemicalProcessor(
								playerInventory.player,
								tileEntityChemicalProcessor,
								slot++,
								116 + (i * 18),
								18 * (j + 1)));
			}
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting) {
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, this.tileChemicalProcessor.chemicalProcessorCookTime);
		par1ICrafting.sendProgressBarUpdate(this, 1, this.tileChemicalProcessor.chemicalProcessorBurnTime);
		par1ICrafting.sendProgressBarUpdate(this, 2, this.tileChemicalProcessor.currentItemBurnTime);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if (this.lastCookTime != this.tileChemicalProcessor.chemicalProcessorCookTime) {
				icrafting.sendProgressBarUpdate(this, 0, this.tileChemicalProcessor.chemicalProcessorCookTime);
			}

			if (this.lastBurnTime != this.tileChemicalProcessor.chemicalProcessorBurnTime) {
				icrafting.sendProgressBarUpdate(this, 1, this.tileChemicalProcessor.chemicalProcessorBurnTime);
			}

			if (this.lastItemBurnTime != this.tileChemicalProcessor.currentItemBurnTime) {
				icrafting.sendProgressBarUpdate(this, 2, this.tileChemicalProcessor.currentItemBurnTime);
			}
		}

		this.lastCookTime = this.tileChemicalProcessor.chemicalProcessorCookTime;
		this.lastBurnTime = this.tileChemicalProcessor.chemicalProcessorBurnTime;
		this.lastItemBurnTime = this.tileChemicalProcessor.currentItemBurnTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		switch (par1) {
			case 0:
				this.tileChemicalProcessor.chemicalProcessorCookTime = par2;
				break;
			case 1:
				this.tileChemicalProcessor.chemicalProcessorBurnTime = par2;
				break;
			case 2:
				this.tileChemicalProcessor.currentItemBurnTime = par2;
				break;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.tileChemicalProcessor.isUseableByPlayer(par1EntityPlayer);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (!this.mergeItemStack(itemstack1, 2 + ChemicalProcessorRecipe.MAX_INPUTS + ChemicalProcessorRecipe.MAX_OUTPUTS, 39, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}
}