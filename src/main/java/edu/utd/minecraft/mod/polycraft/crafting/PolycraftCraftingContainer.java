package edu.utd.minecraft.mod.polycraft.crafting;

import net.minecraft.inventory.Container;

public abstract class PolycraftCraftingContainer extends Container {
	// Returns the enumerated container type
	public abstract PolycraftContainerType getContainerType();
}
