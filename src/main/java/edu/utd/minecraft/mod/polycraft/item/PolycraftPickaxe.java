package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public abstract class PolycraftPickaxe extends ItemPickaxe implements PolycraftItem {
	protected PolycraftPickaxe(ToolMaterial toolMaterial) {
		super(toolMaterial);
		Preconditions.checkNotNull(toolMaterial);
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_PICKAXES;
	}
	
	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}
