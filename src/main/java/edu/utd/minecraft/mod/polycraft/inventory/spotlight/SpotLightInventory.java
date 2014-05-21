package edu.utd.minecraft.mod.polycraft.inventory.spotlight;

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

public class SpotLightInventory extends PolycraftInventory {

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int i = 0; i < 5; i++)
			guiSlots.add(GuiContainerSlot.createInput(i, i, 0, 44, 2));
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		SpotLightInventory.config = config;
		PolycraftInventory.register(new SpotLightBlock(config, SpotLightInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public int fuelHeatTicksRemaining = 0;
	public int fuelHeatIntensity = 0;

	public SpotLightInventory() {
		super(PolycraftContainerType.SPOTLIGHT, config);
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
				updateFuelIntensity(newFuelHeatIntensity);
			}
			if (fuelHeatTicksRemaining > 0) {
				fuelHeatTicksRemaining--;
				markDirty();
			}
		}
	}

	public void updateFuelIntensity(final int newFuelHeatIntensity) {
		setBlockLit(newFuelHeatIntensity > 0, xCoord, yCoord, zCoord);
		final int radiusLit = newFuelHeatIntensity * 10;
		final int radiusToUpdate = Math.max(fuelHeatIntensity * 10, radiusLit);
		final int sideTemp = 0;
		int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		boolean occludedPos = false;
		boolean occludedNeg = false;
		for (int i = 1; i <= radiusToUpdate; i++) {
			final boolean lit = newFuelHeatIntensity > 0 && i <= radiusLit;
			if (lit)
			{
				if (metadata == 4)
					occludedPos |= setBlockLit(lit && !occludedPos, xCoord - i, yCoord, zCoord);
				else if (metadata == 5)
					occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord + i, yCoord, zCoord);
				else if (metadata == 0)
					occludedPos |= setBlockLit(lit && !occludedPos, xCoord, yCoord - i, zCoord);
				else if (metadata == 1)
					occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord, yCoord + i, zCoord);
				else if (metadata == 2)
					occludedPos |= setBlockLit(lit && !occludedPos, xCoord, yCoord, zCoord - i);
				else if (metadata == 3)
					occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord, yCoord, zCoord + i);
			}
			else
			{
				occludedPos |= setBlockLit(lit && !occludedPos, xCoord - i, yCoord, zCoord);
				occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord + i, yCoord, zCoord);
				occludedPos |= setBlockLit(lit && !occludedPos, xCoord, yCoord - i, zCoord);
				occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord, yCoord + i, zCoord);
				occludedPos |= setBlockLit(lit && !occludedPos, xCoord, yCoord, zCoord - i);
				occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord, yCoord, zCoord + i);
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

	private boolean setBlockLit(final boolean lit, final int x, final int y, final int z) {
		final Block block = worldObj.getBlock(x, y, z);
		if (block instanceof BlockLight) {
			if (!lit)
				worldObj.setBlockToAir(x, y, z);
		}
		else if (block.getMaterial() == Material.air) {
			if (lit)
				worldObj.setBlock(x, y, z, PolycraftMod.blockLight);
		}
		else
			return true;
		return false;
	}
}
