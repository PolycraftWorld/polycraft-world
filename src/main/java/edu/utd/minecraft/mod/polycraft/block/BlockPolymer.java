package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper.EnumColor;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;

public class BlockPolymer extends Block implements BlockBouncy {

	public final PolymerBlock polymerBlock;
	private final BlockPolymerHelper helper;
	
	public static final PropertyEnum PROPERTYCOLOR = PropertyEnum.create("color", EnumColor.class);

	public BlockPolymer(final PolymerBlock polymerBlock) {
		super(Material.cloth);
		//super(PolycraftMaterial.plasticWhite);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymerBlock = polymerBlock;
		this.setHardness(5);
		this.helper = new BlockPolymerHelper(polymerBlock.source.source, -1);
	}

//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int colorIndex) {
//		return helper.getIcon(side, colorIndex);
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister p_149651_1_) {
//		helper.registerBlockIcons(p_149651_1_);
//	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
		helper.getSubBlocks(p_149666_1_, p_149666_2_, p_149666_3_);
	}

	@Override
	public int getActiveBounceHeight() {
		return polymerBlock.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return helper.getMomentumReturnedOnPassiveFall();
	}

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(PROPERTYCOLOR, EnumColor.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumColor)state.getValue(PROPERTYCOLOR)).getMetadata();
    }
    
//	@Override
//	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
//		helper.onBlockPlacedBy(world, pos, state, placer, stack);
//	}
    
 // when the block is placed, set the appropriate facing direction based on which way the player is looking
    // the color of block is contained in meta, it corresponds to the values we used for getSubBlocks
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
      EnumColor color = EnumColor.byMetadata(meta);

      return this.getDefaultState().withProperty(PROPERTYCOLOR, color);
    }
	

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {PROPERTYCOLOR});
    }

 // this function returns the correct item type corresponding to the colour of our block;
    // i.e. when a sign is broken, it will drop the correct item.  Ignores Facing, because we get the same item
    //   no matter which way the block is facing
    @Override
    public int damageDropped(IBlockState state)
    {
      EnumColor enumColor = (EnumColor)state.getValue(PROPERTYCOLOR);
      return enumColor.getMetadata();
    }

//	@Override
//	public void dropBlockAsItem(World world, BlockPos pos, IBlockState state, int forture)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(itemstack);
//		super.dropBlockAsItem(world, x, y, z, itemstack);
//	}

//	@Override
//	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player )
//	{
//		ItemStack polycraftItemStack = super.getPickBlock(target, world, pos, player);
//		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
//		return polycraftItemStack;
//
//	}

	@Override
	public String getUnlocalizedName() {
		// TODO Auto-generated method stub
		return super.getUnlocalizedName();
	}
}