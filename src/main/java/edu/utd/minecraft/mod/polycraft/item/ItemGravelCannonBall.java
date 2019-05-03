package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.item.ItemStack;

public class ItemGravelCannonBall extends ItemIronCannonBall{

	public ItemGravelCannonBall(CustomObject config) {
		super(config);
		// TODO Auto-generated constructor stub
	}
	
	 @Override
	 public String getItemStackDisplayName(ItemStack par1ItemStack)
	 {
		 return "Gravel Cannonball";
	 }

}
