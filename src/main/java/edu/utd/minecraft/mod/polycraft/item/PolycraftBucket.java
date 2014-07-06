package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
	
	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}

}
