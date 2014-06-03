package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;

public class ItemPolymerBlock extends ItemBlock implements PolycraftItem {

	public final BlockPolymer blockPolymer;

	public ItemPolymerBlock(Block p_i45355_1_) {
		super(p_i45355_1_);
		this.setHasSubtypes(true);
		this.blockPolymer = (BlockPolymer) p_i45355_1_;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return blockPolymer.getUnlocalizedName(par1ItemStack.getItemDamage());
	}
}