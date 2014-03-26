package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Ore;

public class BlockOre extends net.minecraft.block.BlockOre {

	public final Ore ore;
	private final LabelTexture labelTexture;

	public BlockOre(final Ore ore) {
		this.ore = ore;
		this.labelTexture = new LabelTexture(ore.gameName, ore.gameName + "_flip");
		this.setHardness(ore.hardness);
		this.setResistance(ore.resistance);
		this.setStepSound(Block.soundTypePiston);
	}

	private final Random rand = new Random();

	@Override
	public int getExpDrop(IBlockAccess p_149690_1_, int p_149690_5_, int p_149690_7_) {
		return MathHelper.getRandomIntegerInRange(rand, ore.dropExperienceMin, ore.dropExperienceMax);
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