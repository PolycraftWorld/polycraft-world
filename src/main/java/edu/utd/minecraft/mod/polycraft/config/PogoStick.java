package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PogoStick extends SourcedConfig<PogoStick> {

	public static final ConfigRegistry<PogoStick> registry = new ConfigRegistry<PogoStick>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PogoStick.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PogoStick(
						line[0], //gameID
						line[1], //name
						Tool.Material.valueOf(line[2]), //toolMaterial
						Integer.parseInt(line[3]), //maxBounces
						Float.parseFloat(line[4]), //stableBounceHeight
						Float.parseFloat(line[5]), //jumpMovementFactorBuff
						Boolean.parseBoolean(line[6]), //restrictJumpToGround
						registry.get(line[7]), //source
						MoldedItem.registry.get(line[8]) //grip
				));
	}

	public final MoldedItem grip;
	public final Tool.Material toolMaterial;
	public final int maxBounces;
	public final float stableBounceHeight;
	public final float jumpMovementFactorBuff;
	public final boolean restrictJumpToGround;
	public final float maxFallNoDamageHeight;

	public PogoStick(final String gameID, final String name, final Tool.Material toolMaterial,
			final int maxBounces, final float stableBounceHeight, final float jumpMovementFactorBuff, final boolean restrictJumpToGround,
			final PogoStick source, final MoldedItem grip) {
		super(gameID, name, source);
		this.grip = grip;
		this.toolMaterial = toolMaterial;
		this.maxBounces = maxBounces;
		this.stableBounceHeight = stableBounceHeight;
		this.jumpMovementFactorBuff = jumpMovementFactorBuff;
		this.restrictJumpToGround = restrictJumpToGround;
		this.maxFallNoDamageHeight = PolycraftMod.itemPogoStickMaxFallNoDamageMultiple * stableBounceHeight;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getItem(this), size);
	}

	public double getMotionY(final float fallDistance, final int previousContinuousActiveBounces, final boolean playerActivelyBouncing) {
		if (playerActivelyBouncing) {
			double height = stableBounceHeight;
			if (fallDistance > stableBounceHeight) {
				height = stableBounceHeight + ((fallDistance - stableBounceHeight) * .75);
			}
			else if (previousContinuousActiveBounces < PolycraftMod.itemPogoStickBouncesUntilStable) {
				height = stableBounceHeight * ((double) (previousContinuousActiveBounces + 1) / PolycraftMod.itemPogoStickBouncesUntilStable);
			}
			return PolycraftMod.getVelocityRequiredToReachHeight(height);
		}
		double motion = PolycraftMod.getVelocityRequiredToReachHeight(fallDistance * .5);
		if (motion < .2)
			return 0;
		return motion;
	}
}