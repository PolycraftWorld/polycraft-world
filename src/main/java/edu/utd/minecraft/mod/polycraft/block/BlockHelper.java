package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockHelper {

	/**
	 * gets the way this piston should face for that entity that placed it.
	 */
	public static int determineOrientation(World p_150071_0_, int p_150071_1_, int p_150071_2_, int p_150071_3_, EntityLivingBase p_150071_4_)
	{
		if (MathHelper.abs((float) p_150071_4_.posX - p_150071_1_) < 2.0F && MathHelper.abs((float) p_150071_4_.posZ - p_150071_3_) < 2.0F)
		{
			double d0 = p_150071_4_.posY + 1.82D - p_150071_4_.yOffset;

			if (d0 - p_150071_2_ > 2.0D)
			{
				return 1;
			}

			if (p_150071_2_ - d0 > 0.0D)
			{
				return 0;
			}
		}

		int l = MathHelper.floor_double(p_150071_4_.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
	}

	public static void setFacingMetadata6(Block thisBlock, World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		int l = determineOrientation(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_);
		p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, l, 2);
	}

	public static void setFacingMetadata4(Block thisBlock, World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		Block block = p_149689_1_.getBlock(p_149689_2_, p_149689_3_, p_149689_4_ - 1);
		Block block1 = p_149689_1_.getBlock(p_149689_2_, p_149689_3_, p_149689_4_ + 1);
		Block block2 = p_149689_1_.getBlock(p_149689_2_ - 1, p_149689_3_, p_149689_4_);
		Block block3 = p_149689_1_.getBlock(p_149689_2_ + 1, p_149689_3_, p_149689_4_);
		byte b0 = 0;
		int l = MathHelper.floor_double(p_149689_5_.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (l == 0)
		{
			b0 = 2;
		}

		if (l == 1)
		{
			b0 = 5;
		}

		if (l == 2)
		{
			b0 = 3;
		}

		if (l == 3)
		{
			b0 = 4;
		}

		if (block != thisBlock && block1 != thisBlock && block2 != thisBlock && block3 != thisBlock)
		{
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, b0, 3);
		}
		else
		{
			if ((block == thisBlock || block1 == thisBlock) && (b0 == 4 || b0 == 5))
			{
				if (block == thisBlock)
				{
					p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_ - 1, b0, 3);
				}
				else
				{
					p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_ + 1, b0, 3);
				}

				p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, b0, 3);
			}

			if ((block2 == thisBlock || block3 == thisBlock) && (b0 == 2 || b0 == 3))
			{
				if (block2 == thisBlock)
				{
					p_149689_1_.setBlockMetadataWithNotify(p_149689_2_ - 1, p_149689_3_, p_149689_4_, b0, 3);
				}
				else
				{
					p_149689_1_.setBlockMetadataWithNotify(p_149689_2_ + 1, p_149689_3_, p_149689_4_, b0, 3);
				}

				p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, b0, 3);
			}
		}

		if (p_149689_6_.hasDisplayName())
		{
			((TileEntityChest) p_149689_1_.getTileEntity(p_149689_2_, p_149689_3_, p_149689_4_)).func_145976_a(p_149689_6_.getDisplayName());
		}

		if (p_149689_6_.hasDisplayName())
		{
			((TileEntityChest) p_149689_1_.getTileEntity(p_149689_2_, p_149689_3_, p_149689_4_)).func_145976_a(p_149689_6_.getDisplayName());
		}
	}
}
