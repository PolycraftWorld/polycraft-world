package edu.utd.minecraft.mod.polycraft.item;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemSword;

public abstract class PolycraftSword extends ItemSword implements PolycraftItem {
	public PolycraftSword(ToolMaterial toolMaterial) {
		super(toolMaterial);
		Preconditions.checkNotNull(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_SWORDS;
	}
}
