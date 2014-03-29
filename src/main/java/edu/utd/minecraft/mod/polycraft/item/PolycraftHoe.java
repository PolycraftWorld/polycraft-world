package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemHoe;

public abstract class PolycraftHoe extends ItemHoe implements PolycraftItem {
	public PolycraftHoe(ToolMaterial toolMaterial) {
		super(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_HOES;
	}

}
