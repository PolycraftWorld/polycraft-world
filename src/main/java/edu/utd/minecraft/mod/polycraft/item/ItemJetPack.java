package edu.utd.minecraft.mod.polycraft.item;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ItemJetPack extends PolycraftArmorChest {
	private static final String IGNITED = "ignited";
	private static final String FUEL_UNITS_REMAINING = "fuelUnitsRemaining";
	private static final ArmorSlot armorSlot = ArmorSlot.CHEST;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, armorSlot, ItemJetPack.class);
	}

	public static ItemJetPack getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, armorSlot);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItemStack(player, armorSlot);
	}

	public static boolean allowsFlying(final EntityPlayer player) {
		return !ItemPhaseShifter.isEquipped(player) && !player.isInWater() && isEquipped(player) && getEquippedItem(player).hasFuelRemaining(getEquippedItemStack(player));
	}

	public static double getFuelRemainingPercent(final EntityPlayer player) {
		return getEquippedItem(player).getFuelRemainingPercent(getEquippedItemStack(player));
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

	public static void randomizePosition(final EntityPlayer player, final Random random) {
		player.setPosition(
				player.posX + ((random.nextDouble() - .5) / 10),
				player.posY + ((random.nextDouble() - .5) / 10),
				player.posZ + ((random.nextDouble() - .5) / 10));
	}

	private static final int exhaustRangeY = 5;
	private static final double exhaustPlumeOffset = .05;
	private static final int exhaustParticlesPerTick = 5;
	private static final double exhaustDownwardVelocity = -.8;
	private static final String exhaustParticleSmoke = "smoke";
	private static final String exhaustParticleFlame = "flame";

	public static Collection<PointLightSource> createLightSources(final World world) {
		final Collection<PointLightSource> lightSources = Lists.newLinkedList();
		for (int i = 0; i < exhaustRangeY; i++)
			lightSources.add(new PointLightSource(world));
		return lightSources;
	}

	public static void createExhaust(final EntityPlayer player, final World world, final Collection<PointLightSource> lightSources) {
		spawnExhaustParticles(player, world, -.25);
		spawnExhaustParticles(player, world, .25);
		int i = 0;
		for (final PointLightSource source : lightSources)
			source.update(15, player.posX, player.posY - (i++), player.posZ);
	}

	private static void spawnExhaustParticles(final EntityPlayer player, final World world, final double offset) {
		final double playerRotationRadians = Math.toRadians(player.rotationYaw);
		final double playerRotationSin = Math.sin(playerRotationRadians);
		final double playerRotationCos = Math.cos(playerRotationRadians);
		final double centerX = player.posX + (offset * playerRotationCos);
		final double centerY = player.posY - 1;
		final double centerZ = player.posZ + (offset * playerRotationSin);
		final double offsetX = playerRotationCos * exhaustPlumeOffset;
		final double offsetZ = playerRotationSin * exhaustPlumeOffset;
		for (int i = 0; i < exhaustParticlesPerTick; i++) {
			final double y = centerY - (i * .02);
			world.spawnParticle(exhaustParticleFlame, centerX, y, centerZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
			world.spawnParticle(exhaustParticleFlame, centerX - offsetX, y, centerZ - offsetZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
			world.spawnParticle(exhaustParticleFlame, centerX + offsetX, y, centerZ + offsetZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
			world.spawnParticle(exhaustParticleFlame, centerX - offsetX, y, centerZ + offsetZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
			world.spawnParticle(exhaustParticleFlame, centerX + offsetX, y, centerZ - offsetZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);

			world.spawnParticle(exhaustParticleSmoke, centerX, y, centerZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
			world.spawnParticle(exhaustParticleSmoke, centerX - offsetX, y, centerZ - offsetZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
			world.spawnParticle(exhaustParticleSmoke, centerX + offsetX, y, centerZ + offsetZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
			world.spawnParticle(exhaustParticleSmoke, centerX - offsetX, y, centerZ + offsetZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
			world.spawnParticle(exhaustParticleSmoke, centerX + offsetX, y, centerZ - offsetZ, -player.motionX, player.motionY + exhaustDownwardVelocity, -player.motionZ);
		}
	}

	public static void dealExhaustDamage(final EntityPlayer player, final World world) {
		//TODO doesn't seem to work well in multiplayer
		//light blocks on fire in the exhaust plume
		/*
		final int x = (int) Math.floor(player.posX);
		final int z = (int) Math.floor(player.posZ);
		for (int i = 2; i < exhaustRangeY; i++) {
			final int y = (int) Math.floor(player.posY - player.getYOffset()) - i;
			if (world.isAirBlock(x, y, z) && !world.isAirBlock(x, y - 1, z)) {
				world.setBlock(x, y, z, Blocks.fire);
				break;
			}
		}
		*/

		//light entities on fire in the exhaust plume
		final ItemJetPack equippedItem = getEquippedItem(player);
		final List<Entity> closeEntities = world.getEntitiesWithinAABB(Entity.class,
				AxisAlignedBB.getAABBPool().getAABB(player.posX - 1, player.posY - exhaustRangeY, player.posZ - 1, player.posX + 1, player.posY, player.posZ + 1));
		if (closeEntities != null && closeEntities.size() > 0)
			for (final Entity entity : closeEntities) {
				if (!entity.equals(player)) {
					if (!entity.isBurning())
						entity.setFire(Math.max(exhaustRangeY - (int) Math.round(player.posY - entity.posY), 1));
					entity.attackEntityFrom(DamageSource.onFire, equippedItem.damage);
				}
			}

	}

	public final int fuelUnitsFull;
	public final int fuelUnitsBurnPerTick;
	public final float flySpeedBuff;
	public final int damage;

	public ItemJetPack(final CustomObject config) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.CHAIN);
		this.setTextureName(PolycraftMod.getAssetName("jet_pack"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.fuelUnitsFull = config.params.getInt(0);
		this.fuelUnitsBurnPerTick = config.params.getInt(1);
		this.flySpeedBuff = config.params.getFloat(2);
		this.damage = config.params.getInt(3);
	}

	@Override
	public void addInformation(final ItemStack itemStack, final EntityPlayer entityPlayer, final List par3List, final boolean par4) {
		double percent = getFuelRemainingPercent(itemStack);
		if (percent > 0)
			par3List.add(String.format("%1$.1f%% fuel remaining", percent * 100));
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/jet_pack_layer_1.png");
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
		PolycraftItemHelper.setInteger(itemStack, FUEL_UNITS_REMAINING, fuelUnitsRemaining);
		return fuelUnitsRemaining > 0;
	}

	private double getFuelRemainingPercent(final ItemStack itemStack) {
		if (fuelUnitsFull > 0)
			return (double) getFuelUnitsRemaining(itemStack) / fuelUnitsFull;
		return 0;
	}
}
