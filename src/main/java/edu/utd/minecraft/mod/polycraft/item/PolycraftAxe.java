package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemAxe;

public abstract class PolycraftAxe extends ItemAxe implements PolycraftItem {
	protected PolycraftAxe(ToolMaterial toolMaterial) {
		super(toolMaterial);
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_AXES;
	}
}
