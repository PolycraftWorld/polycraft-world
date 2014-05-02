package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemPhaseShifter extends PolycraftUtilityItem {

	public final int radius;
	public final float flySpeedBuff;
	public final Block boundaryBlock;

	public ItemPhaseShifter(final CustomObject config) {
		this.setTextureName(PolycraftMod.getAssetName("phase_shifter"));
		this.setCreativeTab(CreativeTabs.tabTools);

		this.radius = config.getParamInteger(0);
		this.flySpeedBuff = config.getParamFloat(1);
		this.boundaryBlock = PolycraftMod.getBlock(config.getParamString(2));
	}
}
