package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PogoStick extends SourcedConfig<PogoStick> {

	public static final ConfigRegistry<PogoStick> registry = new ConfigRegistry<PogoStick>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PogoStick.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new PogoStick(
						PolycraftMod.getVersionNumeric(line[index++]),
						line[index++], //gameID
						line[index++], //name
						Tool.Material.valueOf(line[index++]), //toolMaterial
						Integer.parseInt(line[index++]), //maxBounces
						Float.parseFloat(line[index++]), //stableBounceHeight
						Float.parseFloat(line[index++]), //jumpMovementFactorBuff
						Boolean.parseBoolean(line[index++]), //restrictJumpToGround
						registry.get(line[index++]), //source
						MoldedItem.registry.get(line[index++]) //grip
				));
			}
	}

	public final MoldedItem grip;
	public final Tool.Material toolMaterial;
	public final int maxBounces;
	public final float stableBounceHeight;
	public final float jumpMovementFactorBuff;
	public final boolean restrictJumpToGround;
	public final float maxFallNoDamageHeight;

	public PogoStick(final int[] version, final String gameID, final String name, final Tool.Material toolMaterial,
			final int maxBounces, final float stableBounceHeight, final float jumpMovementFactorBuff, final boolean restrictJumpToGround,
			final PogoStick source, final MoldedItem grip) {
		super(version, gameID, name, source);
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
		return new ItemStack(PolycraftRegistry.getItem(this), size);
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
		if (fallDistance == 0)
			return 0;
		double motion = PolycraftMod.getVelocityRequiredToReachHeight(fallDistance * .5);
		if (motion < .2)
			return 0;
		return motion;
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Max Bounces", "Stable Bounce Height", "Jump Movement Buff", "Restrict Jump to Ground");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				PolycraftMod.numFormat.format(maxBounces),
				PolycraftMod.numFormat.format(stableBounceHeight),
				PolycraftMod.numFormat.format(jumpMovementFactorBuff),
				String.valueOf(restrictJumpToGround));
	}
}