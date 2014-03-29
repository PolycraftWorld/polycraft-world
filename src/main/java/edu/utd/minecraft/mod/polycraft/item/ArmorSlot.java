package edu.utd.minecraft.mod.polycraft.item;

// Enumeration of armor slots and IDs.
public enum ArmorSlot {
	HEAD("Head", 0),
	CHEST("Chest", 1),
	LEGS("Legs", 2),
	FEET("Feet", 3);
	
	private final int value;
	private final String name;
	
	// Gets the minecraft integer value of the armor slot.
	public int getValue() {
		return this.value;
	}
	
	// Gets the friendly name of the armor slot.
	public String getName() {
		return this.name;
	}
	
	private ArmorSlot(String name, int value) {
		this.name = name;
		this.value = value;
	}
}
