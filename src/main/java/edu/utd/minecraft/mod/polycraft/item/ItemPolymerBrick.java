package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerBrick;
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;

public class ItemPolymerBrick extends ItemBlock implements PolycraftItem, PolycraftPolymerBrick {

	public final BlockPolymerBrick blockBrick;

	public ItemPolymerBrick(Block p_i45355_1_) {
		super(p_i45355_1_);
		this.setHasSubtypes(true);
		this.blockBrick = (BlockPolymerBrick) p_i45355_1_;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return blockBrick.getUnlocalizedName(par1ItemStack.getItemDamage());
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}

	@Override
	public PolymerBrick getPolymerBrick() {
		// TODO Auto-generated method stub
		return blockBrick.Brick;
	}
}