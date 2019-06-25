package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper.EnumColor;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class BlockCollision extends BlockPolyDirectional {
	
//	@SideOnly(Side.CLIENT)
//	public IIcon iconFront;

	protected final InternalObject config;

	public BlockCollision(final InternalObject config) {
		super(Material.iron);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM));
		this.config = config;
		this.setHardness(7.5F);
		this.setResistance(10.0F);
		// TODO Auto-generated constructor stub
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isFullCube()
	{
		return false;
	}
	
	@Override
    public int getRenderType()
    {
        return -1;
    }

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess p_149655_1_, BlockPos blockPos)
	{
		return false;
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
	 */
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
	{
		return true;
	}
	

//	/**
//     * Convert the given metadata into a BlockState for this Block
//     */
//    public IBlockState getStateFromMeta(int meta)
//    {
//        IBlockState iblockstate = this.getDefaultState().withProperty(VERTICAL, (meta & 4) > 0 ? EnumFacing.UP : EnumFacing.DOWN);
//        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
//        return iblockstate;
//    }
//
//    /**
//     * Convert the BlockState into the correct metadata value
//     */
//    public int getMetaFromState(IBlockState state)
//    {
//        int i = 0;
//
//        if (state.getValue(VERTICAL) == EnumFacing.UP)
//        {
//            i |= 4;
//        }
//
//        i = i | 5 - ((EnumFacing)state.getValue(FACING)).getIndex();
//        return i;
//    }
//
//    protected BlockState createBlockState()
//    {
//        return new BlockState(this, new IProperty[] {FACING, VERTICAL});
//    }

	//0 width length and height box so no wireframe rendered.
	//	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	//	{
	//		return AxisAlignedBB.getBoundingBox((double) par2, (double) par3, (double) par4, (double) par2, (double) par3, (double) par4);
	//	}

	@Override
	public boolean onBlockActivated(World worldObj, BlockPos blockPos, IBlockState state, EntityPlayer player, EnumFacing facing, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		if (facing == EnumFacing.DOWN)
		{
			worldObj.getBlockState(blockPos.down()).getBlock().onBlockActivated(worldObj, blockPos.down(), state, player, facing, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		else if (facing == EnumFacing.EAST)
		{
			worldObj.getBlockState(blockPos.east()).getBlock().onBlockActivated(worldObj, blockPos.east(), state, player, facing, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		else if (facing == EnumFacing.WEST)
		{
			worldObj.getBlockState(blockPos.west()).getBlock().onBlockActivated(worldObj, blockPos.west(), state, player, facing, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		else if (facing == EnumFacing.NORTH)
		{
			worldObj.getBlockState(blockPos.north()).getBlock().onBlockActivated(worldObj, blockPos.north(), state, player, facing, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		else if (facing == EnumFacing.SOUTH)
		{
			worldObj.getBlockState(blockPos.north()).getBlock().onBlockActivated(worldObj, blockPos.north(), state, player, facing, p_149727_7_, p_149727_8_, p_149727_9_);
			return true;
		}
		//TODO: throw some error here: misdefined inventory
		return false;

	}

	@Override
	public boolean canPlaceBlockAt(World p_149742_1_, BlockPos blockPos)
	{
		return false;
	}

	static public TileEntity findConnectedInventory(World worldObj, BlockPos blockPos)
	{
		EnumFacing dir = (EnumFacing) worldObj.getBlockState(blockPos).getProperties().get(FACING);
		TileEntity target = null;

		target = worldObj.getTileEntity(blockPos.offset(dir));
		
		BlockPos pos = blockPos.offset(dir);
		if (target != null) {
			return target;
		}

		return findConnectedInventory(worldObj, blockPos.offset(dir));

	}

	@Override
	public void breakBlock(World world, BlockPos blockPos, IBlockState state)
	{
		breakBlockRecurse(world, blockPos, state);
	}

	public void breakBlockRecurse(World world, BlockPos blockPos, IBlockState state)
	{
		//world.setBlockToAir(x, y, z);
		world.destroyBlock(blockPos, false);
		//get the specific neighbor that this block points to and break it if it is an Collision Block or the inventory
		EnumFacing dir = EnumFacing.getFront(state.getBlock().getMetaFromState(state));

		IBlockState neighbor = world.getBlockState(blockPos.offset(dir)); //follow the way it is pointing
		if (!world.isRemote)
		{
			if (neighbor.getBlock() instanceof BlockCollision)
				((BlockCollision) neighbor.getBlock()).breakBlockRecurse(world, blockPos.offset(dir), neighbor);
			if (neighbor.getBlock() instanceof PolycraftInventoryBlock)
				((PolycraftInventoryBlock) neighbor.getBlock()).breakBlockRecurse(world, blockPos.offset(dir), neighbor, true);
		}


		//get each neighbor if it is a BlockCollision facing this block, then destroy it too. 
		//this eliminates the network going away from the inventory

		//neighbor = world.getBlock(x + 1, y, z);
		if ((neighbor = world.getBlockState(blockPos.east())).getBlock() instanceof BlockCollision)
			if ((dir = (EnumFacing) world.getBlockState(blockPos.east()).getProperties().get(FACING)) == EnumFacing.WEST)
				((BlockCollision) neighbor.getBlock()).breakBlockRecurse(world, blockPos.east(), neighbor);

		if ((neighbor = world.getBlockState(blockPos.west())).getBlock() instanceof BlockCollision)
			if ((dir = (EnumFacing) world.getBlockState(blockPos.west()).getProperties().get(FACING)) == EnumFacing.EAST)
				((BlockCollision) neighbor.getBlock()).breakBlockRecurse(world, blockPos.west(), neighbor);

		if ((neighbor = world.getBlockState(blockPos.south())).getBlock() instanceof BlockCollision)
			if ((dir = (EnumFacing) world.getBlockState(blockPos.south()).getProperties().get(FACING)) == EnumFacing.NORTH)
				((BlockCollision) neighbor.getBlock()).breakBlockRecurse(world, blockPos.south(), neighbor);

		if ((neighbor = world.getBlockState(blockPos.north())).getBlock() instanceof BlockCollision)
			if ((dir = (EnumFacing) world.getBlockState(blockPos.north()).getProperties().get(FACING)) == EnumFacing.SOUTH)
				((BlockCollision) neighbor.getBlock()).breakBlockRecurse(world, blockPos.north(), neighbor);

		if ((neighbor = world.getBlockState(blockPos.up())).getBlock() instanceof BlockCollision)
			if (world.getBlockState(blockPos.up()).getProperties().get(HALF) == EnumHalf.BOTTOM) {
				dir = EnumFacing.DOWN;
				((BlockCollision) neighbor.getBlock()).breakBlockRecurse(world, blockPos.up(), neighbor);
			}

		if ((neighbor = world.getBlockState(blockPos.down())).getBlock() instanceof BlockCollision)
			if (world.getBlockState(blockPos.up()).getProperties().get(HALF) == EnumHalf.TOP) {
				dir = EnumFacing.UP;
				((BlockCollision) neighbor.getBlock()).breakBlockRecurse(world, blockPos.down(), neighbor);
			}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune)
	{
		return null;
	}

	/**
	 * Gets the block's texture. Args: side, meta -- removed in 1.8
	 */
//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int metaData)
//	{
//		return this.iconFront;
//
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister p_149651_1_)
//	{
//		this.iconFront = p_149651_1_.registerIcon(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name)));
//	}

}