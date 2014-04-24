package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Mold;

public class ItemMold extends Item implements PolycraftItem {
	public final Mold mold;

	public ItemMold(final Mold mold) {
		Preconditions.checkNotNull(mold);
		this.setMaxDamage(mold.maxDamage);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(Mold.class.getSimpleName())));
		this.mold = mold;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_MOLD;
	}
}
