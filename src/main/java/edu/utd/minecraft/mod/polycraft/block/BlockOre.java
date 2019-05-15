package edu.utd.minecraft.mod.polycraft.block;

import java.util.Random;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Element;
import edu.utd.minecraft.mod.polycraft.config.Ore;

public class BlockOre extends net.minecraft.block.BlockOre {

	public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

	public final Ore ore;
	private final LabelTexture labelTexture;

	public BlockOre(final Ore ore) {
		this.ore = ore;
		if(ore.name == "OilField"){
			this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
		}
		if ((ore.source) instanceof Element)
		{
			final String texture = PolycraftMod.getFileSafeName(ore.name);
			this.labelTexture = new LabelTexture(texture, texture, texture + "_flipped");
		}
		else
		{
			final String texture = PolycraftMod.getFileSafeName(ore.name);
			this.labelTexture = new LabelTexture(texture, texture, texture + "_flipped");
		}
		this.setHardness(ore.hardness);
		this.setResistance(ore.resistance);
		this.setStepSound(Block.soundTypePiston);
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		return ((Integer)state.getValue(LEVEL)).intValue();
	}

	//	@Override
	//	public int getExpDrop(IBlockAccess p_149690_1_, int p_149690_5_, int p_149690_7_) {
	//		return MathHelper.getRandomIntegerInRange(rand, ore.dropExperienceMin, ore.dropExperienceMax);
	//	}

//	@Override
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
//		return labelTexture.getIcon(p_149691_1_, p_149691_2_);
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister p_149651_1_) {
//		labelTexture.registerBlockIcons(p_149651_1_);
//	}

//	@Override
//	public void dropBlockAsItem(World world, BlockPos blockPos, IBlockState blockState, int fortune)
//	{
//		//PolycraftMod.setPolycraftStackCompoundTag(itemstack);	TODO: Do we even need this? removed in 1.8
//		super.dropBlockAsItem(world, x, y, z, itemstack);
//	}

//	@Override	TODO: Do we even need this? removed in 1.8
//	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
//	{
//		ItemStack polycraftItemStack = super.getPickBlock(target, world, x, y, z);
//		PolycraftMod.setPolycraftStackCompoundTag(polycraftItemStack);
//		return polycraftItemStack;
//
//	}

}