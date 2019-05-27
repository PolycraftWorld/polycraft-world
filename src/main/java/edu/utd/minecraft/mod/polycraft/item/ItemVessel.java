package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.ElementVessel;
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.SourcedVesselConfig;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public class ItemVessel<C extends SourcedVesselConfig> extends Item implements PolycraftItem {
	public final C config;
	private boolean searchForLargerItem = true;
	private ItemVessel largerItem = null;

	public ItemVessel(final C config) {
		Preconditions.checkNotNull(config);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(Vessel.class.getSimpleName() + "_" + config.vesselType.toString())));
		this.config = config;
	}
	
//	@Override
//	public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack)
//    {
//		if ((par1ItemStack != null) && (par1ItemStack.stackTagCompound == null))
//		{
//		PolycraftItemHelper.createTagCompound(par1ItemStack);
//		par1ItemStack.stackTagCompound.setByte("polycraft-recipe", (byte) 1);
//		}
//        return true;
//    }
//
//	@Override
//	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);
//	}
	

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_VESSEL;
	}

	public static void upcycle(final PolycraftInventory inventory) {
		upcycleOrMerge(inventory, true);
	}

	public static void merge(final PolycraftInventory inventory) {
		upcycleOrMerge(inventory, false);
	}

	private static void upcycleOrMerge(final PolycraftInventory inventory, final boolean upcycle) {
		for (final ContainerSlot slot : inventory.getInputSlots()) {
			final int slotIndex = slot.getSlotIndex();
			final ItemStack itemStack = inventory.getStackInSlot(slotIndex);
			if (itemStack != null && itemStack.getItem() instanceof ItemVessel) {
				if (upcycle)
					((ItemVessel) itemStack.getItem()).upcycle(inventory, slotIndex, itemStack);
				else
				{
					if (itemStack.stackSize < 64)
					((ItemVessel) itemStack.getItem()).merge(inventory, slotIndex, itemStack);
				}
			}
		}
	}

	private synchronized void upcycle(final PolycraftInventory inventory, final int slotIndex, final ItemStack itemStack) {
		if (searchForLargerItem) {
			if (config.vesselType.largerType != null) {
				GameIdentifiedConfig largerConfig = null;
				if (config instanceof ElementVessel)
					largerConfig = ElementVessel.registry.find(((ElementVessel) config).source, config.vesselType.largerType);
				if (config instanceof CompoundVessel)
					largerConfig = CompoundVessel.registry.find(((CompoundVessel) config).source, config.vesselType.largerType);
				else if (config instanceof PolymerPellets)
					largerConfig = PolymerPellets.registry.find(((PolymerPellets) config).source, config.vesselType.largerType);
				if (largerConfig != null)
					largerItem = (ItemVessel) PolycraftRegistry.getItem(largerConfig);
			}
			searchForLargerItem = false;
		}

		if (largerItem != null) {
			if (itemStack.stackSize == config.vesselType.largerType.quantityOfSmallerType) {
					
				final ItemStack largerItemStack = new ItemStack(largerItem);
				PolycraftItemHelper.createTagCompound(largerItemStack);
				largerItemStack.getTagCompound().setByte("polycraft-recipe", (byte) 1);
				inventory.setInventorySlotContents(slotIndex, largerItemStack);
			}
		}
	}

	private synchronized void merge(final PolycraftInventory inventory, final int slotIndex, final ItemStack itemStack) {
		if (itemStack.stackSize < 64) {
			//search for a stack with our type that isn't maxed out and try to combine with it
			for (final ContainerSlot combineSlot : inventory.getInputSlots()) {
				if (combineSlot.getSlotIndex() != slotIndex) {
					final ItemStack combineWithItemStack = inventory.getStackInSlot(combineSlot.getSlotIndex());
					if (combineWithItemStack != null && combineWithItemStack.isItemEqual(itemStack)) {
						if (combineWithItemStack.stackSize < 64) {
							final ItemStack sourceStack = combineSlot.getSlotIndex() > slotIndex ? combineWithItemStack : itemStack;
							final ItemStack targetStack = combineSlot.getSlotIndex() > slotIndex ? itemStack : combineWithItemStack;
							final int amountToTransfer = Math.min(sourceStack.stackSize, 64 - targetStack.stackSize);
							targetStack.stackSize += amountToTransfer;
							sourceStack.stackSize -= amountToTransfer;
//		TODO: fix					if (targetStack.stackSize >= 64)
//							{
//								inventory.setInventorySlotContents(combineSlot.getSlotIndex(), targetStack);
//								inventory.setInventorySlotContents(slotIndex, sourceStack);
//								return;
//							}
								
							if (sourceStack.stackSize == 0) {
								inventory.setInventorySlotContents(combineSlot.getSlotIndex() > slotIndex ? combineSlot.getSlotIndex() : slotIndex, null);
								return;
							}
						}
					}
				}
			}
		}
	}
}
