package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemPickaxe;

import com.google.common.base.Preconditions;

public abstract class PolycraftPickaxe extends ItemPickaxe implements PolycraftItem {
	protected PolycraftPickaxe(ToolMaterial toolMaterial) {
		super(toolMaterial);
		Preconditions.checkNotNull(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_PICKAXES;
	}
}
