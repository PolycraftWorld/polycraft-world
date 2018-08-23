package edu.utd.minecraft.mod.polycraft.minigame;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class KillWall {
	public static double killWallDistance=100;
	
	public static double getKillWallDistance()
	{
		return killWallDistance;
	}
	
	public static boolean isInKillWall(EntityPlayer player)
	{
		if((double)MathHelper.sqrt_double(player.posX * player.posX + player.posZ * player.posZ)>getKillWallDistance())
			return true;
		else
			return false;
	}

}
