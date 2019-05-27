package edu.utd.minecraft.mod.polycraft.crafting;

/**
 * A ContainerSlot represents a slot index and type
 * in a crafting container.
 */
public interface ContainerSlot {
	/**
	 * @return The integer-valued slot index
	 */
	public int getSlotIndex();
	
	/**
	 * @return The type of slot (input, output, etc.)
	 */
	public SlotType getSlotType();
	
	/**
	 * @return The relative X position of the container slot in
	 * a 2-d grid.
	 */
	public int getRelativeX();
	
	/**
	 * @return The relative Y position of the container slot in
	 * a 2-d grid.
	 */
	public int getRelativeY();
	

}
