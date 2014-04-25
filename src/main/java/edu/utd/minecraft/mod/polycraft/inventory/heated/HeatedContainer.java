package edu.utd.minecraft.mod.polycraft.inventory.heated;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;

public class HeatedContainer<I extends HeatedInventory> extends PolycraftCraftingContainerGeneric<I> {

	private int lastProcessingTime;
	private int lastHeatTime;
	private int lastItemHeatTime;

	public HeatedContainer(final I inventory, final InventoryPlayer playerInventory) {
		super(inventory, playerInventory);
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		crafting.sendProgressBarUpdate(this, 0, inventory.processingTime);
		crafting.sendProgressBarUpdate(this, 1, inventory.heatTime);
		crafting.sendProgressBarUpdate(this, 2, inventory.currentItemHeatTime);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.crafters.get(i);

			if (this.lastProcessingTime != inventory.processingTime) {
				icrafting.sendProgressBarUpdate(this, 0, inventory.processingTime);
			}

			if (this.lastHeatTime != inventory.heatTime) {
				icrafting.sendProgressBarUpdate(this, 1, inventory.heatTime);
			}

			if (this.lastItemHeatTime != inventory.currentItemHeatTime) {
				icrafting.sendProgressBarUpdate(this, 2, inventory.currentItemHeatTime);
			}
		}
		this.lastProcessingTime = inventory.processingTime;
		this.lastHeatTime = inventory.heatTime;
		this.lastItemHeatTime = inventory.currentItemHeatTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int which, int amount) {
		switch (which) {
		case 0:
			inventory.processingTime = amount;
			break;
		case 1:
			inventory.heatTime = amount;
			break;
		case 2:
			inventory.currentItemHeatTime = amount;
			break;
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return inventory.isUseableByPlayer(par1EntityPlayer);
	}
}