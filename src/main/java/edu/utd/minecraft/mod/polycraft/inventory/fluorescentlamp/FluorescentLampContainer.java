package edu.utd.minecraft.mod.polycraft.inventory.fluorescentlamp;

import net.minecraftforge.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulContainer;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventory;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventoryState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FluorescentLampContainer<I extends StatefulInventory, S extends StatefulInventoryState>
		extends StatefulContainer {

	private static Item BULB_ITEM = (Item) GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("1xn"));

	public FluorescentLampContainer(I inventory, InventoryPlayer playerInventory, S[] states) {
		super(inventory, playerInventory, states);
		// this.inventory = inventory;
	}

	public FluorescentLampContainer(I inventory, InventoryPlayer playerInventory, int playerInventoryOffset,
			S[] states) {
		super(inventory, playerInventory, playerInventoryOffset, states);
		// this.inventory = inventory;
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or
	 * you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			if (itemstack1.getItem().equals(BULB_ITEM) && slotIndex >= getFirstPlayerInventorySlot()
					&& slotIndex < getLastPlayerInventorySlot()) {
				itemstack = itemstack1.copy();
				boolean merge = mergeItemStack(itemstack1, 0, 1, false);
				if (merge && itemstack1.stackSize == 0) {
					slot.putStack((ItemStack) null);
				} else if (merge) {
					slot.onSlotChanged();
				} else {
					return null;
				}
			} else {
				if (slotIndex < getFirstPlayerInventorySlot()) {
					if (!this.mergeItemStack(itemstack1, getFirstPlayerInventorySlot(), this.inventorySlots.size(),
							true)) {
						return null;
					}
				} else if (!this.mergeItemStack(itemstack1, 1, getFirstPlayerInventorySlot(), false)) {
					return null;
				}
				if (itemstack1.stackSize == 0) {
					slot.putStack((ItemStack) null);
				} else {
					slot.onSlotChanged();
				}
			}
		}
		return itemstack;
	}
}
