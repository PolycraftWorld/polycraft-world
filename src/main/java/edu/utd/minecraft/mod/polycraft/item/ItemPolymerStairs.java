package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemPolymerStairs extends ItemBlock implements PolycraftItem {

	public ItemPolymerStairs(Block p_i45355_1_) {
		super(p_i45355_1_);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}
}