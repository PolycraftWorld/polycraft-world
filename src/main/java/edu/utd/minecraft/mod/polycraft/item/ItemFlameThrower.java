package edu.utd.minecraft.mod.polycraft.item;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ItemFlameThrower extends PolycraftUtilityItem {
	private static final String IGNITED = "ignited";
	private static final String FUEL_UNITS_REMAINING = "fuelUnitsRemaining";

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemFlameThrower.class);
	}

	public static ItemFlameThrower getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return player.getCurrentEquippedItem();
	}

	public static boolean allowsFiring(final EntityPlayer player) {
		return !player.isInWater() && isEquipped(player) && getEquippedItem(player).hasFuelRemaining(getEquippedItemStack(player));
	}

	public static double getFuelRemainingPercent(final EntityPlayer player) {
		return isEquipped(player) ? getEquippedItem(player).getFuelRemainingPercent(getEquippedItemStack(player)) : 0;
	}

	public static boolean getIgnited(final EntityPlayer player) {
		return isEquipped(player) && PolycraftItemHelper.getBoolean(getEquippedItemStack(player), IGNITED, false);
	}

	public static void setIgnited(final EntityPlayer player, final boolean ignited) {
		if (isEquipped(player))
			PolycraftItemHelper.setBoolean(getEquippedItemStack(player), IGNITED, ignited);
	}

	public static boolean burnFuel(final EntityPlayer player) {
		return getEquippedItem(player).burnFuel(getEquippedItemStack(player));
	}

	private static final int flameLightSourcesMax = 15;
	private static final int flameParticlesPerTick = 20;
	private static final double flameParticlesRandomSpread = .25; //lowe numbers mean less random spread
	private static final float flameParticleVelocity = .5f;
	private static final String flameParticleFlame = "flame";

	public static Collection<PointLightSource> createLightSources(final World world) {
		final Collection<PointLightSource> lightSources = Lists.newLinkedList();
		for (int i = 0; i < flameLightSourcesMax; i++)
			lightSources.add(new PointLightSource(world));
		return lightSources;
	}

	public static void createFlames(final EntityPlayer player, final WorldClient world, final Random random, final Collection<PointLightSource> lightSources, final boolean playerOnCurrentClient) {
		//make the pretties
		final double playerRotationYawRadians = Math.toRadians(player.rotationYaw - 90);
		final double playerRotationPitchRadians = Math.toRadians(player.rotationPitch - 90);
		final double unitVecX = Math.cos(playerRotationYawRadians) * Math.sin(playerRotationPitchRadians);
		final double unitVecY = -Math.cos(playerRotationPitchRadians);
		final double unitVecZ = Math.sin(playerRotationPitchRadians) * Math.sin(playerRotationYawRadians);
		final double baseMotionX = player.motionX + (flameParticleVelocity * unitVecX);
		final double baseMotionY = (player.onGround ? 0 : player.motionY) + (flameParticleVelocity * unitVecY);
		final double baseMotionZ = player.motionZ + (flameParticleVelocity * unitVecZ);
		final double originX = player.posX + (unitVecX * .5);
		//for some reason other players perceive the particles coming out lower in multiplayer, so adjust by the offset
		final double originY = (playerOnCurrentClient ? player.posY : player.posY - player.getYOffset()) + (unitVecY * .5);
		final double originZ = player.posZ + (unitVecZ * .5);

		for (int a = 0; a < flameParticlesPerTick; a++)
			world.spawnParticle(flameParticleFlame, originX, originY, originZ,
					baseMotionX + ((random.nextDouble() - .5) * flameParticlesRandomSpread),
					baseMotionY + ((random.nextDouble() - .5) * flameParticlesRandomSpread),
					baseMotionZ + ((random.nextDouble() - .5) * flameParticlesRandomSpread));

		int i = 0;
		for (final PointLightSource source : lightSources)
			if (i++ > flameLightSourcesMax)
				source.update(0, 0, 0, 0);
			else
				source.update(15, originX + (i * unitVecX), originY + (i * unitVecY), originZ + (i * unitVecZ));
	}

	public static void dealFlameDamage(final EntityPlayer player, final World world) {
		//light blocks and entities on fire
		final ItemFlameThrower flameThrowerItem = getEquippedItem(player);
		
	//FIXME: get the flamethrower to work in a fast way	
//		final List<Entity> closeEntities = player.worldObj.getEntitiesWithinAABB(Entity.class,
//				AxisAlignedBB.getBoundingBox(
//						player.posX - flameThrowerItem.range - flameThrowerItem.spread,
//						player.posY - flameThrowerItem.range - flameThrowerItem.spread,
//						player.posZ - flameThrowerItem.range - flameThrowerItem.spread,
//						player.posX + flameThrowerItem.range + flameThrowerItem.spread,
//						player.posY + flameThrowerItem.range + flameThrowerItem.spread,
//						player.posZ + flameThrowerItem.range + flameThrowerItem.spread));

		final double playerRotationYawRadians = Math.toRadians(player.rotationYaw - 90);
		final double playerRotationPitchRadians = Math.toRadians(player.rotationPitch - 90);
		for (int i = 0; i <= flameThrowerItem.range; i++) {
			double pathX = player.posX + (i * Math.cos(playerRotationYawRadians) * Math.sin(playerRotationPitchRadians));
			double pathY = player.posY + (-i * Math.cos(playerRotationPitchRadians));
			double pathZ = player.posZ + (i * Math.sin(playerRotationPitchRadians) * Math.sin(playerRotationYawRadians));

			if (i > 3) {
				if (player.worldObj.isAirBlock((int) pathX, (int) pathY, (int) pathZ)) {
					player.worldObj.setBlock((int) pathX, (int) pathY, (int) pathZ, Blocks.fire);
				}
			}
//FIXME: same as above
//			if (closeEntities != null && closeEntities.size() > 0) {
//				for (final Entity entity : closeEntities)
//					if (!entity.equals(player) && Math.abs(entity.posX - pathX) < flameThrowerItem.spread && Math.abs(entity.posY - pathY) < flameThrowerItem.spread
//							&& Math.abs(entity.posZ - pathZ) < flameThrowerItem.spread) {
//						if (!entity.isBurning())
//							entity.setFire(flameThrowerItem.fireDuration);
//						entity.attackEntityFrom(DamageSource.onFire, flameThrowerItem.damage);
//					}
//			}
		}
	}

	public final int fuelUnitsFull;
	public final int fuelUnitsBurnPerTick;
	public final int range;
	public final int spread;
	public final int fireDuration;
	public final int damage;

	public ItemFlameThrower(final CustomObject config, String iconName) {
		this.setTextureName(PolycraftMod.getAssetName(iconName));
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setMaxDamage(100);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.fuelUnitsFull = config.params.getInt(0);
		this.fuelUnitsBurnPerTick = config.params.getInt(1);
		this.range = config.params.getInt(2);
		this.spread = config.params.getInt(3);
		this.fireDuration = config.params.getInt(4);
		this.damage = config.params.getInt(5);
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack par1ItemStack, final World par2World, final EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	@Override
	public int getMaxItemUseDuration(final ItemStack par1ItemStack) {
		return 10;
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		double percent = getFuelRemainingPercent(itemStack);
		if (percent > 0)
			par3List.add(String.format("%1$.1f%% fuel remaining", percent * 100));
	}

	private void setFuelUnitsRemaining(final ItemStack itemStack, int fuelUnitsRemaining) {
		PolycraftItemHelper.setInteger(itemStack, FUEL_UNITS_REMAINING, fuelUnitsRemaining);
	}

	private int getFuelUnitsRemaining(final ItemStack itemStack) {
		return PolycraftItemHelper.getInteger(itemStack, FUEL_UNITS_REMAINING, fuelUnitsFull);
	}

	private boolean hasFuelRemaining(final ItemStack itemStack) {
		return getFuelUnitsRemaining(itemStack) > 0;
	}

	private boolean burnFuel(final ItemStack itemStack) {
		int fuelUnitsRemaining = getFuelUnitsRemaining(itemStack) - fuelUnitsBurnPerTick;
		if (fuelUnitsRemaining < 0)
			fuelUnitsRemaining = 0;
		setFuelUnitsRemaining(itemStack, fuelUnitsRemaining);
		return fuelUnitsRemaining > 0;
	}

	private double getFuelRemainingPercent(final ItemStack itemStack) {
		if (fuelUnitsFull > 0)
			return (double) getFuelUnitsRemaining(itemStack) / fuelUnitsFull;
		return 0;
	}
}
