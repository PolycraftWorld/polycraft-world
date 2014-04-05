package edu.utd.minecraft.mod.polycraft.item;


public abstract class PolycraftArmorFeet extends PolycraftArmor {
	public PolycraftArmorFeet(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance) {
		super(armorMaterial, armorAppearance, ArmorSlot.FEET);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ARMOR_FEET;
	}
}
