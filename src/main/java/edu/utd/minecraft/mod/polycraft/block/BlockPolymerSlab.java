package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;

public class BlockPolymerSlab extends BlockSlab implements BlockBouncy {

	public final PolymerSlab polymerSlab;
	private final boolean isDouble;
	private final BlockPolymerHelper helper;

	public BlockPolymerSlab(final PolymerSlab polymerSlab, boolean isDouble) {
		super(isDouble, Material.cloth);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymerSlab = polymerSlab;
		this.isDouble = isDouble;
		this.helper = new BlockPolymerHelper(polymerSlab.source.source.source, 15);
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
		return polymerSlab.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return isDouble ? helper.getMomentumReturnedOnPassiveFall() : 0;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return PolycraftRegistry.getItem(polymerSlab.itemSlabName);
	}

	@Override
	protected ItemStack createStackedBlock(int p_149644_1_) {
		return new ItemStack(PolycraftRegistry.getItem(polymerSlab.name), 2, p_149644_1_ & 7);
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

	@Override
	public String func_150002_b(int p_150002_1_) {
		return polymerSlab.blockSlabGameID;
	}
}