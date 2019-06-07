package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerWall;

public class ItemPolymerWall extends ItemBlock implements PolycraftItem {

	public final BlockPolymerWall blockPolymerWall;

	public ItemPolymerWall(Block p_i45355_1_) {
		super(p_i45355_1_);
		this.setHasSubtypes(true);
		this.blockPolymerWall = (BlockPolymerWall) p_i45355_1_;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}

//	@Override
//	public String getUnlocalizedName(ItemStack par1ItemStack) {
//		return blockPolymerWall.getUnlocalizedName(par1ItemStack.getItemDamage());
//	}
//	@Override
//	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
//	}
}