package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Ingot;

public class ItemIngot extends Item implements PolycraftItem {

	public ItemIngot(final Ingot ingot) {
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(Ingot.class.getSimpleName() + "_" + ingot.type.getClass().getSimpleName())));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_INGOT;
	}
}
