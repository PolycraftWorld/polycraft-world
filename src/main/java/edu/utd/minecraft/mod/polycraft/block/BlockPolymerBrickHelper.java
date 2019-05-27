package edu.utd.minecraft.mod.polycraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
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
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;

public class BlockPolymerBrickHelper {

	private static final LabelTexture[] labelTextureTop = new LabelTexture[16];
	private static final LabelTexture[] labelTextureBottom = new LabelTexture[16];
	private static final LabelTexture[] labelTextureSides = new LabelTexture[16];
	private static final Random rand = new Random();

	public static final String[] colors = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
//	private static final IIcon[] colorIconList = new IIcon[colors.length];
	private static final String[] colorDisplayNames = new String[colors.length];
	static {
		for (int i = 0; i < colors.length; i++) {
			labelTextureSides[i] = new LabelTexture("brick_side_" + colors[i]);
			labelTextureTop[i] = new LabelTexture("brick_top_" + colors[i]);
			labelTextureBottom[i] = new LabelTexture("brick_bottom_" + colors[i]);
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

	public final PolymerPellets pellets;
	public final PolymerBrick subBrick;
	private final int onlyColor;
	private final int width, length;

	public BlockPolymerBrickHelper(final PolymerPellets pellets, final int width, final int length, final int onlyColor, final PolymerBrick subBrick) {
		this.pellets = pellets;
		this.onlyColor = onlyColor;
		this.length = length;
		this.width = width;
		this.subBrick = subBrick;
	}

//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int colorIndex) {
//		if (onlyColor > -1)
//			return labelTextureSides[onlyColor].getIcon(side, onlyColor);
//		if (side == LabelTexture.SIDE_BOTTOM && labelTextureBottom != null)
//			return labelTextureBottom[colorIndex].getIcon(side, colorIndex % colorIconList.length);
//		if (side == LabelTexture.SIDE_TOP && labelTextureTop != null)
//			return labelTextureTop[colorIndex].getIcon(side, colorIndex % colorIconList.length);
//		return labelTextureSides[colorIndex].getIcon(side, colorIndex % colorIconList.length);
//	}
//
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister p_149651_1_) {
//		for (LabelTexture lt : labelTextureSides)
//			lt.registerBlockIcons(p_149651_1_);
//		for (LabelTexture lt : labelTextureTop)
//			lt.registerBlockIcons(p_149651_1_);
//		for (LabelTexture lt : labelTextureBottom)
//			lt.registerBlockIcons(p_149651_1_);
//	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_) {
//		if (this.subBrick != null)
//		{
//			for (int i = 0; i < colors.length; ++i) {
//				p_149666_3_.add(new ItemStack(PolycraftRegistry.getBlock(this.subBrick), 1, i));
//			}
//		}
//		else
//		{
			for (int i = 0; i < colors.length; ++i) {
				p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
			}
//		}
	}
	

	public static int damageDropped(int p_149692_1_) {
		return p_149692_1_;
	}

	public float getMomentumReturnedOnPassiveFall() {
		return .8f;
	}

	public void onBlockPlacedBy(World world, BlockPos blockPos, IBlockState state, EntityLivingBase placer, ItemStack p_149689_6_) {
		world.setBlockState(blockPos, state, 3);
	}
	
	public ArrayList<ItemStack> getDrops(Block block, BlockPos blockPos, IBlockState state, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        int count = block.quantityDropped(state, fortune, rand);
        for(int i = 0; i < count; i++)
        {
            Item item = block.getItemDropped(state, rand, fortune);
            if (item != null)
            {
            	final ItemStack itemStack = new ItemStack(item, 1, block.getMetaFromState(state));
            	PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(itemStack);
                ret.add(itemStack);
            }
        }
        return ret;
    }
}
