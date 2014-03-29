package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;

public class PolycraftBucket extends ItemBucket implements PolycraftItem {
	public PolycraftBucket(Block block) {
		super(block);
	}

	@Override
	public ItemCategory getCategory() {
		// TODO: Item category?
		return ItemCategory.ITEMS;
	}

}
