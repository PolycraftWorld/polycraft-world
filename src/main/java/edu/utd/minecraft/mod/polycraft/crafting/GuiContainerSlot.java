package edu.utd.minecraft.mod.polycraft.crafting;

/**
 * A container slot within a container GUI
 */
public class GuiContainerSlot implements ContainerSlot {

	// Gui coordinate to map relative position of inputs to pixels in the gui
	public static GuiContainerSlot createInput(final int index, final int relativeX, final int relativeY, final int displayXOffset, final int displayYOffset) {
		return new GuiContainerSlot(index, SlotType.INPUT, relativeX, relativeY, displayXOffset + relativeX * 18, displayYOffset + (relativeY + 1) * 18);
	}

	private final int index;
	private final SlotType type;
	private final int relativeX;
	private final int relativeY;
	private final int displayX;
	private final int displayY;

	public GuiContainerSlot(final int index, final SlotType type, final int relativeX, final int relativeY) {
		this(index, type, relativeX, relativeY, 0, 0);
	}

	public GuiContainerSlot(final int index, final SlotType type, final int relativeX, final int relativeY, final int displayX, final int displayY) {
		this.index = index;
		this.type = type;
		this.relativeX = relativeX;
		this.relativeY = relativeY;
		this.displayX = displayX;
		this.displayY = displayY;
	}

	@Override
	public int getSlotIndex() {
		return index;
	}

	@Override
	public SlotType getSlotType() {
		return type;
	}

	@Override
	public int getRelativeX() {
		return relativeX;
	}

	@Override
	public int getRelativeY() {
		return relativeY;
	};

	public int getDisplayX() {
		return displayX;
	}

	public int getDisplayY() {
		return displayY;
	}
}
