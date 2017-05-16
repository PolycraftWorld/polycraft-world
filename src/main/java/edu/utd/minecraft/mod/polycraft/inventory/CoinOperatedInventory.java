package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;

public abstract class CoinOperatedInventory<S extends StatefulInventoryState> extends StatefulInventory<S> {

	public final int slotIndexFee;

	public CoinOperatedInventory(final PolycraftContainerType containerType, final Inventory config,
			final int playerInventoryOffset, final int slotIndexFee) {
		this(containerType, config, playerInventoryOffset, null, slotIndexFee);
	}

	public CoinOperatedInventory(final PolycraftContainerType containerType, final Inventory config,
			final int playerInventoryOffset, final S[] states,
			final int slotIndexFee) {
		super(containerType, config, playerInventoryOffset, states);
		this.slotIndexFee = slotIndexFee;
	}

	@Override
	public boolean canProcess() {
		if (super.canProcess()) {
			if (slotIndexFee > -1) {
				final ItemStack feeItemStack = getStackInSlot(slotIndexFee);
				if (feeItemStack == null) // feeItemStack.getItem() != ItemCustom.getItemById(per)").getItemStack())
					return false;
			}
			return true;
		}
		return false;
	}
}
