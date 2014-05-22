package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockLight;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventory;

public class FueledLampInventory extends StatefulInventory<FueledLampState> {

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int i = 0; i < 9; i++)
			guiSlots.add(GuiContainerSlot.createInput(i, i, 0, 8, 2));
	}

	private static final int checkOcclusionTicks = 1000;

	private static Inventory config;

	public static final void register(final Inventory config) {
		FueledLampInventory.config = config;
		PolycraftInventory.register(new FueledLampBlock(config, FueledLampInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	private boolean needInitialVote = true;
	private final float rangePerHeatIntensity;

	public FueledLampInventory() {
		super(PolycraftContainerType.FUELED_LAMP, config, 84, FueledLampState.values());
		this.rangePerHeatIntensity = config.params.getFloat(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new FueledLampGui(this, playerInventory);
	}

	@Override
	public synchronized void updateEntity() {
		super.updateEntity();
		if (worldObj != null && !worldObj.isRemote) {
			if (needInitialVote) {
				needInitialVote = false;
				voteOnLights(true);
			}

			if (getState(FueledLampState.FuelTicksRemaining) == 0) {
				Fuel fuel = null;
				int fuelTicksTotal = 0;
				final ContainerSlot fuelSlot = getNextFuelSlot();
				if (fuelSlot != null) {
					final ItemStack fuelStack = getStackInSlot(fuelSlot);
					fuel = Fuel.getFuel(fuelStack.getItem());
					voteOnLights(fuel.heatIntensity, true);
					fuelTicksTotal = PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(fuelStack.getItem()));
					fuelStack.stackSize--;
					if (fuelStack.stackSize == 0)
						clearSlotContents(fuelSlot);
				}
				voteOnLights(false);
				setState(FueledLampState.FuelIndex, fuel == null ? -1 : fuel.index);
				setState(FueledLampState.FuelTicksTotal, fuelTicksTotal);
				setState(FueledLampState.FuelTicksRemaining, fuelTicksTotal);
				setState(FueledLampState.FuelHeatIntensity, fuel == null ? -1 : fuel.heatIntensity);
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

	public Fuel getCurrentFuel() {
		return Fuel.getFuel(getState(FueledLampState.FuelIndex));
	}

	public void voteOnLights(final boolean enabled) {
		voteOnLights(getState(FueledLampState.FuelHeatIntensity), enabled);
	}

	public void voteOnLights(final int heatIntensity, final boolean enabled) {
		if (heatIntensity > 0) {
			final int range = (int) Math.floor(heatIntensity * rangePerHeatIntensity);
			BlockLight.vote(worldObj, xCoord, yCoord, zCoord, enabled);
			int r = enabled ? 1 : range;
			while (r >= 1 && r <= range) {
				final int originX = xCoord - r;
				final int originZ = zCoord - r;
				final int size = (r * 2) + 1;
				for (int s = 0; s < size; s += 2) {
					BlockLight.vote(worldObj, originX + s, yCoord, originZ, enabled);
					BlockLight.vote(worldObj, originX + s, yCoord, originZ + size - 1, enabled);
					if (s > 0 && s < (size - 1)) {
						BlockLight.vote(worldObj, originX, yCoord, originZ + s, enabled);
						BlockLight.vote(worldObj, originX + size - 1, yCoord, originZ + s, enabled);
					}
				}
				r += enabled ? 1 : -1;
			}
		}
	}
}
