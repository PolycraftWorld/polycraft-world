package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemPogoStick extends PolycraftUtilityItem {

	public final int maxFallProtection;
	public final float jumpMotionY;
	public final float jumpMovementFactorBuff;

	public ItemPogoStick(final int maxFallProtection, final float jumpMotionY, final float jumpMovementFactorBuff) {
		this.setTextureName(PolycraftMod.getAssetName("pogo_stick"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.maxFallProtection = maxFallProtection;
		this.jumpMotionY = jumpMotionY;
		this.jumpMovementFactorBuff = jumpMovementFactorBuff;
	}
}
