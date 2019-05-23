package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import java.util.Random;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityGravelCannonBall;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;

public class GravelCannonBlock extends CannonBlock {
	
	public boolean useVelocity;
	public boolean useYaw;
	public boolean usePitch;
	private final static String ItemGravelCannonBall = "1hS";

	public GravelCannonBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass);
		//this.setBlockName("this.config.name");
		this.useVelocity=this.config.params.getBoolean(0);
		this.useYaw=this.config.params.getBoolean(1);
		this.usePitch=this.config.params.getBoolean(2);
	}
	
	@Override
	public void updateTick(World world, BlockPos blockPos, IBlockState state, Random p_149674_5_)
    {
        if (!world.isRemote)
        {
        	if(this.getInventory(world, blockPos).slotHasItem(this.getInventory(world, blockPos).getInputSlots().get(0)))
        	{
		        if( this.getInventory(world, blockPos).getStackInSlot(0).getItem()== GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(ItemGravelCannonBall)))
		        {
		        	this.getInventory(world, blockPos).decrStackSize(0, 1);
		        	
		        	int meta= this.getMetaFromState(world.getBlockState(blockPos));
		        	EnumFacing enumfacing = EnumFacing.getFront(meta);
		
		            GravelCannonInventory tileEntity=(GravelCannonInventory) this.getInventory(world, blockPos);
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
		        	
		        	cannonBall.setPosition((double)blockPos.getX()+.5+x1, (double)blockPos.getY()+.5+y1, (double)blockPos.getZ()+.5+z1);
		            world.spawnEntityInWorld(cannonBall);
		            
		            cannonBall.mass=mass;
		            
		            cannonBall.motionX=velocity*Math.sin(pitch)*Math.cos(rad)/20;
		            cannonBall.motionY=velocity*Math.cos(pitch)/20;
		            
		            cannonBall.motionZ=velocity*Math.sin(pitch)*Math.sin(rad)/20;
		        }
        	}


        }
    }

}
