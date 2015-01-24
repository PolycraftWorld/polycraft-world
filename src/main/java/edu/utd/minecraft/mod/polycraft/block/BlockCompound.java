package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Compound;

public class BlockCompound extends Block {

	public final Compound compound;
	private final LabelTexture labelTexture;

	public BlockCompound(final Compound compound) {
		super(Material.rock);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.compound = compound;
		final String texture = PolycraftMod.getFileSafeName(Compound.class.getSimpleName() + "_" + compound.name);
		this.labelTexture = new LabelTexture(texture, texture + "_flip");
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