package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemMoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItemHelper;
import edu.utd.minecraft.mod.polycraft.item.PolycraftMoldedItem;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ClientProxy extends CommonProxy {

	private static final int statusOverlayStartX = 5;
	private static final int statusOverlayStartY = 5;
	private static final int statusOverlayDistanceBetweenY = 10;

	private Minecraft client;

	@Override
	public void preInit() {
		super.preInit();
		client = FMLClientHandler.instance().getClient();
	}

	private class PlayerState {
		private boolean isFlying = false;
		private boolean flashlightEnabled = false;
		private final Collection<PointLightSource> flashlightLightSources;
		private boolean jetPackAllowFlying = false;
		private boolean jetPackExhaustLightsEnabled = false;
		private final Collection<PointLightSource> jetPackExhaustLightSources;
		private int pogoStickPreviousContinuousActiveBounces = 0;
		private float pogoStickLastFallDistance = 0;
		private boolean phaseShifterEnabled = false;

		private PlayerState(final WorldClient world) {
			flashlightLightSources = ItemFlashlight.createLightSources(world);
			jetPackExhaustLightSources = ItemJetPack.createExhaustLightSources(world);
		}

		private void setFlashlightEnabled(final boolean enabled) {
			if (flashlightEnabled != enabled) {
				flashlightEnabled = enabled;
				DynamicLights.syncLightSources(flashlightLightSources, enabled);
			}
		}

		private void setJetPackExhaustLightsEnabled(final boolean enabled) {
			if (jetPackExhaustLightsEnabled != enabled) {
				jetPackExhaustLightsEnabled = enabled;
				DynamicLights.syncLightSources(jetPackExhaustLightSources, enabled);
			}
		}
	}

	private final Map<EntityPlayer, PlayerState> playerStates = Maps.newHashMap();

	private synchronized PlayerState getPlayerState(final EntityPlayer player) {
		PlayerState playerState = playerStates.get(player);
		if (playerState == null) {
			playerState = new PlayerState(client.theWorld);
			playerStates.put(player, playerState);
		}
		return playerState;
	}

	private boolean noScreenOverlay() {
		return client.currentScreen == null;
	}

	private boolean isTickValid(final TickEvent tick) {
		return tick.phase == Phase.END && client.theWorld != null;
	}

	@Override
	@SubscribeEvent
	public synchronized void onEntityLivingDeath(final LivingDeathEvent event) {
		if (isPlayerEntity(event.entity))
			playerStates.remove(event.entity);
	}

	@Override
	@SubscribeEvent
	public synchronized void onLivingFallEvent(final LivingFallEvent event) {
		super.onLivingFallEvent(event);
		if (isEntityOnClient(event.entity) && isPlayerEntity(event.entity)) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			final PlayerState playerState = getPlayerState(player);
			if (ItemPogoStick.isEquipped(player))
				playerState.pogoStickLastFallDistance = event.distance;
		}
	}

	@SubscribeEvent
	public synchronized void onClientTick(final TickEvent.ClientTickEvent tick) {
		if (isTickValid(tick)) {
			final EntityPlayer player = client.thePlayer;
			if (player != null && player.isEntityAlive()) {
				final PlayerState playerState = getPlayerState(player);
				onClientTickJetPack(player, playerState);
				onClientTickParachute(player, playerState);
				onClientTickRunningShoes(player);
				onClientTickScubaFins(player);
				onClientTickGenericMoldedItem(player);
				onClientTickPogoStick(player, playerState);
				onClientTickBouncyBlock(player, playerState);
				onClientTickFlameThrower(player, playerState);
				onClientTickPhaseShifter(player, playerState);
				if (playerState.isFlying != player.capabilities.isFlying) {
					playerState.isFlying = player.capabilities.isFlying;
					sendMessageToServerIsFlying(player.capabilities.isFlying);
				}
			}
		}
	}

	@Override
	@SubscribeEvent
	public synchronized void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		super.onPlayerTick(tick);
		if (isTickValid(tick)) {
			if (tick.player.isEntityAlive()) {
				final PlayerState playerState = getPlayerState(tick.player);
				if (tick.side == Side.CLIENT) {
					onPlayerTickClientFlashlight(tick.player, playerState);
					onPlayerTickClientJetPack(tick.player, playerState);
					DynamicLights.updateLights(client.theWorld);
				}
			}
		}
	}

	@SubscribeEvent
	public synchronized void onRenderTick(final TickEvent.RenderTickEvent tick) {
		if (isTickValid(tick)) {
			final EntityPlayer player = client.thePlayer;
			if (player != null && player.isEntityAlive()) {
				onRenderTickItemStatusOverlays(player);
			}
		}
	}

	private void onPlayerTickClientFlashlight(final EntityPlayer player, final PlayerState playerState) {
		final boolean flashlightEnabled = ItemFlashlight.isEquipped(player);
		if (flashlightEnabled)
			ItemFlashlight.createLightCone(player, playerState.flashlightLightSources);
		playerState.setFlashlightEnabled(flashlightEnabled);
	}

	private static String getOverlayStatusPercent(final ItemStack itemStack, final double percent) {
		return String.format("%1$s: %2$.1f%%", itemStack.getItem().getItemStackDisplayName(itemStack), percent * 100);
	}

	private void onRenderTickItemStatusOverlays(final EntityPlayer player) {
		if (noScreenOverlay()) {
			int x = statusOverlayStartX;
			int y = statusOverlayStartY;
			if (ItemJetPack.isEquipped(player)) {
				final double fuelRemainingPercent = ItemJetPack.getFuelRemainingPercent(player);
				String message = getOverlayStatusPercent(ItemJetPack.getEquippedItemStack(player), fuelRemainingPercent);
				for (final Entry<Integer, String> warningEntry : PolycraftMod.itemJetPackLandingWarnings.entrySet()) {
					if ((fuelRemainingPercent * 100) <= warningEntry.getKey()) {
						message += " " + warningEntry.getValue();
						break;
					}
				}
				client.fontRenderer.drawStringWithShadow(message, x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}
			else if (ItemScubaTank.isEquipped(player)) {
				client.fontRenderer.drawStringWithShadow(getOverlayStatusPercent(ItemScubaTank.getEquippedItemStack(player), ItemScubaTank.getAirRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}

			if (ItemFlameThrower.isEquipped(player)) {
				client.fontRenderer.drawStringWithShadow(getOverlayStatusPercent(ItemFlameThrower.getEquippedItemStack(player), ItemFlameThrower.getFuelRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}
		}
	}

	private void onClientTickJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackAllowFlying = ItemJetPack.allowsFlying(player);
		if (playerState.jetPackAllowFlying != jetPackAllowFlying) {
			playerState.jetPackAllowFlying = jetPackAllowFlying;
			if (!jetPackAllowFlying)
				player.capabilities.isFlying = jetPackAllowFlying;
			if (!player.capabilities.isCreativeMode)
				player.capabilities.allowFlying = jetPackAllowFlying;
			player.capabilities.setFlySpeed(jetPackAllowFlying ? baseFlySpeed * ItemJetPack.getEquippedItem(player).flySpeedBuff : baseFlySpeed);
		}

		// cause an unstable motion to simulate the unpredictability of the exhaust direction
		if (ItemJetPack.getIgnited(player))
			ItemJetPack.randomizePosition(player, random);
	}

	private void onPlayerTickClientJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackIgnited = ItemJetPack.getIgnited(player);
		if (jetPackIgnited)
			ItemJetPack.createExhaust(player, client.theWorld, playerState.jetPackExhaustLightSources);
		playerState.setJetPackExhaustLightsEnabled(jetPackIgnited);
	}

	private void onClientTickParachute(final EntityPlayer player, final PlayerState playerState) {
		if (ItemParachute.allowsSlowFall(player)) {
			final float descendVelocity = ItemParachute.getEquippedItem(player).descendVelocity;
			if (player.motionY < descendVelocity)
				player.motionY = descendVelocity;
		}
	}

	private void onClientTickRunningShoes(final EntityPlayer player) {
		if (ItemRunningShoes.allowsRunning(player)) {
			if (player.onGround && GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward))
				setPlayerVelocityForward(player, baseWalkSpeed * ItemRunningShoes.getEquippedItem(player).walkSpeedBuff);
		}
	}

	private void onClientTickScubaFins(final EntityPlayer player) {
		if (ItemScubaFins.allowsFastSwimming(player)) {
			if (player.isInWater()) {
				final float swimSpeed = baseSwimSpeed * ItemScubaFins.getEquippedItem(player).swimSpeedBuff;
				if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward)) {
					setPlayerVelocityForward(player, swimSpeed);
				}
				if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump)) {
					player.motionY = swimSpeed;
				}
				else if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
					player.motionY = -swimSpeed;
				}
			}
			else if (player.onGround) {
				if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward))
					setPlayerVelocityForward(player, baseWalkSpeed * ItemScubaFins.getEquippedItem(player).walkSpeedBuff);
			}
		}
	}

	private static String gameIDLifePreserver = "J";

	private void onClientTickGenericMoldedItem(final EntityPlayer player) {
		if (PolycraftItemHelper.checkCurrentEquippedItem(player, ItemMoldedItem.class)) {
			final MoldedItem moldedItem = ((PolycraftMoldedItem) player.getCurrentEquippedItem().getItem()).getMoldedItem();
			if (gameIDLifePreserver.equals(moldedItem.source.gameID)) {
				if (player.isInWater() && !GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump) &&
						!GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak))
					player.motionY = 0;
			}
		}
	}

	private void onClientTickPogoStick(final EntityPlayer player, final PlayerState playerState) {
		float jumpMovementFactor = baseJumpMovementFactor;
		if (ItemPogoStick.isEquipped(player)) {
			final ItemPogoStick pogoStick = ((ItemPogoStick) player.getCurrentEquippedItem().getItem());
			jumpMovementFactor *= pogoStick.config.jumpMovementFactorBuff;
			if (!pogoStick.config.restrictJumpToGround || player.onGround) {
				final boolean playerActivelyBouncing = GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindUseItem) && noScreenOverlay();
				final boolean playerActivelySupressing = !playerActivelyBouncing && GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak) && noScreenOverlay();
				final double motionY = pogoStick.config.getMotionY(playerActivelySupressing ? 0 : playerState.pogoStickLastFallDistance, playerState.pogoStickPreviousContinuousActiveBounces, playerActivelyBouncing);
				if (motionY > 0)
					player.motionY = motionY;
				if (playerActivelyBouncing)
					playerState.pogoStickPreviousContinuousActiveBounces++;
				else
					playerState.pogoStickPreviousContinuousActiveBounces = 0;
			}
		}
		else {
			playerState.pogoStickPreviousContinuousActiveBounces = 0;
		}
		playerState.pogoStickLastFallDistance = 0;

		if (player.jumpMovementFactor != jumpMovementFactor)
			player.jumpMovementFactor = jumpMovementFactor;
	}

	private void onClientTickBouncyBlock(final EntityPlayer player, final PlayerState playerState) {

		//TODO rubber blocks
	}

	private void onClientTickFlameThrower(final EntityPlayer player, final PlayerState playerState) {

		//TODO flame thrower
	}

	private void onClientTickPhaseShifter(final EntityPlayer player, final PlayerState playerState) {
		final boolean phaseShifterEnabled = ItemPhaseShifter.isEquipped(player);
		if (playerState.phaseShifterEnabled != phaseShifterEnabled) {
			playerState.phaseShifterEnabled = phaseShifterEnabled;
			player.noClip = phaseShifterEnabled;
			if (!player.capabilities.isCreativeMode)
				player.capabilities.allowFlying = phaseShifterEnabled;
			player.capabilities.isFlying = phaseShifterEnabled;
			player.capabilities.setFlySpeed(phaseShifterEnabled ? baseFlySpeed * ItemPhaseShifter.getEquippedItem(player).flySpeedBuff : baseFlySpeed);
		}

		if (phaseShifterEnabled) {
			player.capabilities.isFlying = true;
			if (player.isInWater())
				player.setAir(baseFullAir);
			if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward))
				setPlayerVelocityForward(player, baseFlySpeed * ItemPhaseShifter.getEquippedItem(player).flySpeedBuff);
		}
	}
}