package edu.utd.minecraft.mod.polycraft.item;

import java.util.Collection;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityFlameThrowerProjectile;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ItemFlameThrower extends ItemFueledProjectileLauncher {

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemFlameThrower.class);
	}

	public static boolean allowsActivation(final EntityPlayer player) {
		return !player.isInWater() && isEquipped(player) && getEquippedItem(player).hasFuelRemaining(getEquippedItemStack(player));
	}

	private static final int flameLightSourcesMax = 15;
	private static final double flameProjectilesRandomSpread = .25; //lower numbers mean less random spread
	private static final float flameParticleVelocity = 1f;

	public static Collection<PointLightSource> createLightSources(final World world) {
		final Collection<PointLightSource> lightSources = Lists.newLinkedList();
		for (int i = 0; i < flameLightSourcesMax; i++)
			lightSources.add(new PointLightSource(world));
		return lightSources;
	}

	public final int range;
	public final int spread;
	public final int fireDuration;
	public final int damage;

	public ItemFlameThrower(final CustomObject config, String iconName) {
		super(config, iconName);
		this.range = config.params.getInt(2);
		this.spread = config.params.getInt(3);
		this.fireDuration = config.params.getInt(4);
		this.damage = config.params.getInt(5);
	}

	public void spawnProjectiles(final EntityPlayer player, final World world, final Random random) {
		final double playerRotationYawRadians = Math.toRadians(player.rotationYaw - 90);
		final double playerRotationPitchRadians = Math.toRadians(player.rotationPitch - 90);
		final double unitVecX = Math.cos(playerRotationYawRadians) * Math.sin(playerRotationPitchRadians);
		final double unitVecY = -Math.cos(playerRotationPitchRadians);
		final double unitVecZ = Math.sin(playerRotationPitchRadians) * Math.sin(playerRotationYawRadians);
		final double baseMotionX = player.motionX + (flameParticleVelocity * unitVecX);
		final double baseMotionY = (player.onGround ? 0 : player.motionY) + (flameParticleVelocity * unitVecY);
		final double baseMotionZ = player.motionZ + (flameParticleVelocity * unitVecZ);
		//for some reason other players perceive the particles coming out lower in multiplayer, so adjust by the offset
		final double originY = (player.posY - player.getYOffset() + 1) + (unitVecY * .5);

		final EntityFlameThrowerProjectile fireball = new EntityFlameThrowerProjectile(this, world, player, originY,
				baseMotionX + ((random.nextDouble() - .5) * flameProjectilesRandomSpread),
				baseMotionY + ((random.nextDouble() - .5) * flameProjectilesRandomSpread),
				baseMotionZ + ((random.nextDouble() - .5) * flameProjectilesRandomSpread));
		world.spawnEntityInWorld(fireball);
	}
}
