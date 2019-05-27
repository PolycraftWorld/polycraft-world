package edu.utd.minecraft.mod.polycraft.inventory.pump;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.block.BlockHelper;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class PumpBlock extends PolycraftInventoryBlock<PumpInventory> implements Flowable {

	public PumpBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, BlockPos blockPos, IBlockState state, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		BlockHelper.setFacingMetadataFlowable(this, p_149689_1_, blockPos, p_149689_5_, p_149689_6_);
	}
}
