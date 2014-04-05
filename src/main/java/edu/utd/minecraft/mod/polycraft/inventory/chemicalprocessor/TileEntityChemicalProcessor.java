package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftBasicTileEntityContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeComponent;
import edu.utd.minecraft.mod.polycraft.crafting.RecipeInput;
import edu.utd.minecraft.mod.polycraft.item.ItemFluidContainer;

/**
 * Handles processing of the chemical processor container.  Any inputs that use a
 * fluid container automatically generate empty fluid containers on output (they do
 * not need to be, and should not be specified as outputs of the recipe).
 */
public class TileEntityChemicalProcessor extends PolycraftBasicTileEntityContainer<ChemicalProcessorSlot> implements ISidedInventory {
	public TileEntityChemicalProcessor() {
		super(PolycraftContainerType.CHEMICAL_PROCESSOR);
	}

	/**
	 * The number of ticks that the chemical processor will keep burning
	 */
	public int chemicalProcessorBurnTime;
	/**
	 * The number of ticks that a fresh copy of the currently-burning item would keep the chemical processor burning for
	 */
	public int currentItemBurnTime;
	/**
	 * The number of ticks that the current item has been cooking for
	 */
	public int chemicalProcessorCookTime;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.chemicalProcessorBurnTime = tag.getShort("BurnTime");
		this.chemicalProcessorCookTime = tag.getShort("CookTime");
		this.currentItemBurnTime = getItemBurnTime(getStackInSlot(ChemicalProcessorSlot.INPUT_FUEL));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("BurnTime", (short) this.chemicalProcessorBurnTime);
		tag.setShort("CookTime", (short) this.chemicalProcessorCookTime);
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely cooked
	 */
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int scale) {
		return this.chemicalProcessorCookTime * scale / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int scale) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 200;
		}
		return this.chemicalProcessorBurnTime * scale / this.currentItemBurnTime;
	}

	/**
	 * ChemicalProcessor isBurning
	 */
	public boolean isBurning() {
		return this.chemicalProcessorBurnTime > 0;
	}

	// Generates fluid containers in the appropriate slot, if there are 
	// items that use fluid containers
	private void generateFluidContainerOutput() {
		Set<RecipeComponent> inputs = getMaterials();
		final PolycraftRecipe recipe = PolycraftMod.recipeManager.findRecipe(
				PolycraftContainerType.CHEMICAL_PROCESSOR, inputs);
		if (recipe != null) {
			// Recipe is valid			
			int fluidContainersRequired = 0;
			for (final RecipeInput input : recipe.getInputs()) {
				ItemStack item = input.inputs.iterator().next();
				if (item.getItem() instanceof ItemFluidContainer) {
					if (((ItemFluidContainer)item.getItem()).fluidEntity != null) {
						fluidContainersRequired += item.stackSize;						
					}
				}
			}
			
			// Add fluid containers to output
			if (getStackInSlot(ChemicalProcessorSlot.OUTPUT_EMPTY_BOTTLE) != null) {
				// TODO: Validate this is a fluid bottle
				fluidContainersRequired += getStackInSlot(ChemicalProcessorSlot.OUTPUT_EMPTY_BOTTLE).stackSize;
			}
			// TODO: Check max BEFORE processing; don't process if max
			if (fluidContainersRequired > 64) {
				fluidContainersRequired = 64;
			}
			setStackInSlot(ChemicalProcessorSlot.OUTPUT_EMPTY_BOTTLE,
					new ItemStack(PolycraftMod.itemFluidContainer, fluidContainersRequired));
		}
	}
	
	@Override
	public void updateEntity() {
		boolean flag = this.chemicalProcessorBurnTime > 0;
		boolean isDirty = false;

		if (this.chemicalProcessorBurnTime > 0) {
			--this.chemicalProcessorBurnTime;
		}

		if (!this.worldObj.isRemote) {
			if (this.chemicalProcessorBurnTime == 0 && this.canProcess() && getStackInSlot(ChemicalProcessorSlot.INPUT_FUEL) != null) {
				ItemStack fuelStack = getStackInSlot(ChemicalProcessorSlot.INPUT_FUEL);
				this.currentItemBurnTime = this.chemicalProcessorBurnTime = getItemBurnTime(fuelStack);
				if (this.chemicalProcessorBurnTime > 0) {
					isDirty = true;
					if (fuelStack != null) {
						--fuelStack.stackSize;

						if (fuelStack.stackSize == 0) {
							// TODO: What's this do?
							setStackInSlot(ChemicalProcessorSlot.INPUT_FUEL, fuelStack.getItem().getContainerItem(fuelStack));
						}
					}
				}
			}

			if (this.isBurning() && this.canProcess()) {
				++this.chemicalProcessorCookTime;

				if (this.chemicalProcessorCookTime == 200) {
					this.chemicalProcessorCookTime = 0;
					generateFluidContainerOutput();
					this.craftItems();	
					isDirty = true;
				}
			} else {
				this.chemicalProcessorCookTime = 0;
			}

			if (flag != this.chemicalProcessorBurnTime > 0) {
				isDirty = true;
				BlockChemicalProcessor.updateChemicalProcessorBlockState(this.chemicalProcessorBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if (isDirty) {
			this.markDirty();
		}
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the chemical processor burning, or 0 if the item isn't fuel
	 */
	public static int getItemBurnTime(ItemStack itemStack) {
		if (itemStack == null) {
			return 0;
		} else {
			Item item = itemStack.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.wooden_slab) {
					return 150;
				}

				if (block.getMaterial() == Material.wood) {
					return 300;
				}

				if (block == Blocks.coal_block) {
					return 16000;
				}
			}

			if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) {
				return 200;
			}
			if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) {
				return 200;
			}
			if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD")) {
				return 200;
			}
			if (item == Items.stick) {
				return 100;
			}
			if (item == Items.coal) {
				return 1600;
			}
			if (item == Items.lava_bucket) {
				return 20000;
			}
			if (item == Item.getItemFromBlock(Blocks.sapling)) {
				return 100;
			}
			if (item == Items.blaze_rod) {
				return 2400;
			}			
			return GameRegistry.getFuelValue(itemStack);
		}
	}

	public static boolean isItemFuel(ItemStack itemStack) {
		/**
		 * Returns the number of ticks that the supplied fuel item will keep the chemical processor burning, or 0 if the item isn't fuel
		 */
		return getItemBurnTime(itemStack) > 0;
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int par1, ItemStack itemStack) {
		return par1 == 2 ? false : (par1 == 1 ? isItemFuel(itemStack) : true);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		return side != 0 || slotIndex != 1 || itemStack.getItem() == Items.bucket;
	}
}