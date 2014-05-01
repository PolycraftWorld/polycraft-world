package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemFibers extends Item implements PolycraftItem {

	public ItemFibers() {
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName("fibers"));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_FIBER;
	}
}
