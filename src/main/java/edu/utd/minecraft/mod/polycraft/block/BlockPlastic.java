package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDye;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Plastic;

public class BlockPlastic extends Block {

	public final Plastic plastic;
	private final LabelTexture labelTexture;
	//private static final IIcon[] colorIconList = new IIcon[16];
	//public static final String[] colorNames = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};
	//public static final String[] color_names = new String[] {"black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};
	
	public BlockPlastic(final Plastic plastic) {
		super(Material.cloth);
		this.plastic = plastic;
		this.labelTexture = new LabelTexture(plastic.gameName, 5);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return labelTexture.getIcon(p_149691_1_, p_149691_2_);
		//return labelTexture.getIcon(colorIconList[p_149691_2_ % colorIconList.length];
	}
	
//    @SideOnly(Side.CLIENT)
//    public static int func_149997_b(int p_149997_0_)
//    {
//        return ~p_149997_0_ & 15;
//    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		
//		 for (int i = 0; i < color_names.length; ++i)
//	        {
//			 //colorIconList[i] = p_149651_1_.registerIcon(this.getTextureName() + "_" + color_names[func_149997_b(i)]);
//			 //labelTexture.registerBlockIcons(p_149651_1_, color_names[func_149997_b(i)]);
//	        }
		 
		labelTexture.registerBlockIcons(p_149651_1_);
	}
}