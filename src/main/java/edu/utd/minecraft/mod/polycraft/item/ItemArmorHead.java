package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Armor;

public class ItemArmorHead extends PolycraftArmorHeadgear {

	private Armor armor;
	
	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, ArmorSlot.HEAD, ItemArmorHead.class);
	}

	public static ItemArmorHead getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, ArmorSlot.HEAD);
	}
	
	public ItemArmorHead(final Armor armor, final ArmorMaterial material) {
		super(material, ArmorAppearance.CHAIN);
		this.armor = armor;
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(armor.getFullComponentName(ArmorSlot.HEAD))));
		this.setCreativeTab(CreativeTabs.tabCombat);
//		try {
//			this.armor.saveArmorPNG(ArmorSlot.HEAD);
//		} catch (IOException e) {
//			// TODO Uncomment if you want to auto-generate armor textures
//			e.printStackTrace();
//		}
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return armor.getTexture();
		//return PolycraftMod.getAssetName("textures/models/armor/" + PolycraftMod.getFileSafeName(armor.name) + ".png");
	}
}
