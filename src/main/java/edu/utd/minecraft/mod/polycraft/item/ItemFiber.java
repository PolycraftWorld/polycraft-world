package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemFiber extends Item implements PolycraftItem {

	public ItemFiber() {
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName("polymer_fiber"));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_FIBER;
	}
}
