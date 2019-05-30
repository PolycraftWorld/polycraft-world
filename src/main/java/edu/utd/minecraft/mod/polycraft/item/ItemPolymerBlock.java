package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper;

public class ItemPolymerBlock extends ItemBlock implements PolycraftItem {

	public final BlockPolymer blockPolymer;

	public ItemPolymerBlock(Block p_i45355_1_) {
		super(p_i45355_1_);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.blockPolymer = (BlockPolymer) p_i45355_1_;
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.BLOCKS_BUILDING;
	}

	// create a unique unlocalised name for each colour, so that we can give each one a unique name
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
	  BlockPolymerHelper.EnumColor color = BlockPolymerHelper.EnumColor.byMetadata(stack.getMetadata());
	  return super.getUnlocalizedName() + "_" + color.toString();
	}
	  
//	@Override
//	public String getUnlocalizedName(ItemStack par1ItemStack) {
//		return blockPolymer.getUnlocalizedName(par1ItemStack.getItemDamage());
//	}
	
//	@Override
//	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
//	}
}