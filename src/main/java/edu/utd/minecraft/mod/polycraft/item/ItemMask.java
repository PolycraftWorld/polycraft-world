package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Mask;

public class ItemMask extends Item implements PolycraftItem, PolycraftMaskItem {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, Mask.class);
	}

	public static PolycraftMaskItem getEquippedItem(final EntityPlayer player) {
		return (PolycraftMaskItem) PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public final Mask mask;

	public ItemMask(final Mask mask) {
		Preconditions.checkNotNull(mask);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(ItemMask.class.getSimpleName())));// + "_" + mask.name)));

		//		if (maskItem.loadCustomTexture)
		//			this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(MoldedItem.class.getSimpleName() + "_" + maskItem.name)));
		//		else
		//			this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(MoldedItem.class.getSimpleName() + "_" + maskItem.source.polymerObject.name)));
		//		if (maskItem.maxStackSize > 0)
		//			this.setMaxStackSize(maskItem.maxStackSize);
		this.mask = mask;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_MASK_ITEM;
	}

	@Override
	public Mask getMaskItem() {
		return mask;
	}

	public static ItemStack setDamagePerUse(final ItemStack itemStack, final int damagePerUse) {
		PolycraftItemHelper.setInteger(itemStack, "DamagePerUse", damagePerUse);
		return itemStack;
	}

	public static int getDamagePerUse(final ItemStack itemStack) {
		return PolycraftItemHelper.getInteger(itemStack, "DamagePerUse", 1);
	}
}
