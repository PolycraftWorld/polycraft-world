package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Compound;

public class BlockCompound extends net.minecraft.block.BlockOre {

	public final Compound compound;
	private final LabelTexture labelTexture;

	public BlockCompound(final Compound compound) {
		this.compound = compound;
		this.labelTexture = new LabelTexture(compound.gameName, 1);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return labelTexture.getIcon(p_149691_1_, p_149691_2_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		labelTexture.registerBlockIcons(p_149651_1_);
	}
}