package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;

public class FueledLampBlock extends PolycraftInventoryBlock<FueledLampInventory> {

	private final Random random = new Random();

	public FueledLampBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
		getInventory(world, x, y, z).voteOnLights(false);
		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
	}
}
