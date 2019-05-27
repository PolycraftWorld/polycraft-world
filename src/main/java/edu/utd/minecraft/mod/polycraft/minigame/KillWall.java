package edu.utd.minecraft.mod.polycraft.minigame;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class KillWall extends PolycraftMinigame{
	
	public static final int id=1;
	//public boolean active=false;
	public double radius=0;
	public int speed=2;
	public String envoker;
	public Color color= Color.red;
	public float floor=(float) radius;
	public float roof=(float) radius;
	public BoundingBox box = new BoundingBox(0.0, 0.0,radius,floor ,roof, color);
	
	
	
	//public static KillWall INSTANCE= new KillWall();
	
	public KillWall(){
		
	}
	
	@Override
	public void init()
	{
		PolycraftMinigameManager.INSTANCE= new KillWall();
		//ServerEnforcer.INSTANCE.minigameUpdate(this.id);
	}


	@Override
	public double getDouble() // this is not correct. we need a system to Get specific Minigame Information.
	{
		return radius;
	}
	
	@Override
	public void start(World world, int[] args, String envoker)
	{
		start(world,args[0],args[1],envoker);
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
			if(!(p.getCommandSenderEntity().getName()==envoker))
			{
				int x = ThreadLocalRandom.current().nextInt(30-radius, radius-30 + 1);
				int z = ThreadLocalRandom.current().nextInt(30-radius, radius-30 + 1);
				//p.inventory.dropAllItems();
				p.inventory.mainInventory= new ItemStack[36];
				p.inventory.armorInventory = new ItemStack[4];
				p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("5a"))));
				p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("3n"))));
				p.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("3p"))));
				p.setPositionAndUpdate(x, p.worldObj.getTopSolidOrLiquidBlock(new BlockPos(x,0, z)).getY()+6, z);
			}
		}
	}
	
//	public void Update(final String killWallJson) {
//		Gson gson = new Gson();
//		Type typeOfKillWall = new TypeToken<KillWall>() {}.getType();
//		KillWall temp = gson.fromJson(killWallJson, typeOfKillWall);
//		
//		KillWall.INSTANCE.active = temp.active;
//		KillWall.INSTANCE.radius = temp.radius;
//		KillWall.INSTANCE.speed = temp.speed;
//		KillWall.INSTANCE.envoker = temp.envoker;
//	}
//	
	
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
	
	@Override
	public void onServerTick(ServerTickEvent event) {
		ServerEnforcer.INSTANCE.minigameUpdate(this.id);
		if(true)
		{
			this.shrinkKillWall();
		}
		
	}
	
	@Override
	public void onPlayerTick(final TickEvent.PlayerTickEvent event)
	{
		if (event.side == Side.SERVER) {

			Iterator iterator = event.player.worldObj.playerEntities.iterator();
			int aliveCount = 0;
	        while (iterator.hasNext())
	        {
	            EntityPlayer entityplayer = (EntityPlayer)iterator.next();

	            if (entityplayer.isEntityAlive() && !(entityplayer.getCommandSenderEntity().getName()==envoker))
	            {
	                aliveCount++;
	            }
	        }
	        if(aliveCount <= 1) {
	        	
				active=false;
				ServerEnforcer.INSTANCE.minigameUpdate(this.id);
			}
			if (event.player.isEntityAlive() && !(event.player.getCommandSenderEntity().getName()==envoker)) {
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
			//this.shrinkKillWall();
		}
		
	}
	
	public boolean isInKillWall(EntityPlayer player)
	{
		if(Math.abs(player.posX)>radius || Math.abs(player.posZ)>radius)
			return true;
		else
			return false;
	}
	
	
	@Override
	public boolean shouldUpdatePackets()//true if is on tick that needs to update packets
	{
		
		return true;
	}

	@Override
	public Type getType()
	{
		Type t = new TypeToken<KillWall>() {}.getType();
		return t;
	}
	
	@Override
	public void render(Entity entity) {
		renderKillWallBounds(entity);
		
	}
	
	private static void renderKillWallBounds(Entity entity) {
		 if (entity.worldObj.isRemote ){				//&& PolycraftMinigameManager.INSTANCE!=null
			 if(true)//PolycraftMinigameManager.INSTANCE.active
			 {
				 	GL11.glDisable(GL11.GL_TEXTURE_2D);
			        GL11.glEnable(GL11.GL_BLEND);
			        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			        GL11.glDisable(GL11.GL_LIGHTING);
			        GL11.glLineWidth(3.0F);
			        GL11.glBegin(GL11.GL_LINES);//Gl_Line_Loop
	                double dy = 16;
	                double y1 = Math.floor(entity.posY - dy / 2);
	                double y2 = y1 + dy;
	                if (y1 < 0) {
	                    y1 = 0;
	                    y2 = dy;
	                }
	                if (y1 > entity.worldObj.getHeight()) {
	                    y2 = entity.worldObj.getHeight();
	                    y1 = y2 - dy;
	                }
	                double radius;
	                if(PolycraftMinigameManager.INSTANCE!=null)
	                	radius=PolycraftMinigameManager.INSTANCE.getDouble();
	                else
	                	radius=0;
	                
	                //radius=KillWall.INSTANCE.radius;
	                
	                
	                
	                GL11.glColor4d(0.9, 0, 0, .5);
	//                for (double y = (int) y1; y <= y2; y++) {
	//                	
	//                	for (int i=0; i<360 ; i+=4)
	//	                {
	//	                	double degInRad = i*DEG2RAD;
	//	                	GL11.glVertex3f((float)Math.cos(degInRad)*radius, (float) y,(float)Math.sin(degInRad)*radius);
	//	                }
	//	                
	//                }
	                
	                for (double y = (int) y1; y <= y2; y++) {
	                	GL11.glVertex3d(radius, y, radius);
	                	GL11.glVertex3d(-radius, y, radius);
	                	
	                	GL11.glVertex3d(-radius, y, radius);
	                	GL11.glVertex3d(-radius, y, -radius);
	                	
	                	GL11.glVertex3d(-radius, y, -radius);
	                	GL11.glVertex3d(radius, y, -radius);
	                	
	                	GL11.glVertex3d(radius, y, -radius);
	                	GL11.glVertex3d(radius, y, radius);
	                	
	                }
			 
			        GL11.glEnd();
			        GL11.glEnable(GL11.GL_LIGHTING);
			        GL11.glEnable(GL11.GL_TEXTURE_2D);
			        GL11.glDisable(GL11.GL_BLEND);
			        
			 }
		 }
	 }
	
	public static void register(int id,Class c)
	{
		
		PolycraftMinigameManager.registerMinigame(id, c);
	}

	
	

}
