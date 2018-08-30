package edu.utd.minecraft.mod.polycraft.minigame;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class KillWall{
	

	

	public boolean active=false;
	public double radius=200;
	public int speed=2;
	
	public static KillWall INSTANCE= new KillWall();
	
	public KillWall(){
		
	}
	
	public void start(World world,int speed, int radius)
	{
		this.active=true;
		this.radius=radius;
		this.speed=speed;
		
		 		
		for(int i=0;i<world.playerEntities.size();i++)
		{
			EntityPlayer p =(EntityPlayer) world.playerEntities.get(i);
			int x = ThreadLocalRandom.current().nextInt(-radius+20, radius-20 + 1);
			int z = ThreadLocalRandom.current().nextInt(-radius+20, radius-20 + 1);

			p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("5a"))));
			p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("3n"))));
			p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("3p"))));
			p.setPositionAndUpdate(x, p.worldObj.getTopSolidOrLiquidBlock(x, z)+4, z);
		}
	}
	
	public void UpdateKillWall(final String killWallJson) {
		Gson gson = new Gson();
		Type typeOfKillWall = new TypeToken<KillWall>() {}.getType();
		KillWall temp = gson.fromJson(killWallJson, typeOfKillWall);
		
		KillWall.INSTANCE.active = temp.active;
		KillWall.INSTANCE.radius = temp.radius;
		KillWall.INSTANCE.speed = temp.speed;
	}
	
	
	public void shrinkKillWall()
	{
		if(active)
		{
			if(radius>0)
				radius=radius-(speed/16.0);
			else
				radius=0;
		}
	}
	
	public boolean isInKillWall(EntityPlayer player)
	{
		if(Math.abs(player.posX)>radius || Math.abs(player.posZ)>radius)
			return true;
		else
			return false;
	}
	

}
