package edu.utd.minecraft.mod.polycraft.inventory.treetap;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockOldLog;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Polymer;

public class TileEntityTreeTap extends TileEntity implements ITreeTap
{
	private static final int[][] tappedCoordOffsets = new int[][] { new int[] { 1, 0 }, new int[] { 0, 1 }, new int[] { 0, -1 }, new int[] { -1, 0 } };
	private static Item itemPolymerPelletNaturalRubber = null;
	private ItemStack[] inventoryItemStacks = new ItemStack[5];
	private String customInventoryName;
	private int transferCooldown = -1;
	private int spawnAttemptsNaturalRubber = -1;

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_)
	{
		super.readFromNBT(p_145839_1_);
		NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
		this.inventoryItemStacks = new ItemStack[this.getSizeInventory()];

		if (p_145839_1_.hasKey("CustomName", 8))
		{
			this.customInventoryName = p_145839_1_.getString("CustomName");
		}

		this.transferCooldown = p_145839_1_.getInteger("TransferCooldown");
		this.spawnAttemptsNaturalRubber = p_145839_1_.getInteger("SpawnAttemptsNaturalRubber");

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.inventoryItemStacks.length)
			{
				this.inventoryItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_)
	{
		super.writeToNBT(p_145841_1_);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inventoryItemStacks.length; ++i)
		{
			if (this.inventoryItemStacks[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.inventoryItemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		p_145841_1_.setTag("Items", nbttaglist);
		p_145841_1_.setInteger("TransferCooldown", this.transferCooldown);
		p_145841_1_.setInteger("SpawnAttemptsNaturalRubber", this.spawnAttemptsNaturalRubber);

		if (this.hasCustomInventoryName())
		{
			p_145841_1_.setString("CustomName", this.customInventoryName);
		}
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it hasn't changed and skip it.
	 */
	@Override
	public void markDirty()
	{
		super.markDirty();
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory()
	{
		return this.inventoryItemStacks.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int par1)
	{
		return this.inventoryItemStacks[par1];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		if (this.inventoryItemStacks[par1] != null)
		{
			ItemStack itemstack;

			if (this.inventoryItemStacks[par1].stackSize <= par2)
			{
				itemstack = this.inventoryItemStacks[par1];
				this.inventoryItemStacks[par1] = null;
				return itemstack;
			}
			else
			{
				itemstack = this.inventoryItemStacks[par1].splitStack(par2);

				if (this.inventoryItemStacks[par1].stackSize == 0)
				{
					this.inventoryItemStacks[par1] = null;
				}

				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int par1)
	{
		if (this.inventoryItemStacks[par1] != null)
		{
			ItemStack itemstack = this.inventoryItemStacks[par1];
			this.inventoryItemStacks[par1] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{
		this.inventoryItemStacks[par1] = par2ItemStack;

		if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
		{
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getInventoryName()
	{
		return this.hasCustomInventoryName() ? this.customInventoryName : "container." + PolycraftMod.getRegistryName(PolycraftMod.RegistryNamespace.Inventory, PolycraftMod.blockNameTreeTap);
	}

	/**
	 * Returns if the inventory is named
	 */
	@Override
	public boolean hasCustomInventoryName()
	{
		return this.customInventoryName != null && this.customInventoryName.length() > 0;
	}

	public void setCustomInventoryName(String customInventoryName)
	{
		this.customInventoryName = customInventoryName;
	}

	/**
	 * Returns the maximum stack size for a inventory slot.
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
	{
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
	public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
	{
		return true;
	}

	@Override
	public void updateEntity()
	{
		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			--this.transferCooldown;

			if (!this.isTransferCoolingDown())
			{
				this.setTransferCooldown(0);
				this.func_145887_i();
			}
		}
	}

	public boolean func_145887_i()
	{
		if (this.worldObj != null && !this.worldObj.isRemote)
		{
			if (!this.isTransferCoolingDown() && BlockTreeTap.func_149917_c(this.getBlockMetadata()))
			{
				boolean flag = this.func_145883_k();
				flag = func_145891_a(this) || flag;

				if (flag)
				{
					this.setTransferCooldown(8);
					this.markDirty();
					return true;
				}
			}

			return false;
		}
		else
		{
			return false;
		}
	}

	private boolean func_145883_k()
	{
		IInventory iinventory = this.func_145895_l();

		if (iinventory == null)
		{
			return false;
		}
		else
		{
			for (int i = 0; i < this.getSizeInventory(); ++i)
			{
				if (this.getStackInSlot(i) != null)
				{
					ItemStack itemstack = this.getStackInSlot(i).copy();
					ItemStack itemstack1 = func_145889_a(iinventory, this.decrStackSize(i, 1), Facing.oppositeSide[BlockTreeTap.getDirectionFromMetadata(this.getBlockMetadata())]);

					if (itemstack1 == null || itemstack1.stackSize == 0)
					{
						iinventory.markDirty();
						return true;
					}

					this.setInventorySlotContents(i, itemstack);
				}
			}

			return false;
		}
	}

	public static boolean func_145891_a(TileEntityTreeTap p_145891_0_)
	{
		ItemStack nextTappedItemStack = p_145891_0_.getNextTappedItem();
		if (nextTappedItemStack != null)
			func_145889_a(p_145891_0_, nextTappedItemStack, -1);

		IInventory iinventory = func_145884_b(p_145891_0_);

		if (iinventory != null)
		{
			byte b0 = 0;

			if (iinventory instanceof ISidedInventory && b0 > -1)
			{
				ISidedInventory isidedinventory = (ISidedInventory) iinventory;
				int[] aint = isidedinventory.getAccessibleSlotsFromSide(b0);

				for (int k = 0; k < aint.length; ++k)
				{
					if (func_145892_a(p_145891_0_, iinventory, aint[k], b0))
					{
						return true;
					}
				}
			}
			else
			{
				int i = iinventory.getSizeInventory();

				for (int j = 0; j < i; ++j)
				{
					if (func_145892_a(p_145891_0_, iinventory, j, b0))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	private ItemStack getNextTappedItem() {
		if (spawnAttemptsNaturalRubber++ >= PolycraftMod.treeTapSpawnRateNaturalRubber) {
			spawnAttemptsNaturalRubber = 0;
			for (final int[] tappedCoordOffset : tappedCoordOffsets) {
				final int x = xCoord + tappedCoordOffset[0];
				final int z = zCoord + tappedCoordOffset[1];
				Block treeBlock = getWorldObj().getBlock(x, yCoord, z);
				//metadata == 3 is for index of "jungle" in net.minecraft.block.BlockOldLog.field_150168_M
				if (treeBlock != null && treeBlock instanceof BlockOldLog && getWorldObj().getBlockMetadata(x, yCoord, z) == 3) {
					if (itemPolymerPelletNaturalRubber == null)
						itemPolymerPelletNaturalRubber = PolycraftMod.getItem(PolycraftMod.RegistryNamespace.Polymer, Polymer.registry.get("Natural Rubber").itemNamePellet);
					return new ItemStack(itemPolymerPelletNaturalRubber);
				}
			}
		}
		return null;
	}

	private static boolean func_145892_a(ITreeTap p_145892_0_, IInventory p_145892_1_, int p_145892_2_, int p_145892_3_)
	{
		ItemStack itemstack = p_145892_1_.getStackInSlot(p_145892_2_);

		if (itemstack != null && func_145890_b(p_145892_1_, itemstack, p_145892_2_, p_145892_3_))
		{
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = func_145889_a(p_145892_0_, p_145892_1_.decrStackSize(p_145892_2_, 1), -1);

			if (itemstack2 == null || itemstack2.stackSize == 0)
			{
				p_145892_1_.markDirty();
				return true;
			}

			p_145892_1_.setInventorySlotContents(p_145892_2_, itemstack1);
		}

		return false;
	}

	public static ItemStack func_145889_a(IInventory p_145889_0_, ItemStack p_145889_1_, int p_145889_2_)
	{
		if (p_145889_0_ instanceof ISidedInventory && p_145889_2_ > -1)
		{
			ISidedInventory isidedinventory = (ISidedInventory) p_145889_0_;
			int[] aint = isidedinventory.getAccessibleSlotsFromSide(p_145889_2_);

			for (int l = 0; l < aint.length && p_145889_1_ != null && p_145889_1_.stackSize > 0; ++l)
			{
				p_145889_1_ = func_145899_c(p_145889_0_, p_145889_1_, aint[l], p_145889_2_);
			}
		}
		else
		{
			int j = p_145889_0_.getSizeInventory();

			for (int k = 0; k < j && p_145889_1_ != null && p_145889_1_.stackSize > 0; ++k)
			{
				p_145889_1_ = func_145899_c(p_145889_0_, p_145889_1_, k, p_145889_2_);
			}
		}

		if (p_145889_1_ != null && p_145889_1_.stackSize == 0)
		{
			p_145889_1_ = null;
		}

		return p_145889_1_;
	}

	private static boolean func_145885_a(IInventory p_145885_0_, ItemStack p_145885_1_, int p_145885_2_, int p_145885_3_)
	{
		return !p_145885_0_.isItemValidForSlot(p_145885_2_, p_145885_1_) ? false : !(p_145885_0_ instanceof ISidedInventory) || ((ISidedInventory) p_145885_0_).canInsertItem(p_145885_2_, p_145885_1_, p_145885_3_);
	}

	private static boolean func_145890_b(IInventory p_145890_0_, ItemStack p_145890_1_, int p_145890_2_, int p_145890_3_)
	{
		return !(p_145890_0_ instanceof ISidedInventory) || ((ISidedInventory) p_145890_0_).canExtractItem(p_145890_2_, p_145890_1_, p_145890_3_);
	}

	private static ItemStack func_145899_c(IInventory p_145899_0_, ItemStack p_145899_1_, int p_145899_2_, int p_145899_3_)
	{
		ItemStack itemstack1 = p_145899_0_.getStackInSlot(p_145899_2_);

		if (func_145885_a(p_145899_0_, p_145899_1_, p_145899_2_, p_145899_3_))
		{
			boolean flag = false;

			if (itemstack1 == null)
			{
				//Forge: BUGFIX: Again, make things respect max stack sizes.
				int max = Math.min(p_145899_1_.getMaxStackSize(), p_145899_0_.getInventoryStackLimit());
				if (max >= p_145899_1_.stackSize)
				{
					p_145899_0_.setInventorySlotContents(p_145899_2_, p_145899_1_);
					p_145899_1_ = null;
				}
				else
				{
					p_145899_0_.setInventorySlotContents(p_145899_2_, p_145899_1_.splitStack(max));
				}
				flag = true;
			}
			else if (func_145894_a(itemstack1, p_145899_1_))
			{
				//Forge: BUGFIX: Again, make things respect max stack sizes.
				int max = Math.min(p_145899_1_.getMaxStackSize(), p_145899_0_.getInventoryStackLimit());
				if (max > itemstack1.stackSize)
				{
					int l = Math.min(p_145899_1_.stackSize, max - itemstack1.stackSize);
					p_145899_1_.stackSize -= l;
					itemstack1.stackSize += l;
					flag = l > 0;
				}
			}

			if (flag)
			{
				if (p_145899_0_ instanceof TileEntityTreeTap)
				{
					((TileEntityTreeTap) p_145899_0_).setTransferCooldown(8);
					p_145899_0_.markDirty();
				}

				p_145899_0_.markDirty();
			}
		}

		return p_145899_1_;
	}

	private IInventory func_145895_l()
	{
		int i = BlockTreeTap.getDirectionFromMetadata(this.getBlockMetadata());
		return func_145893_b(this.getWorldObj(), this.xCoord + Facing.offsetsXForSide[i], this.yCoord + Facing.offsetsYForSide[i], this.zCoord + Facing.offsetsZForSide[i]);
	}

	public static IInventory func_145884_b(ITreeTap p_145884_0_)
	{
		return func_145893_b(p_145884_0_.getWorldObj(), p_145884_0_.getXPos(), p_145884_0_.getYPos() + 1.0D, p_145884_0_.getZPos());
	}

	public static IInventory func_145893_b(World p_145893_0_, double p_145893_1_, double p_145893_3_, double p_145893_5_)
	{
		IInventory iinventory = null;
		int i = MathHelper.floor_double(p_145893_1_);
		int j = MathHelper.floor_double(p_145893_3_);
		int k = MathHelper.floor_double(p_145893_5_);
		TileEntity tileentity = p_145893_0_.getTileEntity(i, j, k);

		if (tileentity != null && tileentity instanceof IInventory)
		{
			iinventory = (IInventory) tileentity;

			if (iinventory instanceof TileEntityChest)
			{
				Block block = p_145893_0_.getBlock(i, j, k);

				if (block instanceof BlockChest)
				{
					iinventory = ((BlockChest) block).func_149951_m(p_145893_0_, i, j, k);
				}
			}
		}

		if (iinventory == null)
		{
			List list = p_145893_0_.getEntitiesWithinAABBExcludingEntity((Entity) null, AxisAlignedBB.getAABBPool().getAABB(p_145893_1_, p_145893_3_, p_145893_5_, p_145893_1_ + 1.0D, p_145893_3_ + 1.0D, p_145893_5_ + 1.0D),
					IEntitySelector.selectInventories);

			if (list != null && list.size() > 0)
			{
				iinventory = (IInventory) list.get(p_145893_0_.rand.nextInt(list.size()));
			}
		}

		return iinventory;
	}

	private static boolean func_145894_a(ItemStack p_145894_0_, ItemStack p_145894_1_)
	{
		return p_145894_0_.getItem() != p_145894_1_.getItem() ? false : (p_145894_0_.getItemDamage() != p_145894_1_.getItemDamage() ? false : (p_145894_0_.stackSize > p_145894_0_.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(
				p_145894_0_, p_145894_1_)));
	}

	/**
	 * Gets the world X position for this tree tap entity.
	 */
	@Override
	public double getXPos()
	{
		return this.xCoord;
	}

	/**
	 * Gets the world Y position for this tree tap entity.
	 */
	@Override
	public double getYPos()
	{
		return this.yCoord;
	}

	/**
	 * Gets the world Z position for this tree tap entity.
	 */
	@Override
	public double getZPos()
	{
		return this.zCoord;
	}

	public void setTransferCooldown(int transferCooldown)
	{
		this.transferCooldown = transferCooldown;
	}

	public boolean isTransferCoolingDown()
	{
		return this.transferCooldown > 0;
	}
}