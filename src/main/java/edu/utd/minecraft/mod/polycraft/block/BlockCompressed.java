package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;

public class BlockCompressed extends net.minecraft.block.BlockCompressed {

	private final LabelTexture labelTexture;

	public BlockCompressed(final CompressedBlock compressedBlock) {
		super(MapColor.ironColor);
		final String texture = PolycraftMod.getFileSafeName(
				"compressed_" + compressedBlock.source.getClass().getSimpleName());
		this.labelTexture = new LabelTexture(texture, texture + "_flip");
		this.setStepSound(Block.soundTypeMetal);
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