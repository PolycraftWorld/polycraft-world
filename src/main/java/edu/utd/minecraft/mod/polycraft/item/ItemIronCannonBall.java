package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemIronCannonBall extends ItemCustom{
	
	
	
	
	 public ItemIronCannonBall(CustomObject config) {
		super(config);
		this.setCreativeTab(CreativeTabs.tabMisc); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		// TODO Auto-generated constructor stub
	}

	 @Override
	 public String getItemStackDisplayName(ItemStack par1ItemStack)
	 {
		 return "Iron Cannonball";
	 }
	 

	 
	@Override
	 public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	 {
		 if(!world.isRemote)
		 {
				EntityIronCannonBall cannonBall;
	        	cannonBall = new EntityIronCannonBall(world);
	        	cannonBall.forceSpawn=true;
	        	
	        	cannonBall.setPosition((double)x+.5, (double)y+ hitY+.5, (double)z+.5);
	            world.spawnEntityInWorld(cannonBall);
		 }
		return false;
	 
	 }
}
