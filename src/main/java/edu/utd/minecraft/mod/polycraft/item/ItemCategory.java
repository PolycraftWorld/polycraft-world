package edu.utd.minecraft.mod.polycraft.item;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public enum ItemCategory {
	ARMOR("Armor"),
	ARMOR_CHEST("Armor/Chest"),
	ARMOR_FEET("Armor/Feet"),
	ARMOR_HEADGEAR("Armor/Headgear"),
	ARMOR_LEGGINGS("Armor/Leggings"),
	COMPONENTS("Components"),
	BLOCKS("Blocks"),
	BLOCKS_BUILDING("Blocks/Building"),
	ITEMS("Items"),
	ITEMS_MOLD("Items/Mold"),
	ITEMS_MOLDED_ITEM("Items/MoldedItem"),
	ITEMS_UTILITY("Items/Utility"),
	ITEMS_VESSEL("Items/Vessel"),
	MATERIALS("Materials"),
	MATERIALS_CATALYST("Materials/Catalyst"),
	MATERIALS_FIBER("Materials/Fiber"),
	MATERIALS_INGOT("Materials/Ingot"),
	MATERIALS_PELLET("Materials/Pellet"),
	TOOLS("Tools"),
	TOOLS_AXES("Tools/Axes"),
	TOOLS_HOES("Tools/Hoes"),
	TOOLS_PICKAXES("Tools/Pickaxes"),
	TOOLS_SPADES("Tools/Spades"),
	TOOLS_SWORDS("Tools/Swords");

	private final String name;

	public String getValue() {
		return this.name;
	}

	private ItemCategory(final String name) {
		// Item category names must be valid (start with an upper case character) for consistency
		Preconditions.checkNotNull(name);
		Preconditions.checkState(!Strings.isNullOrEmpty(name));
		String[] split = name.split("/");
		for (final String entry : split) {
			Preconditions.checkState(!Strings.isNullOrEmpty(entry));
			Preconditions.checkState(Character.isUpperCase(entry.charAt(0)));
		}
		this.name = name;
	}
}
