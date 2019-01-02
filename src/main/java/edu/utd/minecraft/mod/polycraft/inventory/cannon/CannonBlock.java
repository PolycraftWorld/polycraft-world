package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class CannonBlock extends PolycraftInventoryBlock {
	
    @SideOnly(Side.CLIENT)
    protected IIcon icon1;
    @SideOnly(Side.CLIENT)
    protected IIcon icon2;
    @SideOnly(Side.CLIENT)
    protected IIcon icon3;

	public CannonBlock(Inventory config, Class tileEntityClass) {
		super(config, tileEntityClass);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float what, float these, float are)
	{

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
            double d1 = (double)((float)y + 0.2F);
            double d2 = z + (double)enumfacing.getFrontOffsetZ();

        	EntityIronCannonBall cannonBall;
        	cannonBall = new EntityIronCannonBall(world);
        	cannonBall.forceSpawn=true;
        	
        	cannonBall.setPosition((double)d0+.5, (double)d1, (double)d2+.5);
            world.spawnEntityInWorld(cannonBall);
            
            if(d0<x)
            {
            	cannonBall.motionX=-0.1;
            	world.getClosestPlayerToEntity(cannonBall, 50).addChatMessage(new ChatComponentText("Iron: West, .1"));
            }
            else if(d0>x)
            {
            	cannonBall.motionX=0.1;
            	world.getClosestPlayerToEntity(cannonBall, 50).addChatMessage(new ChatComponentText("Iron: East, .1"));
            }
            else if(d2<z)
            {
            	cannonBall.motionZ=-0.1;
            	world.getClosestPlayerToEntity(cannonBall, 50).addChatMessage(new ChatComponentText("Iron: North, .1"));
            }
            else if(d2>z)
            {
            	cannonBall.motionZ=0.1;
            	world.getClosestPlayerToEntity(cannonBall, 50).addChatMessage(new ChatComponentText("Iron: South, .1"));
            }

        }
    }

	
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (world.isBlockIndirectlyGettingPowered(x, y, z) && world.getBlockPowerInput(x,y,z)>=1 )
        {
        	world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));

        }
    }
	
    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int k = meta & 7;
        return side == k ? (k != 1 && k != 0 ? this.icon2 : this.icon3) : (k != 1 && k != 0 ? (side != 1 && side != 0 ? this.blockIcon : this.icon1) : this.icon1);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.blockIcon = p_149651_1_.registerIcon("furnace_side");
        this.icon1 = p_149651_1_.registerIcon("furnace_top");
        this.icon2 = p_149651_1_.registerIcon(this.getTextureName() + "_front_horizontal");
        this.icon3 = p_149651_1_.registerIcon(this.getTextureName() + "_front_vertical");
    }
	

}
