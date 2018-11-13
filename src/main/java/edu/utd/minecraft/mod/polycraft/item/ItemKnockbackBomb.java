package edu.utd.minecraft.mod.polycraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ItemKnockbackBomb  extends ItemCustom{
	
	public ItemKnockbackBomb(CustomObject config) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName("knockback_bomb"));
		this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		// TODO Auto-generated method stub
		super.onUsingTick(stack, player, count);
		
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		// TODO Auto-generated method stub
		
		
		player.motionX = 1;
		
		return super.onItemRightClick(itemstack, world, player);
	}

}