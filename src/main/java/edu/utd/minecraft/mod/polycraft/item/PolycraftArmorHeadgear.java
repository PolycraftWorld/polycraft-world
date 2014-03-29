package edu.utd.minecraft.mod.polycraft.item;

public abstract class PolycraftArmorHeadgear extends PolycraftArmor {
	public PolycraftArmorHeadgear(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance) {
		super(armorMaterial, armorAppearance, ArmorSlot.HEAD);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ARMOR_HEADGEAR;
	}
}
