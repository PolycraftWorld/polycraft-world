package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.WaferItem;

public class ItemWafer extends Item implements PolycraftItem, PolycraftWaferItem {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemMoldedItem.class);
	}

	public static PolycraftMoldedItem getEquippedItem(final EntityPlayer player) {
		return (PolycraftMoldedItem) PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public final WaferItem waferItem;

	public ItemWafer(final WaferItem waferItem) {
		Preconditions.checkNotNull(waferItem);
		this.setCreativeTab(CreativeTabs.tabMaterials);
//		if (waferItem.loadCustomTexture)
//			this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(WaferItem.class.getSimpleName() + "_" + waferItem.name)));
//		else
//			this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(WaferItem.class.getSimpleName())));// + "_" + waferItem.source.name)));
		//		if (waferItem.maxStackSize > 0)
		//			this.setMaxStackSize(waferItem.maxStackSize);
		this.waferItem = waferItem;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_WAFER_ITEM;
	}

	@Override
	public WaferItem getWaferItem() {
		return waferItem;
	}

}
