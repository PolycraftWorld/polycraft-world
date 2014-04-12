package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;

public class PolycraftBucket extends ItemBucket implements PolycraftItem {
	public PolycraftBucket(Block block) {
		super(block);
		this.setContainerItem(Items.bucket);
	}

	@Override
	public ItemCategory getCategory() {
		// TODO: Item category?
		return ItemCategory.ITEMS;
	}

}
