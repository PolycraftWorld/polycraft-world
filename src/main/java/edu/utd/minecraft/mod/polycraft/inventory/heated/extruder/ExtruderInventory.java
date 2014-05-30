package edu.utd.minecraft.mod.polycraft.inventory.heated.extruder;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.heated.MolderInventory;

public class ExtruderInventory extends MolderInventory {

	private static Inventory config;

	public static final void register(final Inventory config) {
		ExtruderInventory.config = config;
		config.containerType = PolycraftContainerType.EXTRUDER;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, ExtruderInventory.class), new PolycraftInventoryBlock.BasicRenderingHandler(config));
	}

	public ExtruderInventory() {
		super(PolycraftContainerType.EXTRUDER, config);
	}
}
