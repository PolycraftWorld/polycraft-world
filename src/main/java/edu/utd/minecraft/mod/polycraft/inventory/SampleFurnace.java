package edu.utd.minecraft.mod.polycraft.inventory;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.SmokeBehavior;

public class SampleFurnace extends PolycraftInventory {
	public SampleFurnace() {
		super(PolycraftContainerType.TEST_FURNACE, "sample_furnace");
		
		// Configure icon and textures
		setInventoryIcon("front");
		setBlockFace(BlockFace.UP, "front");
		setBlockFace(BlockFace.DOWN, "left");
		setBlockFace(BlockFace.FRONT, "front");
		setBlockFace(BlockFace.DEFAULT, "left");
		setGuiTexture("textures/gui/container/chemical_processor.png");
		setContainerGuiId(PolycraftMod.guiChemicalProcessorID);
		
		// Configure behaviors
		addBehavior(new SmokeBehavior());
	}

}
