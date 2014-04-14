package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemPogoStick extends PolycraftUtilityItem {

	public final float jumpMotionY;
	public final float jumpMovementFactorBuff;

	public ItemPogoStick(final float jumpMotionY, final float jumpMovementFactorBuff) {
		this.setTextureName(PolycraftMod.getAssetName("pogo_stick"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.jumpMotionY = jumpMotionY;
		this.jumpMovementFactorBuff = jumpMovementFactorBuff;
	}
}
