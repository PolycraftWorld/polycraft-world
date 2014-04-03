package edu.utd.minecraft.mod.polycraft.inventory.chemicalprocessor;

import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.GuiContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public enum ChemicalProcessorSlot implements ContainerSlot, GuiContainerSlot {
	INPUT_FUEL			(0,  SlotType.MISC, new GuiCoordinates(26, 54, -1, -1)),	
	
	INPUT_TOP_LEFT		(1,  SlotType.INPUT, GuiCoordinates.Input(0, 0)),
	INPUT_TOP_MIDDLE	(2,  SlotType.INPUT, GuiCoordinates.Input(1, 0)),
	INPUT_TOP_RIGHT		(3,  SlotType.INPUT, GuiCoordinates.Input(2, 0)),
	INPUT_MIDDLE_LEFT	(4,  SlotType.INPUT, GuiCoordinates.Input(0, 1)),
	INPUT_MIDDLE_RIGHT	(5,  SlotType.INPUT, GuiCoordinates.Input(2, 1)),
	
	OUTPUT_EMPTY_BOTTLE	(6,  SlotType.MISC, new GuiCoordinates(71, 54, -1, -1)),
	
	OUTPUT_TOP_LEFT		(7,  SlotType.OUTPUT, GuiCoordinates.Output(0, 0)),
	OUTPUT_TOP_MIDDLE	(8,  SlotType.OUTPUT, GuiCoordinates.Output(1, 0)),
	OUTPUT_TOP_RIGHT	(9,  SlotType.OUTPUT, GuiCoordinates.Output(2, 0)),
	OUTPUT_MIDDLE_LEFT	(10, SlotType.OUTPUT, GuiCoordinates.Output(0, 1)),
	OUTPUT_MIDDLE_MIDDLE(11, SlotType.OUTPUT, GuiCoordinates.Output(1, 1)),
	OUTPUT_MIDDLE_RIGHT	(12, SlotType.OUTPUT, GuiCoordinates.Output(2, 1)),
	OUTPUT_BOTTOM_LEFT	(13, SlotType.OUTPUT, GuiCoordinates.Output(0, 2)),
	OUTPUT_BOTTOM_MIDDLE(14, SlotType.OUTPUT, GuiCoordinates.Output(1, 2)),
	OUTPUT_BOTTOM_RIGHT	(15, SlotType.OUTPUT, GuiCoordinates.Output(2, 2));
	
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
		
		// Gui coordinate to map relative position of outputs to pixels in the gui
		private static GuiCoordinates Output(final int x, final int y) {
			return new GuiCoordinates(116 + (x * 18), 18 * (y + 1), x, y);
		}
	};
	
	private final int relativeX;
	private final int relativeY;
	private final int slotIndex;
	private final int displayX;
	private final int displayY;
	private final SlotType type;
	
	private ChemicalProcessorSlot(final int slotIndex, final SlotType slotType, final GuiCoordinates coords) {
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
