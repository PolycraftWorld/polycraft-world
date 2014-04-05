package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public class ContainerChemicalProcessor extends PolycraftCraftingContainer {
	private final TileEntityChemicalProcessor tileChemicalProcessor;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;

	public ContainerChemicalProcessor(InventoryPlayer playerInventory, TileEntityChemicalProcessor tileEntityChemicalProcessor) {
		super(EnumSet.allOf(ChemicalProcessorSlot.class), tileEntityChemicalProcessor);
		this.tileChemicalProcessor = tileEntityChemicalProcessor;
		
		// TODO: Make utility methods for adding inventory slots in PolycraftCraftingContainer
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
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
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		crafting.sendProgressBarUpdate(this, 0, this.tileChemicalProcessor.chemicalProcessorCookTime);
		crafting.sendProgressBarUpdate(this, 1, this.tileChemicalProcessor.chemicalProcessorBurnTime);
		crafting.sendProgressBarUpdate(this, 2, this.tileChemicalProcessor.currentItemBurnTime);
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
	public void updateProgressBar(int which, int amount) {
		switch (which) {
			case 0:
				this.tileChemicalProcessor.chemicalProcessorCookTime = amount;
				break;
			case 1:
				this.tileChemicalProcessor.chemicalProcessorBurnTime = amount;
				break;
			case 2:
				this.tileChemicalProcessor.currentItemBurnTime = amount;
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
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (!this.mergeItemStack(itemstack1,
					2 + PolycraftContainerType.CHEMICAL_PROCESSOR.getSlots(SlotType.INPUT).size()
					+ PolycraftContainerType.CHEMICAL_PROCESSOR.getSlots(SlotType.INPUT).size(),
					39, false)) {
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

			slot.onPickupFromSlot(entityPlayer, itemstack1);
		}

		return itemstack;
	}

	@Override
	public PolycraftContainerType getContainerType() {
		return PolycraftContainerType.CHEMICAL_PROCESSOR;
	}
}