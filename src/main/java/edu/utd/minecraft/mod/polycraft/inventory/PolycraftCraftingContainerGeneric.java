package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;

public class PolycraftCraftingContainerGeneric<I extends PolycraftInventory> extends PolycraftCraftingContainer {
	protected final I inventory;
	private static final Logger logger = LogManager.getLogger();
	private final int firstPlayerInventorySlot;
	private final boolean dropInputsOnClosing;

	public PolycraftCraftingContainerGeneric(final I inventory, final InventoryPlayer playerInventory) {
		this(inventory, playerInventory, 121);
	}

	public PolycraftCraftingContainerGeneric(final I inventory, final InventoryPlayer playerInventory, final int playerInventoryOffset) {
		super(inventory, inventory.getContainerType());
		this.firstPlayerInventorySlot = addPlayerInventorySlots(playerInventory, playerInventoryOffset);
		this.inventory = inventory;
		this.dropInputsOnClosing = false;
	}

	public PolycraftCraftingContainerGeneric(final I inventory, final InventoryPlayer playerInventory, final int playerInventoryOffset, final boolean dropInputsOnClosing) {
		super(inventory, inventory.getContainerType());
		this.firstPlayerInventorySlot = addPlayerInventorySlots(playerInventory, playerInventoryOffset);
		this.inventory = inventory;
		this.dropInputsOnClosing = dropInputsOnClosing;
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		if (dropInputsOnClosing) {
			if (!par1EntityPlayer.worldObj.isRemote)
			{
				for (final ContainerSlot slot : inventory.getInputSlots()) {
					ItemStack itemstack = inventory.getStackInSlotOnClosing(slot.getSlotIndex());
					if (itemstack != null)
						par1EntityPlayer.dropPlayerItemWithRandomChoice(itemstack, false);
				}
			}
		}
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

			if (!this.mergeItemStack(itemstack1, firstPlayerInventorySlot, firstPlayerInventorySlot + 36, false)) {
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
			if (!(slot instanceof PolycraftCraftingContainer.CraftingSlot) && slot.inventory != null && slot.inventory instanceof PolycraftInventory) {
				((PolycraftInventory) slot.inventory).onPickupFromSlot(entityPlayer, getContainerType().getContainerSlotByIndex(slotIndex), itemstack1);
			}
		}
		return itemstack;
	}

	@Override
	public PolycraftContainerType getContainerType() {
		return inventory.getContainerType();
	}
}