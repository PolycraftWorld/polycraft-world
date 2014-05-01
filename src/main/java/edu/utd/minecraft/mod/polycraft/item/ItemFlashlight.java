package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemFlashlight extends PolycraftUtilityItem {
	public final int maxLightLevel;
	public final float lightLevelDecreaseByDistance;
	public final int viewingConeAngle;

	public ItemFlashlight(final CustomObject config) {
		this.setTextureName(PolycraftMod.getAssetName("flashlight"));
		this.setCreativeTab(CreativeTabs.tabTools);
		this.maxLightLevel = config.getParamInteger(0);
		this.lightLevelDecreaseByDistance = config.getParamFloat(1);
		this.viewingConeAngle = config.getParamInteger(2);
	}
}
