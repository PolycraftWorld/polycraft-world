package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

// A steel scuba tank can hold more air, but is bigger and weighs more.
public class ItemScubaTankSteel extends ItemScubaTank {
	public ItemScubaTankSteel(final int airUnitsFull, final int airUnitsConsumePerTick) {
		super(airUnitsFull, airUnitsConsumePerTick);		
		this.setTextureName(PolycraftMod.getTextureName("scuba_tank_steel"));
	}
}