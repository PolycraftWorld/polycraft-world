package edu.utd.minecraft.mod.polycraft.inventory.machiningmill;

import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public enum MachiningMillSlot implements GuiContainerSlot {
	INPUT_COOLING_WATER(0, SlotType.MISC, new GuiCoordinates(116, 90, -1, -1)),

	INPUT_1(1, SlotType.INPUT, GuiCoordinates.Input(0, 0)),
	INPUT_2(2, SlotType.INPUT, GuiCoordinates.Input(1, 0)),
	INPUT_3(3, SlotType.INPUT, GuiCoordinates.Input(2, 0)),
	INPUT_4(4, SlotType.INPUT, GuiCoordinates.Input(3, 0)),
	INPUT_5(5, SlotType.INPUT, GuiCoordinates.Input(4, 0)),

	INPUT_6(6, SlotType.INPUT, GuiCoordinates.Input(0, 1)),
	INPUT_7(7, SlotType.INPUT, GuiCoordinates.Input(1, 1)),
	INPUT_8(8, SlotType.INPUT, GuiCoordinates.Input(2, 1)),
	INPUT_9(9, SlotType.INPUT, GuiCoordinates.Input(3, 1)),
	INPUT_10(10, SlotType.INPUT, GuiCoordinates.Input(4, 1)),

	INPUT_11(11, SlotType.INPUT, GuiCoordinates.Input(0, 2)),
	INPUT_12(12, SlotType.INPUT, GuiCoordinates.Input(1, 2)),
	INPUT_13(13, SlotType.INPUT, GuiCoordinates.Input(2, 2)),
	INPUT_14(14, SlotType.INPUT, GuiCoordinates.Input(3, 2)),
	INPUT_15(15, SlotType.INPUT, GuiCoordinates.Input(4, 2)),

	INPUT_16(16, SlotType.INPUT, GuiCoordinates.Input(0, 3)),
	INPUT_17(17, SlotType.INPUT, GuiCoordinates.Input(1, 3)),
	INPUT_18(18, SlotType.INPUT, GuiCoordinates.Input(2, 3)),
	INPUT_19(19, SlotType.INPUT, GuiCoordinates.Input(3, 3)),
	INPUT_20(5, SlotType.INPUT, GuiCoordinates.Input(4, 3)),

	INPUT_21(21, SlotType.INPUT, GuiCoordinates.Input(0, 4)),
	INPUT_22(22, SlotType.INPUT, GuiCoordinates.Input(1, 4)),
	INPUT_23(23, SlotType.INPUT, GuiCoordinates.Input(2, 4)),
	INPUT_24(24, SlotType.INPUT, GuiCoordinates.Input(3, 4)),
	INPUT_25(25, SlotType.INPUT, GuiCoordinates.Input(4, 4)),

	OUTPUT(7, SlotType.OUTPUT, new GuiCoordinates(152, 54, -1, -1));

	static class GuiCoordinates {
		public final int x;
		public final int y;

		public final int relativeX;
		public final int relativeY;

		// Coordinates in the gui
		private GuiCoordinates(final int x, final int y, final int relativeX, final int relativeY) {
			this.x = x;
			this.y = y;
			this.relativeX = relativeX;
			this.relativeY = relativeY;
		}

		// Gui coordinate to map relative position of inputs to pixels in the gui
		private static GuiCoordinates Input(final int x, final int y) {
			return new GuiCoordinates(8 + x * 18, (y + 1) * 18, x, y);
		}
	};

	private final int relativeX;
	private final int relativeY;
	private final int slotIndex;
	private final int displayX;
	private final int displayY;
	private final SlotType type;

	private MachiningMillSlot(final int slotIndex, final SlotType slotType, final GuiCoordinates coords) {
		this.slotIndex = slotIndex;
		this.displayX = coords.x;
		this.displayY = coords.y;
		this.type = slotType;
		this.relativeX = coords.relativeX;
		this.relativeY = coords.relativeY;
	}

	@Override
	public int getSlotIndex() {
		return this.slotIndex;
	}

	@Override
	public int getDisplayX() {
		return this.displayX;
	}

	@Override
	public int getDisplayY() {
		return this.displayY;
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
