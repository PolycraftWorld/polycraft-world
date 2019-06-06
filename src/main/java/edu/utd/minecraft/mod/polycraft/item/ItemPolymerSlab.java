package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlab;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerSlabDouble;

public class ItemPolymerSlab extends ItemSlab implements PolycraftItem {

	public ItemPolymerSlab(Block p_i45355_1_, BlockPolymerSlab p_i45355_2_, BlockPolymerSlabDouble p_i45355_3_) {
		super(p_i45355_1_, p_i45355_2_, p_i45355_3_);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}
	
//	@Override TODO: removed in 1.8
//	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
//	}
}