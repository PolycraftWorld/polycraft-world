package edu.utd.minecraft.mod.polycraft.minigame;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import ibxm.Player;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class KillWall{
	

	

	public boolean active=false;
	public double radius=0;
	public int speed=2;
	public String envoker;
	
	public static KillWall INSTANCE= new KillWall();
	
	public KillWall(){
		
	}
	
	public void start(World world, int speed, int radius, String envoker) {
		this.envoker=envoker;
		this.start(world,speed,radius);
		
	}
	
	public void start(World world,int speed, int radius)
	{
		this.active=true;
		this.radius=radius;
		this.speed=speed;
		
		 		
		for(int i=0;i<world.playerEntities.size();i++)
		{
			EntityPlayer p =(EntityPlayer) world.playerEntities.get(i);
			if(!(p.getCommandSenderName()==envoker))
			{
				int x = ThreadLocalRandom.current().nextInt(-radius+30, radius-30 + 1);
				int z = ThreadLocalRandom.current().nextInt(-radius+30, radius-30 + 1);
				//p.inventory.dropAllItems();
				p.inventory.mainInventory= new ItemStack[36];
				p.inventory.armorInventory = new ItemStack[4];
				p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("5a"))));
				p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("3n"))));
				p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("3p"))));
				p.setPositionAndUpdate(x, p.worldObj.getTopSolidOrLiquidBlock(x, z)+6, z);
			}
		}
	}
	
	public void UpdateKillWall(final String killWallJson) {
		Gson gson = new Gson();
		Type typeOfKillWall = new TypeToken<KillWall>() {}.getType();
		KillWall temp = gson.fromJson(killWallJson, typeOfKillWall);
		
		KillWall.INSTANCE.active = temp.active;
		KillWall.INSTANCE.radius = temp.radius;
		KillWall.INSTANCE.speed = temp.speed;
		KillWall.INSTANCE.envoker = temp.envoker;
	}
	
	
	public void shrinkKillWall()
	{
		if(active)
		{
			if(radius>0)
				radius=radius-(speed/64.0);
			else
				radius=0;
		}
	}
	
	public void onTickUpdate(final TickEvent.PlayerTickEvent event)
	{
		if (event.side == Side.SERVER) {

			Iterator iterator = event.player.worldObj.playerEntities.iterator();
			int aliveCount = 0;
	        while (iterator.hasNext())
	        {
	            EntityPlayer entityplayer = (EntityPlayer)iterator.next();

	            if (entityplayer.isEntityAlive() && !(entityplayer.getCommandSenderName()==envoker))
	            {
	                aliveCount++;
	            }
	        }
	        if(aliveCount <= 1) {
	        	
				active=false;
				ServerEnforcer.INSTANCE.minigameUpdate();
			}
			if (event.player.isEntityAlive() && !(event.player.getCommandSenderName()==envoker)) {
				if (this.isInKillWall(event.player) && active)
				{
					
					if(event.player.ticksExisted%20==0)
					{
						((EntityPlayer) event.player).addChatComponentMessage(new ChatComponentText("Past Kill Wall"));
						event.player.setHealth(event.player.getHealth()-2);
					}
				}
			}
		}
		if(true)
		{
			this.shrinkKillWall();
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
