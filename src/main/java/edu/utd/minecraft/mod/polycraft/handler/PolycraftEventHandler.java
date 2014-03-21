package edu.utd.minecraft.mod.polycraft.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFlippers;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaMask;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;

public class PolycraftEventHandler {

	private static final Random random = new Random();
	private static final float baseWalkSpeed = 0.1f;
	private static final float baseJumpMovementFactor = 0.02F;
	private static final float baseFlySpeed = 0.05f;
	private static final float parachuteDescendSpeed = -.3f;
	private static boolean previousAllowRunning = false;
	private static final int[][] jetPackSmokeOffsets = new int[][] {
			new int[] { 0, 0 },
			new int[] { 0, 1 },
			new int[] { 0, -1 },
			new int[] { 1, 0 },
			new int[] { 1, 1 },
			new int[] { 1, -1 },
			new int[] { -1, 0 },
			new int[] { -1, 1 },
			new int[] { -1, -1 }
	};
	private int jetPackCurrentFlightTicks = 0;
	private int jetPackPreviousFuelRemainingPercent = 0;
	private static final Map<Integer, String> jetPackLandingWarnings = new HashMap<Integer, String>();
	static
	{
		jetPackLandingWarnings.put(30, "might want to start thinking about landing...");
		jetPackLandingWarnings.put(20, "dare devil are we?");
		jetPackLandingWarnings.put(10, "we are way low on fuel Mav!");
		jetPackLandingWarnings.put(5, "vapor lock!!");
		jetPackLandingWarnings.put(1, "EJECT EJECT EJECT!!!");
	}
	private static boolean previousWet = false;
	private int scubaTankPreviousAirRemainingPercent = 0;

	@SubscribeEvent
	public void onLivingUpdateEvent(final LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			handleRunningShoes(event, player);
			handleJetPack(event, player);
			handleParachute(event, player);
			handleScuba(event, player);
		}
	}

	private void handleRunningShoes(final LivingUpdateEvent event, final EntityPlayer player) {
		final ItemStack runningShoesItemStack = player.getCurrentArmor(0);
		final boolean allowRunning = runningShoesItemStack != null && runningShoesItemStack.getItem() instanceof ItemRunningShoes;
		if (allowRunning != previousAllowRunning) {
			if (allowRunning) {
				final float walkSpeedBuff = ((ItemRunningShoes) runningShoesItemStack.getItem()).getWalkSpeedBuff();
				player.capabilities.setPlayerWalkSpeed(baseWalkSpeed * (1 + walkSpeedBuff));
				player.jumpMovementFactor = baseJumpMovementFactor * (1 + walkSpeedBuff);
			}
			else {
				player.capabilities.setPlayerWalkSpeed(baseWalkSpeed);
				player.jumpMovementFactor = baseJumpMovementFactor;
			}
			previousAllowRunning = allowRunning;
		}
	}

	private void handleJetPack(final LivingUpdateEvent event, final EntityPlayer player) {
		final ItemStack jetPackItemStack = player.getCurrentArmor(2);
		final ItemJetPack jetPackItem = jetPackItemStack != null && jetPackItemStack.getItem() instanceof ItemJetPack ? (ItemJetPack) jetPackItemStack.getItem() : null;
		final boolean allowFlying = jetPackItem != null && ItemJetPack.hasFuelRemaining(jetPackItemStack);
		if (player.capabilities.allowFlying != allowFlying)
		{
			if (allowFlying)
				player.capabilities.setFlySpeed(baseFlySpeed * (1 + ((ItemJetPack) jetPackItemStack.getItem()).getFlySpeedBuff()));
			else {
				player.capabilities.setFlySpeed(baseFlySpeed);
				player.capabilities.isFlying = false; // TODO doesn't hurt player if falling from max item damage (does if the player un-equips the item...)
			}
			jetPackCurrentFlightTicks = 0;
			jetPackPreviousFuelRemainingPercent = -1;
			player.capabilities.allowFlying = allowFlying;
		}

		if (allowFlying && !player.onGround) {

			if (player.motionY < -1)
				player.capabilities.isFlying = true; // force jet packs to turn on to break falls

			if (player.capabilities.isFlying) {
				jetPackCurrentFlightTicks++;
				if (jetPackItem.burnFuel(jetPackItemStack)) {
					final int fuelRemainingPercent = jetPackItem.getFuelRemainingPercent(jetPackItemStack);
					if (jetPackPreviousFuelRemainingPercent != fuelRemainingPercent) {
						final String warning = jetPackLandingWarnings.get(fuelRemainingPercent);
						player.addChatMessage(new ChatComponentText(fuelRemainingPercent + "% fuel remaining" + (warning == null ? "" : ", " + warning)));
					}
					jetPackPreviousFuelRemainingPercent = fuelRemainingPercent;
				}
				else
					player.addChatMessage(new ChatComponentText("Out of fuel, hope you packed a parachute..."));

				event.entity.worldObj.spawnParticle("hugeexplosion", player.posX, player.posY - 8, player.posZ, -player.motionX, -player.motionY, -player.motionZ);
				for (int i = 0; i < 20; i++)
					event.entity.worldObj.spawnParticle("flame", player.posX, player.posY - i - 2, player.posZ, -player.motionX, -player.motionY, -player.motionZ);
				for (int i = 0; i < jetPackSmokeOffsets.length; i++)
					event.entity.worldObj.spawnParticle("hugeexplosion", player.posX - jetPackSmokeOffsets[i][0], player.posY - 20, player.posZ - jetPackSmokeOffsets[i][1], -player.motionX, -player.motionY, -player.motionZ);

				if (jetPackCurrentFlightTicks == 1 || jetPackCurrentFlightTicks % 30 == 0)
					event.entity.worldObj.playSoundAtEntity(player, PolycraftMod.MODID + ":jetpack.fly", 1f, 1f);
			}
		}
	}

	private void handleParachute(final LivingUpdateEvent event, final EntityPlayer player) {
		if (player.motionY < parachuteDescendSpeed) {
			final ItemStack parachuteItemStack = player.getCurrentEquippedItem();
			if (parachuteItemStack != null && parachuteItemStack.getItem() instanceof ItemParachute) {
				player.setVelocity(player.motionX * .99, parachuteDescendSpeed, player.motionZ * .99);
				player.fallDistance = 0;
			}
		}
	}

	private void handleScuba(final LivingUpdateEvent event, final EntityPlayer player) {
		if (previousWet != player.isWet()) {
			if (player.isWet()) {
				// TODO add clarity if wearing mask
				final ItemStack scubaMaskItemStack = player.getCurrentArmor(3);
				if (scubaMaskItemStack != null && scubaMaskItemStack.getItem() instanceof ItemScubaMask) {
				}

				final ItemStack scubaFlippersItemStack = player.getCurrentArmor(0);
				if (scubaFlippersItemStack != null && scubaFlippersItemStack.getItem() instanceof ItemScubaFlippers) {
					// TODO make this buff configurable, also, doesn't work?
					player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(baseWalkSpeed * 6);
				}
			}
			else {
				player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(baseWalkSpeed);
			}
			scubaTankPreviousAirRemainingPercent = 0;
		}
		previousWet = player.isWet();

		if (player.isWet()) {
			final ItemStack scubaTankItemStack = player.getCurrentArmor(2);
			if (scubaTankItemStack != null && scubaTankItemStack.getItem() instanceof ItemScubaTank) {
				final ItemScubaTank scubaTankItem = (ItemScubaTank) scubaTankItemStack.getItem();
				if (scubaTankItem.consumeAir(scubaTankItemStack)) {
					player.setAir(300);
					final int airRemainingPercent = scubaTankItem.getAirRemainingPercent(scubaTankItemStack);
					if (scubaTankPreviousAirRemainingPercent != airRemainingPercent) {
						player.addChatMessage(new ChatComponentText(airRemainingPercent + "% air remaining"));
						event.entity.worldObj.spawnParticle("bubble", player.posX, player.posY - 8, player.posZ, -player.motionX, -player.motionY, -player.motionZ);
					}
					scubaTankPreviousAirRemainingPercent = airRemainingPercent;
				}
			}
		}
	}
}
