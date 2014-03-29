package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemSword;

public abstract class PolycraftSword extends ItemSword implements PolycraftItem {
	public PolycraftSword(ToolMaterial toolMaterial) {
		super(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_SWORDS;
	}
}
