package edu.utd.minecraft.mod.polycraft.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;

public class PolycraftEventHandler {

	private static final Random random = new Random();

	private static final float baseMovementSpeed = 0.1f;
	private static final float baseFlySpeed = 0.05f;
	private static final int baseFullAir = 300;

	private static boolean jetPackFailsafeEnabled = false;
	private static final int jetPackExhaustParticlesPerTick = 5;
	private static final double jetPackExhaustPlumeOffset = .05;
	private static final double jetPackExhaustDownwardVelocity = -.8;
	private static final long jetPackSoundIntervalMillis = 850;
	private static final Map<Integer, String> jetPackLandingWarnings = new LinkedHashMap<Integer, String>();
	static
	{
		jetPackLandingWarnings.put(30, "might want to start thinking about landing...");
		jetPackLandingWarnings.put(20, "daredevil are we?");
		jetPackLandingWarnings.put(10, "we are way low on fuel Mav!");
		jetPackLandingWarnings.put(5, "vapor lock!!");
		jetPackLandingWarnings.put(1, "EJECT EJECT EJECT!!!");
	}
	private static final long scubaTankSoundIntervalMillis = 2000;

	private float previousMovementSpeedBaseValue = 0;
	private long jetPackLastSoundMillis = 0;
	private long scubaTankLastSoundMillis = 0;
	private int jetPackLastFuelDisplayPercent = 0;
	private int scubaTankLastAirDisplayPercent = 0;

	@SubscribeEvent
	public synchronized void onLivingUpdateEvent(final LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			handleMovementSpeed(event, player);
			handleFlight(event, player);
			handleBreathing(event, player);
		}
	}

	@SubscribeEvent
	public void onPlayerFlyableFallEvent(final PlayerFlyableFallEvent event) {
		if (!jetPackFailsafeEnabled) {
			float fallDamage = event.distance - 3.0f;
			if (fallDamage > 0)
				event.entityPlayer.attackEntityFrom(DamageSource.fall, fallDamage);
		}
	}

	private void handleMovementSpeed(final LivingUpdateEvent event, final EntityPlayer player) {
		float movementSpeedBaseValue = baseMovementSpeed;
		final ItemStack bootsItemStack = player.getCurrentArmor(0);
		if (player.isInWater()) {
			if (bootsItemStack != null && bootsItemStack.getItem() instanceof ItemScubaFins)
				movementSpeedBaseValue = baseMovementSpeed * (1 + ((ItemScubaFins) bootsItemStack.getItem()).swimSpeedBuff);
		}
		else {
			if (bootsItemStack != null && bootsItemStack.getItem() instanceof ItemScubaFins)
				movementSpeedBaseValue = baseMovementSpeed * (1 + ((ItemScubaFins) bootsItemStack.getItem()).walkSpeedBuff);
			else if (bootsItemStack != null && bootsItemStack.getItem() instanceof ItemRunningShoes)
				movementSpeedBaseValue = baseMovementSpeed * (1 + ((ItemRunningShoes) bootsItemStack.getItem()).walkSpeedBuff);
		}

		if (previousMovementSpeedBaseValue != movementSpeedBaseValue) {
			player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(movementSpeedBaseValue);
			previousMovementSpeedBaseValue = movementSpeedBaseValue;
		}
	}

	private void handleFlight(final LivingUpdateEvent event, final EntityPlayer player) {
		final ItemStack jetPackItemStack = player.getCurrentArmor(2);
		final ItemJetPack jetPackItem = jetPackItemStack != null && jetPackItemStack.getItem() instanceof ItemJetPack ? (ItemJetPack) jetPackItemStack.getItem() : null;
		final boolean allowFlying = jetPackItem != null && ItemJetPack.hasFuelRemaining(jetPackItemStack);
		if (player.capabilities.allowFlying != allowFlying)
		{
			if (allowFlying)
				player.capabilities.setFlySpeed(baseFlySpeed * (1 + ((ItemJetPack) jetPackItemStack.getItem()).flySpeedBuff));
			else {
				player.capabilities.setFlySpeed(baseFlySpeed);
				player.capabilities.isFlying = false;
			}
			player.capabilities.allowFlying = allowFlying;
		}

		if (allowFlying && !player.onGround) {

			if (jetPackFailsafeEnabled && player.motionY < -1)
				player.capabilities.isFlying = true; // force jet packs to turn on to break falls

			if (player.capabilities.isFlying) {
				if (jetPackItem.burnFuel(jetPackItemStack)) {
					player.fallDistance = 0;
					final int fuelRemainingPercent = jetPackItem.getFuelRemainingPercent(jetPackItemStack);
					if (jetPackLastFuelDisplayPercent != fuelRemainingPercent) {
						final String warning = jetPackLandingWarnings.get(fuelRemainingPercent);
						player.addChatMessage(new ChatComponentText(fuelRemainingPercent + "% fuel remaining" + (warning == null ? "" : ", " + warning)));
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

				spawnJetpackExhaust(player, -.25);
				spawnJetpackExhaust(player, .25);

				final long currentMillis = System.currentTimeMillis();
				if (jetPackSoundIntervalMillis < currentMillis - jetPackLastSoundMillis) {
					jetPackLastSoundMillis = currentMillis;
					player.worldObj.playSoundAtEntity(player, PolycraftMod.MODID + ":jetpack.fly", 1f, 1f);
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

	private void spawnJetpackExhaust(final EntityPlayer player, final double offset) {
		double playerRotationRadians = Math.toRadians(player.rotationYaw);
		double playerRotationSin = Math.sin(playerRotationRadians);
		double playerRotationCos = Math.cos(playerRotationRadians);
		double centerX = player.posX + (offset * playerRotationCos);
		double centerY = player.posY - 1;
		double centerZ = player.posZ + (offset * playerRotationSin);
		double offsetX = playerRotationCos * jetPackExhaustPlumeOffset;
		double offsetZ = playerRotationSin * jetPackExhaustPlumeOffset;
		for (int i = 0; i < jetPackExhaustParticlesPerTick; i++) {
			double y = centerY - (i * .02);
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

	private void handleBreathing(final LivingUpdateEvent event, final EntityPlayer player) {
		if (player.isInWater() && player.getAir() < baseFullAir) {
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
