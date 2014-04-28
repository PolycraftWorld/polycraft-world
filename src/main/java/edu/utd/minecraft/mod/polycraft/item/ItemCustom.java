package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemCustom extends Item implements PolycraftItem {
	public final CustomObject config;

	public ItemCustom(final CustomObject config) {
		Preconditions.checkNotNull(config);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		//TODO read from config this.setTextureName(PolycraftMod.getAssetName("custom"));
		this.config = config;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_CUSTOM;
	}
}
