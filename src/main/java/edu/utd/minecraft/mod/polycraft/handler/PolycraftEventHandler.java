package edu.utd.minecraft.mod.polycraft.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.dynamiclights.PointLightSource;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;

public class PolycraftEventHandler {
	private final Logger logger = LogManager.getLogger();

	private static final Random random = new Random();

	private static final float baseMovementSpeed = 0.1f;
	private static final float baseFlySpeed = 0.05f;
	private static final int baseFullAir = 300;

	private static final float flameThrowerVelocity = .5f;
	private static final int flameThrowerParticlesPerTick = 20;
	private static final double flameThrowerParticlesOffsetY = -.15;
	private static final boolean jetPackFailsafeEnabled = true;
	private static final int jetPackExhaustRangeY = 5;
	private static final int jetPackExhaustParticlesPerTick = 5;
	private static final double jetPackExhaustPlumeOffset = .05;
	private static final double jetPackExhaustDownwardVelocity = -.8;
	private final Collection<PointLightSource> jetPackExhaustDynamicLights = new LinkedList<PointLightSource>();
	private static final long jetPackSoundIntervalMillis = 100;
	private static final Map<Integer, String> jetPackLandingWarnings = new LinkedHashMap<Integer, String>();
	static {
		jetPackLandingWarnings.put(30, "might want to start thinking about landing...");
		jetPackLandingWarnings.put(20, "daredevil are we?");
		jetPackLandingWarnings.put(10, "we are way low on fuel Mav!");
		jetPackLandingWarnings.put(5, "vapor lock!!");
		jetPackLandingWarnings.put(1, "EJECT EJECT EJECT!!!");
	}
	private static final long scubaTankSoundIntervalMillis = 2000;

	private class PlayerState {
		private final Collection<PointLightSource> flameThrowerDynamicLights = new LinkedList<PointLightSource>();
		private boolean flameThrowerLightsEnabled = false;
		private long jetPackLastSoundMillis = 0;
		private long scubaTankLastSoundMillis = 0;
		private int flameThrowerLastFuelDisplayPercent = 0;
		private int jetPackLastFuelDisplayPercent = 0;
		private int scubaTankLastAirDisplayPercent = 0;
		private boolean jetPackLightsEnabled = false;
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
		if (event.entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			playerStates.remove(player);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public synchronized void onLivingUpdateEventServer(final LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			final PlayerState playerState = getPlayerState(player);
			handleWeapons(event, player, playerState);
			handleFlightServer(event, player, playerState);
			handleBreathingServer(event, player, playerState);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public synchronized void onLivingUpdateEventClient(final LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			final PlayerState playerState = getPlayerState(player);
			handleWeapons(event, player, playerState);
			handleMovementSpeedClient(event, player, playerState);
			handleFlightClient(event, player, playerState);
		}
	}

	@SubscribeEvent
	public void onPlayerFlyableFallEvent(final PlayerFlyableFallEvent event) {
		if (!jetPackFailsafeEnabled) {
			float fallDamage = event.distance - 3.0f;
			if (fallDamage > 0) {
				event.entityPlayer.attackEntityFrom(DamageSource.fall, fallDamage);
			}
		}
	}

	private void handleWeapons(final LivingUpdateEvent event, final EntityPlayer player, final PlayerState playerState) {
		boolean flameThrowerLightsEnabled = false;
		int flameThrowerRange = 0;
		final ItemStack currentEquippedItemStack = player.getCurrentEquippedItem();
		if (currentEquippedItemStack != null) {
			if (currentEquippedItemStack.getItem() instanceof ItemFlameThrower) {
				ItemFlameThrower flameThrowerItem = (ItemFlameThrower) currentEquippedItemStack.getItem();
				if (player.isUsingItem() && flameThrowerItem.hasFuelRemaining(currentEquippedItemStack) && !player.isInWater()) {
					flameThrowerLightsEnabled = true;
					flameThrowerRange = flameThrowerItem.range;
					fireFlamethrower(player, playerState, currentEquippedItemStack, flameThrowerItem);
				}
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
			final int fuelRemainingPercent = flameThrowerItem.getFuelRemainingPercent(itemStack);
			if (playerState.flameThrowerLastFuelDisplayPercent != fuelRemainingPercent) {
				player.addChatMessage(new ChatComponentText(fuelRemainingPercent + "% flame thrower fuel remaining"));
				playerState.flameThrowerLastFuelDisplayPercent = fuelRemainingPercent;
			}

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
				if (bootsItemStack != null && bootsItemStack.getItem() instanceof ItemScubaFins) {
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
				if (bootsItemStack != null && bootsItemStack.getItem() instanceof ItemScubaFins) {
					movementSpeedBaseValue = baseMovementSpeed * (1 + ((ItemScubaFins) bootsItemStack.getItem()).walkSpeedBuff);
				}
				else if (bootsItemStack != null && bootsItemStack.getItem() instanceof ItemRunningShoes) {
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
		if (player.isEntityAlive() && !player.capabilities.isCreativeMode) {
			final ItemStack jetPackItemStack = player.getCurrentArmor(ArmorSlot.CHEST.getInventoryArmorSlot());
			final ItemJetPack jetPackItem =
					(jetPackItemStack != null && jetPackItemStack.getItem() instanceof ItemJetPack) ? (ItemJetPack) jetPackItemStack.getItem() : null;
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
							final int fuelRemainingPercent = jetPackItem.getFuelRemainingPercent(jetPackItemStack);
							if (playerState.jetPackLastFuelDisplayPercent != fuelRemainingPercent) {
								final String warning = jetPackLandingWarnings.get(fuelRemainingPercent);
								player.addChatMessage(new ChatComponentText(fuelRemainingPercent + "% jet pack fuel remaining" + (warning == null ? "" : ", " + warning)));
								playerState.jetPackLastFuelDisplayPercent = fuelRemainingPercent;
							}

							// cause an unstable motion to simulate the unpredictability of the exhaust direction
							player.setPosition(
									player.posX + ((random.nextDouble() / 5) - .1),
									player.posY + ((random.nextDouble() / 5) - .1),
									player.posZ + ((random.nextDouble() / 5) - .1));
						}
						else if (playerState.jetPackLastFuelDisplayPercent != -1) {
							player.addChatMessage(new ChatComponentText("Out of fuel, hope you packed a parachute..."));
							playerState.jetPackLastFuelDisplayPercent = -1;
						}

						final long currentMillis = System.currentTimeMillis();
						if (jetPackSoundIntervalMillis < currentMillis - playerState.jetPackLastSoundMillis) {
							playerState.jetPackLastSoundMillis = currentMillis;
							player.worldObj.playSoundAtEntity(player, PolycraftMod.getAssetName("jetpack.fly"), 1f, 1f);
						}
					}
				}
			}

			final ItemStack parachuteItemStack = player.getCurrentEquippedItem();
			if (parachuteItemStack != null && parachuteItemStack.getItem() instanceof ItemParachute) {
				final float descendVelocity = ((ItemParachute) parachuteItemStack.getItem()).descendVelocity;
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

	@SideOnly(Side.SERVER)
	private void handleBreathingServer(final LivingEvent event, final EntityPlayer player, final PlayerState playerState) {
		if (player.isEntityAlive() && player.isInWater() && player.getAir() < baseFullAir) {
			final ItemStack scubaTankItemStack = player.getCurrentArmor(2);
			if (scubaTankItemStack != null && scubaTankItemStack.getItem() instanceof ItemScubaTank) {
				final ItemScubaTank scubaTankItem = (ItemScubaTank) scubaTankItemStack.getItem();

				if (scubaTankItem.consumeAir(scubaTankItemStack)) {
					player.setAir(baseFullAir);
					final int airRemainingPercent = scubaTankItem.getAirRemainingPercent(scubaTankItemStack);
					if (playerState.scubaTankLastAirDisplayPercent != airRemainingPercent) {
						player.addChatMessage(new ChatComponentText(airRemainingPercent + "% air remaining"));
						playerState.scubaTankLastAirDisplayPercent = airRemainingPercent;
					}
				}

				final long currentMillis = System.currentTimeMillis();
				if (scubaTankSoundIntervalMillis < currentMillis - playerState.scubaTankLastSoundMillis) {
					playerState.scubaTankLastSoundMillis = currentMillis;
					player.worldObj.playSoundAtEntity(player, PolycraftMod.getAssetName("scubatank.breathe"), 1f, 1f);
				}
			}
		}
	}
}