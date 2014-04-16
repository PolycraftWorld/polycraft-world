package edu.utd.minecraft.mod.polycraft.item;


public abstract class PolycraftArmorChest extends PolycraftArmor {
	public PolycraftArmorChest(ArmorMaterial armorMaterial, ArmorAppearance armorAppearance) {
		super(armorMaterial, armorAppearance, ArmorSlot.CHEST);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ARMOR_CHEST;
	}
}