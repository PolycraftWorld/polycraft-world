package edu.utd.minecraft.mod.polycraft.block;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;


public class BlockPolyPortal extends Block{

	CustomObject config;
	public BlockPolyPortal(CustomObject config) {
		super(Material.air);
		this.config=config;
		// TODO Auto-generated constructor stub
	}

}
