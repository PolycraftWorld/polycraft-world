package edu.utd.minecraft.mod.polycraft.handler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
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
	private static boolean jetPackFailsafeEnabled = true;
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

	private final Collection<PointLightSource> flameThrowerDynamicLights = new LinkedList<PointLightSource>();
	private boolean flameThrowerLightsEnabled = false;
	private long jetPackLastSoundMillis = 0;
	private long scubaTankLastSoundMillis = 0;
	private int flameThrowerLastFuelDisplayPercent = 0;
	private int jetPackLastFuelDisplayPercent = 0;
	private int scubaTankLastAirDisplayPercent = 0;
	private boolean jetPackLightsEnabled = false;

	@SubscribeEvent
	public synchronized void onEntityLivingDeath(final LivingDeathEvent event) {
		//if (event.entityLiving instanceof EntityPlayer) {
		//	final EntityPlayer player = (EntityPlayer) event.entity;
		//}
	}

	@SubscribeEvent
	public synchronized void onLivingUpdateEvent(final LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			handleWeapons(event, player);
			handleMovementSpeed(event, player);
			handleFlight(event, player);
			handleBreathing(event, player);
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

	private void handleWeapons(final LivingUpdateEvent event, final EntityPlayer player) {
		boolean flameThrowerLightsEnabled = false;
		int flameThrowerRange = 0;
		final ItemStack currentEquippedItemStack = player.getCurrentEquippedItem();
		if (currentEquippedItemStack != null) {
			if (currentEquippedItemStack.getItem() instanceof ItemFlameThrower) {
				ItemFlameThrower flameThrowerItem = (ItemFlameThrower) currentEquippedItemStack.getItem();
				if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindUseItem) && flameThrowerItem.hasFuelRemaining(currentEquippedItemStack) && !player.isInWater() && Minecraft.getMinecraft().currentScreen == null) {
					flameThrowerLightsEnabled = true;
					flameThrowerRange = flameThrowerItem.range;

					//burn the fuel
					flameThrowerItem.burnFuel(currentEquippedItemStack);
					final int fuelRemainingPercent = flameThrowerItem.getFuelRemainingPercent(currentEquippedItemStack);
					if (flameThrowerLastFuelDisplayPercent != fuelRemainingPercent) {
						player.addChatMessage(new ChatComponentText(fuelRemainingPercent + "% flame thrower fuel remaining"));
						flameThrowerLastFuelDisplayPercent = fuelRemainingPercent;
					}

					//make the pretties
					player.worldObj.playSoundAtEntity(player, PolycraftMod.MODID + ":flamethrower.ignite", 1f, 1f);
					spawnFlamethrowerParticles(player);

					//light blocks and entities on fire
					final List<Entity> closeEntities = player.worldObj.getEntitiesWithinAABB(Entity.class,
							AxisAlignedBB.getAABBPool().getAABB(
									player.posX - flameThrowerRange, player.posY - flameThrowerRange, player.posZ - flameThrowerRange,
									player.posX + flameThrowerRange, player.posY + flameThrowerRange, player.posZ + flameThrowerRange));

					final double playerRotationYawRadians = Math.toRadians(player.rotationYaw - 90);
					final double playerRotationPitchRadians = Math.toRadians(player.rotationPitch - 90);
					for (int i = 1; i < flameThrowerRange; i++) {
						double pathX = player.posX + (i * Math.cos(playerRotationYawRadians) * Math.sin(playerRotationPitchRadians));
						double pathY = player.posY + (-i * Math.cos(playerRotationPitchRadians));
						double pathZ = player.posZ + (i * Math.sin(playerRotationPitchRadians) * Math.sin(playerRotationYawRadians));

						final Block burnBlock = player.worldObj.getBlock((int) pathX, (int) pathY, (int) pathZ);
						if (burnBlock != null && burnBlock.isAir(player.worldObj, (int) pathX, (int) pathY, (int) pathZ)) {
							player.worldObj.setBlock((int) pathX, (int) pathY, (int) pathZ, Blocks.fire);
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
		}
		updateFlameThrowerLights(player, flameThrowerLightsEnabled, flameThrowerRange);
	}

	private void spawnFlamethrowerParticles(final EntityPlayer player) {
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

	private void updateFlameThrowerLights(final EntityPlayer player, final boolean enabled, final int range) {
		if (flameThrowerDynamicLights.size() == 0)
			for (int i = 0; i < range; i++)
				flameThrowerDynamicLights.add(new PointLightSource(player.worldObj));
		if (enabled) {
			final double playerRotationYawRadians = Math.toRadians(player.rotationYaw - 90);
			final double playerRotationPitchRadians = Math.toRadians(player.rotationPitch - 90);
			int i = 1;
			for (final PointLightSource source : flameThrowerDynamicLights) {
				source.update(15,
						player.posX + (i * Math.cos(playerRotationYawRadians) * Math.sin(playerRotationPitchRadians)),
						player.posY + (-i * Math.cos(playerRotationPitchRadians)),
						player.posZ + (i * Math.sin(playerRotationPitchRadians) * Math.sin(playerRotationYawRadians)));
				i++;
			}
		}

		if (flameThrowerLightsEnabled != enabled) {
			flameThrowerLightsEnabled = enabled;
			for (final PointLightSource source : flameThrowerDynamicLights)
				if (enabled)
					DynamicLights.addLightSource(source);
				else
					DynamicLights.removeLightSource(source);
		}
	}

	private void handleMovementSpeed(final LivingEvent event, final EntityPlayer player) {
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

	private void handleFlight(final LivingEvent event, final EntityPlayer player) {
		if (player.isEntityAlive() && !player.capabilities.isCreativeMode) {
			final ItemStack jetPackItemStack = player.getCurrentArmor(ArmorSlot.CHEST.getInventoryArmorSlot());
			final ItemJetPack jetPackItem =
					(jetPackItemStack != null && jetPackItemStack.getItem() instanceof ItemJetPack)
							? (ItemJetPack) jetPackItemStack.getItem() : null;
			final boolean allowFlying = jetPackItem != null && ItemJetPack.hasFuelRemaining(jetPackItemStack) && !player.isInWater();

			if (player.capabilities.allowFlying != allowFlying) {
				if (allowFlying) {
					player.capabilities.setFlySpeed(baseFlySpeed * (1 + ((ItemJetPack) jetPackItemStack.getItem()).flySpeedBuff));
				} else {
					player.capabilities.setFlySpeed(baseFlySpeed);
					player.capabilities.isFlying = false;
				}
				player.capabilities.allowFlying = allowFlying;
			}

			if (allowFlying && !player.onGround) {

				if (jetPackFailsafeEnabled && player.motionY < -1) {
					player.capabilities.isFlying = true; // force jet packs to turn on to break falls
				}

				if (player.capabilities.isFlying) {
					if (jetPackItem.burnFuel(jetPackItemStack)) {
						player.fallDistance = 0;
						final int fuelRemainingPercent = jetPackItem.getFuelRemainingPercent(jetPackItemStack);
						if (jetPackLastFuelDisplayPercent != fuelRemainingPercent) {
							final String warning = jetPackLandingWarnings.get(fuelRemainingPercent);
							player.addChatMessage(new ChatComponentText(fuelRemainingPercent + "% jet pack fuel remaining" + (warning == null ? "" : ", " + warning)));
							jetPackLastFuelDisplayPercent = fuelRemainingPercent;
						}

						// cause an unstable motion to simulate the unpredictability of the exhaust direction
						player.setPosition(
								player.posX + ((random.nextDouble() / 5) - .1),
								player.posY + ((random.nextDouble() / 5) - .1),
								player.posZ + ((random.nextDouble() / 5) - .1));
					}
					else if (jetPackLastFuelDisplayPercent != -1) {
						player.addChatMessage(new ChatComponentText("Out of fuel, hope you packed a parachute..."));
						jetPackLastFuelDisplayPercent = -1;
					}

					updateJetpackExhaustLight(player, true);
					spawnJetpackExhaust(player, -.25);
					spawnJetpackExhaust(player, .25);

					final long currentMillis = System.currentTimeMillis();
					if (jetPackSoundIntervalMillis < currentMillis - jetPackLastSoundMillis) {
						jetPackLastSoundMillis = currentMillis;
						player.worldObj.playSoundAtEntity(player, PolycraftMod.MODID + ":jetpack.fly", 1f, 1f);
					}
				}
			}
			else {
				updateJetpackExhaustLight(player, false);
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
	}

	private void updateJetpackExhaustLight(final EntityPlayer player, final boolean enabled) {
		if (jetPackExhaustDynamicLights.size() == 0)
			for (int i = 0; i < jetPackExhaustRangeY; i++)
				jetPackExhaustDynamicLights.add(new PointLightSource(player.worldObj));
		if (enabled) {
			int i = 0;
			for (final PointLightSource source : jetPackExhaustDynamicLights)
				source.update(15, player.posX, player.posY - (i++), player.posZ);
		}

		if (jetPackLightsEnabled != enabled) {
			jetPackLightsEnabled = enabled;
			for (final PointLightSource source : jetPackExhaustDynamicLights)
				if (enabled)
					DynamicLights.addLightSource(source);
				else
					DynamicLights.removeLightSource(source);
		}
	}

	private void spawnJetpackExhaust(final EntityPlayer player, final double offset) {
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

		//light blocks on fire in the exhaust plume
		for (int i = 1; i <= jetPackExhaustRangeY; i++) {
			final int x = (int) player.posX;
			final int y = (int) player.posY - i - 2;
			final int z = (int) player.posZ - 1;
			final Block burnBlock = player.worldObj.getBlock(x, y, z);
			if (burnBlock != null && burnBlock.isAir(player.worldObj, x, y, z)) {
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

	private void handleBreathing(final LivingEvent event, final EntityPlayer player) {
		if (player.isEntityAlive() && player.isInWater() && player.getAir() < baseFullAir) {
			final ItemStack scubaTankItemStack = player.getCurrentArmor(2);
			if (scubaTankItemStack != null && scubaTankItemStack.getItem() instanceof ItemScubaTank) {
				final ItemScubaTank scubaTankItem = (ItemScubaTank) scubaTankItemStack.getItem();
				if (scubaTankItem.consumeAir(scubaTankItemStack)) {
					player.setAir(baseFullAir);
					final int airRemainingPercent = scubaTankItem.getAirRemainingPercent(scubaTankItemStack);
					if (scubaTankLastAirDisplayPercent != airRemainingPercent) {
						player.addChatMessage(new ChatComponentText(airRemainingPercent + "% air remaining"));
						for (int i = 0; i < 5; i++)
							event.entity.worldObj.spawnParticle("bubble", player.posX, player.posY + .5, player.posZ, 0, .5, 0);
						scubaTankLastAirDisplayPercent = airRemainingPercent;
					}

					final long currentMillis = System.currentTimeMillis();
					if (scubaTankSoundIntervalMillis < currentMillis - scubaTankLastSoundMillis) {
						scubaTankLastSoundMillis = currentMillis;
						player.worldObj.playSoundAtEntity(player, PolycraftMod.MODID + ":scubatank.breathe", 1f, 1f);
					}
				}
			}
		}
	}
}