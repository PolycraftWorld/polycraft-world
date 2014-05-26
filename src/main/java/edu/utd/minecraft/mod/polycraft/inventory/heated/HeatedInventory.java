package edu.utd.minecraft.mod.polycraft.inventory.heated;

import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.WateredInventory;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.AutomaticInputBehavior;

public abstract class HeatedInventory extends WateredInventory<HeatedInventoryState> implements ISidedInventory {

	protected static Random random = new Random();

	private final int slotIndexHeatSource;
	private final int defaultProcessingTicks;
	private final int defaultHeatIntensityMin;
	private final int defaultHeatIntensityMax;

	public HeatedInventory(final PolycraftContainerType containerType, final Inventory config, final int slotIndexHeatSource, final int slotIndexCoolingWater, final int slotIndexHeatingWater) {
		this(containerType, config, 0, slotIndexHeatSource, slotIndexCoolingWater, slotIndexHeatingWater);
	}

	public HeatedInventory(final PolycraftContainerType containerType, final Inventory config, final int playerInventoryOffset, final int slotIndexHeatSource, final int slotIndexCoolingWater, final int slotIndexHeatingWater) {
		super(containerType, config, playerInventoryOffset, HeatedInventoryState.values(), slotIndexCoolingWater, slotIndexHeatingWater);
		this.slotIndexHeatSource = slotIndexHeatSource;
		this.defaultProcessingTicks = (config.params == null) ? 0 : PolycraftMod.convertSecondsToGameTicks(config.params.getInt(0));
		this.defaultHeatIntensityMin = (config.params == null) ? 0 : config.params.getInt(1);
		this.defaultHeatIntensityMax = (config.params == null) ? 0 : config.params.getInt(2);
		if (config.params != null)
			this.addBehavior(new AutomaticInputBehavior<HeatedInventory>(true, PolycraftMod.convertSecondsToGameTicks(config.params.getDouble(3))));
	}

	protected abstract HeatedGui getGuiHeated(final InventoryPlayer playerInventory);

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		if (super.canInsertItem(slot, item, side)) {
			if (!isHeated()) {
				if (Fuel.getFuel(item.getItem()) != null)
					return slotIndexHeatSource == slot;
			}
			return true;
		}
		return false;
	}

	protected int getTotalProcessingTicksForCurrentInputs() {
		return defaultProcessingTicks;
	}

	protected int getProcessingHeatIntensityForCurrentInputs(final boolean min) {
		return min ? defaultHeatIntensityMin : defaultHeatIntensityMax;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return getGuiHeated(playerInventory);
	}

	public boolean isHeated() {
		return getState(HeatedInventoryState.HeatSourceTicksRemaining) > 0;
	}

	private boolean isHeatIntensityValid() {
		return getState(HeatedInventoryState.HeatSourceIntensity) >= getProcessingHeatIntensityForCurrentInputs(true) &&
				getState(HeatedInventoryState.HeatSourceIntensity) <= getProcessingHeatIntensityForCurrentInputs(false);
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much heat time is left on the current heat source, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getHeatSourceTimeRemainingScaled(final int scale) {
		final double total = getState(HeatedInventoryState.HeatSourceTicksTotal);
		if (total > 0)
			return (int) ((getState(HeatedInventoryState.HeatSourceTicksRemaining) / total) * scale);
		return 0;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely processed
	 */
	@SideOnly(Side.CLIENT)
	public int getProcessingProgressScaled(final int scale) {
		final double total = getTotalProcessingTicksForCurrentInputs();
		if (total > 0)
			return (int) ((getState(HeatedInventoryState.ProcessingTicks) / total) * scale);
		return 0;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		boolean isDirty = false;

		if (isHeated())
			updateState(HeatedInventoryState.HeatSourceTicksRemaining, -1);

		if (!worldObj.isRemote) {
			if (canProcess()) {
				if (!isHeated()) {
					final ItemStack heatSourceItemStack = getStackInSlot(slotIndexHeatSource);
					if (heatSourceItemStack != null) {
						setState(HeatedInventoryState.HeatSourceTicksRemaining, setState(HeatedInventoryState.HeatSourceTicksTotal,
								PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(heatSourceItemStack.getItem()))));
						setState(HeatedInventoryState.HeatSourceIntensity, Fuel.getHeatIntensity(heatSourceItemStack.getItem()));
						--heatSourceItemStack.stackSize;
						if (heatSourceItemStack.stackSize == 0)
							setInventorySlotContents(slotIndexHeatSource, heatSourceItemStack.getItem().getContainerItem(heatSourceItemStack));
						isDirty = true;
					}
				}

				if (isHeated() && isHeatIntensityValid()) {
					if (updateState(HeatedInventoryState.ProcessingTicks, 1) == getTotalProcessingTicksForCurrentInputs()) {
						finishProcessing();
						setState(HeatedInventoryState.ProcessingTicks, 0);
						isDirty = true;
					}
				}
				else
					setState(HeatedInventoryState.ProcessingTicks, 0);
			}
			else
				setState(HeatedInventoryState.ProcessingTicks, 0);
		}

		if (isDirty)
			markDirty();
	}

	protected void finishProcessing() {
		Set<RecipeComponent> inputs = getMaterials();
		final PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(containerType, inputs);
		if (recipe != null) {
			for (final RecipeComponent output : recipe.getOutputs(this)) {
				if (getStackInSlot(output.slot) == null)
					setStackInSlot(output.slot, output.itemStack.copy());
				else
					getStackInSlot(output.slot).stackSize += output.itemStack.stackSize;

			}
			final Set<RecipeInput> usedInputs = Sets.newHashSet();
			for (final RecipeComponent input : ImmutableList.copyOf(inputs))
				finishProcessingInput(input.slot.getSlotIndex(), getStackInSlot(input.slot), recipe.getItemstackForInput(input, usedInputs));
		}
	}

	protected abstract void finishProcessingInput(final int slotIndex, final ItemStack actualInput, final ItemStack recipeInput);
}
