package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public abstract class WateredInventory<S extends StatefulInventoryState> extends StatefulInventory<S> implements ISidedInventory {

	private final int slotIndexCoolingWater;
	private final int slotIndexHeatingWater;
	private final int[] accessibleSlots;

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
		accessibleSlots = new int[inputSlots.size() + miscSlots.size() + outputSlots.size()];
		int index = 0;
		for (final ContainerSlot slot : inputSlots)
			accessibleSlots[index++] = slot.getSlotIndex();
		for (final ContainerSlot slot : miscSlots)
			accessibleSlots[index++] = slot.getSlotIndex();
		for (final ContainerSlot slot : outputSlots)
			accessibleSlots[index++] = slot.getSlotIndex();
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

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return accessibleSlots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		if (isItemValidForSlot(slot, item)) {
			if (item.getItem() == Items.water_bucket)
				return ((slotIndexCoolingWater > -1 && slotIndexCoolingWater == slot) || (slotIndexHeatingWater > -1 && slotIndexHeatingWater == slot));
			return true;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return getContainerType().getContainerSlotByIndex(slot).getSlotType() == SlotType.OUTPUT;
	}
}
