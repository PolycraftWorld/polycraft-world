package edu.utd.minecraft.mod.polycraft.inventory.behaviors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryBehavior;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public class AutomaticInputBehavior<I extends PolycraftInventory & ISidedInventory> extends InventoryBehavior<I> {

	private final boolean directional;
	private final int cooldownTicksStart;
	private int cooldownTicksCurrent = 0;

	public AutomaticInputBehavior(final boolean directional, final int cooldownTicks) {
		this.directional = directional;
		this.cooldownTicksStart = cooldownTicks;
	}

	@Override
	public boolean updateEntity(final I inventory, final World world) {
		if (inventory.getWorldObj() != null && !inventory.getWorldObj().isRemote) {
			if (cooldownTicksCurrent == 0) {
				cooldownTicksCurrent = cooldownTicksStart;
				attemptAutomaticInput(inventory);
			}
			else
				cooldownTicksCurrent--;
		}
		return false;
	}

	private void attemptAutomaticInput(final I inventory) {
		IInventory inputInventory = null;
		final int directionFacing = inventory.getBlockMetadata() & 7;
		for (final ForgeDirection direction : ForgeDirection.values()) {
			if (direction != ForgeDirection.UNKNOWN) {
				if (directional && (directionFacing == direction.ordinal() || direction == ForgeDirection.DOWN))
					continue;
				inputInventory = getInventoryAt(inventory.getWorldObj(),
						inventory.xCoord + direction.offsetX,
						inventory.yCoord + direction.offsetY,
						inventory.zCoord + direction.offsetZ);
				if (inputInventory != null) {
					if (inputInventory instanceof ISidedInventory) {
						ISidedInventory isidedinventory = (ISidedInventory) inputInventory;
						int[] aint = isidedinventory.getAccessibleSlotsFromSide(0);
						if (aint != null)
							for (int k = 0; k < aint.length; ++k)
								if (transfer(inventory, isidedinventory, aint[k], 0))
									return;
					}
					else
					{
						int i = inputInventory.getSizeInventory();
						for (int j = 0; j < i; ++j)
							if (transfer(inventory, inputInventory, j, 0))
								return;
					}
				}
			}
		}
	}

	private IInventory getInventoryAt(final World world, final double x, final double y, final double z) {
		IInventory iinventory = null;
		final int i = MathHelper.floor_double(x);
		final int j = MathHelper.floor_double(y);
		final int k = MathHelper.floor_double(z);
		final TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity != null && tileentity instanceof IInventory) {
			iinventory = (IInventory) tileentity;
			if (iinventory instanceof TileEntityChest) {
				final Block block = world.getBlock(i, j, k);
				if (block instanceof BlockChest)
					iinventory = ((BlockChest) block).func_149951_m(world, i, j, k);
			}
		}
		return iinventory;
	}

	private boolean transfer(I target, IInventory source, int sourceSlotIndex, int side) {
		ItemStack itemstack = source.getStackInSlot(sourceSlotIndex);

		if (itemstack != null && (!(source instanceof ISidedInventory) || ((ISidedInventory) source).canExtractItem(sourceSlotIndex, itemstack, side))) {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = func_145889_a(target, source.decrStackSize(sourceSlotIndex, 1), -1);

			if (itemstack2 == null || itemstack2.stackSize == 0)
			{
				source.markDirty();
				return true;
			}

			source.setInventorySlotContents(sourceSlotIndex, itemstack1);
		}

		return false;
	}

	public static ItemStack func_145889_a(IInventory target, ItemStack p_145889_1_, int p_145889_2_) {
		if (target instanceof ISidedInventory && p_145889_2_ > -1) {
			ISidedInventory isidedinventory = (ISidedInventory) target;
			int[] aint = isidedinventory.getAccessibleSlotsFromSide(p_145889_2_);

			for (int l = 0; l < aint.length && p_145889_1_ != null && p_145889_1_.stackSize > 0; ++l) {
				p_145889_1_ = func_145899_c(target, p_145889_1_, aint[l], p_145889_2_);
			}
		}
		else {
			int j = target.getSizeInventory();

			for (int k = 0; k < j && p_145889_1_ != null && p_145889_1_.stackSize > 0; ++k) {
				p_145889_1_ = func_145899_c(target, p_145889_1_, k, p_145889_2_);
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
				p_145899_0_.markDirty();
			}
		}

		return p_145899_1_;
	}

	private static boolean func_145885_a(IInventory p_145885_0_, ItemStack p_145885_1_, int p_145885_2_, int p_145885_3_) {
		return !p_145885_0_.isItemValidForSlot(p_145885_2_, p_145885_1_) ? false : !(p_145885_0_ instanceof ISidedInventory) || ((ISidedInventory) p_145885_0_).canInsertItem(p_145885_2_, p_145885_1_, p_145885_3_);
	}

	private static boolean func_145894_a(ItemStack p_145894_0_, ItemStack p_145894_1_) {
		return p_145894_0_.getItem() != p_145894_1_.getItem() ? false : (p_145894_0_.getItemDamage() != p_145894_1_.getItemDamage() ? false : (p_145894_0_.stackSize > p_145894_0_.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(
				p_145894_0_, p_145894_1_)));
	}
}
