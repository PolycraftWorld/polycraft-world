package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemParachute extends PolycraftUtilityItem {

	public final float descendVelocity;

	public ItemParachute(final CustomObject config) {
		this.setTextureName(PolycraftMod.getAssetName("parachute"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.descendVelocity = config.params.getFloat(0);
	}
}
