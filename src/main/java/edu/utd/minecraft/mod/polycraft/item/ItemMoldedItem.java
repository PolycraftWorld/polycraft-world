package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemMoldedItem extends Item implements PolycraftItem {
	public final MoldedItem moldedItem;

	public ItemMoldedItem(final MoldedItem moldedItem) {
		Preconditions.checkNotNull(moldedItem);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(MoldedItem.class.getSimpleName() + "_" + moldedItem.name)));
		this.moldedItem = moldedItem;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_MOLD;
	}
}
