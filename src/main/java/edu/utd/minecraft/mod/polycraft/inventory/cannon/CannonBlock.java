package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import java.lang.reflect.Type;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.EntityPellet__Old;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class CannonBlock extends PolycraftInventoryBlock {
	
   // @SideOnly(Side.CLIENT)
    //protected IIcon icon1;
   //@SideOnly(Side.CLIENT)
    //protected IIcon icon2;
   // @SideOnly(Side.CLIENT)
    //protected IIcon icon3;


    
	public CannonBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass);
		//this.setBlockName("Cannon");
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState state, EntityPlayer player, EnumFacing facing, float what, float these, float are)
	{
		super.onBlockActivated(world, blockPos, state, player, facing, what, these, are);
		return false;
	}
	
	

    /**
     * Ticks the block if it's been scheduled
     */
	
	
	
	
    public void updateTick(World world, BlockPos blockPos, IBlockState state, Random p_149674_5_)
    {
        if (!world.isRemote)
        {

        	int meta= this.getMetaFromState(world.getBlockState(blockPos));
        	EnumFacing enumfacing = EnumFacing.getFront(meta);

            CannonInventory tileEntity=(CannonInventory) this.getInventory(world, blockPos);
            double velocity=tileEntity.velocity;
            double theta=tileEntity.theta;
            double mass=tileEntity.mass;

            //cannonBall.mass=mass;
    
            double rad = -theta/180*Math.PI;
          
            
            
        	EntityIronCannonBall cannonBall;
        	cannonBall = new EntityIronCannonBall(world);
        	cannonBall.forceSpawn=true;
        	
            double x1= 1.1*Math.cos(rad);
            double z1= 1.1*Math.sin(rad);
        	
        	cannonBall.setPosition((double)blockPos.getX()+.5+x1, (double)blockPos.getY()+.5, (double)blockPos.getZ()+.5+z1);
            world.spawnEntityInWorld(cannonBall);
            
            cannonBall.mass=mass;
            
            cannonBall.motionX=velocity*Math.cos(rad)/20;
            
            cannonBall.motionZ=velocity*Math.sin(rad)/20;
            
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
    
//    @Override
//    public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
//    {
//        return 0;
//    }
	
    public void onNeighborBlockChange(World world, BlockPos blockPos, IBlockState state, Block block)
    {
        if (world.isBlockIndirectlyGettingPowered(blockPos) >=1)
        {
        	
        	world.scheduleBlockUpdate(blockPos, this, this.tickRate(world), 1);
        	
        }
    }
    
    @Override
    public int tickRate(World p_149738_1_)
    {
        return 40;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(int side, int meta)
//    {
//        int k = meta & 7;
//        return side == k ? (k != 1 && k != 0 ? this.icon2 : this.icon3) : (k != 1 && k != 0 ? (side != 1 && side != 0 ? this.blockIcon : this.icon1) : this.icon1);
//    }
//
//    @SideOnly(Side.CLIENT)
//    public void registerBlockIcons(IIconRegister p_149651_1_)
//    {
//        this.blockIcon = p_149651_1_.registerIcon("furnace_side");
//        this.icon1 = p_149651_1_.registerIcon("furnace_top");
//        this.icon2 = p_149651_1_.registerIcon(this.getTextureName() + "_front_horizontal");
//        this.icon3 = p_149651_1_.registerIcon(this.getTextureName() + "_front_vertical");
//    }
	

}
