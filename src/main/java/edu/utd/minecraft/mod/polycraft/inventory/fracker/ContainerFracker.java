package edu.utd.minecraft.mod.polycraft.inventory.fracker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerFracker extends Container {

	private final TileEntityFracker tileFracker;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;

	public ContainerFracker(InventoryPlayer par1InventoryPlayer, TileEntityFracker par2TileEntityFracker) {
		this.tileFracker = par2TileEntityFracker;
		this.addSlotToContainer(new Slot(par2TileEntityFracker, 0, 19, 17)); // input
		this.addSlotToContainer(new Slot(par2TileEntityFracker, 1, 56, 34)); // input
		this.addSlotToContainer(new Slot(par2TileEntityFracker, 2, 19, 53)); // fuel
		this.addSlotToContainer(new SlotFracker(par1InventoryPlayer.player, par2TileEntityFracker, 3, 116, 35)); // output
		int i;

		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(par1InventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(par1InventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting) {
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, this.tileFracker.frackerCookTime);
		par1ICrafting.sendProgressBarUpdate(this, 1, this.tileFracker.frackerBurnTime);
		par1ICrafting.sendProgressBarUpdate(this, 2, this.tileFracker.currentItemBurnTime);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if (this.lastCookTime != this.tileFracker.frackerCookTime) {
				icrafting.sendProgressBarUpdate(this, 0, this.tileFracker.frackerCookTime);
			}

			if (this.lastBurnTime != this.tileFracker.frackerBurnTime) {
				icrafting.sendProgressBarUpdate(this, 1, this.tileFracker.frackerBurnTime);
			}

			if (this.lastItemBurnTime != this.tileFracker.currentItemBurnTime) {
				icrafting.sendProgressBarUpdate(this, 2, this.tileFracker.currentItemBurnTime);
			}
		}

		this.lastCookTime = this.tileFracker.frackerCookTime;
		this.lastBurnTime = this.tileFracker.frackerBurnTime;
		this.lastItemBurnTime = this.tileFracker.currentItemBurnTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		if (par1 == 0) {
			this.tileFracker.frackerCookTime = par2;
		}

		if (par1 == 1) {
			this.tileFracker.frackerBurnTime = par2;
		}

		if (par1 == 2) {
			this.tileFracker.currentItemBurnTime = par2;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return this.tileFracker.isUseableByPlayer(par1EntityPlayer);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 == 2) {
				if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (par2 != 1 && par2 != 0) {
				if (FrackerRecipes.getFrackingResult(itemstack1) != null) {
					if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
						return null;
					}
				} else if (TileEntityFracker.isItemFuel(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
						return null;
					}
				} else if (par2 >= 3 && par2 < 30) {
					if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
						return null;
					}
				} else if (par2 >= 30 && par2 < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
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

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}
}