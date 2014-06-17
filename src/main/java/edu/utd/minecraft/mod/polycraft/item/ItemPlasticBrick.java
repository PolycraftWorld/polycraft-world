package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.block.BlockBrick;

public class ItemPlasticBrick extends ItemBlock implements PolycraftItem {

	public final BlockBrick blockBrick;

	public ItemPlasticBrick(Block p_i45355_1_) {
		super(p_i45355_1_);
		this.setHasSubtypes(true);
		this.blockBrick = (BlockBrick) p_i45355_1_;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return blockBrick.getUnlocalizedName(par1ItemStack.getItemDamage());
	}
}