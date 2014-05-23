package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerWall;

public class ItemPolymerWall extends ItemBlock implements PolycraftItem {

	private final BlockPolymerWall blockPolymerWall;

	public ItemPolymerWall(Block p_i45355_1_) {
		super(p_i45355_1_);
		this.blockPolymerWall = (BlockPolymerWall) p_i45355_1_;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return blockPolymerWall.getUnlocalizedName(par1ItemStack.getItemDamage());
	}
}