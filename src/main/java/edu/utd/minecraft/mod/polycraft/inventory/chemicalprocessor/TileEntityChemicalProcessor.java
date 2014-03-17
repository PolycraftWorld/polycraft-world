package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class TileEntityChemicalProcessor extends TileEntity implements ISidedInventory {
	/**
	 * The ItemStacks that hold the items currently being used in the chemical processor
	 */
	private ItemStack[] chemicalProcessorItemStacks = new ItemStack[14];

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
	private String inventoryName;

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return this.chemicalProcessorItemStacks.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int par1) {
		return this.chemicalProcessorItemStacks[par1];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1, int par2) {
		if (this.chemicalProcessorItemStacks[par1] != null) {
			ItemStack itemstack;

			if (this.chemicalProcessorItemStacks[par1].stackSize <= par2) {
				itemstack = this.chemicalProcessorItemStacks[par1];
				this.chemicalProcessorItemStacks[par1] = null;
				return itemstack;
			} else {
				itemstack = this.chemicalProcessorItemStacks[par1].splitStack(par2);

				if (this.chemicalProcessorItemStacks[par1].stackSize == 0) {
					this.chemicalProcessorItemStacks[par1] = null;
				}

				return itemstack;
			}
		} else {
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (this.chemicalProcessorItemStacks[par1] != null) {
			ItemStack itemstack = this.chemicalProcessorItemStacks[par1];
			this.chemicalProcessorItemStacks[par1] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		this.chemicalProcessorItemStacks[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.inventoryName : "container.chemical_processor";
	}

	/**
	 * Returns if the inventory is named
	 */
	@Override
	public boolean hasCustomInventoryName() {
		return this.inventoryName != null && this.inventoryName.length() > 0;
	}

	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
		this.chemicalProcessorItemStacks = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.chemicalProcessorItemStacks.length) {
				this.chemicalProcessorItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		this.chemicalProcessorBurnTime = p_145839_1_.getShort("BurnTime");
		this.chemicalProcessorCookTime = p_145839_1_.getShort("CookTime");
		this.currentItemBurnTime = getItemBurnTime(this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel]);

		if (p_145839_1_.hasKey("CustomName", 8)) {
			this.inventoryName = p_145839_1_.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setShort("BurnTime", (short) this.chemicalProcessorBurnTime);
		p_145841_1_.setShort("CookTime", (short) this.chemicalProcessorCookTime);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.chemicalProcessorItemStacks.length; ++i) {
			if (this.chemicalProcessorItemStacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.chemicalProcessorItemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		p_145841_1_.setTag("Items", nbttaglist);

		if (this.hasCustomInventoryName()) {
			p_145841_1_.setString("CustomName", this.inventoryName);
		}
	}

	/**
	 * Returns the maximum stack size for a inventory slot.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close the current item is to being completely cooked
	 */
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int p_145953_1_) {
		return this.chemicalProcessorCookTime * p_145953_1_ / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel item, where 0 means that the item is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int p_145955_1_) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 200;
		}

		return this.chemicalProcessorBurnTime * p_145955_1_ / this.currentItemBurnTime;
	}

	/**
	 * ChemicalProcessor isBurning
	 */
	public boolean isBurning() {
		return this.chemicalProcessorBurnTime > 0;
	}

	@Override
	public void updateEntity() {
		boolean flag = this.chemicalProcessorBurnTime > 0;
		boolean flag1 = false;

		if (this.chemicalProcessorBurnTime > 0) {
			--this.chemicalProcessorBurnTime;
		}

		if (!this.worldObj.isRemote) {
			if (this.chemicalProcessorBurnTime == 0 && this.canProcess()) {
				this.currentItemBurnTime = this.chemicalProcessorBurnTime = getItemBurnTime(this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel]);

				if (this.chemicalProcessorBurnTime > 0) {
					flag1 = true;

					if (this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel] != null) {
						--this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel].stackSize;

						if (this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel].stackSize == 0) {
							this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel] = chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel].getItem().getContainerItem(
									chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel]);
						}
					}
				}
			}

			if (this.isBurning() && this.canProcess()) {
				++this.chemicalProcessorCookTime;

				if (this.chemicalProcessorCookTime == 200) {
					this.chemicalProcessorCookTime = 0;
					this.processItem();
					flag1 = true;
				}
			} else {
				this.chemicalProcessorCookTime = 0;
			}

			if (flag != this.chemicalProcessorBurnTime > 0) {
				flag1 = true;
				BlockChemicalProcessor.updateChemicalProcessorBlockState(this.chemicalProcessorBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}

		if (flag1) {
			this.markDirty();
		}
	}

	private ItemStack[] getMaterials() {
		int i = 0;
		for (i = 0; i < ChemicalProcessorRecipe.maxMaterials && chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstMaterial + i] != null; i++)
			;
		ItemStack[] materials = new ItemStack[i];
		for (i = 0; i < materials.length; i++)
			materials[i] = chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstMaterial + i];
		return materials;
	}

	/**
	 * Returns true if the chemical processor can process an item, i.e. has a source item, destination stack isn't full, etc.
	 */
	private boolean canProcess() {
		if (this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFuel] == null || this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstMaterial] == null)
			return false;

		final ChemicalProcessorRecipe recipe = ChemicalProcessorRecipe.findRecipe(getMaterials());
		if (recipe == null)
			return false;

		if (recipe.fluidContainersRequired > 0) {
			final ItemStack fluidContainer = chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFluidContainer];
			if (fluidContainer == null || fluidContainer.getItem() != PolycraftMod.itemFluidContainer || recipe.fluidContainersRequired > fluidContainer.stackSize)
				return false;
		}

		for (int i = 0; i < recipe.materials.length; i++) {
			final ItemStack material = chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstMaterial + i];
			if (material == null || material.stackSize < recipe.materials[i].stackSize)
				return false;
		}

		for (int i = 0; i < recipe.results.length; i++) {
			final ItemStack desiredResult = recipe.results[i];
			final ItemStack currentResult = chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstResult + i];
			if (currentResult != null) {
				if (!currentResult.isItemEqual(desiredResult))
					return false;
				int newTotal = currentResult.stackSize + desiredResult.stackSize;
				if (newTotal > getInventoryStackLimit() || newTotal > desiredResult.getMaxStackSize())
					return false;
			}
		}

		return true;
	}

	/**
	 * Turn one item from the chemical processor source stack into the appropriate processed item in the chemical processor result stack
	 */
	public void processItem() {
		if (this.canProcess()) {
			final ChemicalProcessorRecipe recipe = ChemicalProcessorRecipe.findRecipe(getMaterials());

			for (int i = 0; i < recipe.results.length; i++) {
				if (this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstResult + i] == null) {
					this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstResult + i] = recipe.results[i].copy();
				} else {
					this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstResult + i].stackSize += recipe.results[i].stackSize; // Forge BugFix: Results may have multiple items
				}
			}

			for (int i = 0; i < recipe.materials.length; i++) {
				this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstMaterial + i].stackSize -= recipe.materials[i].stackSize;
				if (this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstMaterial + i].stackSize <= 0) {
					this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFirstMaterial + i] = null;
				}
			}

			if (recipe.fluidContainersRequired > 0) {
				this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFluidContainer].stackSize -= recipe.fluidContainersRequired;
				if (this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFluidContainer].stackSize <= 0) {
					this.chemicalProcessorItemStacks[ContainerChemicalProcessor.slotIndexFluidContainer] = null;
				}
			}
		}
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the chemical processor burning, or 0 if the item isn't fuel
	 */
	public static int getItemBurnTime(ItemStack p_145952_0_) {
		if (p_145952_0_ == null) {
			return 0;
		} else {
			Item item = p_145952_0_.getItem();

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

			if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD"))
				return 200;
			if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD"))
				return 200;
			if (item instanceof ItemHoe && ((ItemHoe) item).getToolMaterialName().equals("WOOD"))
				return 200;
			if (item == Items.stick)
				return 100;
			if (item == Items.coal)
				return 1600;
			if (item == Items.lava_bucket)
				return 20000;
			if (item == Item.getItemFromBlock(Blocks.sapling))
				return 100;
			if (item == Items.blaze_rod)
				return 2400;
			return GameRegistry.getFuelValue(p_145952_0_);
		}
	}

	public static boolean isItemFuel(ItemStack p_145954_0_) {
		/**
		 * Returns the number of ticks that the supplied fuel item will keep the chemical processor burning, or 0 if the item isn't fuel
		 */
		return getItemBurnTime(p_145954_0_) > 0;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
		return par1 == 2 ? false : (par1 == 1 ? isItemFuel(par2ItemStack) : true);
	}

	/**
	 * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this block.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int par1) {
		return null;
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3) {
		return this.isItemValidForSlot(par1, par2ItemStack);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3) {
		return par3 != 0 || par1 != 1 || par2ItemStack.getItem() == Items.bucket;
	}
}