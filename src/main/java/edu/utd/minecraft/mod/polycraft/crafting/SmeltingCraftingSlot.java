package edu.utd.minecraft.mod.polycraft.crafting;

public enum SmeltingCraftingSlot implements ContainerSlot, GuiContainerSlot {
	INPUT_SLOT(0, SlotType.INPUT, 0, 0),
	FUEL_INPUT(1, SlotType.INPUT, 0, 1),
	OUTPUT_SLOT(2, SlotType.OUTPUT, 1, 0);
	
	private final int relativeX;
	private final int relativeY;
	private final int slotIndex;
	private final SlotType type;
	
	private SmeltingCraftingSlot(final int slotIndex, final SlotType slotType,
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

	@Override
	public int getDisplayX() {
		return 0;
	}

	@Override
	public int getDisplayY() {
		return 0;
	}
}
