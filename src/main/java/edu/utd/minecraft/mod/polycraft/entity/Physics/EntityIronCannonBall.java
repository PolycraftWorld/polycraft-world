package edu.utd.minecraft.mod.polycraft.entity.Physics;

import java.util.List;

import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityDummy;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.PolycraftEntityLiving;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
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
	
	double mass=5.0;
	private static PolycraftEntity config;
	

	public EntityIronCannonBall(World p_i1582_1_) {
		super(p_i1582_1_);
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

	
	public void onUpdate()
	{
		super.onUpdate();
		
		if (!this.worldObj.isRemote)
        {
			
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
            World world= this.worldObj;
            int x=(int) this.posX;
            int y=(int) this.posY;
            int z=(int) this.posZ;
            
            
            if (!(world.isAirBlock(x, y, z)))
            {
                this.setDead();
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
    
            
        }
	}
	
	@Override
	public void applyEntityCollision(Entity entity)
	{
		double m1=this.mass;
		double m2=((EntityIronCannonBall) entity).mass;
		
		double x1=this.posX;
		double z1=this.posZ;
		double x2=entity.posX;
		double z2=entity.posZ;
		
		double Ux1=this.motionX;
		double Uz1=this.motionZ;
		double Ux2=entity.motionX;
		double Uz2=entity.motionZ;
		
		double U1= Math.sqrt((Ux1*Ux1+Uz1*Uz1));
		double U2= Math.sqrt((Ux2*Ux2+Uz2*Uz2));
		
//		double U1ang= Math.atan((Uz1/Ux1));
//		if(Ux1<0)
//			U1ang=U1ang+Math.PI;
//		double U2ang= Math.atan((Uz2/Ux2));
//		if(Ux2<0)
//			U2ang=U2ang+Math.PI;
//		
		
		double a1= Math.atan2(z2-z1, x2-x1);
		double b1= Math.atan2(Uz1,Ux1);
		double a2= Math.atan2(z1-z2, x1-x2);
		double b2= Math.atan2(Uz2,Ux2);
		double c1= b1-a1;
		double c2= b2-a2;
		
		double U12=U1*Math.cos(c1);
		double U11=U1*Math.sin(c1);
		double U21=U2*Math.cos(c2);
		double U22=U2*Math.sin(c2);
		
		double V12=(1/(m1+m2))*((m1-m2)*U12 -2*m2*U21);
		double V21=(1/(m1+m2))*((m1-m2)*U21 +2*m2*U12);
		
		double V1=Math.sqrt((U11*U11+V12*V12));
		double V2=Math.sqrt((U22*U22+V21*V21));
		
		double V1ang= Math.atan((U11/V1));
		if(V1<0)
			V1ang=V1ang+Math.PI;
		V1ang=a1+((Math.PI)/2);
		double V2ang= Math.atan((U11/V2));
		if(V2<0)
			V2ang=V2ang+Math.PI;
		V2ang=a2+((Math.PI)/2);		
		
		double Vx1=V1*Math.cos(V1ang);
		double Vz1=V1*Math.sin(V1ang);
		double Vx2=V2*Math.cos(V2ang);
		double Vz2=V2*Math.sin(V2ang);
		
		this.motionX=Vx1;
		this.motionZ=Vz1;
		entity.motionX=Vx2;
		entity.motionZ=Vz2;
		
		World world= entity.worldObj;
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Ball 1: X:"+Vx1+" , Z: "+Vz1));
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Ball 2: X:"+Vx2+" , Z: "+Vz2));
		
	}
	
	public static final void register(final PolycraftEntity polycraftEntity) {
		EntityIronCannonBall.config = polycraftEntity;
		PolycraftEntityLiving.register(EntityIronCannonBall.class, config.entityID, config.name);
	}

}
