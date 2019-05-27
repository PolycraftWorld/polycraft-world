package edu.utd.minecraft.mod.polycraft.inventory.pump;

import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventoryState;

public enum PumpState implements StatefulInventoryState {
	FuelIndex(-1), //The index of the current fuel
	FuelTicksTotal, //The total number of ticks that the current fuel will keep this inventory fueled
	FuelTicksRemaining, //The number of ticks that the current fuel will remain fueling this inventory
	FuelHeatIntensity, //The intensity of the fuel (when it was loaded, could have changed after a restart)
	FuelTicksTotalEpochs, //track the total number of epochs based on the fuel type
	FuelTicksRemainingEpochs, //helps track the number of ticks
	FlowTicksRemaining; //The number of ticks until the next flow event

	private final int defaultValue;

	private PumpState() {
		this(0);
	}

	private PumpState(final int defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public int getIdentifier() {
		return ordinal();
	}

	@Override
	public int getDefaultValue() {
		return defaultValue;
	}
}