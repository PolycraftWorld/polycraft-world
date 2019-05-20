package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;

public class BlockPolymer extends Block implements BlockBouncy {

	public final PolymerBlock polymerBlock;
	private final BlockPolymerHelper helper;

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

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		helper.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return helper.damageDropped(state);
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

	public String getUnlocalizedName(int colorIndex) {
		return polymerBlock.gameID + "." + colorIndex;
	}
}