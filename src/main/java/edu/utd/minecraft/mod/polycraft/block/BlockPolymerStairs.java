package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerStairs;

public class BlockPolymerStairs extends BlockStairs implements BlockBouncy {

	public final PolymerStairs polymerStairs;
	private final BlockPolymerHelper helper;

	public BlockPolymerStairs(final PolymerStairs polymerStairs) {
		super(PolycraftRegistry.getBlock(polymerStairs.source.name).getDefaultState());
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymerStairs = polymerStairs;
		this.helper = new BlockPolymerHelper(polymerStairs.source.source.source, 15);
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
	public int getActiveBounceHeight() {
		return polymerStairs.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {
		return PolycraftRegistry.getItem(polymerStairs.itemStairsName);
	}

	@Override
	protected ItemStack createStackedBlock(IBlockState state) {
		return new ItemStack(PolycraftRegistry.getItem(polymerStairs.name), 2, getMetaFromState(state) & 7);
	}

//	@Override
//	protected void dropBlockAsItem(World world, BlockPos pos, IBlockState state, int fortune)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(itemstack);
//		super.dropBlockAsItem(world, x, y, z, itemstack);
//	}

//	@Override
//	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
//	{
//		ItemStack polycraftItemStack = super.getPickBlock(target, world, x, y, z);
//		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
//		return polycraftItemStack;
//
//	}

	public String getUnlocalizedName(int meta) {
		return polymerStairs.name;
	}
}