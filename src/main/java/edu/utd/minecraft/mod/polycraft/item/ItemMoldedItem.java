package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemMoldedItem extends Item implements PolycraftItem, PolycraftMoldedItem {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemMoldedItem.class);
	}

	public static PolycraftMoldedItem getEquippedItem(final EntityPlayer player) {
		return (PolycraftMoldedItem) PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public final MoldedItem moldedItem;

	public ItemMoldedItem(final MoldedItem moldedItem) {
		Preconditions.checkNotNull(moldedItem);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(MoldedItem.class.getSimpleName() + "_" + moldedItem.source.polymerObject.name)));
		if (moldedItem.maxStackSize > 0)
			this.setMaxStackSize(moldedItem.maxStackSize);
		this.moldedItem = moldedItem;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_MOLDED_ITEM;
	}

	@Override
	public MoldedItem getMoldedItem() {
		return moldedItem;
	}
}
