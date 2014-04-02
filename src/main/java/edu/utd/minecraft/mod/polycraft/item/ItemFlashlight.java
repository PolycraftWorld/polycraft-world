package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemFlashlight extends PolycraftUtilityItem {

	public final int maxLightLevel;
	public final float lightLevelDecreaseByDistance;
	public final int viewingConeAngle;

	public ItemFlashlight(final int maxLightLevel, final float lightLevelDecreaseByDistance, final int viewingConeAngle) {
		this.setTextureName(PolycraftMod.getTextureName("flashlight"));
		this.setCreativeTab(CreativeTabs.tabTools);
		this.maxLightLevel = maxLightLevel;
		this.lightLevelDecreaseByDistance = lightLevelDecreaseByDistance;
		this.viewingConeAngle = viewingConeAngle;
	}
}
