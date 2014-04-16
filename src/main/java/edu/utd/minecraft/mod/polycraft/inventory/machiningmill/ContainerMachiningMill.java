package edu.utd.minecraft.mod.polycraft.inventory.machiningmill;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public class ContainerMachiningMill extends PolycraftCraftingContainer {
	private final TileEntityMachiningMill tileMachiningMill;

	public ContainerMachiningMill(InventoryPlayer playerInventory, TileEntityMachiningMill tileEntityMachiningMill) {
		super(EnumSet.allOf(MachiningMillSlot.class), tileEntityMachiningMill);
		addPlayerInventorySlots(playerInventory, 121);
		this.tileMachiningMill = tileEntityMachiningMill;
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.tileMachiningMill.isUseableByPlayer(par1EntityPlayer);
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
					2 + PolycraftContainerType.MACHINING_MILL.getSlots(SlotType.INPUT).size()
							+ PolycraftContainerType.MACHINING_MILL.getSlots(SlotType.INPUT).size(),
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
		return PolycraftContainerType.MACHINING_MILL;
	}
}