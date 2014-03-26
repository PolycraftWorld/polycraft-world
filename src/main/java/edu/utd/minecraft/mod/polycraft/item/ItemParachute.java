package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.Item;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemParachute extends Item {

	public final float descendVelocity;

	public ItemParachute(final float descendVelocity) {
		this.setTextureName(PolycraftMod.getTextureName("parachute"));
		this.descendVelocity = descendVelocity;
	}
}
