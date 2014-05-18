package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemPolymerWall extends ItemBlock implements PolycraftItem {

	public ItemPolymerWall(Block p_i45355_1_) {
		super(p_i45355_1_);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}
}