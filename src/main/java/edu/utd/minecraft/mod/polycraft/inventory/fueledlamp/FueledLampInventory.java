package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;

public class FueledLampInventory extends PolycraftInventory {

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int i = 0; i < 5; i++)
			guiSlots.add(GuiContainerSlot.createInput(i, i, 0, 44, 2));
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		FueledLampInventory.config = config;
		PolycraftInventory.register(new FueledLampBlock(config, FueledLampInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public int fuelHeatTicksRemaining = 0;
	public int fuelHeatIntensity = 0;

	public FueledLampInventory() {
		super(PolycraftContainerType.FUELED_LAMP, config);
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 51);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		return new PolycraftInventoryGui(this, playerInventory, 133, false);
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		this.fuelHeatTicksRemaining = p_145839_1_.getInteger("FuelHeatTicksRemaining");
		this.fuelHeatIntensity = p_145839_1_.getInteger("FuelHeatIntensity");
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setInteger("FuelHeatTicksRemaining", this.fuelHeatTicksRemaining);
		p_145841_1_.setInteger("FuelHeatIntensity", this.fuelHeatIntensity);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj != null && !worldObj.isRemote) {
			if (fuelHeatTicksRemaining == 0) {
				int newFuelHeatIntensity = 0;
				// find new fuel
				final ContainerSlot fuelSlot = getNextFuelSlot();
				if (fuelSlot != null) {
					final ItemStack fuelStack = getStackInSlot(fuelSlot);
					fuelHeatTicksRemaining = PolycraftMod.convertSecondsToGameTicks(Fuel.getHeatDurationSeconds(fuelStack.getItem()));
					newFuelHeatIntensity = Fuel.getHeatIntensity(fuelStack.getItem());
					fuelStack.stackSize--;
					if (fuelStack.stackSize == 0)
						clearSlotContents(fuelSlot);
				}
				updateFuelIntensity(newFuelHeatIntensity, false);
			}
			if (fuelHeatTicksRemaining > 0) {
				fuelHeatTicksRemaining--;
				markDirty();
			}
		}
	}

	public void updateFuelIntensity(final int newFuelHeatIntensity, final boolean turnOff) {

		setBlockLit(newFuelHeatIntensity > 0, xCoord, yCoord, zCoord, turnOff);
		final int radiusLit;

		if (turnOff)
		{
			radiusLit = fuelHeatIntensity;

		}
		else
		{
			radiusLit = newFuelHeatIntensity;
		}

		final int radiusToUpdate = Math.max(fuelHeatIntensity, radiusLit);
		boolean occludedA = false;

		double angleInRadians;
		if (newFuelHeatIntensity > 0)
			angleInRadians = 2.0 / newFuelHeatIntensity * Math.PI;
		else if (fuelHeatIntensity > 0)
			angleInRadians = 2.0 / fuelHeatIntensity * Math.PI;
		else
			angleInRadians = 0;
		double lineAngle = 0;

		if (angleInRadians > 0)
		{
			for (int lines = 0; lineAngle <= 2 * Math.PI; lines++) {
				lineAngle = angleInRadians * lines;

				occludedA = false;
				for (int i = 1; i <= radiusLit; i++) {
					final boolean lit = newFuelHeatIntensity > 0 && i <= radiusLit;
					occludedA |= setBlockLit(lit && !occludedA, xCoord + (int) (Math.ceil(Math.sin(lineAngle) * i)), yCoord, zCoord + (int) (Math.ceil(Math.cos(lineAngle) * i)), turnOff);

				}

			}
		}

		fuelHeatIntensity = newFuelHeatIntensity;
	}

	private ContainerSlot getNextFuelSlot() {
		for (final ContainerSlot inputSlot : getInputSlots()) {
			final ItemStack fuelStack = getStackInSlot(inputSlot);
			if (fuelStack != null && Fuel.getHeatDurationSeconds(fuelStack.getItem()) > 0)
				return inputSlot;
		}
		return null;
	}

	private boolean setBlockLit(final boolean lit, final int x, final int y, final int z, boolean turnOff) {
		final Block block = worldObj.getBlock(x, y, z);
		if (block instanceof BlockLight) {
			if (!lit && turnOff)
				worldObj.setBlockToAir(x, y, z);
		}
		else if (block.getMaterial() == Material.air) {
			if (lit && !turnOff)
				worldObj.setBlock(x, y, z, PolycraftMod.blockLight);
		}
		else if (block instanceof FueledLampBlock)
		{
			return false;
		}
		else
			return true;
		return false;
	}
}
