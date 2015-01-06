package edu.utd.minecraft.mod.polycraft.inventory.heated;

import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventoryState;

public enum HeatedInventoryState implements StatefulInventoryState {
	HeatSourceTicksTotal, //The total number of ticks that the current heat source will keep this inventory heated
	HeatSourceTicksRemaining, //The number of ticks that the current heat source will remain heating this inventory
	HeatSourceIntensity, //How intense the current heat source is
	ProcessingTicks, //The number of ticks the current recipe has been processed
	HeatSourceTicksTotalEpochs, //track the total number of epochs based on the fuel type
	HeatSourceTicksRemainingEpochs; ////helps track the number of ticks

	@Override
	public int getIdentifier() {
		return ordinal();
	}

	@Override
	public int getDefaultValue() {
		return 0;
	}
}
