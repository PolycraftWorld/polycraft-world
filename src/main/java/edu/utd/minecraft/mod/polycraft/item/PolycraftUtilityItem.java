package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.Item;

// Utility items - items usable by the player that don't have a more specific category
public class PolycraftUtilityItem extends Item implements PolycraftItem {
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.ITEMS_UTILITY;
	}

}
