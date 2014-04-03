package edu.utd.minecraft.mod.polycraft.crafting;

/**
 * Generic crafting slot for the standard crafting interface
 * used by the crafting bench and inventory.
 */
public enum GenericCraftingSlot implements ContainerSlot {	
	INPUT_TOP_LEFT		(0,  SlotType.INPUT, 0, 0),
	INPUT_TOP_MIDDLE	(1,  SlotType.INPUT, 1, 0),
	INPUT_TOP_RIGHT		(2,  SlotType.INPUT, 2, 0),
	INPUT_MIDDLE_LEFT	(3,  SlotType.INPUT, 0, 1),
	INPUT_MIDDLE_MIDDLE	(4,  SlotType.INPUT, 1, 1),
	INPUT_MIDDLE_RIGHT	(5,  SlotType.INPUT, 2, 1),
	INPUT_BOTTOM_LEFT	(6,  SlotType.INPUT, 0, 2),
	INPUT_BOTTOM_MIDDLE	(7,  SlotType.INPUT, 1, 2),
	INPUT_BOTTOM_RIGHT	(8,  SlotType.INPUT, 2, 2),

	OUTPUT_TOP_LEFT		(9,  SlotType.OUTPUT, 0, 0),
	OUTPUT_TOP_MIDDLE	(10, SlotType.OUTPUT, 1, 0),
	OUTPUT_TOP_RIGHT	(11, SlotType.OUTPUT, 2, 0),
	OUTPUT_MIDDLE_LEFT	(12, SlotType.OUTPUT, 0, 1),
	OUTPUT_MIDDLE_MIDDLE(13, SlotType.OUTPUT, 1, 1),
	OUTPUT_MIDDLE_RIGHT	(14, SlotType.OUTPUT, 2, 1),
	OUTPUT_BOTTOM_LEFT	(15, SlotType.OUTPUT, 0, 2),
	OUTPUT_BOTTOM_MIDDLE(16, SlotType.OUTPUT, 1, 2),
	OUTPUT_BOTTOM_RIGHT	(17, SlotType.OUTPUT, 2, 2);
		
	private final int relativeX;
	private final int relativeY;
	private final int slotIndex;
	private final SlotType type;
	
	private GenericCraftingSlot(final int slotIndex, final SlotType slotType,
			final int x, final int y) {
		this.slotIndex = slotIndex;
		this.type = slotType;
		this.relativeX = x;
		this.relativeY = y;
	}
	
	@Override
	public int getSlotIndex() {
		return this.slotIndex;
	}

	@Override
	public SlotType getSlotType() {
		return this.type;
	}

	@Override
	public int getRelativeX() {
		return this.relativeX;
	}

	@Override
	public int getRelativeY() {
		return this.relativeY;
	}
}