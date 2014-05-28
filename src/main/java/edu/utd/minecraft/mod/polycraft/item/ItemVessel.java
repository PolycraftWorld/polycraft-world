package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.config.SourcedVesselConfig;
import edu.utd.minecraft.mod.polycraft.config.Vessel;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;

public class ItemVessel<C extends SourcedVesselConfig> extends Item implements PolycraftItem {
	public final C cofig;
	private boolean searchForLargerItem = true;
	private ItemVessel largerItem = null;

	public ItemVessel(final C cofig) {
		Preconditions.checkNotNull(cofig);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(Vessel.class.getSimpleName() + "_" + cofig.vesselType.toString())));
		this.cofig = cofig;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_VESSEL;
	}

	public static void upcycle(final PolycraftInventory inventory) {
		for (final ContainerSlot slot : inventory.getInputSlots())
			upcycle(inventory, slot.getSlotIndex());
	}

	public static void upcycle(final PolycraftInventory inventory, final int slotIndex) {
		final ItemStack itemStack = inventory.getStackInSlot(slotIndex);
		if (itemStack != null && itemStack.getItem() instanceof ItemVessel)
			((ItemVessel) itemStack.getItem()).upcycle(inventory, slotIndex, itemStack);
	}

	public synchronized void upcycle(final PolycraftInventory inventory, final int slotIndex, final ItemStack itemStack) {
		if (searchForLargerItem) {
			if (cofig.vesselType.largerType != null) {
				GameIdentifiedConfig largerConfig = null;
				if (cofig instanceof CompoundVessel)
					largerConfig = CompoundVessel.registry.find(((CompoundVessel) cofig).source, cofig.vesselType.largerType);
				else if (cofig instanceof PolymerPellets)
					largerConfig = PolymerPellets.registry.find(((PolymerPellets) cofig).source, cofig.vesselType.largerType);
				if (largerConfig != null)
					largerItem = (ItemVessel) PolycraftRegistry.getItem(largerConfig);
				else
					throw new Error("Unable to find larger vessel for: " + cofig.source.name);
			}
			searchForLargerItem = false;
		}

		if (largerItem != null)
			if (itemStack.stackSize == cofig.vesselType.largerType.quantityOfSmallerType)
				inventory.setInventorySlotContents(slotIndex, new ItemStack(largerItem));
	}
}
