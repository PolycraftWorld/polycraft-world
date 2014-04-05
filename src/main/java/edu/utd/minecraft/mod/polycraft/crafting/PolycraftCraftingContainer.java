package edu.utd.minecraft.mod.polycraft.crafting;

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
}
