package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

// An aluminium scuba tank is lighter and smaller, but holds less air.
public class ItemScubaTankAluminium extends ItemScubaTank {
	public ItemScubaTankAluminium(final int airUnitsFull, final int airUnitsConsumePerTick) {
		super(airUnitsFull, airUnitsConsumePerTick);		
		this.setTextureName(PolycraftMod.getTextureName("scuba_tank_aluminium"));
	}
}
