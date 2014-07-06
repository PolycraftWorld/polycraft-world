package edu.utd.minecraft.mod.polycraft.item;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class PolycraftAxe extends ItemAxe implements PolycraftItem {
	protected PolycraftAxe(final ToolMaterial toolMaterial) {
		super(toolMaterial);
		Preconditions.checkNotNull(toolMaterial);
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.TOOLS_AXES;
	}
	
	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) 
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
	}
}
