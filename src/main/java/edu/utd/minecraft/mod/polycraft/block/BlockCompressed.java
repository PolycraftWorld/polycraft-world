package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;

public class BlockCompressed extends net.minecraft.block.BlockCompressed {

	public final CompressedBlock compressedBlock;
	private final LabelTexture labelTexture;

	public BlockCompressed(final CompressedBlock compressedBlock) {
		super(compressedBlock.color);
		this.compressedBlock = compressedBlock;
		final String texture = PolycraftMod.getFileSafeName(
				"compressed_" + compressedBlock.type.getClass().getSimpleName() + "_" + compressedBlock.type.type.getClass().getSimpleName() + "_" + compressedBlock.name);
		this.labelTexture = new LabelTexture(texture, texture + "_flip");
		this.setHardness(compressedBlock.hardness);
		this.setResistance(compressedBlock.resistance);
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