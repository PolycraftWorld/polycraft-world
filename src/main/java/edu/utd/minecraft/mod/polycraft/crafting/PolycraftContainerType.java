package edu.utd.minecraft.mod.polycraft.crafting;

import com.google.common.base.Preconditions;

// Enumeration of Polycraft container types.
public enum PolycraftContainerType {
	// Crafting table container, or for recipes needing less than
	// a 4x4 space, inventory container.
	CRAFTING_TABLE("Crafting Table"),
	// Chemical processor container
	CHEMICAL_PROCESSOR("Chemical Processor");
	
	private final String friendlyName;
	
	private PolycraftContainerType(final String friendlyName) {
		Preconditions.checkNotNull(friendlyName);
		this.friendlyName = friendlyName;
	}
	
	@Override
	public String toString() {
		return this.friendlyName;
	}
}
