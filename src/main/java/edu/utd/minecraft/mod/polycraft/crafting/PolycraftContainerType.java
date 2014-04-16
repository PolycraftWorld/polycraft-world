package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor.ChemicalProcessorSlot;
import edu.utd.minecraft.mod.polycraft.inventory.machiningmill.MachiningMillSlot;

/**
 * Enumeration of Polycraft container types.
 */
public enum PolycraftContainerType {
	/**
	 * Crafting table container, or for recipes needing less than a 4x4 space, inventory container.
	 */
	CRAFTING_TABLE("Crafting Table"), //, EnumSet.allOf(GenericCraftingSlot.class)),
	/**
	 * Minecraft furnace.
	 */
	FURNANCE("Furnance"), //, EnumSet.allOf(SmeltingCraftingSlot.class)),
	/**
	 * Machining mill container
	 */
	MACHINING_MILL("Machining Mill"), //, EnumSet.allOf(MachiningMillSlot.class));
	/**
	 * Chemical processor container
	 */
	CHEMICAL_PROCESSOR("Chemical Processor");//, EnumSet.allOf(ChemicalProcessorSlot.class));

	private final String friendlyName;

	private final Map<SlotType, Collection<ContainerSlot>> slotsByType = Maps.newHashMap();
	private final Map<SlotType, ContainerSlot[][]> slotGridsByType = Maps.newHashMap();
	private final Map<Integer, ContainerSlot> slotsByIndex = Maps.newHashMap();

	private void generateContainerSlotGrid(final SlotType slotType) {
		Collection<ContainerSlot> slots = getSlots(slotType);
		int maxX = 0;
		int maxY = 0;
		for (final ContainerSlot slot : slots) {
			maxX = Math.max(maxX, slot.getRelativeX());
			maxY = Math.max(maxY, slot.getRelativeY());
		}
		maxX++;
		maxY++;
		ContainerSlot[][] grid = new ContainerSlot[maxX][maxY];
		for (final ContainerSlot slot : slots) {
			int x = slot.getRelativeX();
			int y = slot.getRelativeY();
			if (x >= 0 && y >= 0) {
				grid[slot.getRelativeX()][slot.getRelativeY()] = slot;
			}
		}
		slotGridsByType.put(slotType, grid);
	}

	static {
		CRAFTING_TABLE.initialize(EnumSet.allOf(GenericCraftingSlot.class));
		FURNANCE.initialize(EnumSet.allOf(SmeltingCraftingSlot.class));
		MACHINING_MILL.initialize(EnumSet.allOf(MachiningMillSlot.class));
		CHEMICAL_PROCESSOR.initialize(EnumSet.allOf(ChemicalProcessorSlot.class));
	}

	private void initialize(EnumSet<?> slots) {
		for (SlotType slotType : EnumSet.allOf(SlotType.class)) {
			List<ContainerSlot> slotList = Lists.newArrayList();
			for (Object slotObj : slots) {
				ContainerSlot slot = (ContainerSlot) slotObj;
				if (slot.getSlotType().equals(slotType)) {
					slotList.add(slot);
				}
			}

			for (ContainerSlot slot : slotList) {
				slotsByIndex.put(slot.getSlotIndex(), slot);
			}
			slotsByType.put(slotType, slotList);
			generateContainerSlotGrid(slotType);
		}
	}

	private PolycraftContainerType(final String friendlyName) {
		Preconditions.checkNotNull(friendlyName);
		this.friendlyName = friendlyName;
	}

	/**
	 * Returns the enumerated version of the container slot by index, or null if the index is not valid.
	 */
	public ContainerSlot getContainerSlotByIndex(final int slotIndex) {
		return this.slotsByIndex.get(slotIndex);
	}

	/**
	 * Returns the enumerated version of the container slot by index, or null if the index is not valid.
	 */
	public ContainerSlot getContainerSlotByIndex(final ContainerSlot slot) {
		return this.slotsByIndex.get(slot.getSlotIndex());
	}

	/**
	 * Gets a slot index by relative x/y position.
	 */
	public ContainerSlot getRelativeContainerSlot(final SlotType slotType, final int x, final int y) {
		ContainerSlot[][] grid = slotGridsByType.get(slotType);
		if (x >= 0 && x < grid.length) {
			if (y >= 0 && y < grid[x].length) {
				return grid[x][y];
			}
		}
		return null;
	}

	/**
	 * @param type
	 *            The type of slot (input, output, etc.) to create a grid for.
	 * @return a 2-dimensional array of the container slot arrangement in a grid, in [x][y] format for the array indices. Each element either contains a ContainerSlot or null to indicate no item can be in that slot.
	 */
	public ContainerSlot[][] getContainerSlotGrid(final SlotType slotType) {
		return slotGridsByType.get(slotType);
	}

	/**
	 * @param type
	 *            The type of slot (input, output, etc.) to return
	 * @return a collection of slots for the specified type
	 */
	public Collection<ContainerSlot> getSlots(final SlotType type) {
		return slotsByType.get(type);
	}

	@Override
	public String toString() {
		return this.friendlyName;
	}
}
