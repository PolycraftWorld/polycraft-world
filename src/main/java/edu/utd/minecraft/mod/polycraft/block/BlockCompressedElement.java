package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressed;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.Element;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class BlockCompressedElement extends BlockCompressed
{
	@SideOnly(Side.CLIENT)
	private IIcon PlainTexture;
	@SideOnly(Side.CLIENT)
	private IIcon LabelTexture;

	public final Element element;

	public BlockCompressedElement(Element element)
	{
		super(element.compressedColor);
		this.element = element;
		this.setHardness(element.compressedHardness);
		this.setResistance(element.compressedResistance);
		this.setStepSound(Block.soundTypeMetal);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		switch (p_149691_1_)
		{
		// TO
		case 1:
			return this.PlainTexture;
		default:
			return this.LabelTexture;
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.PlainTexture = p_149651_1_.registerIcon(PolycraftMod.MODID + ":" + element.blockNameCompressed);
		this.LabelTexture = p_149651_1_.registerIcon(PolycraftMod.MODID + ":" + element.blockNameCompressed + "_label");
	}
}