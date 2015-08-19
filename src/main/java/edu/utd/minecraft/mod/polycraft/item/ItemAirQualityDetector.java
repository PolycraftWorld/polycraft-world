package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemAirQualityDetector extends Item implements PolycraftItem {

	public final CustomObject config;

	public ItemAirQualityDetector(final CustomObject config) {
		super();
		this.config = config;
		Preconditions.checkNotNull(config);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name)));
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);

	}

	@Override
	public ItemCategory getCategory() {
		// TODO Auto-generated method stub
		return ItemCategory.ITEMS_CUSTOM;
	}

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemAirQualityDetector.class);
	}

}
