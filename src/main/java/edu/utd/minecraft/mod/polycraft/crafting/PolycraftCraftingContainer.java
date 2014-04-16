package edu.utd.minecraft.mod.polycraft.crafting;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public abstract class PolycraftCraftingContainer extends Container {
	/**
	 * @return the enumerated container type
	 */
	public abstract PolycraftContainerType getContainerType();

	public PolycraftCraftingContainer(final Iterable<? extends GuiContainerSlot> slots, final IInventory inventory) {
		for (final GuiContainerSlot slot : slots) {
			Slot newSlot = new Slot(inventory, slot.getSlotIndex(), slot.getDisplayX(), slot.getDisplayY());
			this.addSlotToContainer(newSlot);
		}
	}

	protected void addPlayerInventorySlots(InventoryPlayer playerInventory, int offset) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, offset + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, offset + 58));
		}
	}
}