package edu.utd.minecraft.mod.polycraft.inventory.spotlight;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class SpotLightBlock extends PolycraftInventoryBlock<SpotLightInventory> {

	private final Random random = new Random();

	public SpotLightBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass);

	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
		getInventory(world, x, y, z).updateFuelIntensity(0);
		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);

	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
	{
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

		if (block != this && block1 != this && block2 != this && block3 != this)
		{
			p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, b0, 3);
		}
		else
		{
			if ((block == this || block1 == this) && (b0 == 4 || b0 == 5))
			{
				if (block == this)
				{
					p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_ - 1, b0, 3);
				}
				else
				{
					p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_ + 1, b0, 3);
				}

				p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, b0, 3);
			}

			if ((block2 == this || block3 == this) && (b0 == 2 || b0 == 3))
			{
				if (block2 == this)
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
	}
}
