package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Polymer;

public class BlockPolymerHelper {

	private static LabelTexture labelTextureBottom;
	private static final LabelTexture[] labelTextures = new LabelTexture[16];

	public static final String[] colors = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};;
//	private static final IIcon[] colorIconList = new IIcon[colors.length];
	private static final String[] colorDisplayNames = new String[colors.length];
	static {
		for (int i = 0; i < colors.length; i++) {
			labelTextures[i] = new LabelTexture("polymer_" + colors[i]);
			final StringBuffer color = new StringBuffer(colors[i]);
			color.setCharAt(0, Character.toUpperCase(color.charAt(0)));
			for (int c = 1; c < color.length(); c++) {
				if (color.charAt(c) == '_') {
					if (color.length() > c + 1) {
						color.setCharAt(c, ' ');
						color.setCharAt(c + 1, Character.toUpperCase(color.charAt(c + 1)));
					}
					else {
						color.deleteCharAt(c);
					}
				}
			}
			colorDisplayNames[i] = color.toString();
		}
	}

	public static String getColorDisplayName(final int index) {
		return colorDisplayNames[index];
	}

	public final Polymer polymer;
	private final int onlyColor;

	public BlockPolymerHelper(final Polymer polymer, final int onlyColor) {
		this.polymer = polymer;
		this.labelTextureBottom = polymer.resinCode.recyclingNumber >= 1 && polymer.resinCode.recyclingNumber <= 7
				? new LabelTexture("polymer_" + polymer.resinCode.recyclingNumber + "_bottom") : null;
		this.onlyColor = onlyColor;
	}

//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int colorIndex) {
//		if (side == LabelTexture.SIDE_BOTTOM && labelTextureBottom != null)
//			return labelTextureBottom.getIcon(side, colorIndex);
//		if (onlyColor > -1)
//			return labelTextures[onlyColor].getIcon(side, onlyColor);
//		return labelTextures[colorIndex].getIcon(side, colorIndex % colorIconList.length);
//	}
//
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister p_149651_1_) {
//		for (LabelTexture lt : labelTextures)
//			lt.registerBlockIcons(p_149651_1_);
//		if (labelTextureBottom != null)
//			labelTextureBottom.registerBlockIcons(p_149651_1_);
//	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
		for (int i = 0; i < colors.length; ++i) {
			p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
		}
	}

	public static int damageDropped(IBlockState state) {
		return 0;
	}

	public float getMomentumReturnedOnPassiveFall() {
		return .8f;
	}

	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state, 3);
	}
}
