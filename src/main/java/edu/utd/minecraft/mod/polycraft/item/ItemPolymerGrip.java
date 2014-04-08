package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemPolymerGrip extends Item implements PolycraftItem {

	public ItemPolymerGrip() {
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setTextureName(PolycraftMod.getAssetName("polymer_grip"));
	}

	@Override
	public ItemCategory getCategory() {
		// TODO: Category?
		return ItemCategory.ITEMS;
	}
}