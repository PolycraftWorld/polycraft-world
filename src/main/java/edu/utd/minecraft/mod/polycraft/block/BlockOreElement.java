package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.Element;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class BlockOreElement extends BlockOre
{
	@SideOnly(Side.CLIENT)
	private IIcon PlainTexture;
	@SideOnly(Side.CLIENT)
	private IIcon LabelTexture;

	public final Element element;

	public BlockOreElement(Element element)
	{
		this.element = element;
		this.setHardness(element.oreHardness);
		this.setResistance(element.oreResistance);
		this.setStepSound(Block.soundTypePiston);
	}

	private Random rand = new Random();

	@Override
	public int getExpDrop(IBlockAccess p_149690_1_, int p_149690_5_, int p_149690_7_)
	{
		return MathHelper.getRandomIntegerInRange(rand, element.dropExperienceMin, element.dropExperienceMax);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		switch (p_149691_1_)
		{
		// TOP
		case 1:
			return this.PlainTexture;
		default:
			return this.LabelTexture;
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.PlainTexture = p_149651_1_.registerIcon(PolycraftMod.MODID + ":" + element.blockNameOre);
		this.LabelTexture = p_149651_1_.registerIcon(PolycraftMod.MODID + ":" + element.blockNameOre + "_label");
	}
}