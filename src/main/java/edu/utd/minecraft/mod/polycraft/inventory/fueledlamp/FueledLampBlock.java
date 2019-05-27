package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.block.BlockHelper;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class FueledLampBlock extends PolycraftInventoryBlock<FueledLampInventory> {

	private final Random random = new Random();

	public FueledLampBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass);
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, BlockPos blockPos, IBlockState state, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		BlockHelper.setFacingMetadata4(this, p_149689_1_, blockPos, p_149689_5_, p_149689_6_);
	}

	@Override
	public void breakBlock(World world, BlockPos blockPos, IBlockState state) {
		getInventory(world, blockPos).removeCurrentLightSource();
		super.breakBlock(world, blockPos, state);
	}
}
