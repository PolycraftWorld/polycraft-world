package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlab;

public class ItemPolymerSlab extends ItemSlab implements PolycraftItem {

	public ItemPolymerSlab(Block p_i45355_1_, BlockPolymerSlab p_i45355_2_, BlockPolymerSlab p_i45355_3_, Boolean p_i45355_4_) {
		super(p_i45355_1_, p_i45355_2_, p_i45355_3_, p_i45355_4_);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}
}