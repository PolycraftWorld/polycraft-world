package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerStairs;

public class BlockPolymerStairs extends BlockStairs implements BlockBouncy {

	public final PolymerStairs polymerStairs;
	private final BlockPolymerHelper helper;

	public BlockPolymerStairs(final PolymerStairs polymerStairs, int meta) {
		super(PolycraftRegistry.getBlock(polymerStairs.source.name), meta);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymerStairs = polymerStairs;
		this.helper = new BlockPolymerHelper(polymerStairs.source.source.source, 15);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int colorIndex) {
		return helper.getIcon(side, colorIndex);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		helper.registerBlockIcons(p_149651_1_);
	}

	@Override
	public int getActiveBounceHeight() {
		return polymerStairs.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return 0;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return PolycraftRegistry.getItem(polymerStairs.itemStairsName);
	}

	@Override
	protected ItemStack createStackedBlock(int p_149644_1_) {
		return new ItemStack(PolycraftRegistry.getItem(polymerStairs.name), 2, p_149644_1_ & 7);
	}

	@Override
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemstack)
	{
		PolycraftMod.setPolycraftStackCompoundTag(itemstack);
		super.dropBlockAsItem(world, x, y, z, itemstack);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		ItemStack polycraftItemStack = super.getPickBlock(target, world, x, y, z);
		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
		return polycraftItemStack;

	}

	public String getUnlocalizedName(int meta) {
		return polymerStairs.blockStairsGameID;
	}
}