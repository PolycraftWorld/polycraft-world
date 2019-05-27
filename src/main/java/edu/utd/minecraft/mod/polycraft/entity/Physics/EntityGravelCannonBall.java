package edu.utd.minecraft.mod.polycraft.entity.Physics;

import java.util.List;

//import codechicken.lib.vec.Vector3;
//import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymer;
import edu.utd.minecraft.mod.polycraft.block.HPBlock;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonBlock;
import edu.utd.minecraft.mod.polycraft.render.PolyParticleSpawner;
import edu.utd.minecraft.mod.polycraft.tileentity.TileEntityHPBlock;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityGravelCannonBall extends EntityIronCannonBall {
	
	public double acc=-9.8/200;
	private final static String HP_BLOCK = "1hH";
	
	public EntityGravelCannonBall(World p_i1582_1_) {
		super(p_i1582_1_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onUpdate()
	{
		this.onEntityUpdate();
       

		if (!this.worldObj.isRemote)
        {
			
			if(this.ticksExisted>600)
			{
				this.setDead();
				
			}
			
            World world= this.worldObj;
            double xOffset;
            double yOffset;
            double zOffset;
            if(this.motionX<0)
            {
            	xOffset=this.motionX-.25;
            }
            else
            {
            	xOffset=this.motionX+.25;
            }
            if(this.motionY<0)
            {
            	yOffset=this.motionY-.25;
            }
            else
            {
            	yOffset=this.motionY+.25;
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
            this.motionY+=this.acc;
            
            /* TODO: update to 1.8
            if (!(world.isAirBlock(x, y, z)) && !(world.getBlock(x, y, z) instanceof CannonBlock))
            {
            	
            	if(world.getBlock(x, y, z) instanceof BlockPolymer)
            	{
            		
            		double x1=this.posX-this.motionX;
            		//double y1=this.posY-this.motionY;
            		double z1=this.posZ-this.motionZ;

            		
            		double Ux1=this.motionX;
            		//double Uy1=this.motionY;
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
		else
		{
			 for (int i = 0; i < 4; ++i)
		        {
				 	PolyParticleSpawner.EntityCritParticle(this.posX + this.motionX * (double)i / 4.0D, this.posY + this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
		        }
		}
	}

}
