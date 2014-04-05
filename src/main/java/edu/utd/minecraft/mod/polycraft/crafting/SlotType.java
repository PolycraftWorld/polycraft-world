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
	 
	/**
	 * Gets all of the slots for the specified type.  e.g.:
	 * allInputs = SlotType.INPUT.getAll(ChemicalProcessorSlot.class);
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends Enum & ContainerSlot> Collection<T>getAll(final Class<T> clazz) {
		List<T> list = Lists.newArrayList();
		for (final Object slotObj : EnumSet.allOf(clazz)) {
			T slot = (T)slotObj;
			if (slot.getSlotType() != null && slot.getSlotType().equals(this)) {
				list.add(slot);
			}
		}
		return list;
	}
	
	private SlotType() {		
	}
}
