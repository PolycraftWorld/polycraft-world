package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import java.util.Random;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityGravelCannonBall;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class GravelCannonBlock extends CannonBlock {
	
	public boolean useVelocity;
	public boolean useYaw;
	public boolean usePitch;

	public GravelCannonBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass);
		this.setBlockName("this.config.name");
		this.useVelocity=this.config.params.getBoolean(0);
		this.useYaw=this.config.params.getBoolean(1);
		this.usePitch=this.config.params.getBoolean(2);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random p_149674_5_)
    {
        if (!world.isRemote)
        {
        	
        	int meta= world.getBlockMetadata(x, y, z);
        	EnumFacing enumfacing = EnumFacing.getFront(meta);
            double d0 = x + (double)enumfacing.getFrontOffsetX();
            
            double d2 = z + (double)enumfacing.getFrontOffsetZ();

            GravelCannonInventory tileEntity=(GravelCannonInventory) this.getInventory(world, x, y, z);
            double velocity=tileEntity.velocity;
            double theta=tileEntity.theta;
            double mass=tileEntity.mass;
            double phi=tileEntity.phi;
            double phi2=-phi+90;
            double pitch=((phi2)/180*Math.PI);
    
            double rad = -theta/180*Math.PI;
           // if(phi%360==90)
            
            
        	EntityGravelCannonBall cannonBall;
        	cannonBall = new EntityGravelCannonBall(world);
        	cannonBall.forceSpawn=true;
        	
            double x1= 1.1*Math.sin(pitch)*Math.cos(rad);
            double z1= 1.1*Math.sin(pitch)* Math.sin(rad);
            double y1= 1.1*Math.cos(pitch);
        	
        	cannonBall.setPosition((double)x+.5+x1, (double)y+.5+y1, (double)z+.5+z1);
            world.spawnEntityInWorld(cannonBall);
            
            cannonBall.mass=mass;
            
            cannonBall.motionX=velocity*Math.sin(pitch)*Math.cos(rad)/20;
            cannonBall.motionY=velocity*Math.cos(pitch)/20;
            
            cannonBall.motionZ=velocity*Math.sin(pitch)*Math.sin(rad)/20;
            
//            
            
//            if(d0<x)
//            {
//            	cannonBall.motionX=-0.1;
//            	//MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Iron: West, .1"));
//            	
//            }
//            else if(d0>x)
//            {
//            	cannonBall.motionX=0.1;
//            	//MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Iron: East, .1"));
//            }
//            else if(d2<z)
//            {
//            	cannonBall.motionZ=-0.1;
//            	//MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Iron: North, .1"));
//            }
//            else if(d2>z)
//            {
//            	cannonBall.motionZ=0.1;
//            	MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Iron: South, .1"));
//            }

        }
    }

}
