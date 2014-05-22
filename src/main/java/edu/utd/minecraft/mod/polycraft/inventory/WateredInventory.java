package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;

public abstract class WateredInventory<S extends StatefulInventoryState> extends StatefulInventory<S> {

	private final int slotIndexCoolingWater;
	private final int slotIndexHeatingWater;

	public WateredInventory(final PolycraftContainerType containerType, final Inventory config,
			final int playerInventoryOffset, final int slotIndexCoolingWater, final int slotIndexHeatingWater) {
		this(containerType, config, playerInventoryOffset, null, slotIndexCoolingWater, slotIndexHeatingWater);
	}

	public WateredInventory(final PolycraftContainerType containerType, final Inventory config,
			final int playerInventoryOffset, final S[] states,
			final int slotIndexCoolingWater, final int slotIndexHeatingWater) {
		super(containerType, config, playerInventoryOffset, states);
		this.slotIndexCoolingWater = slotIndexCoolingWater;
		this.slotIndexHeatingWater = slotIndexHeatingWater;
	}

	@Override
	public boolean canProcess() {
		if (super.canProcess()) {
			if (slotIndexCoolingWater > 0) {
				final ItemStack coolingWaterItemStack = getStackInSlot(slotIndexCoolingWater);
				if (coolingWaterItemStack == null || coolingWaterItemStack.getItem() != Items.water_bucket)
					return false;
			}
			if (slotIndexHeatingWater > 0) {
				final ItemStack coolingWaterItemStack = getStackInSlot(slotIndexCoolingWater);
				if (coolingWaterItemStack == null || coolingWaterItemStack.getItem() != Items.water_bucket)
					return false;
			}
			return true;
		}
		return false;
	}
}
