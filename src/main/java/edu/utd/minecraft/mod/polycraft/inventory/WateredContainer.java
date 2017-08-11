package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Watered inventories have 1 to 2 slots for water buckets only, so allow
 * shift-click to prioritize those.
 */
public class WateredContainer<I extends WateredInventory, S extends StatefulInventoryState> extends StatefulContainer {
	private I inventory;

	public WateredContainer(I inventory, InventoryPlayer playerInventory, S[] states) {
		super(inventory, playerInventory, states);
		this.inventory = inventory;
	}

	public WateredContainer(I inventory, InventoryPlayer playerInventory, int playerInventoryOffset, S[] states) {
		super(inventory, playerInventory, playerInventoryOffset, states);
		this.inventory = inventory;
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack() && slotIndex >= getFirstPlayerInventorySlot()
				&& slotIndex < getLastPlayerInventorySlot()) {
			ItemStack itemstack1 = slot.getStack();
			if (itemstack1.getItem().equals(Items.water_bucket)) {
				itemstack = itemstack1.copy();
				int cool = inventory.slotIndexCoolingWater;
				int heat = inventory.slotIndexHeatingWater;
				System.out.println("Cool: " + cool + ". Heat: " + heat + ".");
				boolean merge = cool != -1 && mergeItemStack(itemstack1, cool, cool + 1, false)
						|| heat != -1 && mergeItemStack(itemstack1, heat, heat + 1, false);
				if (merge && itemstack1.stackSize == 0) {
					slot.putStack((ItemStack) null);
				} else if (merge) {
					slot.onSlotChanged();
				} else {
					return null;
				}
			}
		}
		if (itemstack == null)
			return super.transferStackInSlot(entityPlayer, slotIndex);
		return itemstack;
	}

}
