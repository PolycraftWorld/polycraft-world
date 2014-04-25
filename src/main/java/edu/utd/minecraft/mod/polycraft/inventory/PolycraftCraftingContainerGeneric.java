package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public class PolycraftCraftingContainerGeneric<I extends PolycraftInventory> extends PolycraftCraftingContainer {
	protected final I inventory;
	private static final Logger logger = LogManager.getLogger();

	public PolycraftCraftingContainerGeneric(final I inventory, final InventoryPlayer playerInventory) {
		this(inventory, playerInventory, 121);
	}

	public PolycraftCraftingContainerGeneric(final I inventory, final InventoryPlayer playerInventory, final int playerInventoryOffset) {
		super(inventory, inventory.getContainerType());
		addPlayerInventorySlots(playerInventory, playerInventoryOffset);
		this.inventory = inventory;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
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

			if (!this.mergeItemStack(itemstack1, inventory.getContainerType().getSlots(SlotType.INPUT).size(), 39, false)) {
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
		return inventory.getContainerType();
	}
}