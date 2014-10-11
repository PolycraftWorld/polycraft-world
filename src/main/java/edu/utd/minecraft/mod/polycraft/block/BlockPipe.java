package edu.utd.minecraft.mod.polycraft.block;

import java.lang.reflect.Proxy;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.client.RenderIDs;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.inventory.BlockFace;
import edu.utd.minecraft.mod.polycraft.inventory.pump.Flowable;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import edu.utd.minecraft.mod.polycraft.render.TileEntityBlockPipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

//TODO replace getIcon and registerBlockIcons with custom rendering
public class BlockPipe extends Block implements ITileEntityProvider, Flowable{

	//private final Map<BlockFace, IIcon> blockFaceIcons = Maps.newHashMap();
	private static final Logger logger = LogManager.getLogger();
	
   // private final Random field_149922_a = new Random();
    @SideOnly(Side.CLIENT)
	public IIcon iconOutside;
    @SideOnly(Side.CLIENT)
    public IIcon iconInside;
    
    @SideOnly(Side.CLIENT)
    public IIcon iconVertical;
    @SideOnly(Side.CLIENT)
    public IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    public IIcon iconRight;
    @SideOnly(Side.CLIENT)
    public IIcon iconHorizontal;
    @SideOnly(Side.CLIENT)
    public IIcon iconFront;
    @SideOnly(Side.CLIENT)
    public IIcon iconBack;
    
    @SideOnly(Side.CLIENT)
    public IIcon iconSolid;
    
    
    
    
    public final Class tileEntityClass;
    protected final InternalObject config;
    //private static final String __OBFID = "CL_00010000";
	
	public BlockPipe(final InternalObject config, final Class tileEntityClass) {
		super(Material.iron);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setStepSound(Block.soundTypeMetal);
		this.tileEntityClass = tileEntityClass;
		this.config = config;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int var1) {
		try {
			return (TileEntity) tileEntityClass.newInstance();
		} catch (Exception e) {
			logger.error("Can't create an instance of your tile entity: " + e.getMessage());
		}
		return null;
	}
 

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_, p_149743_7_);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
//    public int onBlockPlaced(World worldObj, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
//    {
//        int j1 = Facing.oppositeSide[p_149660_5_];
//
//        if (j1 == 1)
//        {
//            j1 = 0;
//        }
//
//        return j1;
//    }

    
	@Override
	public void onBlockPlacedBy(World worldObj, int xCoord, int yCoord, int zCoord, EntityLivingBase player, ItemStack itemStack) {
		BlockHelper.setFacingMetadata6(this, worldObj, xCoord, yCoord, zCoord, player, itemStack);
		setDirectionIn(worldObj, xCoord, yCoord, zCoord);
	}

	public void setDirectionIn(World worldObj, int xCoord, int yCoord,
			int zCoord) {
		TileEntityBlockPipe bte = (TileEntityBlockPipe) worldObj.getTileEntity(xCoord, yCoord, zCoord);
		
		for (ForgeDirection testdir: ForgeDirection.values())
		{
			if (isDirectionIn(worldObj,testdir, xCoord, yCoord, zCoord))
			{
				bte.setDirectionIn((short)testdir.ordinal()); 
				return;
			}
		}	
		bte.setDirectionIn((short)ForgeDirection.UNKNOWN.ordinal());
	}
	
	
	
	
	public boolean isDirectionIn(World worldObj, ForgeDirection testdir, int xCoord, int yCoord, int zCoord)
	{
		if ((worldObj.getBlock(xCoord+testdir.offsetX, yCoord+testdir.offsetY, zCoord+testdir.offsetZ) instanceof Flowable) && (worldObj.getBlockMetadata(xCoord+testdir.offsetX, yCoord+testdir.offsetY, zCoord+testdir.offsetZ)==testdir.getOpposite().ordinal()))
			return true;
		return false;		
	}
	


    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World worldObj, int xCoord, int yCoord, int zCoord)
    {
        super.onBlockAdded(worldObj, xCoord, yCoord, zCoord);
        //this.setMetaDataIfPoweredNeighbor(worldObj, xCoord, yCoord, zCoord);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        //do nothing upon right click TODO: for now...could display if this is a valid network    
    	return true;
      
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    @Override
    public void onNeighborBlockChange(World worldObj, int xCoord, int yCoord, int zCoord, Block p_149695_5_)
    {
    	//FIXME JM: I think we will need to do something more to get this to work...
    	this.setDirectionIn(worldObj, xCoord, yCoord, zCoord);

        //this.setMetaDataIfPoweredNeighbor(worldObj, xCoord, yCoord, zCoord);
    }

    private void setMetaDataIfPoweredNeighbor(World worldObj, int xCoord, int yCoord, int zCoord)
    {
//        int l = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
//        int i1 = getDirectionFromMetadata(l);
//        boolean flag = !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
//        boolean flag1 = isLargestMetaDataBitSet(l);
//
//        if (flag != flag1)
//        {
//            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, i1 | (flag ? 0 : 8), 4);
//        }
    }

    @Override
    public void breakBlock(World worldObj, int xCoord, int yCoord, int zCoord, Block block, int metaData)
    {
        super.breakBlock(worldObj, xCoord, yCoord, zCoord, block, metaData);
    }


    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        return true;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metaData)
    {
    	if (side == metaData)
        	return this.iconFront;
    	return this.iconSolid;
    }

    public static int getDirectionFromMetadata(int metaData)
    {
        return metaData & 7;
    }

    public static boolean isLargestMetaDataBitSet(int metaData)
    {
        return (metaData & 8) != 8;
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
//    public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
//    {
//        return Container.calcRedstoneFromInventory(func_149920_e(p_149736_1_, p_149736_2_, p_149736_3_, p_149736_4_));
//    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        
		this.iconOutside = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_outside")));
		this.iconInside = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_inside")));
		
		this.iconVertical = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_top")));
		this.iconBottom = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_bottom")));
		
		this.iconRight = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_right")));
		this.iconHorizontal = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_left")));
		
		this.iconFront = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_front")));
		this.iconBack = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_back")));
		
		this.iconSolid = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_solid")));

		
    }

    public static TileEntityBlockPipe getTileEntityBlockPipeAtXYZ(IBlockAccess blockAccess, int xCoord, int yCoord, int zCoord)
    {
        return (TileEntityBlockPipe)blockAccess.getTileEntity(xCoord, yCoord, zCoord);
    }

    /**
     * Gets the icon name of the ItemBlock corresponding to this block. Used by hoppers and pipes.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public String getItemIconName()
    {
        return "ItemPipe"; //not sure what to put here... TODO

    }
    

	@Override
	public int getRenderType() {

		return config.renderID; //http://www.minecraftforge.net/wiki/Multiple_Pass_Render_Blocks

	}

 
}