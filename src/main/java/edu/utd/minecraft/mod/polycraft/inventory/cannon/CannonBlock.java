package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import java.lang.reflect.Type;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.EntityPaintball;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
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
		this.setBlockName("Cannon");
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are)
	{
		super.onBlockActivated(world, x, y, z, player, metadata, what, these, are);
		return false;
	}
	
	

    /**
     * Ticks the block if it's been scheduled
     */
	
	
	
	
    public void updateTick(World world, int x, int y, int z, Random p_149674_5_)
    {
        if (!world.isRemote)
        {
        	
        	int meta= world.getBlockMetadata(x, y, z);
        	EnumFacing enumfacing = EnumFacing.getFront(meta);
            double d0 = x + (double)enumfacing.getFrontOffsetX();
            
            double d2 = z + (double)enumfacing.getFrontOffsetZ();

<<<<<<< HEAD
            EntityPaintball cannonBall;
        	cannonBall = new EntityPaintball(world);
        	cannonBall.forceSpawn=true;
        	
        	cannonBall.setPosition((double)d0+.5, (double)y+.5, (double)d2+.5);
            world.spawnEntityInWorld(cannonBall);
            
=======
>>>>>>> refs/remotes/origin/PhysicsCannonInput
            CannonInventory tileEntity=(CannonInventory) this.getInventory(world, x, y, z);
            double velocity=tileEntity.velocity;
            double theta=tileEntity.theta;
            double mass=tileEntity.mass;
            
<<<<<<< HEAD
            //cannonBall.mass=mass;
=======
    
            double rad = -theta/180*Math.PI;
          
            
            
        	EntityIronCannonBall cannonBall;
        	cannonBall = new EntityIronCannonBall(world);
        	cannonBall.forceSpawn=true;
        	
            double x1= 1.1*Math.cos(rad);
            double z1= 1.1*Math.sin(rad);
        	
        	cannonBall.setPosition((double)x+.5+x1, (double)y+.5, (double)z+.5+z1);
            world.spawnEntityInWorld(cannonBall);
            
            cannonBall.mass=mass;
>>>>>>> refs/remotes/origin/PhysicsCannonInput
            
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
	
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (world.isBlockIndirectlyGettingPowered(x, y, z) && world.getBlockPowerInput(x,y,z)>=1 )
        {
        	
        	world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
        	
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
