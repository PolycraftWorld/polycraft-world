package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Ore;

public class BlockOre extends net.minecraft.block.BlockOre {

	private final Random rand = new Random();
	public final Ore ore;
	private final LabelTexture labelTexture;

	public BlockOre(final Ore ore) {
		this.ore = ore;
		final String texture = PolycraftMod.getFileSafeName(ore.name);
		this.labelTexture = new LabelTexture(texture, texture, texture + "_flipped");
		this.setHardness(ore.hardness);
		this.setResistance(ore.resistance);
		this.setStepSound(Block.soundTypePiston);
	}

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

	@Override
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemstack)
	{
		PolycraftMod.setPolycraftStackCompoundTag(itemstack);
		super.dropBlockAsItem(world, x, y, z, itemstack);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		ItemStack polycraftItemStack = super.getPickBlock(target, world, x, y, z);
		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
		return polycraftItemStack;

	}

}