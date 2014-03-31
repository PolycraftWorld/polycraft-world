package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemFlashlight extends PolycraftUtilityItem {

	public final int luminosity;

	public ItemFlashlight(final int luminosity) {
		this.setTextureName(PolycraftMod.getTextureName("flashlight"));
		this.luminosity = luminosity;
	}
}
