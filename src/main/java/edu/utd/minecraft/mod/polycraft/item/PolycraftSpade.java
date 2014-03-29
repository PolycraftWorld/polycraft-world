package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemSpade;

public abstract class PolycraftSpade extends ItemSpade implements PolycraftItem {
	public PolycraftSpade(ToolMaterial toolMaterial) {
		super(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_SPADES;
	}
}
