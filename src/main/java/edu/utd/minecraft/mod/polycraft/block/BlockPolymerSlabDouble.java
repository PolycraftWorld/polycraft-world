package edu.utd.minecraft.mod.polycraft.block;

import edu.utd.minecraft.mod.polycraft.config.PolymerSlab;

public class BlockPolymerSlabDouble extends BlockPolymerSlab{

	public BlockPolymerSlabDouble(PolymerSlab polymerSlab) {
		super(polymerSlab);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Says that this block is the double version of the tin slab
	 */
	@Override
	public boolean isDouble() {
		return true;
	}
	
}
