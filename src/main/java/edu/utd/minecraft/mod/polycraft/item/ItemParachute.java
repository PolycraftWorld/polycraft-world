package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemParachute extends PolycraftUtilityItem {

	public final float descendVelocity;

	public ItemParachute(final float descendVelocity) {
		this.setTextureName(PolycraftMod.getAssetName("parachute"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.descendVelocity = descendVelocity;
	}
}
