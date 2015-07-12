package edu.utd.minecraft.mod.polycraft.inventory.heated;

import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.WateredInventory;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.AutomaticInputBehavior;

public abstract class HeatedInventory extends WateredInventory<HeatedInventoryState> implements ISidedInventory {

	protected static Random random = new Random();
	private static final int maxTicksPerEpoch = (int) Math.pow(2, 15);

	public final int slotIndexHeatSource;
	private final int defaultProcessingTicks;
	private final int defaultHeatIntensityMin;
	private final int defaultHeatIntensityMax;
	private final int[] accessibleSlots;
	private BlockLight.Source currentLightSource = null;

	public HeatedInventory(final PolycraftContainerType containerType, final Inventory config, final int slotIndexHeatSource, final int slotIndexCoolingWater, final int slotIndexHeatingWater) {
		this(containerType, config, 0, slotIndexHeatSource, slotIndexCoolingWater, slotIndexHeatingWater);
	}

	public HeatedInventory(final PolycraftContainerType containerType, final Inventory config, final int playerInventoryOffset, final int slotIndexHeatSource, final int slotIndexCoolingWater, final int slotIndexHeatingWater) {
		super(containerType, config, playerInventoryOffset, HeatedInventoryState.values(), slotIndexCoolingWater, slotIndexHeatingWater);
		this.slotIndexHeatSource = slotIndexHeatSource;
		this.defaultProcessingTicks = (config.params == null) ? 0 : PolycraftMod.convertSecondsToGameTicks(config.params.getInt(0));
		this.defaultHeatIntensityMin = (config.params == null) ? 0 : config.params.getInt(1);
		this.defaultHeatIntensityMax = (config.params == null) ? 0 : config.params.getInt(2);
		if (config.params != null) {
			this.addBehavior(new AutomaticInputBehavior<HeatedInventory>(true, PolycraftMod.convertSecondsToGameTicks(config.params.getDouble(3))));
			accessibleSlots = new int[inputSlots.size() + miscSlots.size() + outputSlots.size()];
			int index = 0;
			for (final ContainerSlot slot : inputSlots)
				accessibleSlots[index++] = slot.getSlotIndex();
			for (final ContainerSlot slot : miscSlots)
				accessibleSlots[index++] = slot.getSlotIndex();
			for (final ContainerSlot slot : outputSlots)
				accessibleSlots[index++] = slot.getSlotIndex();
		}
		else
			accessibleSlots = null;
		//this.addBehavior(new VesselUpcycler());
		this.addBehavior(new HeatParticlesBehavior());
	}

	protected abstract HeatedGui getGuiHeated(final InventoryPlayer playerInventory);

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return accessibleSlots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		if (isItemValidForSlot(slot, item)) {
			if (slot == slotIndexCoolingWater || slot == slotIndexHeatingWater)
				return getStackInSlot(slot) == null && item.getItem() == Items.water_bucket;
			if (item.getItem() == Items.water_bucket) {
				if (slotIndexCoolingWater > -1 && getStackInSlot(slotIndexCoolingWater) == null)
					return slot == slotIndexCoolingWater;
				if (slotIndexHeatingWater > -1 && getStackInSlot(slotIndexHeatingWater) == null)
					return slot == slotIndexHeatingWater;
			}
			if (Fuel.getFuel(item.getItem()) != null) {
				final ItemStack currentHeatStack = getStackInSlot(slotIndexHeatSource);
				if (currentHeatStack == null || (currentHeatStack.getItem() == item.getItem() && currentHeatStack.stackSize + item.stackSize <= currentHeatStack.getMaxStackSize()))
					return slotIndexHeatSource == slot;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return getContainerType().getContainerSlotByIndex(slot).getSlotType() == SlotType.OUTPUT;
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

	protected boolean isHeatIntensityValid() {
		return getState(HeatedInventoryState.HeatSourceIntensity) >= getProcessingHeatIntensityForCurrentInputs(true) &&
				getState(HeatedInventoryState.HeatSourceIntensity) <= getProcessingHeatIntensityForCurrentInputs(false);
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much heat time is left on the current heat source, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getHeatSourceTimeRemainingScaled(final int scale) {
		final double ticksRemaining = getState(HeatedInventoryState.HeatSourceTicksRemaining);
		final double ticksRemainingEpochs = getState(HeatedInventoryState.HeatSourceTicksRemainingEpochs);
		if (ticksRemaining > 0 || ticksRemainingEpochs > 0)
			return (int) ((((double) (ticksRemaining + ticksRemainingEpochs * maxTicksPerEpoch) / (double) (getState(HeatedInventoryState.HeatSourceTicksTotal) + (getState(HeatedInventoryState.HeatSourceTicksTotalEpochs) * maxTicksPerEpoch)))) * scale);
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

		if (!worldObj.isRemote) {

			int ticksRemaining = 0;
			if (isHeated())
				ticksRemaining = updateState(HeatedInventoryState.HeatSourceTicksRemaining, -1);

			if (ticksRemaining == 0 && getState(HeatedInventoryState.HeatSourceTicksRemainingEpochs) > 0) //decrement tickEpoch
			{
				ticksRemaining = setState(HeatedInventoryState.HeatSourceTicksRemaining, maxTicksPerEpoch);
				updateState(HeatedInventoryState.HeatSourceTicksRemainingEpochs, -1);
			}

			if (canProcess()) {

				if (!isHeated()) {
					final ItemStack heatSourceItemStack = getStackInSlot(slotIndexHeatSource);
					if (heatSourceItemStack != null) {
						final Fuel fuel = Fuel.getFuel(heatSourceItemStack.getItem());
						if (fuel != null)
						{
							final int heatSourceTicksTotal = PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(heatSourceItemStack.getItem()));
							setState(HeatedInventoryState.HeatSourceTicksRemaining, heatSourceTicksTotal % maxTicksPerEpoch);
							setState(HeatedInventoryState.HeatSourceTicksTotal, heatSourceTicksTotal % maxTicksPerEpoch);
							setState(HeatedInventoryState.HeatSourceTicksRemainingEpochs, heatSourceTicksTotal / maxTicksPerEpoch);
							setState(HeatedInventoryState.HeatSourceTicksTotalEpochs, heatSourceTicksTotal / maxTicksPerEpoch);
							setState(HeatedInventoryState.HeatSourceIntensity, Fuel.getHeatIntensity(heatSourceItemStack.getItem()));

							--heatSourceItemStack.stackSize;
							if (heatSourceItemStack.stackSize == 0)
								setInventorySlotContents(slotIndexHeatSource, heatSourceItemStack.getItem().getContainerItem(heatSourceItemStack));
							isDirty = true;
						}
					}
				}

				if (isHeated() && isHeatIntensityValid()) {
					if (updateState(HeatedInventoryState.ProcessingTicks, 1) == getTotalProcessingTicksForCurrentInputs()) {
						finishProcessing();
						setState(HeatedInventoryState.ProcessingTicks, 0);
						isDirty = true;
					}
				}
				else {
					setState(HeatedInventoryState.ProcessingTicks, 0);
				}
			}
			else {
				setState(HeatedInventoryState.ProcessingTicks, 0);
			}

			if (isHeated()) {
				if (currentLightSource == null) {
					currentLightSource = addLightSource(getState(HeatedInventoryState.HeatSourceIntensity));
				}
			}
			else {
				removeCurrentLightSource();
			}
		}

		if (isDirty)
			markDirty();
	}

	private BlockLight.Source addLightSource(final int heatIntensity) {
		return BlockLight.addSource(worldObj, new BlockLight.Source(worldObj, xCoord, yCoord, zCoord, (int) Math.floor(heatIntensity * .25), false));
	}

	private boolean removeCurrentLightSource() {
		if (currentLightSource != null) {
			BlockLight.removeSource(worldObj, currentLightSource);
			currentLightSource = null;
			return true;
		}
		return false;
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

	//automatically moves storage slots into the input slot
	private class HeatParticlesBehavior extends InventoryBehavior<HeatedInventory> {
		/**
		 * A randomly called display update to be able to add particles or other items for display
		 */
		@Override
		public boolean randomDisplayTick(HeatedInventory inventory, World world, int x, int y, int z, Random random) {
			if (inventory.isHeated()) {
				int l = world.getBlockMetadata(x, y, z);
				float f = (float) x + 0.5F;
				float f1 = (float) y + 0.0F + random.nextFloat() * 6.0F / 16.0F;
				float f2 = (float) z + 0.5F;
				float f3 = 0.52F;
				float f4 = random.nextFloat() * 0.6F - 0.3F;

				if (l == 4)
				{
					world.spawnParticle("smoke", (double) (f - f3), (double) f1, (double) (f2 + f4), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (double) (f - f3), (double) f1, (double) (f2 + f4), 0.0D, 0.0D, 0.0D);
				}
				else if (l == 5)
				{
					world.spawnParticle("smoke", (double) (f + f3), (double) f1, (double) (f2 + f4), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (double) (f + f3), (double) f1, (double) (f2 + f4), 0.0D, 0.0D, 0.0D);
				}
				else if (l == 2)
				{
					world.spawnParticle("smoke", (double) (f + f4), (double) f1, (double) (f2 - f3), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (double) (f + f4), (double) f1, (double) (f2 - f3), 0.0D, 0.0D, 0.0D);
				}
				else if (l == 3)
				{
					world.spawnParticle("smoke", (double) (f + f4), (double) f1, (double) (f2 + f3), 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", (double) (f + f4), (double) f1, (double) (f2 + f3), 0.0D, 0.0D, 0.0D);
				}
			}
			return false;
		}
	}
}
