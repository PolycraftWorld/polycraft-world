package edu.utd.minecraft.mod.polycraft.inventory.fueledlamp;

import edu.utd.minecraft.mod.polycraft.inventory.StatefulInventoryState;

public enum FueledLampState implements StatefulInventoryState {
	FuelIndex(-1), //The index of the current fuel
	FuelTicksTotal, //The total number of ticks that the current fuel will keep this inventory fueled
	FuelTicksRemaining, //The number of ticks that the current fuel will remain fueling this inventory
	FuelHeatIntensity; //The intensity of the fuel (when it was loaded, could have changed after a restart)

	private final int defaultValue;

	private FueledLampState() {
		this(0);
	}

	private FueledLampState(final int defaultValue) {
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