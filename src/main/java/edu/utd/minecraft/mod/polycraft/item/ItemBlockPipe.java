package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPipe;

public class ItemBlockPipe extends ItemBlock implements PolycraftItem {

	public final BlockPipe blockPipe;
	
	public ItemBlockPipe(Block block) {
		super(block);
		this.blockPipe = (BlockPipe) block;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return blockPipe.getUnlocalizedName(par1ItemStack.getItemDamage());
	}
}