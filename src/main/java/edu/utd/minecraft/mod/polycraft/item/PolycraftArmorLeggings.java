package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemArmor.ArmorMaterial;

public abstract class PolycraftArmorLeggings extends PolycraftArmor {
	public PolycraftArmorLeggings(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance) {
		super(armorMaterial, armorAppearance, ArmorSlot.LEGS);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ARMOR_LEGGINGS;
	}
}
