package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		helper.onBlockPlacedBy(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return PolycraftRegistry.getItem(polymerWall.itemWallName);
	}

	@Override
	protected ItemStack createStackedBlock(int p_149644_1_) {
		return new ItemStack(PolycraftRegistry.getItem(polymerWall.name), 2, p_149644_1_ & helper.colors.length);
	}

	@Override
	public int damageDropped(int p_149692_1_) {
		return helper.damageDropped(p_149692_1_);
	}

	public String getUnlocalizedName(int colorIndex) {
		return polymerWall.blockWallGameID + "." + colorIndex;
	}
}