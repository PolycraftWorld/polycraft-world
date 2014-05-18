package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerWall;

public class BlockPolymerWall extends BlockWall implements BlockBouncy
{
	public final PolymerWall polymerWall;
	private final boolean isDouble;

	private final LabelTexture labelTexture;

	public BlockPolymerWall(final PolymerWall polymerWall)
	{
		super(PolycraftRegistry.getBlock(polymerWall.source.name)); // this line may crash TODO: Jim have a look
		this.isDouble = false;
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymerWall = polymerWall;
		this.labelTexture = polymerWall.source.source.source.resinCode.recyclingNumber >= 1 && polymerWall.source.source.source.resinCode.recyclingNumber <= 7
				? new LabelTexture("polymer", null, "polymer_" + polymerWall.source.source.source.resinCode.recyclingNumber + "_bottom")
				: new LabelTexture("polymer", null, "polymer_bottom");

		// Examples
		// polymerWall.source.name = Block (PolyIsoPrene)
		// polymerWall.source.source.name = Jar (PolyIsoPrene Pellets)
		// polymerWall.source.source.source.name = PolyIsoPrene

	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		return labelTexture.getIcon(p_149691_1_, p_149691_2_);
		// from the BlockWall Code
		// return p_149691_2_ == 1 ? Blocks.mossy_cobblestone.getBlockTextureFromSide(p_149691_1_) : Blocks.cobblestone.getBlockTextureFromSide(p_149691_1_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		labelTexture.registerBlockIcons(p_149651_1_);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return PolycraftRegistry.getItem(polymerWall.itemWallName);
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int p_149644_1_)
	{
		return new ItemStack(PolycraftRegistry.getItem(polymerWall.name), 2, p_149644_1_ & 7);
	}

	// @Override
	// public String func_150002_b(int p_150002_1_)
	// {
	// return polymerWall.blockWallGameID;
	// }

	// /**
	// * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	// */
	// @Override
	// @SideOnly(Side.CLIENT)
	// public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
	// {
	// if (p_149666_1_ != PolycraftRegistry.getItem(polymerWall.blockDoubleSlabName))
	// {
	// for (int i = 0; i <= 7; ++i)
	// {
	// if (i != 2)
	// {
	// p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
	// }
	// }
	// }
	// }

	@Override
	public int getActiveBounceHeight() {
		return polymerWall.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return isDouble ? .8f : 0;
	}
}