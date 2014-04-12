package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Catalyst;

public class ItemCatalyst extends Item implements PolycraftItem {
	public final Catalyst catalyst;

	public ItemCatalyst(final Catalyst catalyst) {
		Preconditions.checkNotNull(catalyst);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName("catalyst"));
		this.catalyst = catalyst;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_CATALYST;
	}
}
