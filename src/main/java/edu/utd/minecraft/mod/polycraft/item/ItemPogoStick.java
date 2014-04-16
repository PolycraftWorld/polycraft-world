package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemPogoStick extends PolycraftUtilityItem {

	public static class Settings {
		public final String itemName;
		public final String materialName;
		public final ToolMaterial material;
		public final int maxBounces;
		public final float stableBounceHeight;
		public final float maxFallNoDamageHeight;
		public final float jumpMovementFactorBuff;

		public Settings(final String materialName, final ToolMaterial material, final int maxBounces, final float stableBounceHeight, final float jumpMovementFactorBuff) {
			itemName = materialName + "_" + PolycraftMod.itemNamePogoStick;
			this.materialName = materialName;
			this.material = material;
			this.maxBounces = maxBounces;
			this.stableBounceHeight = stableBounceHeight;
			this.maxFallNoDamageHeight = stableBounceHeight * PolycraftMod.itemPogoStickMaxFallNoDamageMultiple;
			this.jumpMovementFactorBuff = jumpMovementFactorBuff;
		}

		private static final float acceleration = .08f;

		public double getMotionY(final float fallDistance, final int previousContinuousActiveBounces, final boolean playerActivelyBouncing) {
			if (playerActivelyBouncing) {
				double height = stableBounceHeight;
				if (fallDistance > stableBounceHeight) {
					height = stableBounceHeight + ((fallDistance - stableBounceHeight) * .75);
				}
				else if (previousContinuousActiveBounces < PolycraftMod.itemPogoStickBouncesUntilStable) {
					height = stableBounceHeight * ((double) (previousContinuousActiveBounces + 1) / PolycraftMod.itemPogoStickBouncesUntilStable);
				}
				return Math.sqrt(2 * acceleration * height);
			}
			double motion = Math.sqrt(2 * acceleration * fallDistance) * .5;
			if (motion < .2)
				return 0;
			return motion;
		}
	}

	public final Settings settings;

	public ItemPogoStick(final Settings settings) {
		this.setTextureName(PolycraftMod.getAssetName(settings.itemName));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setMaxDamage(settings.maxBounces);
		this.settings = settings;
	}
}
