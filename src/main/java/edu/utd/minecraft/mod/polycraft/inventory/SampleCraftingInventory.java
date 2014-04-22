package edu.utd.minecraft.mod.polycraft.inventory;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.behaviors.CraftingBehavior;

public class SampleCraftingInventory extends PolycraftInventory {
	public SampleCraftingInventory() {
		super(PolycraftContainerType.TEST_INVENTORY, "sample_inventory");
		
		// Configure icon and textures
		setInventoryIcon("front");
		setBlockFace(BlockFace.UP, "front");
		setBlockFace(BlockFace.DOWN, "left");
		setBlockFace(BlockFace.FRONT, "front");
		setBlockFace(BlockFace.DEFAULT, "left");
		setGuiTexture("textures/gui/container/machining_mill.png");
		setContainerGuiId(PolycraftMod.guiMachiningMillID);
		
		// Add standard crafting behavior.
		addBehavior(new CraftingBehavior());
	}

}
