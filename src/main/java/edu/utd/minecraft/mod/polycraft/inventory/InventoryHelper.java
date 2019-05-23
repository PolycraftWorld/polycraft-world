package edu.utd.minecraft.mod.polycraft.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import edu.utd.minecraft.mod.polycraft.inventory.heated.HeatedInventory;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory.FlowNetwork.ExplicitTerminal;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory.FlowNetwork.FuelTerminal;
import edu.utd.minecraft.mod.polycraft.inventory.pump.PumpInventory.FlowNetwork.InputTerminal;
import edu.utd.minecraft.mod.polycraft.inventory.solararray.SolarArrayInventory;
import edu.utd.minecraft.mod.polycraft.item.ItemVessel;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItemHelper;
import net.minecraft.util.EnumFacing;

public class InventoryHelper {
	
	/**
	 * Takes in a Chest (only a regular chest for now) and removes ALL of its items
	 * The removed items are handled by GarbageCollect, for now.
	 * @param entity the chest to empty
	 * @return the chest object with NO items inside.
	 */
	public static TileEntity clearChestContents(TileEntity entity) {
		
		if(entity instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest) entity;
			for (int i = 0; i < chest.getSizeInventory(); i++) {
				if(chest.getStackInSlot(i) == null)
					continue;
				chest.decrStackSize(i, chest.getStackInSlot(i).stackSize);
			}
			
			return chest;
		}
		
		
		return entity;
	}

	public static boolean transfer(IInventory target, IInventory source, int sourceSlotIndex, EnumFacing facing, int size) {
		ItemStack itemstack = source.getStackInSlot(sourceSlotIndex);

		if (itemstack != null)
		{
			//need to return here if this is not an output slot if this is a solar array
			if (source instanceof SolarArrayInventory)
				if (!((SolarArrayInventory) source).isOutputSlot(sourceSlotIndex)) //this should only pull from output slots (may need to extend to other inventories)
					return false;

			//if (target instanceof ContactPrinterInventory)
			//if (!((ContactPrinterInventory) target).itemInCorrectSlot(itemstack, sourceSlotIndex)) //this should only push to specific input slots slots (may need to extend to other inventories)
			//return false;

			if ((!(source instanceof ISidedInventory) || ((ISidedInventory) source).canExtractItem(sourceSlotIndex, itemstack, facing))) {
				ItemStack itemstack1 = itemstack.copy();
				ItemStack itemstack2 = transferItemToNextValidSlot(target, source.decrStackSize(sourceSlotIndex, size), EnumFacing.DOWN);

				if (itemstack2 == null || itemstack2.stackSize == 0)
				{
					source.markDirty();
					return true;
				}

				source.setInventorySlotContents(sourceSlotIndex, itemstack1);
			}
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
		ItemStack regulatedItemStack = target.itemStackInFlowRegulator;

		if (sourceItemStack == null)
			return false;

		if ((regulatedItemStack != null) && (sourceItemStack.getItem() == regulatedItemStack.getItem()))
		{
			if (sourceItemStack.stackSize < regulatedItemStack.stackSize)
			{
				return false;
			}
			else if (regulatedItemStack.stackSize > 1)
			{
				if (targetItemStack != null)
				{
					return false;
				}
				else //limit the flow to the stack number
				{
					// TODO: Not sure if we need to copy damage values here but just in case I'll put it in (PM-55)
					targetItemStack = new ItemStack(sourceItemStack.getItem(), regulatedItemStack.stackSize, sourceItemStack.getItemDamage());

					if (sourceItemStack.hasTagCompound())
					{
						PolycraftItemHelper.createTagCompound(targetItemStack);
						targetItemStack.setTagCompound(sourceItemStack.getTagCompound());
					}
					sourceItemStack.stackSize = sourceItemStack.stackSize - targetItemStack.stackSize;
					target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
					source.setInventorySlotContents(sourceSlotIndex, sourceItemStack);
				}
			}
			else //flow as many as possible even though it is regulated
			{
				if (targetItemStack == null)
				{
					// TODO: This is just a marker for the item repair glitch (PM-55)
					targetItemStack = new ItemStack(sourceItemStack.getItem(), sourceItemStack.stackSize, sourceItemStack.getItemDamage());

					if (sourceItemStack.hasTagCompound())
					{
						PolycraftItemHelper.createTagCompound(targetItemStack);
						targetItemStack.setTagCompound(sourceItemStack.getTagCompound());
					}
					sourceItemStack = null;
					target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
					source.setInventorySlotContents(sourceSlotIndex, sourceItemStack);

				}
				else
				{
					if (targetItemStack.getItem() != sourceItemStack.getItem())
						return false;

					else if (sourceItemStack.stackSize + targetItemStack.stackSize > targetItemStack.getMaxStackSize())
					{
						// TODO: This is just a marker for the item dupe glitch (PM-55)
						sourceItemStack.stackSize = sourceItemStack.stackSize + targetItemStack.stackSize - targetItemStack.getMaxStackSize();
						targetItemStack.stackSize = targetItemStack.getMaxStackSize();
						target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
						source.setInventorySlotContents(sourceSlotIndex, sourceItemStack);
					}
					else
					{
						targetItemStack.stackSize += sourceItemStack.stackSize;
						sourceItemStack = null;
						target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
						source.setInventorySlotContents(sourceSlotIndex, sourceItemStack);
					}
				}

			}

		}
		else if ((regulatedItemStack != null) && (sourceItemStack.getItem() != regulatedItemStack.getItem()))
		{
			if (!(sourceItemStack.getItem() instanceof ItemVessel))
				return false;
			if (!(regulatedItemStack.getItem() instanceof ItemVessel))
				return false;

			ItemVessel sourceVessel = (ItemVessel) sourceItemStack.getItem();
			ItemVessel regulatedVessel = (ItemVessel) regulatedItemStack.getItem();

			if (sourceVessel.config.vesselType.isLarger(regulatedVessel.config.vesselType))
			{

				if (regulatedItemStack.stackSize > 1)
				{
					if (targetItemStack != null)
					{
						return false;
					}

					targetItemStack = new ItemStack(regulatedVessel, regulatedItemStack.stackSize);
					if (sourceItemStack.hasTagCompound())
					{
						PolycraftItemHelper.createTagCompound(targetItemStack);
						targetItemStack.setTagCompound(sourceItemStack.getTagCompound());
					}

					sourceItemStack.stackSize--;

					if (sourceItemStack.stackSize == 0)
						source.setInventorySlotContents(sourceSlotIndex, null);

					//deal with the waste
					ItemStack splitStack = new ItemStack(regulatedVessel, targetItemStack.getMaxStackSize() - targetItemStack.stackSize);
					if (sourceItemStack.hasTagCompound())
					{
						PolycraftItemHelper.createTagCompound(splitStack);
						splitStack.setTagCompound(sourceItemStack.getTagCompound());
					}
					transferItemToNextValidSlot(source, splitStack, EnumFacing.DOWN); //transfers the remainder into the next open slot in the source

					target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
				}
				else //regulatedItemStack.stackSize == 1 so 
				{

					if (targetItemStack != null)
					{
						if (targetItemStack.stackSize == targetItemStack.getMaxStackSize())
							return false;
						sourceItemStack.stackSize--;
						if (sourceItemStack.stackSize == 0)
							source.setInventorySlotContents(sourceSlotIndex, null);

						//deal with the waste
						ItemStack splitStack = new ItemStack(regulatedVessel, targetItemStack.getMaxStackSize() - targetItemStack.stackSize);
						if (sourceItemStack.hasTagCompound())
						{
							PolycraftItemHelper.createTagCompound(splitStack);
							splitStack.setTagCompound(sourceItemStack.getTagCompound());
						}
						transferItemToNextValidSlot(source, splitStack, EnumFacing.DOWN); //transfers the remainder into the next open slot in the source
						targetItemStack.stackSize = targetItemStack.getMaxStackSize();
						target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
					}
					else //target was null so fill it up
					{
						sourceItemStack.stackSize--;
						if (sourceItemStack.stackSize == 0)
							source.setInventorySlotContents(sourceSlotIndex, null);
						targetItemStack = new ItemStack(regulatedVessel, sourceItemStack.getMaxStackSize());
						if (sourceItemStack.hasTagCompound())
						{
							PolycraftItemHelper.createTagCompound(targetItemStack);
							targetItemStack.setTagCompound(sourceItemStack.getTagCompound());
						}
						target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
					}

				}

			}
		}

		else //not flow regulated
		{
			if (targetItemStack == null)
			{
				// TODO: This is just a marker for the item repair glitch (PM-55)
				targetItemStack = new ItemStack(sourceItemStack.getItem(), sourceItemStack.stackSize, sourceItemStack.getItemDamage());

				if (sourceItemStack.hasTagCompound())
				{
					PolycraftItemHelper.createTagCompound(targetItemStack);
					targetItemStack.setTagCompound(sourceItemStack.getTagCompound());
				}
				sourceItemStack = null;
				target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
				source.setInventorySlotContents(sourceSlotIndex, sourceItemStack);

			}
			else
			{
				if (targetItemStack.getItem() != sourceItemStack.getItem())
					return false;

				else if (sourceItemStack.stackSize + targetItemStack.stackSize > targetItemStack.getMaxStackSize())
				{
					// TODO: This is just a marker for the item dupe glitch (PM-55)
					sourceItemStack.stackSize = sourceItemStack.stackSize + targetItemStack.stackSize - targetItemStack.getMaxStackSize();
					targetItemStack.stackSize = targetItemStack.getMaxStackSize();
					target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
					source.setInventorySlotContents(sourceSlotIndex, sourceItemStack);
				}
				else
				{
					targetItemStack.stackSize += sourceItemStack.stackSize;
					sourceItemStack = null;
					target.inventory.setInventorySlotContents(targetSlotIndex, targetItemStack);
					source.setInventorySlotContents(sourceSlotIndex, sourceItemStack);
				}
			}

		}

		source.markDirty();
		target.inventory.markDirty();
		return true;

	}

	private static ItemStack transferItemToNextValidSlot(IInventory target, ItemStack itemStack, EnumFacing facing) {
		if (target instanceof ISidedInventory && facing.getIndex() > -1) {
			ISidedInventory isidedinventory = (ISidedInventory) target;
			int[] aint = isidedinventory.getSlotsForFace(facing);

			for (int l = 0; l < aint.length && itemStack != null && itemStack.stackSize > 0; ++l) {
				itemStack = func_145899_c(target, itemStack, aint[l], facing.getIndex());
			}
		}
		else {
			int j = target.getSizeInventory();

			for (int k = 0; k < j && itemStack != null && itemStack.stackSize > 0; ++k) {
				itemStack = func_145899_c(target, itemStack, k, facing.getIndex());
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
		return !p_145885_0_.isItemValidForSlot(p_145885_2_, p_145885_1_) ? false : !(p_145885_0_ instanceof ISidedInventory) || ((ISidedInventory) p_145885_0_).canInsertItem(p_145885_2_, p_145885_1_, EnumFacing.getFront(p_145885_3_));
	}

	private static boolean func_145894_a(ItemStack p_145894_0_, ItemStack p_145894_1_) {
		return p_145894_0_.getItem() != p_145894_1_.getItem() ? false : (p_145894_0_.getItemDamage() != p_145894_1_.getItemDamage() ? false : (p_145894_0_.stackSize > p_145894_0_.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(
				p_145894_0_, p_145894_1_)));
	}
}
