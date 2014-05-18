package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.PolymerBlock;

public class BlockPolymer extends Block implements BlockBouncy {

	public final PolymerBlock polymerBlock;
	private static final LabelTexture[] labelTextures = new LabelTexture[16];
	private final int colorIndex;

	private static final IIcon[] colorIconList = new IIcon[16];
	public static final String[] colors = new String[] { "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white" };
	public static final String[] color_names = new String[] { "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white" };

	static
	{
		for (int i = 0; i < labelTextures.length; i++)
		{
			labelTextures[i] = new LabelTexture("plastic_top_" + color_names[i]);
		}

	}

	public BlockPolymer(final PolymerBlock polymerBlock) {
		super(Material.cloth);
		this.polymerBlock = polymerBlock;
		this.colorIndex = 0;
		// this.labelTexture = polymerBlock.source.source.resinCode.recyclingNumber >= 1 && polymerBlock.source.source.resinCode.recyclingNumber <= 7
		// ? new LabelTexture("polymer", null, "polymer_" + polymerBlock.source.source.resinCode.recyclingNumber + "_bottom")
		// : new LabelTexture("polymer", null, "polymer_bottom");

		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int colorIndex) {

		return labelTextures[colorIndex].getIcon(side, colorIndex);
		// return labelTexture.getIcon(colorIconList[p_149691_2_ % colorIconList.length];
	}

	// @SideOnly(Side.CLIENT)
	// public static int func_149997_b(int p_149997_0_)
	// {
	// return ~p_149997_0_ & 15;
	// }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {

		// for (int i = 0; i < color_names.length; ++i)
		// {
		// colorIconList[i] = p_149651_1_.registerIcon(this.getTextureName() + "_" + color_names[func_149997_b(i)]);
		// labelTexture.registerBlockIcons(p_149651_1_, color_names[func_149997_b(i)]);
		// }
		for (LabelTexture lt : labelTextures)
		{
			lt.registerBlockIcons(p_149651_1_);
		}
	}

	@Override
	public int getActiveBounceHeight() {
		return polymerBlock.bounceHeight;
	}

	@Override
	public float getMomentumReturnedOnPassiveFall() {
		return .8f;
	}
}
