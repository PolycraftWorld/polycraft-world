package edu.utd.minecraft.mod.polycraft.inventory.pump;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.block.BlockHelper;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.BlockFace;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class FlowRegulatorBlock extends PolycraftInventoryBlock<FlowRegulatorInventory> implements Flowable{

	public FlowRegulatorBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass);
	}

	@Override
	public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_) {
		BlockHelper.setFacingMetadata6(this, p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);
	}
}
