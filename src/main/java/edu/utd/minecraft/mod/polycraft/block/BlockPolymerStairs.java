package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerStairs;

public class BlockPolymerStairs extends BlockStairs implements BlockBouncy
{
	public final PolymerStairs polymerStairs;
	private final boolean isDouble;

	private final LabelTexture labelTexture;

	public BlockPolymerStairs(final PolymerStairs polymerStairs, int dontKnowWhatThisDoes)
	{
		super(PolycraftRegistry.getBlock(polymerStairs.source.name), dontKnowWhatThisDoes); // this line may crash TODO: Jim have a look
		this.isDouble = false;
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.polymerStairs = polymerStairs;
		this.labelTexture = polymerStairs.source.source.source.resinCode.recyclingNumber >= 1 && polymerStairs.source.source.source.resinCode.recyclingNumber <= 7
				? new LabelTexture("polymer", null, "polymer_" + polymerStairs.source.source.source.resinCode.recyclingNumber + "_bottom")
				: new LabelTexture("polymer", null, "polymer_bottom");

		// Examples
		// polymerStairs.source.name = Block (PolyIsoPrene)
		// polymerStairs.source.source.name = Jar (PolyIsoPrene Pellets)
		// polymerStairs.source.source.source.name = PolyIsoPrene

	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	// @Override
	// @SideOnly(Side.CLIENT)
	// public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	// {
	// return labelTexture.getIcon(p_149691_1_, p_149691_2_);
	// }

	// @Override
	// @SideOnly(Side.CLIENT)
	// public void registerBlockIcons(IIconRegister p_149651_1_)
	// {
	// labelTexture.registerBlockIcons(p_149651_1_);
	// }

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return PolycraftRegistry.getItem(polymerStairs.itemStairsName);
	}

	/**
	 * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
	 */
	@Override
	protected ItemStack createStackedBlock(int p_149644_1_)
	{
		return new ItemStack(PolycraftRegistry.getItem(polymerStairs.name), 2, p_149644_1_ & 7);
	}

	// @Override
	// public String func_150002_b(int p_150002_1_)
	// {
	// return polymerStairs.blockStairsGameID;
	// }

	// /**
	// * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	// */
	// @Override
	// @SideOnly(Side.CLIENT)
	// public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
	// {
	// if (p_149666_1_ != PolycraftRegistry.getItem(polymerStairs.blockDoubleSlabName))
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
		return polymerStairs.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return isDouble ? .8f : 0;
	}
}