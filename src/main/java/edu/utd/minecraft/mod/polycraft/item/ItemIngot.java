package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.Item;

public class ItemIngot extends Item implements PolycraftItem {
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_INGOT;
	}
}
