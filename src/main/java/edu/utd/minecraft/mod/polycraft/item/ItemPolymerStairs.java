package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerStairs;

public class ItemPolymerStairs extends ItemBlock implements PolycraftItem {

	private final BlockPolymerStairs blockPolymerStairs;

	public ItemPolymerStairs(Block p_i45355_1_) {
		super(p_i45355_1_);
		this.blockPolymerStairs = (BlockPolymerStairs) p_i45355_1_;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return blockPolymerStairs.getUnlocalizedName(par1ItemStack.getItemDamage());
	}
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}