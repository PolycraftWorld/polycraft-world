package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemPickaxe;

public abstract class PolycraftPickaxe extends ItemPickaxe implements PolycraftItem {
	protected PolycraftPickaxe(ToolMaterial toolMaterial) {
		super(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_PICKAXES;
	}
}
