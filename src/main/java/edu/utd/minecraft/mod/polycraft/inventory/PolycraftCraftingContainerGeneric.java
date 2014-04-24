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

public class PolycraftCraftingContainerGeneric extends PolycraftCraftingContainer {
	private final PolycraftInventory tileEntity;
	private static final Logger logger = LogManager.getLogger();

	public PolycraftCraftingContainerGeneric(final PolycraftInventory tileEntity, final InventoryPlayer playerInventory) {
		this(tileEntity, playerInventory, 121);
	}

	public PolycraftCraftingContainerGeneric(final PolycraftInventory tileEntity, final InventoryPlayer playerInventory, final int playerInventoryOffset) {
		super(tileEntity.getContainerType(), tileEntity);
		addPlayerInventorySlots(playerInventory, playerInventoryOffset);
		this.tileEntity = tileEntity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
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

			if (!this.mergeItemStack(itemstack1, tileEntity.getContainerType().getSlots(SlotType.INPUT).size(), 39, false)) {
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
		return tileEntity.getContainerType();
	}
}