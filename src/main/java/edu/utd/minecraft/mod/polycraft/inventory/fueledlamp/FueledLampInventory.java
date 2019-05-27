package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventory;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.AutomaticInputBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;

public abstract class FueledLampInventory extends StatefulInventory<FueledLampState> implements ISidedInventory {

	private static int[] accessibleSlots = new int[9];
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int i = 0; i < accessibleSlots.length; i++) {
			guiSlots.add(GuiContainerSlot.createInput(i, i, 0, 8, 2));
			accessibleSlots[i] = i;
		}
	}

	private static final int checkOcclusionTicks = 1000;

	private static Inventory config;

	protected final float rangePerHeatIntensity;
	private BlockLight.Source currentLightSource = null;

	protected FueledLampInventory(final PolycraftContainerType containerType, final Inventory config) {
		super(containerType, config, 84, FueledLampState.values());
		this.rangePerHeatIntensity = config.params.getFloat(0);
		this.addBehavior(new AutomaticInputBehavior<HeatedInventory>(false, PolycraftMod.convertSecondsToGameTicks(config.params.getDouble(1))));
		this.addBehavior(new VesselUpcycler());
	}

	@Override
	public int[] getSlotsForFace(EnumFacing facing) {
		return accessibleSlots;
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, EnumFacing facing) {
		return Fuel.getFuel(var2.getItem()) != null;
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, EnumFacing facing) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new FueledLampGui(this, playerInventory);
	}

	private static final int maxTicksPerEpoch = (int) Math.pow(2, 15);

	@Override
	public synchronized void update() {
		super.update();
		if (worldObj != null && !worldObj.isRemote) {
			if (getState(FueledLampState.FuelTicksRemaining) == 0) {

				if (getState(FueledLampState.FuelTicksRemainingEpochs) > 0) //decrement tickEpoch
				{
					setState(FueledLampState.FuelTicksRemaining, maxTicksPerEpoch);
					updateState(FueledLampState.FuelTicksRemainingEpochs, -1);
				}

				else //the lamp should go off or take next fuel
				{
					final ContainerSlot fuelSlot = getNextFuelSlot();
					if (fuelSlot == null) {
						if (removeCurrentLightSource()) {
							setState(FueledLampState.FuelIndex, -1);
							setState(FueledLampState.FuelTicksTotal, 0);
							setState(FueledLampState.FuelHeatIntensity, -1);
						}
					}
					else {
						final ItemStack fuelStack = getStackInSlot(fuelSlot);
						fuelStack.stackSize--;
						if (fuelStack.stackSize == 0)
							clearSlotContents(fuelSlot);

						final Fuel fuel = Fuel.getFuel(fuelStack.getItem());
						final int fuelTicksTotal = PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(fuelStack.getItem()));

						setState(FueledLampState.FuelTicksRemaining, fuelTicksTotal % maxTicksPerEpoch);
						setState(FueledLampState.FuelIndex, fuel.index);
						setState(FueledLampState.FuelTicksTotal, fuelTicksTotal % maxTicksPerEpoch);
						setState(FueledLampState.FuelTicksRemainingEpochs, fuelTicksTotal / maxTicksPerEpoch);
						setState(FueledLampState.FuelTicksTotalEpochs, fuelTicksTotal / maxTicksPerEpoch);
						//used to only do the following if fuel.heatIntensity != getState(FueledLampState.FuelHeatIntensity,
						//but now we just do it all the time so as to update occlusions on fuel switch (as good a time as any)
						setState(FueledLampState.FuelHeatIntensity, fuel.heatIntensity);
						removeCurrentLightSource();
						final BlockLight.Source newLightSource = addLightSource(fuel.heatIntensity);
						currentLightSource = newLightSource;
					}
				}
			}
			else if (currentLightSource == null) {
				currentLightSource = addLightSource(getState(FueledLampState.FuelHeatIntensity));
			}

			if (getState(FueledLampState.FuelTicksRemaining) > 0) {
				updateState(FueledLampState.FuelTicksRemaining, -1);
				markDirty();
			}
		}
	}

	private ContainerSlot getNextFuelSlot() {
		for (final ContainerSlot inputSlot : getInputSlots()) {
			final ItemStack fuelStack = getStackInSlot(inputSlot);
			if (fuelStack != null && Fuel.getHeatDurationSeconds(fuelStack.getItem()) > 0)
				return inputSlot;
		}
		return null;
	}

	protected abstract BlockLight.Source addLightSource(final int heatIntensity);

	public synchronized boolean removeCurrentLightSource() {
		if (currentLightSource != null) {
			BlockLight.removeSource(worldObj, currentLightSource);
			currentLightSource = null;
			return true;
		}
		return false;
	}
}
