package edu.utd.minecraft.mod.polycraft.inventory.treetap;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCraftingContainerGeneric;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselMerger;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.VesselUpcycler;

public class TreeTapInventory extends PolycraftInventory {

	public static List<GuiContainerSlot> guiSlots = Lists.newArrayList();
	static {
		for (int i = 0; i < 5; i++)
			guiSlots.add(GuiContainerSlot.createInput(i, i, 0, 44, 2));
	}

	private static Inventory config;

	public static final void register(final Inventory config) {
		TreeTapInventory.config = config;
		config.containerType = PolycraftContainerType.TREE_TAP;
		PolycraftInventory.register(new TreeTapBlock(config, TreeTapInventory.class));
	}

	private static final int[][] tappedCoordOffsets = new int[][] { new int[] { 1, 0 }, new int[] { 0, 1 }, new int[] { 0, -1 }, new int[] { -1, 0 } };

	private int transferCooldown = -1;
	private int spawnAttempts = -1;
	private final PolymerPellets polymerPelletsToSpawn;
	private final int amountToSpawn;
	private final int defaultSpawnFrequencyTicks;
	private final int jungleSpawnFrequencyTicks;

	public TreeTapInventory() {
		super(PolycraftContainerType.TREE_TAP, config);
		this.polymerPelletsToSpawn = PolymerPellets.registry.get(config.params.get(0));
		this.amountToSpawn = config.params.getInt(1);
		this.defaultSpawnFrequencyTicks = PolycraftMod.convertSecondsToGameTicks(config.params.getInt(2));
		this.jungleSpawnFrequencyTicks = PolycraftMod.convertSecondsToGameTicks(config.params.getInt(3));
		this.addBehavior(new VesselUpcycler());
		this.addBehavior(new VesselMerger());
	}

	@Override
	public PolycraftCraftingContainer getCraftingContainer(final InventoryPlayer playerInventory) {
		return new PolycraftCraftingContainerGeneric(this, playerInventory, 51);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		// return new PolycraftInventoryGui(this, playerInventory, 133, false);
		return new TreeTapGui(this, playerInventory);
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
		super.readFromNBT(p_145839_1_);
		this.transferCooldown = p_145839_1_.getInteger("TransferCooldown");
		this.spawnAttempts = p_145839_1_.getInteger("SpawnAttempts");
	}

	@Override
	public void writeToNBT(NBTTagCompound p_145841_1_) {
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setInteger("TransferCooldown", this.transferCooldown);
		p_145841_1_.setInteger("SpawnAttempts", this.spawnAttempts);
	}

	@Override
	public void update() {
		super.update();

		if (this.worldObj != null && !this.worldObj.isRemote) {
			--this.transferCooldown;

			if (!this.isTransferCoolingDown()) {
				this.setTransferCooldown(0);
				this.func_145887_i();
			}
		}
	}

	private ItemStack getNextTappedItem() {
		for (final int[] tappedCoordOffset : tappedCoordOffsets) {
			final int x = tappedCoordOffset[0];
			final int z = tappedCoordOffset[1];
			final Block treeBlock = getWorld().getBlockState(pos.add(x, 0, z)).getBlock();
			//metadata == 3 is for index of "jungle" in net.minecraft.block.BlockOldLog.field_150168_M
			if (treeBlock != null && ((treeBlock instanceof BlockOldLog) || (treeBlock instanceof BlockNewLog))) {
				// Changed "metadata == 3" to "(metadata & 3) == 3" to account for log rotations. - Chris
				if (spawnAttempts++ >= ((treeBlock.getMetaFromState(getWorld().getBlockState(pos.add(x, 0, z))) & 3) == 3 ? jungleSpawnFrequencyTicks : defaultSpawnFrequencyTicks)) {
					spawnAttempts = 0;
					return polymerPelletsToSpawn.getItemStack(amountToSpawn);
				}
				return null;
			}
		}
		spawnAttempts = 0;
		return null;
	}

	public boolean func_145887_i() {
		if (this.worldObj != null && !this.worldObj.isRemote) {
			if (!this.isTransferCoolingDown() && func_149917_c(this.getBlockMetadata())) {
				boolean flag = this.func_145883_k();
				flag = func_145891_a(this) || flag;

				if (flag) {
					this.setTransferCooldown(8);
					this.markDirty();
					return true;
				}
			}
			return false;
		}
		return false;
	}

	public static boolean func_145891_a(TreeTapInventory p_145891_0_) {
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
				int[] aint = isidedinventory.getSlotsForFace(EnumFacing.getFront(b0));

				if (aint != null) {
					for (int k = 0; k < aint.length; ++k)
					{
						if (func_145892_a(p_145891_0_, iinventory, aint[k], b0))
						{
							return true;
						}
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

	private static boolean func_145892_a(TreeTapInventory p_145892_0_, IInventory p_145892_1_, int p_145892_2_, int p_145892_3_) {
		ItemStack itemstack = p_145892_1_.getStackInSlot(p_145892_2_);

		if (itemstack != null && func_145890_b(p_145892_1_, itemstack, p_145892_2_, p_145892_3_)) {
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

	private static boolean func_145890_b(IInventory p_145890_0_, ItemStack p_145890_1_, int p_145890_2_, int p_145890_3_) {
		return !(p_145890_0_ instanceof ISidedInventory) || ((ISidedInventory) p_145890_0_).canExtractItem(p_145890_2_, p_145890_1_, EnumFacing.getFront(p_145890_3_));
	}

	public static IInventory func_145884_b(TreeTapInventory p_145884_0_) {
		if(getClickedOnInventory(p_145884_0_.getWorld(), p_145884_0_.pos.up())!=null)
			return getClickedOnInventory(p_145884_0_.getWorld(), p_145884_0_.pos.up());
		else
			return getClickedOnInventory(p_145884_0_.getWorld(), p_145884_0_.pos);
	}

	private boolean func_145883_k() {
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
					ItemStack itemstack1 = func_145889_a(iinventory, this.decrStackSize(i, 1), EnumFacing.getFront(getDirectionFromMetadata(this.getBlockMetadata())).getOpposite().getIndex());

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

	public static ItemStack func_145889_a(IInventory p_145889_0_, ItemStack p_145889_1_, int p_145889_2_) {
		if (p_145889_0_ instanceof ISidedInventory && p_145889_2_ > -1) {
			ISidedInventory isidedinventory = (ISidedInventory) p_145889_0_;
			int[] aint = isidedinventory.getSlotsForFace(EnumFacing.getFront(p_145889_2_));

			for (int l = 0; l < aint.length && p_145889_1_ != null && p_145889_1_.stackSize > 0; ++l) {
				p_145889_1_ = func_145899_c(p_145889_0_, p_145889_1_, aint[l], p_145889_2_);
			}
		}
		else {
			int j = p_145889_0_.getSizeInventory();

			for (int k = 0; k < j && p_145889_1_ != null && p_145889_1_.stackSize > 0; ++k) {
				p_145889_1_ = func_145899_c(p_145889_0_, p_145889_1_, k, p_145889_2_);
			}
		}

		if (p_145889_1_ != null && p_145889_1_.stackSize == 0) {
			p_145889_1_ = null;
		}

		return p_145889_1_;
	}

	private static ItemStack func_145899_c(IInventory p_145899_0_, ItemStack p_145899_1_, int p_145899_2_, int p_145899_3_) {
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
				if (p_145899_0_ instanceof TreeTapInventory)
				{
					((TreeTapInventory) p_145899_0_).setTransferCooldown(8);
					p_145899_0_.markDirty();
				}

				p_145899_0_.markDirty();
			}
		}

		return p_145899_1_;
	}

	private static boolean func_145894_a(ItemStack p_145894_0_, ItemStack p_145894_1_) {
		return p_145894_0_.getItem() != p_145894_1_.getItem() ? false : (p_145894_0_.getItemDamage() != p_145894_1_.getItemDamage() ? false : (p_145894_0_.stackSize > p_145894_0_.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(
				p_145894_0_, p_145894_1_)));
	}

	private static boolean func_145885_a(IInventory p_145885_0_, ItemStack p_145885_1_, int p_145885_2_, int p_145885_3_) {
		return !p_145885_0_.isItemValidForSlot(p_145885_2_, p_145885_1_) ? false : !(p_145885_0_ instanceof ISidedInventory) || ((ISidedInventory) p_145885_0_).canInsertItem(p_145885_2_, p_145885_1_, EnumFacing.getFront(p_145885_3_));
	}

	private IInventory func_145895_l() {
		int i = getDirectionFromMetadata(this.getBlockMetadata());
		return getClickedOnInventory(this.getWorld(), this.pos.offset(EnumFacing.getFront(i)));
	}

	public static IInventory getClickedOnInventory(World worldObj, BlockPos blockPos) {
		IInventory iinventory = null;
		TileEntity tileentity = worldObj.getTileEntity(blockPos);

		if (tileentity != null && tileentity instanceof IInventory)
		{
			iinventory = (IInventory) tileentity;

			if (iinventory instanceof TileEntityChest)
			{
				Block block = worldObj.getBlockState(blockPos).getBlock();

				if (block instanceof BlockChest)
				{
					iinventory = ((BlockChest) block).getLockableContainer(worldObj, blockPos);
				}
			}
		}
		
		if (iinventory == null)
		{
			List list = null; //worldObj.getEntitiesWithinAABB(null, AxisAlignedBB.fromBounds(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1), EntitySelectors.selectInventories);
			
			if (list != null && list.size() > 0)
			{
				iinventory = (IInventory) list.get(worldObj.rand.nextInt(list.size()));
			}
		}

		return iinventory;
	}

	static int getDirectionFromMetadata(int p_149918_0_) {
		return p_149918_0_ & 7;
	}

	public void setTransferCooldown(int transferCooldown) {
		this.transferCooldown = transferCooldown;
	}

	public boolean isTransferCoolingDown() {
		return this.transferCooldown > 0;
	}

	private static boolean func_149917_c(int p_149917_0_) {
		return (p_149917_0_ & 8) != 8;
	}
}
