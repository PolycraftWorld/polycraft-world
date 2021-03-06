package edu.utd.minecraft.mod.polycraft.entity.Physics;

import java.util.List;


import org.lwjgl.opengl.GL11;

//import codechicken.lib.vec.Vector3;
//import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.HPBlock;
import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityDummy;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.PolycraftEntityLiving;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonBlock;
import edu.utd.minecraft.mod.polycraft.render.PolyParticleSpawner;
import edu.utd.minecraft.mod.polycraft.tileentity.TileEntityHPBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHardenedClay;
import net.minecraft.block.BlockStoneBrick;
//import net.minecraft.block.BlockWood;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;


public class EntityIronCannonBall extends Entity {
	
	public double mass=5.0;
	private static PolycraftEntity config;
	private final static String HP_BLOCK = "1hH";
	//public double acc=-9.8/200;
	

	public EntityIronCannonBall(World p_i1582_1_) {
		super(p_i1582_1_);
		this.setSize(.5F, .5F); 
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		// TODO Auto-generated method stub
		
	}
	
//	public void render(Entity entity) {
//		if (entity.worldObj.isRemote && rendering) {
//			GL11.glDisable(GL11.GL_TEXTURE_2D);
//			GL11.glEnable(GL11.GL_BLEND);
//			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//			GL11.glDisable(GL11.GL_LIGHTING);
//			GL11.glLineWidth(lineWidth);
//			GL11.glBegin(GL11.GL_LINES);
//			GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F,
//					color.getAlpha() / 255F); // Set color to specified color.
//
//			GL11.glVertex3d(x1o4, y2, z1o4);
//
//			GL11.glEnd();
//			GL11.glEnable(GL11.GL_CULL_FACE);
//			GL11.glEnable(GL11.GL_LIGHTING);
//			GL11.glEnable(GL11.GL_TEXTURE_2D);
//			GL11.glDisable(GL11.GL_BLEND);
//		}
//	}
	
	public void onUpdate()
	{
  		super.onUpdate();
		
		if (!this.worldObj.isRemote)
        {
			
			if(this.ticksExisted>600)
			{
				this.setDead();
				
			}
			
            World world= this.worldObj;
            double xOffset;
            double zOffset;
            if(this.motionX<0)
            {
            	xOffset=this.motionX-.25;
            }
            else
            {
            	xOffset=this.motionX+.25;
            }
            
            if(this.motionZ<0)
            {
            	zOffset=this.motionZ-.25;
            }
            else
            {
            	zOffset=this.motionZ+.25;
            }
            int x=(int)(Math.floor(this.posX));//+xOffset
            int y=(int)(Math.floor(this.posY) );
            int z=(int)(Math.floor(this.posZ));//+zOffset
            //this.motionY+=this.acc;
            
            /* TODO update to 1.8
            if (!(world.isAirBlock(x, y, z)) && !(world.getBlock(x, y, z) instanceof CannonBlock))
            {
            	
            	if(world.getBlock(x, y, z) instanceof BlockPolymer)
            	{
            		
            		double x1=this.posX-this.motionX;
            		double z1=this.posZ-this.motionZ;

            		
            		double Ux1=this.motionX;
            		double Uz1=this.motionZ;
            		
            		double U1= Math.sqrt((Ux1*Ux1+Uz1*Uz1));
            		
            		
            		Vector3 Vecx=new Vector3();
            		Vecx.set(1, 0, 0);

            		
            		Vector3 VecV1=new Vector3();
            		VecV1.set(Ux1, 0, Uz1);
            		
            		Vector3 VecImpact1=new Vector3();
            		VecImpact1.set((x-x1), 0, (z-z1));

            		
            		Vector3 VecImpact2=new Vector3();
            		VecImpact2.set((x1-x),0,(z1-z));
            		
            		double A1=Vecx.angle(VecImpact1);
            		double A2=Vecx.angle(VecImpact2);
            		//Vector3 test =VecV1.multiply(VecV1.mag());
            		double U12=-this.motionX;
            		double U11=-this.motionZ;
            	
            		
            		if(Uz1<0)
            		{
            			if(Ux1<0)
                		{
            				double test1=z;
            				double test2=z1;
            				double test3=this.motionZ;
                			if((z<(z1+this.motionZ)))
                			{
                				U11= -VecV1.mag()*Math.sin(A2);
                				U12= VecV1.mag()*Math.cos(A2);
                			}
                			else
                			{
                				U11= -VecV1.mag()*Math.sin(A2);
                				U12= VecV1.mag()*Math.cos(A2);
                			}
                		}
                		else
                		{
                			U11= -VecV1.mag()*Math.sin(A2);
                			U12=  VecV1.mag()*Math.cos(A2);
                		}
            			//U12= -VecV1.mag()*Math.cos(A2);
            			
            		}
            		else
            		{
            			if(Ux1<0)
                		{
            				if((z-1)<z1 && x+1<x1)
            				{
                        		VecImpact1.set((x-x1), 0, (z));
                        		VecImpact2.set((x1-x),0,(z));
                        		  
                        		A1=Vecx.angle(VecImpact1);
                        		A2=Vecx.angle(VecImpact2);
            					U11= VecV1.mag()*Math.sin(A1);
            					U12= -VecV1.mag()*Math.cos(A1);
            					U12=-this.motionX;
            					U11=this.motionZ;
            				}
            				else if((z-1)>z1 && x+1>x1)
            				{
                        		VecImpact1.set((x-x1), 0, (z));
                        		VecImpact2.set((x1-x),0,(z));
                        		  
                        		A1=Vecx.angle(VecImpact1);
                        		A2=Vecx.angle(VecImpact2);
            					U11= -VecV1.mag()*Math.sin(A2);
            					U12= -VecV1.mag()*Math.cos(A2);
            					U12=-this.motionX;
            					U11=-this.motionZ;
            				}
            				
                		}
                		else
                		{
                			U11= VecV1.mag()*Math.sin(A2);
                			U12= VecV1.mag()*Math.cos(A2);
                		}
            			//U12= VecV1.mag()*Math.cos(A2);
            			
            		}
            		
            	
            		
            		
            		this.motionX=U12;
            		this.motionZ=U11;
            		
            		//this.motionX=-this.motionX;
            		//this.motionZ=-this.motionZ;
            	}
            	else
            	{
            	
	            	if(world.getBlock(x, y, z) instanceof HPBlock)
	            	{
	            		TileEntityHPBlock tile =((TileEntityHPBlock)world.getTileEntity(x, y, z));
	            		
	            		double velocity=Math.sqrt(this.motionX*this.motionX + this.motionY*this.motionY + this.motionZ*this.motionZ)*400;
	            		double impulse=(20*20*this.mass*velocity);
	            		double keneticEngergy=(.5*this.mass*velocity*velocity);
	            		tile.setHP(tile.getHP()-keneticEngergy);
	            		if(tile.getHP()<=0)
	            		{
	            			world.createExplosion(this, x, y, z, 0, true);
	            			world.setBlock(x, y, z, Blocks.air);
	            			for (int i = 0; i < 8; ++i)
	            	        {
	            	        	PolyParticleSpawner.EntityBreakingParticle(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(HP_BLOCK)), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
	            	           
	            	        }
	            		}
	            		else
	            		{
	            			
	            		}
	            	}

	            	this.setDead();
            	}
               
            }

            
            Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
            vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null)
            {
                vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }
            
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
            
            
            if (list != null && !list.isEmpty())
            {
                for (int k1 = 0; k1 < list.size(); ++k1)
                {
                    Entity entity = (Entity)list.get(k1);

                    if (entity instanceof EntityIronCannonBall)
                    {
                        ((EntityIronCannonBall)entity).applyEntityCollision(this);
                    }
                }
            }
            
            

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) + 90.0F;

            for (this.rotationPitch = (float)(Math.atan2((double)f1, this.motionY) * 180.0D / Math.PI) - 90.0F; this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
            this.setPosition(this.posX, this.posY, this.posZ);
    */
            
        }
	}
	
	@Override
	public void applyEntityCollision(Entity entity)
	{
		double m1=this.mass;
		double m2=((EntityIronCannonBall) entity).mass;
		
		double x1=this.posX-this.motionX;
		double z1=this.posZ-this.motionZ;
		double x2=entity.posX-entity.motionX;
		double z2=entity.posZ-entity.motionZ;
		
		double Ux1=this.motionX;
		double Uz1=this.motionZ;
		double Ux2=entity.motionX;
		double Uz2=entity.motionZ;
		
		double U1= Math.sqrt((Ux1*Ux1+Uz1*Uz1));
		double U2= Math.sqrt((Ux2*Ux2+Uz2*Uz2));
		
		/* TODO: update to 1.8
		Vector3 Vecx=new Vector3();
		Vecx.set(1, 0, 0);

		
		Vector3 VecV1=new Vector3();
		VecV1.set(Ux1, 0, Uz1);

		
		Vector3 VecV2=new Vector3();
		VecV2.set(Ux2, 0, Uz2);

		
		Vector3 VecImpact1=new Vector3();
		VecImpact1.set((x2-x1), 0, (z2-z1));

		
		Vector3 VecImpact2=new Vector3();
		VecImpact2.set((x1-x2),0,(z1-z2));
		
		double A1=VecV1.angle(VecImpact1);
		double A2=VecV2.angle(VecImpact2);
		
		
		double U12= VecV1.mag()*Math.cos(A1);
		double U11= VecV1.mag()*Math.sin(A1);
		


		double U21= VecV2.mag()*Math.cos(A2);
		double U22= VecV2.mag()*Math.sin(A2);
		
		double test1 = (1/(m1+m2));
		double test2 = (m1-m2)*U12;
		double test3 = -2*m2*U21;
		
		double test4=test1*(test2+test3);

		double V12=Math.abs( (1/(m1+m2))*((m1-m2)*U12 -2*m2*U21) );
		double V21=Math.abs( (1/(m1+m2))*((m1-m2)*U21 +2*m1*U12) );
		
		
		Vector3 VecU12=VecImpact1.normalize();
		VecU12.multiply(U12);
		
		Vector3 VecU21=VecImpact2.normalize();
		VecU21.multiply(U21);
		
		Vector3 VecU11=new Vector3();
		VecU11.set(VecV1);
		VecU11.subtract(VecU12);
		
		
		Vector3 VecU22=new Vector3();
		VecU22.set(VecV2);
		VecU22.subtract(VecU21);
		
		Vector3 VecV12=VecImpact2.normalize();
		VecV12.multiply(V12);
		
		Vector3 VecV21=VecImpact1.normalize();
		VecV21.multiply(V21);
		
		
		Vector3 Vec1=VecU11.add(VecV12);
		Vector3 Vec2=VecU22.add(VecV21);
		
		this.motionX=Vec1.x;
		this.motionZ=Vec1.z;
		entity.motionX=Vec2.x;
		entity.motionZ=Vec2.z;
		
        this.posX += this.motionX;
        //this.posY += this.motionY;
        this.posZ += this.motionZ;
        
        entity.posX += entity.motionX;
        //entity.posY += entity.motionY;
        entity.posZ += entity.motionZ;
        
		
		World world= entity.worldObj;
		//MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Ball 1: X:"+Vec1.x+" , Z: "+Vec1.z));
		//MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Ball 2: X:"+Vec2.x+" , Z: "+Vec2.z));
		*/
		
				
	}
	
	public static final void register(final PolycraftEntity polycraftEntity) {
		EntityIronCannonBall.config = polycraftEntity;
		PolycraftEntityLiving.register(EntityIronCannonBall.class, config.entityID, config.name);
	}

}
