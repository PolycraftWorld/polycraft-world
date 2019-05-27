package edu.utd.minecraft.mod.polycraft.inventory.fluorescentlamp;

import java.util.Random;

import edu.utd.minecraft.mod.polycraft.block.BlockHelper;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Note: Start IDs at 1xl. (one-x-l)
 */
public class FluorescentLampBlock extends PolycraftInventoryBlock<FluorescentLampInventory> {

	private final Random random = new Random();

	public FluorescentLampBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.2F);
		// Change later to reflect new model.
		this.setBlockBounds(0.0F, 0.6F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, BlockPos blockPos, IBlockState state,
			EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		BlockHelper.setFacingMetadata4(this, p_149689_1_,blockPos, p_149689_5_,
				p_149689_6_);
	}

	@Override
	public void breakBlock(World world, BlockPos blockPos, IBlockState state) {
		getInventory(world, blockPos).removeCurrentLightSource();
		super.breakBlock(world, blockPos, state);
	}
}
