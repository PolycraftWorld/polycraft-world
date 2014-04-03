package edu.utd.minecraft.mod.polycraft.item;

/**
 * Enumeration of armor slots and IDs.
 */
public enum ArmorSlot {
	HEAD("Head", 0, 3),
	CHEST("Chest", 1, 2),
	LEGS("Legs", 2, 1),
	FEET("Feet", 3, 0);
	
	private final int value;
	private final int inventoryArmorSlot;
	private final String name;
	
	/**
	 * @return the minecraft integer value of the armor slot.
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * @return the corresponding spot in the inventory for the armor item.
	 */
	public int getInventoryArmorSlot() {
		return this.inventoryArmorSlot;
	}
	
	/**
	 * @return the friendly name of the armor slot.
	 */
	public String getName() {
		return this.name;
	}
	
	private ArmorSlot(String name, int value, int inventoryArmorSlot) {
		this.name = name;
		this.value = value;
		this.inventoryArmorSlot = inventoryArmorSlot;
	}
}
