package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public abstract class PolycraftHoe extends ItemHoe implements PolycraftItem {
	public PolycraftHoe(ToolMaterial toolMaterial) {
		super(toolMaterial);
		Preconditions.checkNotNull(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_HOES;
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
	

}
