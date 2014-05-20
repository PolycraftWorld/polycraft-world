package edu.utd.minecraft.mod.polycraft.block;

import net.minecraft.block.BlockAir;

public class BlockLight extends BlockAir {

	public BlockLight(final float level) {
		super();
		this.setLightLevel(level);
	}
}
