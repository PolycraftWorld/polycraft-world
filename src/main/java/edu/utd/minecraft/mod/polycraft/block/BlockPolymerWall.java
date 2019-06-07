package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerWall;

public class BlockPolymerWall extends BlockWall implements BlockBouncy {

	public final PolymerWall polymerWall;
	private final BlockPolymerHelper helper;

	public BlockPolymerWall(final PolymerWall polymerWall) {
		super(PolycraftRegistry.getBlock(polymerWall.source.name));
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymerWall = polymerWall;
		this.helper = new BlockPolymerHelper(polymerWall.source.source.source, -1);
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
		return polymerWall.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return 0;
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, BlockPos blockPos, IBlockState state, EntityLivingBase placer, ItemStack p_149689_6_) {
		helper.onBlockPlacedBy(p_149689_1_, blockPos, state, placer, p_149689_6_);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random p_149650_2_, int p_149650_3_) {
		return PolycraftRegistry.getItem(polymerWall.itemWallName);
	}

	@Override
	protected ItemStack createStackedBlock(IBlockState state) {
		return new ItemStack(PolycraftRegistry.getItem(polymerWall.name), 2, this.getMetaFromState(state) & helper.colors.length);
	}

	@Override
	public int damageDropped(IBlockState p_149692_1_) {
		return helper.damageDropped(p_149692_1_);
	}

//	@Override
//	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemstack)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(itemstack);
//		super.dropBlockAsItem(world, x, y, z, itemstack);
//	}
//
//	@Override
//	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
//	{
//		ItemStack polycraftItemStack = super.getPickBlock(target, world, x, y, z);
//		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
//		return polycraftItemStack;
//
//	}

	public String getUnlocalizedName(int colorIndex) {
		//return polymerWall.blockWallGameID + "." + colorIndex;
		return polymerWall.name + "." + colorIndex;
	}
}