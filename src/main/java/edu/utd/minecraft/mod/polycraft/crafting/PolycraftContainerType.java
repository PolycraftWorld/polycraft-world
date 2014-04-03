package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.ChemicalProcessorSlot;

/**
 * Enumeration of Polycraft container types.
 */
public enum PolycraftContainerType {
	/**
	 * Crafting table container, or for recipes needing less than
	 * a 4x4 space, inventory container.
	 */
	CRAFTING_TABLE("Crafting Table", GenericCraftingSlot.class),
	/**
	 * Minecraft furnace.
	 */
	FURNANCE("Furnance", SmeltingCraftingSlot.class),
	/**
	 * Chemical processor container
	 */
	CHEMICAL_PROCESSOR("Chemical Processor", ChemicalProcessorSlot.class);
	
	private final String friendlyName;

	private Map<SlotType, Collection<ContainerSlot>> slotsByType = Maps.newHashMap();
		
	private <T extends Enum & ContainerSlot> PolycraftContainerType(final String friendlyName, final Class<T> slotEnum) {
		Preconditions.checkNotNull(friendlyName);
		this.friendlyName = friendlyName;		
		for (SlotType slotType : EnumSet.allOf(SlotType.class)) {
			slotsByType.put(slotType, (Collection<ContainerSlot>)slotType.getAll(slotEnum));
		}
	}
		
	public Collection<ContainerSlot> getSlots(final SlotType type) {
		return slotsByType.get(type);		
	}
	
	@Override
	public String toString() {
		return this.friendlyName;
	}
}
