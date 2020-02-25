package edu.utd.minecraft.mod.polycraft.block;

import edu.utd.minecraft.mod.polycraft.config.GameIdentifiedConfig;
import edu.utd.minecraft.mod.polycraft.config.InternalObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockPolyDirectional extends Block {

	protected final GameIdentifiedConfig config;

    public static final PropertyEnum<BlockStairs.EnumHalf> HALF = PropertyEnum.<BlockStairs.EnumHalf>create("half", BlockStairs.EnumHalf.class);
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public BlockPolyDirectional(final GameIdentifiedConfig config) {
		super(Material.iron);
		this.config = config;
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM));
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	/**
     * Convert the given metadata into a BlockState for this Block
     */
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
//		IBlockState iblockstate = this.getDefaultState();
//        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront((meta)));
//		switch(meta)
//		{
//		case 0:
//			
//	        iblockstate = iblockstate.withProperty(HALF, BlockStairs.EnumHalf.TOP);
//			break;
//		case 1:
//	        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront((meta)));	 
//			break;
//		case 2:
//	        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront((meta))); 
//			break;
//		case 3:
//	        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront((meta)));
//			break;
//		case 4:
//	        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront((meta))); 
//			break;
//		case 5:
//	        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront((meta)));
//			break;
//		default:
//	        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront((meta))); 
//			break;
//		}
//        return iblockstate;
		IBlockState iblockstate = this.getDefaultState().withProperty(HALF, (meta & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);
        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;

        if (state.getValue(HALF) == BlockStairs.EnumHalf.TOP)
        {
            i |= 4;
        }

        i = i | 5 - ((EnumFacing)state.getValue(FACING)).getIndex();
        return i;
    }
    
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, HALF});
    }
    
    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState iblockstate = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        iblockstate = iblockstate.withProperty(FACING, placer.getHorizontalFacing());
        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5D) ? iblockstate.withProperty(HALF, BlockStairs.EnumHalf.BOTTOM) : iblockstate.withProperty(HALF, BlockStairs.EnumHalf.TOP);
    }

    public static enum EnumHalf implements IStringSerializable
    {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        private EnumHalf(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }

}
