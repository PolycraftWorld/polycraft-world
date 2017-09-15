package edu.utd.minecraft.mod.polycraft.inventory.fluorescentlamp;

import java.util.List;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.block.LabelTexture;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventory;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.AutomaticInputBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;
import edu.utd.minecraft.mod.polycraft.item.ItemFluorescentBulbs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FluorescentLampInventory extends StatefulInventory<FluorescentLampState> implements ISidedInventory {

	private static int bulbSlot = 0;
	private static int[] fuelSlots = new int[7];
	private static Item BULB_ITEM;
	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		BULB_ITEM = (Item) GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("1xn"));
		// System.out.println(BULB_ITEM); // Is null for now because inventories
		// are init before special items.
		// System.out.println("asdf");
		guiSlots.add(new GuiContainerSlot(0, SlotType.MISC, -1, -1, 8, 20, BULB_ITEM)); // Bulb
		// slot
		for (int i = 0; i < fuelSlots.length; i++) {
			fuelSlots[i] = guiSlots.size();
			guiSlots.add(GuiContainerSlot.createInput(guiSlots.size(), i + 2, 0, 8, 2));
		}
	}

	private static final int checkOcclusionTicks = 1000;

	private static Inventory config;

	protected final float rangePerHeatIntensity;
	private BlockLight.Source currentLightSource = null;

	public static void register(final Inventory config) {
		FluorescentLampInventory.config = config;
		config.containerType = PolycraftContainerType.FLUORESCENT_LAMP;
		PolycraftInventory.register(new FluorescentLampBlock(config, FluorescentLampInventory.class));
	}

	public FluorescentLampInventory() {
		super(PolycraftContainerType.FLUORESCENT_LAMP, config, 84, FluorescentLampState.values());
		this.rangePerHeatIntensity = config.params.getFloat(0);
		this.addBehavior(new AutomaticInputBehavior<HeatedInventory>(false,
				PolycraftMod.convertSecondsToGameTicks(config.params.getDouble(1))));
		this.addBehavior(new VesselUpcycler());
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return fuelSlots;
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		if (playerInventoryOffset > 0)
			return new FluorescentLampContainer(this, playerInventory, playerInventoryOffset,
					FluorescentLampState.values());
		return new FluorescentLampContainer(this, playerInventory, FluorescentLampState.values());
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		System.out.println("Check can insert.");
		if (var1 == 0) // Bulb slot
			return var2.getItem().equals(BULB_ITEM);
		return Fuel.getFuel(var2.getItem()) != null;
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		System.out.println("Check can extract.");
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new FluorescentLampGui(this, playerInventory);
	}

	private static final int maxTicksPerEpoch = (int) Math.pow(2, 15);

	private boolean isBulbInserted() {
		ItemStack bulb = getStackInSlot(0);
		return bulb != null && bulb.getItem() instanceof ItemFluorescentBulbs
				&& bulb.getItemDamage() < bulb.getMaxDamage();
	}

	private boolean damageBulb() {
		if (!isBulbInserted())
			return false;
		ItemStack bulb = getStackInSlot(0);
		bulb.setItemDamage(bulb.getItemDamage() + 1);
		return true;
	}

	/*
	 * @Override public void onPickupFromSlot(EntityPlayer player, ContainerSlot
	 * slot, ItemStack bulb) { super.onPickupFromSlot(player, slot, bulb); if
	 * (slot.getSlotIndex() == 0 && bulb.getItem() instanceof
	 * ItemFluorescentBulbs && bulb.getItemDamage() < bulb.getMaxDamage() &&
	 * currentLightSource != null) bulb.setItemDamage(bulb.getItemDamage() + 1);
	 * }
	 */

	@Override
	public synchronized void updateEntity() {
		super.updateEntity();
		if (worldObj != null && !worldObj.isRemote) {
			ItemStack bulb = getStackInSlot(0);
			if (getState(FluorescentLampState.FuelTicksRemaining) == 0) {

				if (getState(FluorescentLampState.FuelTicksRemainingEpochs) > 0) // decrement
																					// tickEpoch
				{
					setState(FluorescentLampState.FuelTicksRemaining, maxTicksPerEpoch);
					updateState(FluorescentLampState.FuelTicksRemainingEpochs, -1);
				}

				else // the lamp should go off or take next fuel
				{
					final ContainerSlot fuelSlot = getNextFuelSlot();
					if (fuelSlot == null) {
						if (removeCurrentLightSource()) {
							setState(FluorescentLampState.FuelIndex, -1);
							setState(FluorescentLampState.FuelTicksTotal, 0);
							setState(FluorescentLampState.FuelHeatIntensity, -1);
						}
					} else {
						final ItemStack fuelStack = getStackInSlot(fuelSlot);
						fuelStack.stackSize--;
						if (fuelStack.stackSize == 0)
							clearSlotContents(fuelSlot);
						damageBulb();

						final Fuel fuel = Fuel.getFuel(fuelStack.getItem());
						final int fuelTicksTotal = PolycraftMod
								.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(fuelStack.getItem()));

						setState(FluorescentLampState.FuelTicksRemaining, fuelTicksTotal % maxTicksPerEpoch);
						setState(FluorescentLampState.FuelIndex, fuel.index);
						setState(FluorescentLampState.FuelTicksTotal, fuelTicksTotal % maxTicksPerEpoch);
						setState(FluorescentLampState.FuelTicksRemainingEpochs, fuelTicksTotal / maxTicksPerEpoch);
						setState(FluorescentLampState.FuelTicksTotalEpochs, fuelTicksTotal / maxTicksPerEpoch);
						// used to only do the following if fuel.heatIntensity
						// != getState(FluorescentLampState.FuelHeatIntensity,
						// but now we just do it all the time so as to update
						// occlusions on fuel switch (as good a time as any)
						setState(FluorescentLampState.FuelHeatIntensity, fuel.heatIntensity);
						removeCurrentLightSource();
						if (isBulbInserted())
							currentLightSource = addLightSource(fuel.heatIntensity);
						// final BlockLight.Source newLightSource = addLightSource(fuel.heatIntensity);
						// currentLightSource = newLightSource;
					}
				}
			} else if (!isBulbInserted()) {
				removeCurrentLightSource();
			} else if (currentLightSource == null) {
				currentLightSource = addLightSource(getState(FluorescentLampState.FuelHeatIntensity));
			}

			if (getState(FluorescentLampState.FuelTicksRemaining) > 0) {
				updateState(FluorescentLampState.FuelTicksRemaining, -1);
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

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		// TODO Auto-generated method stub
		return super.isItemValidForSlot(var1, var2);
		// System.out.println("Check valid");
		// if (var1 == 0)
		// return var2.getItem().equals(BULB_ITEM);
		// return !var2.getItem().equals(BULB_ITEM);
	}

	protected BlockLight.Source addLightSource(final int heatIntensity) {
		return BlockLight.addSource(worldObj, new BlockLight.Source(worldObj, xCoord, yCoord, zCoord,
				(int) Math.floor(heatIntensity * rangePerHeatIntensity), LabelTexture.SIDE_BOTTOM));
	}

	public synchronized boolean removeCurrentLightSource() {
		if (currentLightSource != null) {
			BlockLight.removeSource(worldObj, currentLightSource);
			currentLightSource = null;
			return true;
		}
		return false;
	}

}
