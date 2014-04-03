package edu.utd.minecraft.mod.polycraft.item;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemAxe;

public abstract class PolycraftAxe extends ItemAxe implements PolycraftItem {
	protected PolycraftAxe(final ToolMaterial toolMaterial) {
		super(toolMaterial);
		Preconditions.checkNotNull(toolMaterial);
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_AXES;
	}
}
