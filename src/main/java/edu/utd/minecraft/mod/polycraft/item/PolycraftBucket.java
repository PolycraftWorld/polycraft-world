package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

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
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}

}
