package edu.utd.minecraft.mod.polycraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CompressedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockCompressed extends net.minecraft.block.BlockCompressed {

	private final LabelTexture labelTexture;

	public BlockCompressed(final CompressedBlock compressedBlock) {
		super(MapColor.ironColor);
		final String texture = PolycraftMod.getFileSafeName(compressedBlock.name);
		this.labelTexture = new LabelTexture(texture, texture); // + "_flipped");
		this.setStepSound(Block.soundTypeMetal);
		this.setHardness(3);
		this.setResistance(5);
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
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemstack) {
		PolycraftMod.setPolycraftStackCompoundTag(itemstack);
		super.dropBlockAsItem(world, x, y, z, itemstack);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		ItemStack polycraftItemStack = super.getPickBlock(target, world, x, y, z);
		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
		return polycraftItemStack;

	}
}