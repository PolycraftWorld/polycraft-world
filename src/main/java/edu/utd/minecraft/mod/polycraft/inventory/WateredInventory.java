package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;

public abstract class WateredInventory<S extends StatefulInventoryState> extends StatefulInventory<S> {

	public final int slotIndexCoolingWater;
	public final int slotIndexHeatingWater;
	private S[] states;

	public WateredInventory(final PolycraftContainerType containerType, final Inventory config,
			final int playerInventoryOffset, final int slotIndexCoolingWater, final int slotIndexHeatingWater) {
		this(containerType, config, playerInventoryOffset, null, slotIndexCoolingWater, slotIndexHeatingWater);
		this.states = (S[]) new StatefulInventoryState[0];
	}

	public WateredInventory(final PolycraftContainerType containerType, final Inventory config,
			final int playerInventoryOffset, final S[] states,
			final int slotIndexCoolingWater, final int slotIndexHeatingWater) {
		super(containerType, config, playerInventoryOffset, states);
		this.slotIndexCoolingWater = slotIndexCoolingWater;
		this.slotIndexHeatingWater = slotIndexHeatingWater;
		this.states = states;
	}
	
	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		if (playerInventoryOffset > 0)
			return new WateredContainer(this, playerInventory, playerInventoryOffset, states);
		return new WateredContainer(this, playerInventory, states);
	}

	@Override
	public boolean canProcess() {
		if (super.canProcess()) {
			if (slotIndexCoolingWater > -1) {
				final ItemStack coolingWaterItemStack = getStackInSlot(slotIndexCoolingWater);
				if (coolingWaterItemStack == null || coolingWaterItemStack.getItem() != Items.water_bucket)
					return false;
			}
			if (slotIndexHeatingWater > -1) {
				final ItemStack heatingWaterItemStack = getStackInSlot(slotIndexHeatingWater);
				if (heatingWaterItemStack == null || heatingWaterItemStack.getItem() != Items.water_bucket)
					return false;
			}
			return true;
		}
		return false;
	}
}
