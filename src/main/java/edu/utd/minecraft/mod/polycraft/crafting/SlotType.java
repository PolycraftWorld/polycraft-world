package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * A SlotType describes the usage of a ContainerSlot.
 */
public enum SlotType {
	/**
	 * An input slot accepts materials for consumption.
	 */
	INPUT(),
	/**
	 * An output slot contains the results of recipe crafting.
	 */
	OUTPUT(),
	/**
	 * An inventory slot represents a player inventory slot.
	 */
	INVENTORY(),
	/**
	 * A misc slot that may be manipulated by the container, may either
	 * be input or output, and is not affected by the standard methods
	 * used to interact with recipes and containers.
	 */
	MISC();
	 	
	private SlotType() {		
	}
}
