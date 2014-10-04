package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventoryHelper {
	
	public static boolean transfer(IInventory target, IInventory source, int sourceSlotIndex, int side) {
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

	private static ItemStack func_145889_a(IInventory target, ItemStack p_145889_1_, int p_145889_2_) {
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
