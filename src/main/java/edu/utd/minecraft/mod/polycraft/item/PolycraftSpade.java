package edu.utd.minecraft.mod.polycraft.item;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemSpade;

public abstract class PolycraftSpade extends ItemSpade implements PolycraftItem {
	public PolycraftSpade(ToolMaterial toolMaterial) {
		super(toolMaterial);
		Preconditions.checkNotNull(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_SPADES;
	}
}
