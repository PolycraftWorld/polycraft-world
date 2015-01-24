package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Armor;

public class ItemArmorChest extends PolycraftArmorChest {

	private Armor armor;
	
	public ItemArmorChest(final Armor armor, final ArmorMaterial material) {
		super(material, ArmorAppearance.CHAIN);
		this.armor = armor;
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(armor.getFullComponentName(ArmorSlot.CHEST))));
		this.setCreativeTab(CreativeTabs.tabCombat);
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return armor.getTexture();
	}
}
