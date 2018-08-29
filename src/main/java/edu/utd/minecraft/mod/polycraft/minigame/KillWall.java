package edu.utd.minecraft.mod.polycraft.minigame;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class KillWall{
	

	

	public static boolean active=false;
	public static double radius=200;
	
	public KillWall(){

	}
	
	
	
	public static void shrinkKillWall()
	{
		if(active)
		{
			if(radius>0)
				radius=radius-.2;
			else
				radius=0;
		}
	}
	
	public static  boolean isInKillWall(EntityPlayer player)
	{
		if(Math.abs(player.posX)>radius || Math.abs(player.posZ)>radius)
			return true;
		else
			return false;
	}
	

}
