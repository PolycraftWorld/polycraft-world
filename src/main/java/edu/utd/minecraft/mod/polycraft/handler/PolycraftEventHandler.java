package edu.utd.minecraft.mod.polycraft.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Point3d;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockBouncy;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.dynamiclights.PointLightSource;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemMoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.item.PolycraftMoldedItem;

public class PolycraftEventHandler extends PolycraftHandler {

	private static final float baseJumpMovementFactor = 0.02F;
	private static final float baseMovementSpeed = 0.1f;
	private static final float baseFlySpeed = 0.05f;
	private static final int baseFullAir = 300;

	private static final float flameThrowerVelocity = .5f;
	private static final int flameThrowerParticlesPerTick = 20;
	private static final double flameThrowerParticlesOffsetY = -.15;
	private static final boolean jetPackFailsafeEnabled = false; //TODO enabling has an issue with pogo sticks
	private static final int jetPackExhaustRangeY = 5;
	private static final int jetPackExhaustParticlesPerTick = 5;
	private static final double jetPackExhaustPlumeOffset = .05;
	private static final double jetPackExhaustDownwardVelocity = -.8;
	private final Collection<PointLightSource> jetPackExhaustDynamicLights = Lists.newLinkedList();
	private static final long jetPackSoundIntervalMillis = 100;
	private static final long scubaTankSoundIntervalMillis = 2000;
	private static final Map<Integer, Map<Point3d, Boolean>> spherePointsByRadius = Maps.newHashMap();

	private static final Map<Point3d, Boolean> getPhaseShifterPointsForRadius(final int radius) {
		Map<Point3d, Boolean> points = spherePointsByRadius.get(radius);
		if (points == null) {
			points = Maps.newHashMap();
			final Point3d center = new Point3d(0, 0, 0);
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						final Point3d target = new Point3d(x, y, z);
						//final double distanceFromCenter = target.distance(center);
						//if (distanceFromCenter <= radius)
						//	points.put(target, Math.ceil(distanceFromCenter) == radius);
						points.put(target, Math.abs(x) == radius || Math.abs(y) == radius || Math.abs(z) == radius);
					}
				}
			}
			spherePointsByRadius.put(radius, points);
		}
		return points;

	}

	private static class PlayerState {
		private final Collection<PointLightSource> flameThrowerDynamicLights = Lists.newLinkedList();
		private boolean flameThrowerLightsEnabled = false;
		private long jetPackLastSoundMillis = 0;
		private long scubaTankLastSoundMillis = 0;
		private boolean jetPackLightsEnabled = false;
		private int pogoStickPreviousContinuousActiveBounces = 0;
		private float pogoStickLastFallDistance = 0;
		private float bouncyBlockBounceHeight = 0;
		private boolean phaseShifted = false;
		private final Map<Point3d, Boolean> phaseShiftWorldPoints = Maps.newHashMap();
		private final Map<Point3d, Block> phaseShiftedBlocks = Maps.newHashMap();
	}

	private final Map<EntityPlayer, PlayerState> playerStates = new HashMap<EntityPlayer, PlayerState>();

	private synchronized PlayerState getPlayerState(final EntityPlayer player) {
		PlayerState playerState = playerStates.get(player);
		if (playerState == null) {
			playerState = new PlayerState();
			playerStates.put(player, playerState);
		}
		return playerState;
	}

	@SubscribeEvent
	public synchronized void onEntityLivingDeath(final LivingDeathEvent event) {
		if (isPlayer(event)) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			playerStates.remove(player);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public synchronized void onLivingUpdateEventServer(final LivingUpdateEvent event) {
		if (isPlayer(event)) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			final PlayerState playerState = getPlayerState(player);

			handleWeapons(event, player, playerState);
			handleFlightServer(event, player, playerState);
			handleBreathing(event, player, playerState);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public synchronized void onLivingUpdateEventClient(final LivingUpdateEvent event) {
		if (isPlayer(event)) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			final PlayerState playerState = getPlayerState(player);

			handleBouncingClient(event, player, playerState);
			handleWeapons(event, player, playerState);
			handleMovementSpeedClient(event, player, playerState);
			handleFlightClient(event, player, playerState);
			handleBreathing(event, player, playerState);
			handleSwimmingClient(event, player, playerState);
			handlePhaseShifterClient(event, player, playerState);
		}
	}

	@SubscribeEvent
	public synchronized void onPlayerFlyableFallEvent(final PlayerFlyableFallEvent event) {
		if (!jetPackFailsafeEnabled) {
			float fallDamage = event.distance - 3.0f;
			if (fallDamage > 0 && !checkCurrentEquippedItem(event.entityPlayer, ItemPhaseShifter.class)) {
				event.entityPlayer.attackEntityFrom(DamageSource.fall, fallDamage);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public synchronized void onLivingFallEvent(final LivingFallEvent event) {
		if (isPlayer(event)) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			final PlayerState playerState = getPlayerState(player);

			final Block blockUnderPlayer = getBlockUnderPlayer(player);
			final boolean onBouncyBlock = blockUnderPlayer instanceof BlockBouncy;
			if (checkCurrentEquippedItem(player, ItemPogoStick.class, true)) {
				final ItemStack currentItemStack = player.getCurrentEquippedItem();
				final ItemPogoStick pogoStick = ((ItemPogoStick) currentItemStack.getItem());
				currentItemStack.attemptDamageItem(1, random);
				playerState.pogoStickLastFallDistance = event.distance;
				if (event.distance > pogoStick.config.maxFallNoDamageHeight && !onBouncyBlock)
					event.distance *= PolycraftMod.itemPogoStickMaxFallExcedeDamageReduction;
				else
					event.distance = 0;
			}
			else if (onBouncyBlock) {
				final BlockBouncy bouncyBlock = (BlockBouncy) blockUnderPlayer;
				//if we are actively jumping
				if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump) && noScreen())
					playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
				//if we are supposed to return momentum while not actively jumping
				else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0)
					playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				event.distance = 0;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void handleBouncingClient(final LivingUpdateEvent event, final EntityPlayer player, final PlayerState playerState) {
		float jumpMovementFactor = baseJumpMovementFactor;
		if (checkCurrentEquippedItem(player, ItemPogoStick.class, true)) {
			final ItemPogoStick pogoStick = ((ItemPogoStick) player.getCurrentEquippedItem().getItem());
			jumpMovementFactor *= pogoStick.config.jumpMovementFactorBuff;
			if (!pogoStick.config.restrictJumpToGround || player.onGround) {
				final boolean playerActivelyBouncing = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindUseItem) && noScreen();
				final double motionY = pogoStick.config.getMotionY(playerState.pogoStickLastFallDistance, playerState.pogoStickPreviousContinuousActiveBounces, playerActivelyBouncing);
				if (motionY > 0)
					player.motionY = motionY;
				if (playerActivelyBouncing)
					playerState.pogoStickPreviousContinuousActiveBounces++;
				else
					playerState.pogoStickPreviousContinuousActiveBounces = 0;
			}
			playerState.bouncyBlockBounceHeight = 0;
		}
		else {
			playerState.pogoStickPreviousContinuousActiveBounces = 0;
			//if the player is on the ground and holding down jump, then wait for the jump to occur
			//(if we try to set the y velocity before the game jumps, it will override our velocity)
			if (playerState.bouncyBlockBounceHeight > 0 && !(player.onGround && GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump))) {
				final double motionY = PolycraftMod.getVelocityRequiredToReachHeight(playerState.bouncyBlockBounceHeight);
				if (motionY > .2)
					player.motionY = motionY;
				playerState.bouncyBlockBounceHeight = 0;
			}
		}
		playerState.pogoStickLastFallDistance = 0;

		if (player.jumpMovementFactor != jumpMovementFactor)
			player.jumpMovementFactor = jumpMovementFactor;
	}

	private void handleWeapons(final LivingUpdateEvent event, final EntityPlayer player, final PlayerState playerState) {
		boolean flameThrowerLightsEnabled = false;
		int flameThrowerRange = 0;
		if (checkCurrentEquippedItem(player, ItemFlameThrower.class)) {
			final ItemStack currentEquippedItemStack = player.getCurrentEquippedItem();
			ItemFlameThrower flameThrowerItem = (ItemFlameThrower) currentEquippedItemStack.getItem();
			if (player.isUsingItem() && flameThrowerItem.hasFuelRemaining(currentEquippedItemStack) && !player.isInWater()) {
				flameThrowerLightsEnabled = true;
				flameThrowerRange = flameThrowerItem.range;
				fireFlamethrower(player, playerState, currentEquippedItemStack, flameThrowerItem);
			}
		}
		updateFlameThrowerLights(player, playerState, flameThrowerLightsEnabled, flameThrowerRange);
	}

	private void fireFlamethrower(final EntityPlayer player, final PlayerState playerState, final ItemStack itemStack, final ItemFlameThrower flameThrowerItem) {
		if (player.worldObj.isRemote) {
			//make the pretties
			final double playerRotationYawRadians = Math.toRadians(player.rotationYaw - 90);
			final double playerRotationPitchRadians = Math.toRadians(player.rotationPitch - 90);
			final double originX = player.posX;
			final double originY = player.posY + flameThrowerParticlesOffsetY;
			final double originZ = player.posZ;
			for (int a = 0; a < flameThrowerParticlesPerTick; a++)
				player.worldObj.spawnParticle("flame", originX, originY, originZ,
						player.motionX + (1 - (random.nextDouble() - .5) / 2) * flameThrowerVelocity * Math.cos(playerRotationYawRadians) * Math.sin(playerRotationPitchRadians),
						player.motionY + (1 - (random.nextDouble() - .5) / 2) * -flameThrowerVelocity * Math.cos(playerRotationPitchRadians),
						player.motionZ + (1 - (random.nextDouble() - .5) / 2) * flameThrowerVelocity * Math.sin(playerRotationPitchRadians) * Math.sin(playerRotationYawRadians));
		}
		else {
			player.worldObj.playSoundAtEntity(player, PolycraftMod.getAssetName("flamethrower.ignite"), 1f, 1f);

			//burn the fuel
			flameThrowerItem.burnFuel(itemStack);

			//light blocks and entities on fire
			final List<Entity> closeEntities = player.worldObj.getEntitiesWithinAABB(Entity.class,
					AxisAlignedBB.getAABBPool().getAABB(
							player.posX - flameThrowerItem.range - flameThrowerItem.spread,
							player.posY - flameThrowerItem.range - flameThrowerItem.spread,
							player.posZ - flameThrowerItem.range - flameThrowerItem.spread,
							player.posX + flameThrowerItem.range + flameThrowerItem.spread,
							player.posY + flameThrowerItem.range + flameThrowerItem.spread,
							player.posZ + flameThrowerItem.range + flameThrowerItem.spread));

			final double playerRotationYawRadians = Math.toRadians(player.rotationYaw - 90);
			final double playerRotationPitchRadians = Math.toRadians(player.rotationPitch - 90);
			for (int i = 0; i <= flameThrowerItem.range; i++) {
				double pathX = player.posX + (i * Math.cos(playerRotationYawRadians) * Math.sin(playerRotationPitchRadians));
				double pathY = player.posY + (-i * Math.cos(playerRotationPitchRadians));
				double pathZ = player.posZ + (i * Math.sin(playerRotationPitchRadians) * Math.sin(playerRotationYawRadians));

				if (i > 1) {
					if (player.worldObj.isAirBlock((int) pathX, (int) pathY, (int) pathZ)) {
						player.worldObj.setBlock((int) pathX, (int) pathY, (int) pathZ, Blocks.fire);
					}
				}

				if (closeEntities != null && closeEntities.size() > 0) {
					for (final Entity entity : closeEntities)
						if (!entity.equals(player) && Math.abs(entity.posX - pathX) < flameThrowerItem.spread && Math.abs(entity.posY - pathY) < flameThrowerItem.spread
								&& Math.abs(entity.posZ - pathZ) < flameThrowerItem.spread) {
							if (!entity.isBurning())
								entity.setFire(flameThrowerItem.fireDuration);
							entity.attackEntityFrom(DamageSource.onFire, flameThrowerItem.damage);
						}
				}
			}
		}
	}

	private void updateFlameThrowerLights(final EntityPlayer player, final PlayerState playerState, final boolean enabled, final int range) {
		if (player.worldObj.isRemote) {
			if (playerState.flameThrowerDynamicLights.size() < range)
				for (int i = 0; i < range; i++)
					playerState.flameThrowerDynamicLights.add(new PointLightSource(player.worldObj));
			if (enabled) {
				final double playerRotationYawRadians = Math.toRadians(player.rotationYaw - 90);
				final double playerRotationPitchRadians = Math.toRadians(player.rotationPitch - 90);
				int i = 1;
				for (final PointLightSource source : playerState.flameThrowerDynamicLights) {
					source.update(i > range ? 0 : 15,
							player.posX + (i * Math.cos(playerRotationYawRadians) * Math.sin(playerRotationPitchRadians)),
							player.posY + (-i * Math.cos(playerRotationPitchRadians)),
							player.posZ + (i * Math.sin(playerRotationPitchRadians) * Math.sin(playerRotationYawRadians)));
					i++;
				}
			}

			if (playerState.flameThrowerLightsEnabled != enabled) {
				playerState.flameThrowerLightsEnabled = enabled;
				for (final PointLightSource source : playerState.flameThrowerDynamicLights)
					if (enabled)
						DynamicLights.addLightSource(source);
					else
						DynamicLights.removeLightSource(source);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void handleMovementSpeedClient(final LivingEvent event, final EntityPlayer player, final PlayerState playerState) {
		if (player.isEntityAlive()) {
			float movementSpeedBaseValue = baseMovementSpeed;
			final ItemStack bootsItemStack = player.getCurrentArmor(ArmorSlot.FEET.getInventoryArmorSlot());
			if (player.isInWater()) {
				if (checkItem(bootsItemStack, ItemScubaFins.class)) {
					movementSpeedBaseValue = baseMovementSpeed * (1 + ((ItemScubaFins) bootsItemStack.getItem()).swimSpeedBuff);
					boolean setVelocity = false;
					double motionX = 0;
					double motionY = player.motionY;
					double motionZ = 0;
					if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward)) {
						setVelocity = true;
						final double playerRotationRadians = Math.toRadians(player.rotationYaw + 90);
						motionX = movementSpeedBaseValue * Math.cos(playerRotationRadians);
						motionZ = movementSpeedBaseValue * Math.sin(playerRotationRadians);
					}
					if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump)) {
						setVelocity = true;
						motionY = movementSpeedBaseValue;
					}
					else if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
						setVelocity = true;
						motionY = -movementSpeedBaseValue;
					}

					if (setVelocity)
						player.setVelocity(motionX, motionY, motionZ);
				}
			}
			else {
				if (checkItem(bootsItemStack, ItemScubaFins.class)) {
					movementSpeedBaseValue = baseMovementSpeed * (1 + ((ItemScubaFins) bootsItemStack.getItem()).walkSpeedBuff);
				}
				else if (checkItem(bootsItemStack, ItemRunningShoes.class)) {
					movementSpeedBaseValue = baseMovementSpeed * (1 + ((ItemRunningShoes) bootsItemStack.getItem()).walkSpeedBuff);
				}
			}

			if (player.capabilities.getWalkSpeed() != movementSpeedBaseValue) {
				player.capabilities.setPlayerWalkSpeed(movementSpeedBaseValue);
			}
		}
	}

	@SideOnly(Side.SERVER)
	private void handleFlightServer(final LivingEvent event, final EntityPlayer player, final PlayerState playerState) {
		handleFlight(event, player, playerState);
	}

	@SideOnly(Side.CLIENT)
	private void handleFlightClient(final LivingEvent event, final EntityPlayer player, final PlayerState playerState) {
		if (handleFlight(event, player, playerState))
			player.capabilities.setFlySpeed(player.capabilities.allowFlying ? baseFlySpeed * (1 + ((ItemJetPack) player.getCurrentArmor(ArmorSlot.CHEST.getInventoryArmorSlot()).getItem()).flySpeedBuff) : baseFlySpeed);
	}

	private boolean handleFlight(final LivingEvent event, final EntityPlayer player, final PlayerState playerState) {
		boolean jetpackExhaustLightsEnabled = false;
		boolean allowFlyingChanged = false;
		if (player.isEntityAlive() && !player.capabilities.isCreativeMode && !checkCurrentEquippedItem(player, ItemPhaseShifter.class)) {
			final ItemStack jetPackItemStack = player.getCurrentArmor(ArmorSlot.CHEST.getInventoryArmorSlot());
			final ItemJetPack jetPackItem = checkItem(jetPackItemStack, ItemJetPack.class) ? (ItemJetPack) jetPackItemStack.getItem() : null;
			final boolean allowFlying = jetPackItem != null && ItemJetPack.hasFuelRemaining(jetPackItemStack) && !player.isInWater();
			allowFlyingChanged = player.capabilities.allowFlying != allowFlying;
			if (allowFlyingChanged) {
				if (!allowFlying)
					player.capabilities.isFlying = false;
				player.capabilities.allowFlying = allowFlying;
			}

			if (allowFlying && !player.onGround) {

				if (jetPackFailsafeEnabled && player.motionY < -1) {
					player.capabilities.isFlying = true; // force jet packs to turn on to break falls
				}

				if (player.capabilities.isFlying) {

					jetpackExhaustLightsEnabled = true;

					spawnJetpackExhaust(player, -.25);
					spawnJetpackExhaust(player, .25);

					if (!player.worldObj.isRemote) {
						if (jetPackItem.burnFuel(jetPackItemStack)) {
							player.fallDistance = 0;
							// cause an unstable motion to simulate the unpredictability of the exhaust direction
							player.setPosition(
									player.posX + ((random.nextDouble() / 5) - .1),
									player.posY + ((random.nextDouble() / 5) - .1),
									player.posZ + ((random.nextDouble() / 5) - .1));
						}

						final long currentMillis = System.currentTimeMillis();
						if (jetPackSoundIntervalMillis < currentMillis - playerState.jetPackLastSoundMillis) {
							playerState.jetPackLastSoundMillis = currentMillis;
							player.worldObj.playSoundAtEntity(player, PolycraftMod.getAssetName("jetpack.fly"), 1f, 1f);
						}
					}
				}
			}

			if (checkCurrentEquippedItem(player, ItemParachute.class)) {
				final float descendVelocity = ((ItemParachute) player.getCurrentEquippedItem().getItem()).descendVelocity;
				if (player.motionY < descendVelocity) {
					player.setVelocity(player.motionX * .99, descendVelocity, player.motionZ * .99);
					player.fallDistance = 0;
				}
			}
		}
		updateJetpackExhaustLights(player, playerState, jetpackExhaustLightsEnabled);

		return allowFlyingChanged;
	}

	private void spawnJetpackExhaust(final EntityPlayer player, final double offset) {
		if (player.worldObj.isRemote) {
			final double playerRotationRadians = Math.toRadians(player.rotationYaw);
			final double playerRotationSin = Math.sin(playerRotationRadians);
			final double playerRotationCos = Math.cos(playerRotationRadians);
			final double centerX = player.posX + (offset * playerRotationCos);
			final double centerY = player.posY - 1;
			final double centerZ = player.posZ + (offset * playerRotationSin);
			final double offsetX = playerRotationCos * jetPackExhaustPlumeOffset;
			final double offsetZ = playerRotationSin * jetPackExhaustPlumeOffset;
			for (int i = 0; i < jetPackExhaustParticlesPerTick; i++) {
				final double y = centerY - (i * .02);
				player.worldObj.spawnParticle("flame", centerX, y, centerZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
				player.worldObj.spawnParticle("flame", centerX - offsetX, y, centerZ - offsetZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
				player.worldObj.spawnParticle("flame", centerX + offsetX, y, centerZ + offsetZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
				player.worldObj.spawnParticle("flame", centerX - offsetX, y, centerZ + offsetZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
				player.worldObj.spawnParticle("flame", centerX + offsetX, y, centerZ - offsetZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);

				player.worldObj.spawnParticle("smoke", centerX, y, centerZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
				player.worldObj.spawnParticle("smoke", centerX - offsetX, y, centerZ - offsetZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
				player.worldObj.spawnParticle("smoke", centerX + offsetX, y, centerZ + offsetZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
				player.worldObj.spawnParticle("smoke", centerX - offsetX, y, centerZ + offsetZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
				player.worldObj.spawnParticle("smoke", centerX + offsetX, y, centerZ - offsetZ, -player.motionX, player.motionY + jetPackExhaustDownwardVelocity, -player.motionZ);
			}
		}
		else {
			//light blocks on fire in the exhaust plume
			for (int i = 0; i < jetPackExhaustRangeY; i++) {
				final int x = (int) player.posX;
				final int y = (int) player.posY - i - 2;
				final int z = (int) player.posZ - 1;
				if (player.worldObj.isAirBlock(x, y, z)) {
					player.worldObj.setBlock(x, y, z, Blocks.fire);
				}
			}

			//light entities on fire in the exhaust plume
			final List<Entity> closeEntities = player.worldObj.getEntitiesWithinAABB(Entity.class,
					AxisAlignedBB.getAABBPool().getAABB(player.posX - 1, player.posY - jetPackExhaustRangeY, player.posZ - 1, player.posX + 1, player.posY, player.posZ + 1));
			if (closeEntities != null && closeEntities.size() > 0)
				for (final Entity entity : closeEntities)
					if (!entity.equals(player) && !entity.isBurning())
						entity.setFire(Math.max(jetPackExhaustRangeY - (int) Math.round(player.posY - entity.posY), 1));
		}
	}

	private void updateJetpackExhaustLights(final EntityPlayer player, final PlayerState playerState, final boolean enabled) {
		if (player.worldObj.isRemote) {
			if (jetPackExhaustDynamicLights.size() == 0)
				for (int i = 0; i < jetPackExhaustRangeY; i++)
					jetPackExhaustDynamicLights.add(new PointLightSource(player.worldObj));
			if (enabled) {
				int i = 0;
				for (final PointLightSource source : jetPackExhaustDynamicLights)
					source.update(15, player.posX, player.posY - (i++), player.posZ);
			}

			if (playerState.jetPackLightsEnabled != enabled) {
				playerState.jetPackLightsEnabled = enabled;
				for (final PointLightSource source : jetPackExhaustDynamicLights)
					if (enabled)
						DynamicLights.addLightSource(source);
					else
						DynamicLights.removeLightSource(source);
			}
		}
	}

	private void handleBreathing(final LivingEvent event, final EntityPlayer player, final PlayerState playerState) {
		if (player.isEntityAlive() && player.isInWater()) {
			if (player.getAir() < baseFullAir) {
				final ItemStack scubaTankItemStack = player.getCurrentArmor(ArmorSlot.CHEST.getInventoryArmorSlot());
				if (checkItem(scubaTankItemStack, ItemScubaTank.class)) {
					final ItemScubaTank scubaTankItem = (ItemScubaTank) scubaTankItemStack.getItem();
					if (player.worldObj.isRemote) {
						if (scubaTankItem.consumeAir(scubaTankItemStack)) {
							player.setAir(baseFullAir);
						}
					}
					else {
						final long currentMillis = System.currentTimeMillis();
						if (scubaTankSoundIntervalMillis < currentMillis - playerState.scubaTankLastSoundMillis) {
							playerState.scubaTankLastSoundMillis = currentMillis;
							player.worldObj.playSoundAtEntity(player, PolycraftMod.getAssetName("scubatank.breathe"), 1f, 1f);
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void handleSwimmingClient(final LivingEvent event, final EntityPlayer player, final PlayerState playerState) {
		if (player.isEntityAlive() && player.isInWater()) {
			if (checkCurrentEquippedItem(player, ItemMoldedItem.class)) {
				final MoldedItem moldedItem = ((PolycraftMoldedItem) player.getCurrentEquippedItem().getItem()).getMoldedItem();
				if ("J".equals(moldedItem.source.gameID)) { //Life Preserver
					if (!GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump) &&
							!GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak))
						player.motionY = 0;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void handlePhaseShifterClient(final LivingEvent event, final EntityPlayer player, final PlayerState playerState) {
		if (player.isEntityAlive()) {
			if (checkCurrentEquippedItem(player, ItemPhaseShifter.class)) {
				final ItemPhaseShifter phaseShifter = (ItemPhaseShifter) player.getCurrentEquippedItem().getItem();
				if (!player.capabilities.isFlying) {
					if (!player.capabilities.isCreativeMode)
						player.capabilities.allowFlying = true;
					player.capabilities.isFlying = true;
					player.capabilities.setFlySpeed(baseFlySpeed * phaseShifter.flySpeedBuff);
					player.capabilities.disableDamage = true;
					playerState.phaseShifted = true;
				}

				if (!player.worldObj.isRemote) {
					playerState.phaseShiftWorldPoints.clear();
					for (final Entry<Point3d, Boolean> point : getPhaseShifterPointsForRadius(phaseShifter.radius).entrySet()) {
						Point3d worldPoint = new Point3d(
								(int) (Math.round(player.posX) + point.getKey().x),
								(int) (Math.round(player.posY - player.getYOffset()) + point.getKey().y),
								(int) (Math.round(player.posZ) + point.getKey().z));
						playerState.phaseShiftWorldPoints.put(worldPoint, point.getValue());
					}

					final Iterator<Entry<Point3d, Block>> phaseShiftedBlocks = playerState.phaseShiftedBlocks.entrySet().iterator();
					while (phaseShiftedBlocks.hasNext()) {
						final Entry<Point3d, Block> psb = phaseShiftedBlocks.next();
						final Point3d worldPoint = psb.getKey();
						if (!playerState.phaseShiftWorldPoints.containsKey(worldPoint)) {
							player.worldObj.setBlock((int) worldPoint.x, (int) worldPoint.y, (int) worldPoint.z, psb.getValue());
							phaseShiftedBlocks.remove();
						}
					}

					for (final Entry<Point3d, Boolean> worldPointEntry : playerState.phaseShiftWorldPoints.entrySet()) {
						final Point3d worldPoint = worldPointEntry.getKey();
						final Block block = player.worldObj.getBlock((int) worldPoint.x, (int) worldPoint.y, (int) worldPoint.z);
						if (!playerState.phaseShiftedBlocks.containsKey(worldPoint))
							playerState.phaseShiftedBlocks.put(worldPoint, block);
						//edge
						if (worldPointEntry.getValue()) {
							if (!block.equals(phaseShifter.boundaryBlock))
								player.worldObj.setBlock((int) worldPoint.x, (int) worldPoint.y, (int) worldPoint.z, phaseShifter.boundaryBlock);
						}
						//inner
						else if (!block.equals(Blocks.air))
							player.worldObj.setBlockToAir((int) worldPoint.x, (int) worldPoint.y, (int) worldPoint.z);
					}
					playerState.phaseShiftWorldPoints.clear();
				}
			}
			else {
				if (playerState.phaseShifted) {
					player.capabilities.disableDamage = false;
					player.capabilities.isFlying = false;
					player.capabilities.setFlySpeed(baseFlySpeed);
					if (!player.capabilities.isCreativeMode)
						player.capabilities.allowFlying = false;
					playerState.phaseShifted = false;
				}

				if (!player.worldObj.isRemote) {
					if (playerState.phaseShiftedBlocks.size() > 0) {
						int k;
						for (k = 63; !player.worldObj.isAirBlock((int) player.posX, k + 1, (int) player.posZ); ++k)
							;
						player.posY = k;
						player.motionY = 0;
						for (final Entry<Point3d, Block> psb : playerState.phaseShiftedBlocks.entrySet()) {
							final Point3d worldPoint = psb.getKey();
							player.worldObj.setBlock((int) worldPoint.x, (int) worldPoint.y, (int) worldPoint.z, psb.getValue());
						}
						playerState.phaseShiftedBlocks.clear();
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static boolean noScreen() {
		return Minecraft.getMinecraft().currentScreen == null;
	}

	private static boolean isPlayer(final LivingEvent event) {
		return event.entityLiving instanceof EntityPlayer;
	}
}