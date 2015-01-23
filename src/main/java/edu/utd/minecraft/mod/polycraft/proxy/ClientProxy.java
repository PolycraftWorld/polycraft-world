package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockBouncy;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.client.RenderIDs;
import edu.utd.minecraft.mod.polycraft.client.TileEntityPolymerBrick;
import edu.utd.minecraft.mod.polycraft.client.TileEntityPolymerBrickRenderer;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.GameID;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.Ore;
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
	private KeyBinding keyBindingCheatInfo1;
	private KeyBinding keyBindingCheatInfo2;
	private KeyBinding keyBindingCheatInfo3;

	@Override
	public void preInit() {
		super.preInit();
		client = FMLClientHandler.instance().getClient();
		gameSettings = client.gameSettings;
		keyBindingToggleArmor = new KeyBinding("key.toggle.armor", Keyboard.KEY_F, "key.categories.gameplay");
		keyBindingCheatInfo1 = new KeyBinding("key.cheat.info.1", Keyboard.KEY_J, "key.categories.gameplay");
		keyBindingCheatInfo2 = new KeyBinding("key.cheat.info.2", Keyboard.KEY_I, "key.categories.gameplay");
		keyBindingCheatInfo3 = new KeyBinding("key.cheat.info.3", Keyboard.KEY_M, "key.categories.gameplay");
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
		private boolean placeBrickBackwards = false;
		private int cheatInfoTicksRemaining = 0;
		private Map<Ore, Integer> cheatInfoOreBlocksFound = null;

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
		else if (cancelGravity && player.motionY < 0)
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
				if (getBlockUnderEntity(player) instanceof BlockBouncy)				
				{
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				}
				else if (getBlockUnderNorthOfEntity(player) instanceof BlockBouncy)				
				{
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderNorthOfEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				}
				
				else if (getBlockUnderSouthOfEntity(player) instanceof BlockBouncy)				
				{
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderSouthOfEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				}
				else if (getBlockUnderEastOfEntity(player) instanceof BlockBouncy)				
				{
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderEastOfEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				}
				else if (getBlockUnderWestOfEntity(player) instanceof BlockBouncy)				
				{
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderWestOfEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				}		
				else if (getBlockUnderEntity(player) instanceof BlockBed)				
				{
					final BlockBed bouncyBed = (BlockBed) getBlockUnderEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = 3; //hard coded bed value spring height
				}
				
				
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
				//onClientTickPlasticBrick(player, playerState);
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
				onRenderTickItemStatusOverlays(player, getPlayerState(player));
			}
		}
	}

	private void onPlayerTickClientFlashlight(final EntityPlayer player, final PlayerState playerState) {
		int equippedFlashlightRange = CustomObject.getEquippedFlashlightRange(player);
		if (equippedFlashlightRange > 0)
			ItemFlashlight.createLightCone(player, playerState.flashlightLightSources, equippedFlashlightRange);
		playerState.setFlashlightEnabled(equippedFlashlightRange > 0);
	}

	private static String getOverlayStatusPercent(final ItemStack itemStack, final double percent) {
		return String.format("%1$s: %2$.1f%%", itemStack.getItem().getItemStackDisplayName(itemStack), percent * 100);
	}

	private void onRenderTickItemStatusOverlays(final EntityPlayer player, final PlayerState playerState) {
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

			if (playerState.cheatInfoTicksRemaining == 0) {
				final boolean cheatInfoActivated = isKeyDown(keyBindingCheatInfo1) && isKeyDown(keyBindingCheatInfo2) && isKeyDown(keyBindingCheatInfo3);
				if (cheatInfoActivated) {
					if (playerState.cheatInfoOreBlocksFound == null) {
						playerState.cheatInfoOreBlocksFound = Maps.newLinkedHashMap();
					}
					for (final Ore ore : Ore.getByDescendingAbundance())
						playerState.cheatInfoOreBlocksFound.put(ore, 0);
						
					playerState.cheatInfoTicksRemaining = 400;
					for (int testX = -8; testX <= 8; testX++) {
						for (int testY = 0; testY < 64; testY++) {
							for (int testZ = -8; testZ <= 8; testZ++) {
								final int blockX = (int)(player.posX + testX);
								final int blockY = testY;
								final int blockZ = (int)(player.posZ + testZ);
								if (!player.worldObj.isAirBlock(blockX, blockY, blockZ)) {
									final Block testBlock = player.worldObj.getBlock(blockX, blockY, blockZ);
									if (testBlock instanceof BlockOre) {
										final Ore ore = ((BlockOre)testBlock).ore;
										Integer found = playerState.cheatInfoOreBlocksFound.get(ore);
										if (found == null)
											playerState.cheatInfoOreBlocksFound.put(ore, 1);
										else
											playerState.cheatInfoOreBlocksFound.put(ore, found + 1);
									}
								}
							}
						}
					}
				}
			}
			else {
				client.fontRenderer.drawStringWithShadow("Cheat Info (" + playerState.cheatInfoTicksRemaining + ")", x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
				for (final Entry<Ore, Integer> foundOre : playerState.cheatInfoOreBlocksFound.entrySet())
				{
					client.fontRenderer.drawStringWithShadow(foundOre.getValue() + " " + foundOre.getKey().name, x, y, 16777215);
					y += statusOverlayDistanceBetweenY;
				}
				y += statusOverlayDistanceBetweenY;
				playerState.cheatInfoTicksRemaining--;
			}
		}
	}
//	private void onClientTickPlasticBrick(EntityPlayer player, PlayerState playerState) {
//		boolean placeBrickBackwards = false;
//		if (isKeyDown(gameSettings.keyBindSneak))
//		{
//			playerState.placeBrickBackwards = true;
//		}
//		else
//			playerState.placeBrickBackwards = false;
//		
//	}

	private void onClientTickJetPack(final EntityPlayer player, final PlayerState playerState) {
		boolean jetPackIsFlying = false;
		if (ItemJetPack.allowsFlying(player)) {
			jetPackIsFlying = playerState.jetPackIsFlying;
			if (keyBindingToggleArmor.isPressed())
				jetPackIsFlying = !jetPackIsFlying;
		}

		if (playerState.jetPackIsFlying != jetPackIsFlying) {
			playerState.jetPackIsFlying = jetPackIsFlying;
			sendMessageToServerJetPackIsFlying(jetPackIsFlying);
			if (jetPackIsFlying) {
				player.motionY = 1; // takeoff
			}
		}
		else if (playerState.jetPackIsFlying) {
			final float velocityInAir = ItemJetPack.getEquippedItem(player).velocityInAir;
			setPlayerVelocityFromInputXZ(player, velocityInAir);
			setPlayerVelocityFromInputY(player, velocityInAir, true);

			// cause an unstable motion to simulate the unpredictability of the exhaust direction
			ItemJetPack.randomizePosition(player, random);
		}
	}

	private void onPlayerTickClientJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackIgnited = ItemJetPack.getIgnited(player);
		if (jetPackIgnited)
			ItemJetPack.createExhaust(player, client.theWorld, playerState.jetPackLightSources, random);
		playerState.setJetPackLightsEnabled(jetPackIgnited);
	}

	private void onPlayerTickClientFlameThrower(final EntityPlayer player, final PlayerState playerState) {
		final boolean playerOnCurrentClient = client.thePlayer.equals(player);
		final boolean flameThrowerIgnited = playerOnCurrentClient ? ItemFlameThrower.allowsFiring(player) && player.isUsingItem() : ItemFlameThrower.getIgnited(player);
		if (flameThrowerIgnited)
			ItemFlameThrower.createFlames(player, client.theWorld, random, playerState.flameThrowerLightSources, playerOnCurrentClient);
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
			final float velocityDescent = -ItemParachute.getEquippedItem(player).velocityDescent;
			if (player.motionY < velocityDescent)
				player.motionY = velocityDescent;
		}
	}

	private void onClientTickRunningShoes(final EntityPlayer player) {
		if (ItemRunningShoes.allowsRunning(player))
			if (player.onGround)
				setPlayerVelocityFromInputXZ(player, ItemRunningShoes.getEquippedItem(player).velocityOnGround);
	}

	private void onClientTickScubaFins(final EntityPlayer player) {
		if (ItemScubaFins.allowsFastSwimming(player)) {
			if (player.isInWater()) {
				final float velocityInWater = ItemScubaFins.getEquippedItem(player).velocityInWater;
				setPlayerVelocityFromInputXZ(player, velocityInWater);
				setPlayerVelocityFromInputY(player, velocityInWater, false);
			}
			else if (player.onGround) {
				setPlayerVelocityFromInputXZ(player, ItemScubaFins.getEquippedItem(player).velocityOnGround);
			}
		}
	}

	private void onClientTickGenericMoldedItem(final EntityPlayer player) {
		if (ItemMoldedItem.isEquipped(player)) {
			final MoldedItem moldedItem = ItemMoldedItem.getEquippedItem(player).getMoldedItem();
			if (GameID.MoldLifePreserver.matches(moldedItem.source)) {
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
				final boolean playerActivelyBouncing = isKeyDown(gameSettings.keyBindUseItem) && noScreenOverlay() && !isPlayerLookingAtPogoCancellingBlock();
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

	private boolean isPlayerLookingAtPogoCancellingBlock()
	{
		if (client.objectMouseOver != null)
		{
			final Block block = client.theWorld.getBlock(client.objectMouseOver.blockX, client.objectMouseOver.blockY, client.objectMouseOver.blockZ);
			return block instanceof BlockContainer
					|| block instanceof BlockWorkbench
					 || block instanceof BlockDoor
					 || block instanceof BlockButton
					 || block == Blocks.bed;
		}
		return false;
	}

	private void onClientTickBouncyBlock(final EntityPlayer player, final PlayerState playerState) {
		if (playerState.bouncyBlockBounceHeight > 0) {
			// if the player is on the ground and holding down jump, then wait for the jump to occur
			// (if we try to set the y velocity before the game jumps, it will override our velocity)
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
			final float velocity = ItemPhaseShifter.getEquippedItem(player).velocity;
			setPlayerVelocityFromInputXZ(player, velocity);
			setPlayerVelocityFromInputY(player, velocity, true);
			if (player.isInWater())
				player.setAir(baseFullAir);
		}
	}

	@Override
	public void registerRenderers() {
		RenderIDs.PolymerBrickID = RenderingRegistry.getNextAvailableRenderId();

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPolymerBrick.class, new TileEntityPolymerBrickRenderer());

	}
}