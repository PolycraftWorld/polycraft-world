package edu.utd.minecraft.mod.polycraft.inventory.treetap;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class TreeTapBlock extends PolycraftInventoryBlock {

	  public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>()
	    {
	        public boolean apply(EnumFacing p_apply_1_)
	        {
	            return p_apply_1_ != EnumFacing.UP;
	        }
	    });
	    public static final PropertyBool ENABLED = PropertyBool.create("enabled");


//	@SideOnly(Side.CLIENT)
//	public IIcon iconOutside;
//	@SideOnly(Side.CLIENT)
//	public IIcon iconTop;
//	@SideOnly(Side.CLIENT)
//	public IIcon iconInside;

	public TreeTapBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.wood, 2.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(ENABLED, Boolean.valueOf(true)));
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
//	{
//		return p_149691_1_ == 1 ? this.iconTop : this.iconOutside;
//	}

//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister p_149651_1_)
//	{
//		this.iconOutside = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_outside")));
//		this.iconTop = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_top")));
//		this.iconInside = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name + "_inside")));
//	}

	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

	/**
	 * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
	 */
//	@Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

	@Override
	public void onBlockPlacedBy(World worldObj, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemToPlace) {
	}

	/**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
	@Override
	public IBlockState onBlockPlaced(World p_149660_1_, BlockPos blockPos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing enumfacing = facing.getOpposite();

        if (enumfacing == EnumFacing.UP)
        {
            enumfacing = EnumFacing.DOWN;
        }

		 return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ENABLED, Boolean.valueOf(true));
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World worldObj, BlockPos blockPos, IBlockState state, Block neighborBlock)
	{
		this.notifyIfNeighborPowered(worldObj, blockPos);
	}

	//TODO: is the TREE Tap supposed to do something when powered???
	private void notifyIfNeighborPowered(World worldObj, BlockPos blockPos)
	{
		boolean flag = worldObj.isBlockPowered(blockPos);
//		int i1 = TreeTapInventory.getDirectionFromMetadata(l);
//		boolean flag = !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
//		boolean flag1 = isLargestMetaDataBitSet(l);

		//if (flag != flag1)
//		if(flag)
//		{
//			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, i1 | (flag ? 0 : 8), 4);
//		}
	}

	public static boolean isLargestMetaDataBitSet(int metaData)
	{
		return (metaData & 8) != 8;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube()
	{
		return false;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

//	/**
//	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
//	 */
//	@Override
//	@SideOnly(Side.CLIENT)
//	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
//	{
//		return true;
//	}

	/**
	 * If this returns true, then comparators facing away from this block will use the value from getComparatorInputOverride instead of the actual redstone signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal strength when this block inputs to a comparator.
	 */
	@Override
	public int getComparatorInputOverride(World worldIn, BlockPos blockPos)
	{
		return Container.calcRedstoneFromInventory(func_149920_e(worldIn, blockPos));
	}

	public static TreeTapInventory func_149920_e(IBlockAccess p_149920_0_, BlockPos blockPos)
	{
		return (TreeTapInventory) p_149920_0_.getTileEntity(blockPos);
	}

	/**
	 * Gets the icon name of the ItemBlock corresponding to this block. Used by tree taps.
	 */
//	@Override
//	@SideOnly(Side.CLIENT)
//	public String getItemIconName() {
//		return PolycraftMod.getAssetNameString(PolycraftMod.getFileSafeName(config.name));
//	}
	
	 public int getRenderType()
	    {
	        return 3;
	    }

	    public boolean isFullCube()
	    {
	        return false;
	    }

	    /**
	     * Used to determine ambient occlusion and culling when rebuilding chunks for render
	     */

	    @SideOnly(Side.CLIENT)
	    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	    {
	        return true;
	    }

	    public static EnumFacing getFacing(int meta)
	    {
	        return EnumFacing.getFront(meta & 7);
	    }

	    /**
	     * Get's the hopper's active status from the 8-bit of the metadata. Note that the metadata stores whether the block
	     * is powered, so this returns true when that bit is 0.
	     */
	    public static boolean isEnabled(int meta)
	    {
	        return (meta & 8) != 8;
	    }


	    @SideOnly(Side.CLIENT)
	    public EnumWorldBlockLayer getBlockLayer()
	    {
	        return EnumWorldBlockLayer.CUTOUT_MIPPED;
	    }

	    /**
	     * Convert the given metadata into a BlockState for this Block
	     */
	    public IBlockState getStateFromMeta(int meta)
	    {
	        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(ENABLED, Boolean.valueOf(isEnabled(meta)));
	    }

	    /**
	     * Convert the BlockState into the correct metadata value
	     */
	    public int getMetaFromState(IBlockState state)
	    {
	        int i = 0;
	        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

	        if (!((Boolean)state.getValue(ENABLED)).booleanValue())
	        {
	            i |= 8;
	        }

	        return i;
	    }

	    protected BlockState createBlockState()
	    {
	        return new BlockState(this, new IProperty[] {FACING, ENABLED});
	    }
}
