package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemPellet extends Item implements PolycraftItem {

	public ItemPellet() {
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName("polymer_pellet"));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_PELLET;
	}
}
