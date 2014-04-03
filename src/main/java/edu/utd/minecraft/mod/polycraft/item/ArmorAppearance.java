package edu.utd.minecraft.mod.polycraft.item;

/**
 * Enumeration of armor appearances.
 */
public enum ArmorAppearance {
	LEATHER("Leather", 0),
	CHAIN("Chain", 1),
	IRON("Iron", 2),
	GOLD("Gold", 3),
	DIAMOND("Diamond", 4);
	
	private final int value;
	private final String name;
	
	/**
	 * @return the minecraft integer value of the armor render index.
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * @return the friendly name of the armor render index.
	 */
	public String getName() {
		return this.name;
	}
	
	private ArmorAppearance(String name, int value) {
		this.name = name;
		this.value = value;
	}
}
