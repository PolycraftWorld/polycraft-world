package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Electronics;

public class ItemElectronics extends Item implements PolycraftItem, PolycraftElectronics {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemMoldedItem.class);
	}

	public static PolycraftMoldedItem getEquippedItem(final EntityPlayer player) {
		return (PolycraftMoldedItem) PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public final Electronics electronics;

	public ItemElectronics(final Electronics electronics) {
		Preconditions.checkNotNull(electronics);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(ItemElectronics.class.getSimpleName())));// + "_" + electronics.name)));
		this.electronics = electronics;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_WAFER_ITEM;
	}

	@Override
	public Electronics getElectronics() {
		return electronics;
	}

}
