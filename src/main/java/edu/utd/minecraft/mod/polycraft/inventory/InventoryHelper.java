package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory.FlowNetwork.ExplicitTerminal;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory.FlowNetwork.FuelTerminal;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory.FlowNetwork.InputTerminal;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItemHelper;

public class InventoryHelper {

	public static boolean transfer(IInventory target, IInventory source, int sourceSlotIndex, int side) {
		ItemStack itemstack = source.getStackInSlot(sourceSlotIndex);

		if (itemstack != null && (!(source instanceof ISidedInventory) || ((ISidedInventory) source).canExtractItem(sourceSlotIndex, itemstack, side))) {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = transferItemToNextValidSlot(target, source.decrStackSize(sourceSlotIndex, 1), -1);

			if (itemstack2 == null || itemstack2.stackSize == 0)
			{
				source.markDirty();
				return true;
			}

			source.setInventorySlotContents(sourceSlotIndex, itemstack1);
		}

		return false;
	}

	public static boolean transferExplicit(ExplicitTerminal target, IInventory source, int sourceSlotIndex) {

		int targetSlotIndex = 0;
		if (target instanceof InputTerminal)
		{
			targetSlotIndex = ((PolycraftInventory) target.inventory).getInputSlots().get(((InputTerminal) target).offsetIndex).getSlotIndex();
		}
		else if (target instanceof FuelTerminal)
		{
			if (target.inventory instanceof HeatedInventory)
			{
				targetSlotIndex = ((HeatedInventory) target.inventory).slotIndexHeatSource;
			}
			else
				return false;

		}
		else
		{
			return false;
		}

		ItemStack sourceItemStack = source.getStackInSlot(sourceSlotIndex);
		ItemStack targetItemStack = target.inventory.getStackInSlot(targetSlotIndex);
		ItemStack prototypeStack = target.itemStack;

		if ((sourceItemStack == null) || (targetItemStack != null))
			return false;

		if (sourceItemStack.getItem() instanceof ItemVessel)
		{
			if (!(prototypeStack.getItem() instanceof ItemVessel))
			{
				return false;
			}

			ItemVessel sourceVessel = (ItemVessel) sourceItemStack.getItem();
			ItemVessel prototypeVessel = (ItemVessel) prototypeStack.getItem();

			if (sourceVessel.config.vesselType == prototypeVessel.config.vesselType)
			{
				if (sourceItemStack.stackSize < prototypeStack.stackSize)
				{
					return false;
				}

				ItemStack splitStack = new ItemStack(prototypeVessel, prototypeStack.stackSize);
				PolycraftItemHelper.createTagCompound(splitStack);
				splitStack.stackTagCompound.setByte("polycraft-recipe", (byte) 1);
				target.inventory.setInventorySlotContents(targetSlotIndex, splitStack);

				sourceItemStack.stackSize -= prototypeStack.stackSize;
				if (sourceItemStack.stackSize == 0)
					source.setInventorySlotContents(sourceSlotIndex, null);
				else
					source.setInventorySlotContents(sourceSlotIndex, sourceItemStack);

			}
			else if (!sourceVessel.config.vesselType.isLarger(prototypeVessel.config.vesselType))
				return false;
			else
			{
				sourceItemStack.stackSize--;
				targetItemStack = new ItemStack(prototypeVessel, prototypeStack.stackSize);
				PolycraftItemHelper.createTagCompound(targetItemStack);
				targetItemStack.stackTagCompound.setByte("polycraft-recipe", (byte) 1);

				target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);

				if (sourceItemStack.stackSize == 0)
					source.setInventorySlotContents(sourceSlotIndex, null);

				//deal with the waste
				ItemStack splitStack = new ItemStack(prototypeVessel, targetItemStack.getMaxStackSize() - targetItemStack.stackSize);
				PolycraftItemHelper.createTagCompound(splitStack);
				splitStack.stackTagCompound.setByte("polycraft-recipe", (byte) 1);
				transferItemToNextValidSlot(source, splitStack, 0); //transfers the remainder into the next open slot in the source

			}
		}
		else if (sourceItemStack.stackSize < prototypeStack.stackSize)
		{
			return false;
		}
		else if (sourceItemStack.getItem() != prototypeStack.getItem())
		{
			return false;
		}
		else
		{
			target.inventory.setInventorySlotContents(targetSlotIndex, sourceItemStack);
			target.inventory.setInventorySlotContents(sourceSlotIndex, null);
		}

		source.markDirty();
		target.inventory.markDirty();
		return true;

	}

	private static ItemStack transferItemToNextValidSlot(IInventory target, ItemStack itemStack, int side) {
		if (target instanceof ISidedInventory && side > -1) {
			ISidedInventory isidedinventory = (ISidedInventory) target;
			int[] aint = isidedinventory.getAccessibleSlotsFromSide(side);

			for (int l = 0; l < aint.length && itemStack != null && itemStack.stackSize > 0; ++l) {
				itemStack = func_145899_c(target, itemStack, aint[l], side);
			}
		}
		else {
			int j = target.getSizeInventory();

			for (int k = 0; k < j && itemStack != null && itemStack.stackSize > 0; ++k) {
				itemStack = func_145899_c(target, itemStack, k, side);
			}
		}

		if (itemStack != null && itemStack.stackSize == 0) {
			itemStack = null;
		}

		return itemStack;
	}

	private static ItemStack func_145899_c(IInventory iInventory, ItemStack itemStack, int p_145899_2_, int side) {
		ItemStack itemstack1 = iInventory.getStackInSlot(p_145899_2_);

		if (func_145885_a(iInventory, itemStack, p_145899_2_, side))
		{
			boolean flag = false;

			if (itemstack1 == null)
			{
				//Forge: BUGFIX: Again, make things respect max stack sizes.
				int max = Math.min(itemStack.getMaxStackSize(), iInventory.getInventoryStackLimit());
				if (max >= itemStack.stackSize)
				{
					iInventory.setInventorySlotContents(p_145899_2_, itemStack);
					itemStack = null;
				}
				else
				{
					iInventory.setInventorySlotContents(p_145899_2_, itemStack.splitStack(max));
				}
				flag = true;
			}
			else if (func_145894_a(itemstack1, itemStack))
			{
				//Forge: BUGFIX: Again, make things respect max stack sizes.
				int max = Math.min(itemStack.getMaxStackSize(), iInventory.getInventoryStackLimit());
				if (max > itemstack1.stackSize)
				{
					int l = Math.min(itemStack.stackSize, max - itemstack1.stackSize);
					itemStack.stackSize -= l;
					itemstack1.stackSize += l;
					flag = l > 0;
				}
			}

			if (flag)
			{
				iInventory.markDirty();
			}
		}

		return itemStack;
	}

	private static boolean func_145885_a(IInventory p_145885_0_, ItemStack p_145885_1_, int p_145885_2_, int p_145885_3_) {
		return !p_145885_0_.isItemValidForSlot(p_145885_2_, p_145885_1_) ? false : !(p_145885_0_ instanceof ISidedInventory) || ((ISidedInventory) p_145885_0_).canInsertItem(p_145885_2_, p_145885_1_, p_145885_3_);
	}

	private static boolean func_145894_a(ItemStack p_145894_0_, ItemStack p_145894_1_) {
		return p_145894_0_.getItem() != p_145894_1_.getItem() ? false : (p_145894_0_.getItemDamage() != p_145894_1_.getItemDamage() ? false : (p_145894_0_.stackSize > p_145894_0_.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(
				p_145894_0_, p_145894_1_)));
	}
}
