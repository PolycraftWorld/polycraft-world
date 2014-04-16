package edu.utd.minecraft.mod.polycraft.inventory.machiningmill;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftBasicTileEntityContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;

public class TileEntityMachiningMill extends PolycraftBasicTileEntityContainer implements ISidedInventory {

	public TileEntityMachiningMill() {
		super(PolycraftContainerType.MACHINING_MILL, PolycraftMod.blockNameMachiningMill);
	}

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote) {
			//TODO this is probably incorrect
			if (this.canProcess()) {
				this.craftItems();
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		// TODO
		return false;
	}
}