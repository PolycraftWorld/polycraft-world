package edu.utd.minecraft.mod.polycraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
		if(!world.isRemote) {
			double velX = 1*Math.sin(Math.toRadians(player.rotationYaw%360));
			double velY = 1;
			double velZ = 1*Math.cos(Math.toRadians(player.rotationYaw%360));
			System.out.println("Rotation: " + player.rotationYaw +":: Rotation Mod: " + player.renderYawOffset );
			player.setVelocity(velX, velY, velZ);
			((EntityPlayerMP) world.getPlayerEntityByName(player.getDisplayName())).addVelocity(velX, velY, velZ);
		}
		
		
		return super.onItemRightClick(itemstack, world, player);
	}

}