package edu.utd.minecraft.mod.polycraft.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.PolymerBrick;
import edu.utd.minecraft.mod.polycraft.config.PolymerPellets;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipeManager;

public class BlockPolymerBrickHelper {

	private static final LabelTexture[] labelTextureTop = new LabelTexture[16];
	private static final LabelTexture[] labelTextureBottom = new LabelTexture[16];
	private static final LabelTexture[] labelTextureSides = new LabelTexture[16];

	public static final String[] colors = ItemDye.field_150921_b;
	private static final IIcon[] colorIconList = new IIcon[colors.length];
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

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int colorIndex) {
		if (onlyColor > -1)
			return labelTextureSides[onlyColor].getIcon(side, onlyColor);
		if (side == LabelTexture.SIDE_BOTTOM && labelTextureBottom != null)
			return labelTextureBottom[colorIndex].getIcon(side, colorIndex % colorIconList.length);
		if (side == LabelTexture.SIDE_TOP && labelTextureTop != null)
			return labelTextureTop[colorIndex].getIcon(side, colorIndex % colorIconList.length);
		return labelTextureSides[colorIndex].getIcon(side, colorIndex % colorIconList.length);
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		for (LabelTexture lt : labelTextureSides)
			lt.registerBlockIcons(p_149651_1_);
		for (LabelTexture lt : labelTextureTop)
			lt.registerBlockIcons(p_149651_1_);
		for (LabelTexture lt : labelTextureBottom)
			lt.registerBlockIcons(p_149651_1_);
	}

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

	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, p_149689_6_.getItemDamage(), 3);
	}
	
	public ArrayList<ItemStack> getDrops(Block block, World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        int count = block.quantityDropped(metadata, fortune, world.rand);
        for(int i = 0; i < count; i++)
        {
            Item item = block.getItemDropped(metadata, world.rand, fortune);
            if (item != null)
            {
            	final ItemStack itemStack = new ItemStack(item, 1, damageDropped(metadata));
            	PolycraftRecipeManager.markItemStackAsFromPolycraftRecipe(itemStack);
                ret.add(itemStack);
            }
        }
        return ret;
    }
}
