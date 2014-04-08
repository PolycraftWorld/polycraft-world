package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemFluidContainerNozzle extends Item implements PolycraftItem {

	public ItemFluidContainerNozzle() {
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName("fluid_container_nozzle"));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.COMPONENTS;
	}
}