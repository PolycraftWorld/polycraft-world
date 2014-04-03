package edu.utd.minecraft.mod.polycraft.crafting;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PolycraftCraftingContainer extends Container {
	private static final Logger logger = LogManager.getLogger();

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
