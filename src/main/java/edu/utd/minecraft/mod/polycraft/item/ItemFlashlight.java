package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemFlashlight extends PolycraftUtilityItem {

	public final int luminosity;
	public final int range;
	public final int luminosityDecreaseByRange;
	public final int viewingConeAngle;

	public ItemFlashlight(final int luminosity, final int range, final int luminosityDecreaseByRange, final int viewingConeAngle) {
		this.setTextureName(PolycraftMod.getTextureName("flashlight"));
		this.setCreativeTab(CreativeTabs.tabTools);
		this.luminosity = luminosity;
		this.range = range;
		this.luminosityDecreaseByRange = luminosityDecreaseByRange;
		this.viewingConeAngle = viewingConeAngle;
	}
}
