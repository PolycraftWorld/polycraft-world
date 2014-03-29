package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.Item;

public class ItemPellet extends Item implements PolycraftItem {
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_PELLET;
	}
	
	public ItemPellet() {
	}
}
