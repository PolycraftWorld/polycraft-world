package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemArmor;

public abstract class PolycraftArmor extends ItemArmor implements PolycraftItem {
	public PolycraftArmor(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance, ArmorSlot armorSlot) {
		super(armorMaterial, armorAppearance.getValue(), armorSlot.getValue());
	}

	@Override
	public abstract ItemCategory getCategory();
}
