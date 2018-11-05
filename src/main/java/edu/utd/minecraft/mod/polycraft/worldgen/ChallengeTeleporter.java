package edu.utd.minecraft.mod.polycraft.worldgen;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class ChallengeTeleporter extends PolycraftTeleporter {
	public boolean flag;

	public ChallengeTeleporter(WorldServer server, boolean flag) {
		super(server);
		this.flag=flag;//flag true if going into challenge dimension
	}
	
	public ChallengeTeleporter(WorldServer server, int x, int y, int z, boolean flag) {
		super(server, x, y, z);
		this.flag = flag;
	}
	

	@Override
	public void placeInPortal(Entity entity, double var1, double var2, double var3, float var4) {
		if(flag)
		{
			if(!entity.worldObj.isRemote)
			{				 
//				 PrivateProperty pp2 =  new PrivateProperty(
//							false,
//							(EntityPlayerMP) entity,
//							"NO",
//							"CANT LEAVE",
//							new Chunk(-1,2),
//							new Chunk(2,-1),
//							new int[] {3,4,5,6,44},
//							0);
//				 for(int x=-1;x<3;x++)
//				 {
//					 for(int z=-1;z<3;z++)
//					 {
//						 if((x==0 || x==1) && (z==0 || z==1))
//						 {
//							 Enforcer.removeChallengePropertyByChunk(pp2, x, z); 
//						 }else
//						 {
//							 Enforcer.addChallengePropertyByChunk(pp2, x, z); 
//						 }
//					 }
//					 
//				 }
//				 
//				 		
//				 //ServerEnforcer.INSTANCE.sendTempPPDataPackets();
//				 ServerEnforcer.INSTANCE.sendTempCPDataPackets((EntityPlayerMP)entity);
//				 
//				 PrivateProperty pp =  new PrivateProperty(
//							false,
//							(EntityPlayerMP) entity,
//							"Challenge",
//							"Good Luck!",
//							new Chunk(0,1),
//							new Chunk(1,0),
//							new int[] {0,3,4,5,6,44},
//							0);
//			
//					 Enforcer.addChallengeProperty(pp);		
//					 //ServerEnforcer.INSTANCE.sendTempPPDataPackets();
//					 ServerEnforcer.INSTANCE.sendTempCPDataPackets((EntityPlayerMP)entity);
					 //I don't think we should do this here?
					 ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("Welcome to Polycraft Labs™!"));
					 //TODO: Delet this.
				 
			}
			entity.setLocationAndAngles(2, 90, 2, entity.rotationYaw, 0.0F);
	        entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		}else
		{
			 entity.setLocationAndAngles(2, 90, 2, entity.rotationYaw, 0.0F);
	         entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		}
	}

}
