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

	public PolycraftCraftingContainer(final IInventory inventory, final Iterable<? extends GuiContainerSlot> slots) {
		for (final GuiContainerSlot slot : slots) {
			addInventorySlot(inventory, slot);
		}
	}

	public PolycraftCraftingContainer(final IInventory inventory, final PolycraftContainerType containerType) {
		for (final ContainerSlot slot : containerType.getSlots()) {
			if (slot instanceof GuiContainerSlot) {
				addInventorySlot(inventory, (GuiContainerSlot) slot);
			}
		}
	}

	private void addInventorySlot(final IInventory inventory, final GuiContainerSlot guiSlot) {
		//TODO need to use SlotCrafting, or make our own to get crafting to work for real (the CraftingBehavior class doesn't work by itself)
		Slot newSlot = new Slot(inventory, guiSlot.getSlotIndex(), guiSlot.getDisplayX(), guiSlot.getDisplayY());
		addSlotToContainer(newSlot);
	}

	protected void addPlayerInventorySlots(InventoryPlayer playerInventory, int offset) {
		int nextPlayerSlotIndex = 0;
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(playerInventory, nextPlayerSlotIndex++, 8 + i * 18, 58 + offset));
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(playerInventory, nextPlayerSlotIndex++, 8 + j * 18, i * 18 + offset));
			}
		}
	}
}