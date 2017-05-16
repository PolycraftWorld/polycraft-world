package edu.utd.minecraft.mod.polycraft.inventory.heated.injectionmolder;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.heated.MolderInventory;

public class InjectionMolderInventory extends MolderInventory {

	private static Inventory config;

	public static final void register(final Inventory config) {
		InjectionMolderInventory.config = config;
		config.containerType = PolycraftContainerType.INJECTION_MOLDER;
		PolycraftInventory.register(new PolycraftInventoryBlock(config, InjectionMolderInventory.class));
	}

	public InjectionMolderInventory() {
		super(PolycraftContainerType.INJECTION_MOLDER, config);
	}
}
