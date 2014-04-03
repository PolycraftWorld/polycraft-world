package edu.utd.minecraft.mod.polycraft.item;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemArmor;

public abstract class PolycraftArmor extends ItemArmor implements PolycraftItem {
	public PolycraftArmor(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance, ArmorSlot armorSlot) {
		super(armorMaterial, armorAppearance.getValue(), armorSlot.getValue());
		Preconditions.checkNotNull(armorMaterial);
		Preconditions.checkNotNull(armorAppearance);
		Preconditions.checkNotNull(armorSlot);
	}

	@Override
	public abstract ItemCategory getCategory();
}
