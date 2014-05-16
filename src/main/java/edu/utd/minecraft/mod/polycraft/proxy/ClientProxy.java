package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockBouncy;
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
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;

public class ClientProxy extends CommonProxy {

	private static final int statusOverlayStartX = 5;
	private static final int statusOverlayStartY = 5;
	private static final int statusOverlayDistanceBetweenY = 10;

	private Minecraft client;
	private GameSettings gameSettings;
	private KeyBinding keyBindingToggleArmor;

	@Override
	public void preInit() {
		super.preInit();
		client = FMLClientHandler.instance().getClient();
		gameSettings = client.gameSettings;
		keyBindingToggleArmor = new KeyBinding("key.toggle.armor", Keyboard.KEY_F, "key.categories.gameplay");
	}

	private class PlayerState {
		private boolean flashlightEnabled = false;
		private final Collection<PointLightSource> flashlightLightSources;
		private boolean jetPackIsFlying = false;
		private boolean jetPackLightsEnabled = false;
		private final Collection<PointLightSource> jetPackLightSources;
		private boolean flameThrowerLightsEnabled = false;
		private final Collection<PointLightSource> flameThrowerLightSources;
		private int pogoStickPreviousContinuousActiveBounces = 0;
		private float pogoStickLastFallDistance = 0;
		private boolean phaseShifterEnabled = false;
		private final Collection<PointLightSource> phaseShifterLightSources;
		private boolean phaseShifterLightsEnabled = false;
		private float bouncyBlockBounceHeight = 0;

		private PlayerState(final WorldClient world) {
			flashlightLightSources = ItemFlashlight.createLightSources(world);
			jetPackLightSources = ItemJetPack.createLightSources(world);
			flameThrowerLightSources = ItemFlameThrower.createLightSources(world);
			phaseShifterLightSources = ItemPhaseShifter.createLightSources(world);
		}

		private void setFlashlightEnabled(final boolean enabled) {
			if (flashlightEnabled != enabled) {
				flashlightEnabled = enabled;
				DynamicLights.syncLightSources(flashlightLightSources, enabled);
			}
		}

		private void setJetPackLightsEnabled(final boolean enabled) {
			if (jetPackLightsEnabled != enabled) {
				jetPackLightsEnabled = enabled;
				DynamicLights.syncLightSources(jetPackLightSources, enabled);
			}
		}

		private void setFlameThrowerLightsEnabled(final boolean enabled) {
			if (flameThrowerLightsEnabled != enabled) {
				flameThrowerLightsEnabled = enabled;
				DynamicLights.syncLightSources(flameThrowerLightSources, enabled);
			}
		}

		private void setPhaseShifterLightsEnabled(final boolean enabled) {
			if (phaseShifterLightsEnabled != enabled) {
				phaseShifterLightsEnabled = enabled;
				DynamicLights.syncLightSources(phaseShifterLightSources, enabled);
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

	private boolean isKeyDown(final KeyBinding keyBinding) {
		return GameSettings.isKeyDown(keyBinding);
	}

	private void setPlayerVelocityFromInputXZ(final EntityPlayer player, final float velocity) {
		double directionRadians = Math.toRadians(player.rotationYaw + 90);
		final boolean forward = isKeyDown(gameSettings.keyBindForward);
		final boolean back = isKeyDown(gameSettings.keyBindBack);
		final boolean left = isKeyDown(gameSettings.keyBindLeft);
		final boolean right = isKeyDown(gameSettings.keyBindRight);
		if (back)
			directionRadians += -Math.PI;
		if (left)
			directionRadians += Math.PI / (forward ? -4 : back ? 4 : -2);
		if (right)
			directionRadians += Math.PI / (forward ? 4 : back ? -4 : 2);

		if (forward != back || left != right) {
			player.motionX = velocity * Math.cos(directionRadians);
			player.motionZ = velocity * Math.sin(directionRadians);
		}
	}

	private void setPlayerVelocityFromInputY(final EntityPlayer player, final float velocity, final boolean cancelGravity) {
		if (isKeyDown(gameSettings.keyBindJump))
			player.motionY = velocity;
		else if (isKeyDown(gameSettings.keyBindSneak))
			player.motionY = -velocity;
		else if (cancelGravity)
			player.motionY = 0;
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
		if (isPlayerEntity(event.entity)) {
			final EntityPlayer player = (EntityPlayer) event.entity;
			final PlayerState playerState = getPlayerState(player);
			if (ItemPogoStick.isEquipped(player))
				playerState.pogoStickLastFallDistance = event.distance;
			else if (isEntityOnBouncyBlock(player)) {
				final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderEntity(player);
				//if we are actively jumping
				if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
					playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
				//if we are supposed to return momentum while not actively jumping (or sneaking)
				else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
					playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
			}
		}
		super.onLivingFallEvent(event);
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
				onClientTickPhaseShifter(player, playerState);
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
					onPlayerTickClientFlameThrower(tick.player, playerState);
					onPlayerTickClientPhaseShifter(tick.player, playerState);
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

	private synchronized void onClientTickJetPack(final EntityPlayer player, final PlayerState playerState) {
		boolean jetPackIsFlying = false;
		if (ItemJetPack.allowsFlying(player)) {
			jetPackIsFlying = playerState.jetPackIsFlying;
			if (keyBindingToggleArmor.isPressed())
				jetPackIsFlying = !jetPackIsFlying;
		}

		if (playerState.jetPackIsFlying != jetPackIsFlying) {
			playerState.jetPackIsFlying = jetPackIsFlying;
			sendMessageToServerJetPackIsFlying(jetPackIsFlying);
		}

		if (playerState.jetPackIsFlying) {
			final float flySpeed = baseFlySpeed * ItemJetPack.getEquippedItem(player).flySpeedBuff;
			setPlayerVelocityFromInputXZ(player, flySpeed);
			setPlayerVelocityFromInputY(player, flySpeed, true);

			// cause an unstable motion to simulate the unpredictability of the exhaust direction
			ItemJetPack.randomizePosition(player, random);
		}
	}

	private void onPlayerTickClientJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackIgnited = ItemJetPack.getIgnited(player);
		if (jetPackIgnited)
			ItemJetPack.createExhaust(player, client.theWorld, playerState.jetPackLightSources);
		playerState.setJetPackLightsEnabled(jetPackIgnited);
	}

	private void onPlayerTickClientFlameThrower(final EntityPlayer player, final PlayerState playerState) {
		final boolean flameThrowerIgnited = ItemFlameThrower.getIgnited(player);
		if (flameThrowerIgnited)
			ItemFlameThrower.createFlames(player, client.theWorld, random, playerState.flameThrowerLightSources);
		playerState.setFlameThrowerLightsEnabled(flameThrowerIgnited);
	}

	private void onPlayerTickClientPhaseShifter(final EntityPlayer player, final PlayerState playerState) {
		final boolean isPhaseShifterGlowing = ItemPhaseShifter.isGlowing(player);
		if (isPhaseShifterGlowing)
			ItemPhaseShifter.createGlow(player, client.theWorld, playerState.phaseShifterLightSources);
		playerState.setPhaseShifterLightsEnabled(isPhaseShifterGlowing);
	}

	private void onClientTickParachute(final EntityPlayer player, final PlayerState playerState) {
		if (ItemParachute.allowsSlowFall(player)) {
			final float descendVelocity = ItemParachute.getEquippedItem(player).descendVelocity;
			if (player.motionY < descendVelocity)
				player.motionY = descendVelocity;
		}
	}

	private void onClientTickRunningShoes(final EntityPlayer player) {
		if (ItemRunningShoes.allowsRunning(player))
			if (player.onGround)
				setPlayerVelocityFromInputXZ(player, baseWalkSpeed * ItemRunningShoes.getEquippedItem(player).walkSpeedBuff);
	}

	private void onClientTickScubaFins(final EntityPlayer player) {
		if (ItemScubaFins.allowsFastSwimming(player)) {
			if (player.isInWater()) {
				final float swimSpeed = baseSwimSpeed * ItemScubaFins.getEquippedItem(player).swimSpeedBuff;
				setPlayerVelocityFromInputXZ(player, swimSpeed);
				setPlayerVelocityFromInputY(player, swimSpeed, false);
			}
			else if (player.onGround) {
				setPlayerVelocityFromInputXZ(player, baseWalkSpeed * ItemScubaFins.getEquippedItem(player).walkSpeedBuff);
			}
		}
	}

	private static String gameIDLifePreserver = "J";

	private void onClientTickGenericMoldedItem(final EntityPlayer player) {
		if (ItemMoldedItem.isEquipped(player)) {
			final MoldedItem moldedItem = ItemMoldedItem.getEquippedItem(player).getMoldedItem();
			if (gameIDLifePreserver.equals(moldedItem.source.gameID)) {
				if (player.isInWater() && !isKeyDown(gameSettings.keyBindJump) && !isKeyDown(gameSettings.keyBindSneak))
					player.motionY = 0;
			}
		}
	}

	private void onClientTickPogoStick(final EntityPlayer player, final PlayerState playerState) {
		float jumpMovementFactor = baseJumpMovementFactor;
		if (ItemPogoStick.isEquipped(player)) {
			final ItemPogoStick pogoStick = ItemPogoStick.getEquippedItem(player);
			jumpMovementFactor *= pogoStick.config.jumpMovementFactorBuff;
			if (!pogoStick.config.restrictJumpToGround || player.onGround) {
				final boolean playerActivelyBouncing = isKeyDown(gameSettings.keyBindUseItem) && noScreenOverlay();
				final boolean playerActivelySupressing = !playerActivelyBouncing && isKeyDown(gameSettings.keyBindSneak) && noScreenOverlay();
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
		if (playerState.bouncyBlockBounceHeight > 0) {
			//if the player is on the ground and holding down jump, then wait for the jump to occur
			//(if we try to set the y velocity before the game jumps, it will override our velocity)
			if (!(player.onGround && isKeyDown(gameSettings.keyBindJump))) {
				final double motionY = PolycraftMod.getVelocityRequiredToReachHeight(playerState.bouncyBlockBounceHeight);
				if (motionY > .2)
					player.motionY = motionY;
				playerState.bouncyBlockBounceHeight = 0;
			}
		}
	}

	private void onClientTickPhaseShifter(final EntityPlayer player, final PlayerState playerState) {
		final boolean phaseShifterEnabled = ItemPhaseShifter.isEquipped(player);
		if (playerState.phaseShifterEnabled != phaseShifterEnabled) {
			playerState.phaseShifterEnabled = phaseShifterEnabled;
			player.noClip = phaseShifterEnabled;
		}

		if (phaseShifterEnabled) {
			final float flySpeed = baseFlySpeed * ItemPhaseShifter.getEquippedItem(player).flySpeedBuff;
			setPlayerVelocityFromInputXZ(player, flySpeed);
			setPlayerVelocityFromInputY(player, flySpeed, true);
			if (player.isInWater())
				player.setAir(baseFullAir);
		}
	}
}