package edu.utd.minecraft.mod.polycraft.inventory.heated;

import java.util.Map;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public abstract class HeatedInventory extends PolycraftInventory {

	public enum State {
		HeatSourceTicksTotal, //The total number of ticks that the current heat source will keep this inventory heated
		HeatSourceTicksRemaining, //The number of ticks that the current heat source will remain heating this inventory
		HeatSourceIntensity, //How intense the current heat source is
		ProcessingTicks //The number of ticks the current recipe has been processed
	}

	private final Map<State, Integer> stateValues = Maps.newHashMap();

	private final int heatSourceSlotIndex;

	public HeatedInventory(final PolycraftContainerType containerType, final Inventory config, final int heatSourceSlotIndex) {
		super(containerType, config);
		this.heatSourceSlotIndex = heatSourceSlotIndex;
		for (final State state : State.values())
			setState(state, 0);
	}

	protected abstract HeatedGui getGuiHeated(final InventoryPlayer playerInventory);

	/*
	 * min = true should return min, otherwise max
	 */
	protected abstract int getProcessingHeatIntensityForCurrentInputs(final boolean min);

	protected abstract int getTotalProcessingTicksForCurrentInputs();

	protected abstract void finishProcessing();

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new HeatedContainer(this, playerInventory);
	}

	@Override
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return getGuiHeated(playerInventory);
	}

	public int getState(final State state) {
		return stateValues.get(state);
	}

	public int setState(final State state, final int value) {
		stateValues.put(state, value);
		return value;
	}

	public int updateState(final State state, final int update) {
		return setState(state, getState(state) + update);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		for (final State state : State.values())
			setState(state, tag.getShort(state.toString()));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		for (final State state : State.values())
			tag.setShort(state.toString(), (short) getState(state));
	}

	public boolean isHeated() {
		return getState(State.HeatSourceTicksRemaining) > 0;
	}

	private boolean isHeatIntensityValid() {
		return getState(State.HeatSourceIntensity) >= getProcessingHeatIntensityForCurrentInputs(true) &&
				getState(State.HeatSourceIntensity) <= getProcessingHeatIntensityForCurrentInputs(false);
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much heat time is left on the current heat source, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getHeatSourceTimeRemainingScaled(final int scale) {
		final double total = getState(State.HeatSourceTicksTotal);
		if (total > 0)
			return (int) ((getState(State.HeatSourceTicksRemaining) / total) * scale);
		return 0;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely processed
	 */
	@SideOnly(Side.CLIENT)
	public int getProcessingProgressScaled(final int scale) {
		final double total = getTotalProcessingTicksForCurrentInputs();
		if (total > 0)
			return (int) ((getState(State.ProcessingTicks) / total) * scale);
		return 0;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		boolean isDirty = false;

		if (isHeated())
			updateState(State.HeatSourceTicksRemaining, -1);

		if (!worldObj.isRemote) {
			if (canProcess()) {
				if (!isHeated()) {
					final ItemStack heatSourceItemStack = getStackInSlot(heatSourceSlotIndex);
					if (heatSourceItemStack != null) {
						setState(State.HeatSourceTicksRemaining, setState(State.HeatSourceTicksTotal,
								PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(heatSourceItemStack.getItem()))));
						setState(State.HeatSourceIntensity, Fuel.getHeatIntensity(heatSourceItemStack.getItem()));
						setState(State.ProcessingTicks, 0);
						--heatSourceItemStack.stackSize;
						if (heatSourceItemStack.stackSize == 0)
							setInventorySlotContents(heatSourceSlotIndex, heatSourceItemStack.getItem().getContainerItem(heatSourceItemStack));
						isDirty = true;
					}
				}

				if (isHeated() && isHeatIntensityValid()) {
					if (updateState(State.ProcessingTicks, 1) == getTotalProcessingTicksForCurrentInputs()) {
						finishProcessing();
						setState(State.ProcessingTicks, 0);
						isDirty = true;
					}
				}
				else
					setState(State.ProcessingTicks, 0);
			}
			else
				setState(State.ProcessingTicks, 0);
		}

		if (isDirty)
			markDirty();
	}
}
