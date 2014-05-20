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
				//find new fuel
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
		final int radiusLit = newFuelHeatIntensity;
		final int radiusToUpdate = Math.max(fuelHeatIntensity, radiusLit);

		boolean occludedPos = false;
		boolean occludedNeg = false;
		for (int x = 1; x <= radiusToUpdate; x++) {
			final boolean lit = newFuelHeatIntensity > 0 && x <= radiusLit;
			occludedPos |= setBlockLit(lit && !occludedPos, xCoord + x, yCoord, zCoord);
			occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord - x, yCoord, zCoord);
		}

		occludedPos = false;
		occludedNeg = false;
		for (int y = 1; y <= radiusToUpdate; y++) {
			final boolean lit = newFuelHeatIntensity > 0 && y <= radiusLit;
			occludedPos |= setBlockLit(lit && !occludedPos, xCoord, yCoord + y, zCoord);
			occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord, yCoord - y, zCoord);
		}

		occludedPos = false;
		occludedNeg = false;
		for (int z = 1; z <= radiusToUpdate; z++) {
			final boolean lit = newFuelHeatIntensity > 0 && z <= radiusLit;
			occludedPos |= setBlockLit(lit && !occludedPos, xCoord, yCoord, zCoord + z);
			occludedNeg |= setBlockLit(lit && !occludedNeg, xCoord, yCoord, zCoord - z);
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
