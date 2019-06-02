package edu.utd.minecraft.mod.polycraft.block;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
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
		return colors[index];
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
	public void getSubBlocks(Item itemIn, CreativeTabs p_149666_2_, List list) {
//		for (int i = 0; i < colors.length; ++i) {
//			p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
//		}
		EnumColor[] allColors = EnumColor.values();
	    for (EnumColor color : allColors) {
	      list.add(new ItemStack(itemIn, 1, color.getMetadata()));
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
	
	// create a new enum for our four colours, with some supporting methods to convert to & from metadata, and to get
	  //  human-readable names.
	  public static enum EnumColor implements IStringSerializable
	  {
		BLACK(0,"black"), 
		RED(1, "red"), 
		GREEN(2, "green"), 
		BROWN(3, "brown"), 
		BLUE(4, "blue"), 
		PURPLE(5, "purple"), 
		CYAN(6, "cyan"), 
		SILVER(7, "silver"), 
		GRAY(8, "gray"), 
		PINK(9, "pink"), 
		LINE(10, "lime"), 
		YELLOW(11, "yellow"), 
		LIGHT_BLUE(12, "light_blue"), 
		MAGENTA(13, "magenta"), 
		ORANGE(14, "orange"), 
		WHITE(15, "white");

	    public int getMetadata()
	    {
	      return this.meta;
	    }

	    @Override
	    public String toString()
	    {
	      return this.name;
	    }

	    public static EnumColor byMetadata(int meta)
	    {
	      if (meta < 0 || meta >= META_LOOKUP.length)
	      {
	        meta = 0;
	      }

	      return META_LOOKUP[meta];
	    }

	    public String getName()
	    {
	      return this.name;
	    }

	    private final int meta;
	    private final String name;
	    private static final EnumColor[] META_LOOKUP = new EnumColor[values().length];

	    private EnumColor(int i_meta, String i_name)
	    {
	      this.meta = i_meta;
	      this.name = i_name;
	    }

	    static
	    {
	      for (EnumColor colour : values()) {
	        META_LOOKUP[colour.getMetadata()] = colour;
	      }
	    }
	  }
}
