package edu.utd.minecraft.mod.polycraft.proxy;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;
import edu.utd.minecraft.mod.polycraft.block.BlockBouncy;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.BlockPasswordDoor;
import edu.utd.minecraft.mod.polycraft.block.GuiScreenPasswordDoor;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiAITrainingRoom;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiConsent;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentList;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiHalftime;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiTutorial;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiTutorialMenu;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.client.gui.experiment.GuiExperimentManager;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.GameID;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import edu.utd.minecraft.mod.polycraft.entity.EntityPaintBall__Old;
import edu.utd.minecraft.mod.polycraft.entity.EntityPellet__Old;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityIronCannonBall;
import edu.utd.minecraft.mod.polycraft.entity.Physics.RenderCannonBall;
import edu.utd.minecraft.mod.polycraft.entity.boss.AttackWarning;
import edu.utd.minecraft.mod.polycraft.entity.boss.TestTerritoryFlagBoss;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityAndroid;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityDummy;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityOilSlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.model.ModelPolySlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.model.ModelPolycraftBiped;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderDummy;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderOilSlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderPolycraftBiped;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderTerritoryFlag2;
import edu.utd.minecraft.mod.polycraft.entity.projectile.RenderPaintball;
import edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureBase;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.TutorialRender;
import edu.utd.minecraft.mod.polycraft.handler.ResyncHandler;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCleanroom;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.condenser.CondenserRenderingHandler;
import edu.utd.minecraft.mod.polycraft.inventory.oilderrick.OilDerrickRenderingHandler;
import edu.utd.minecraft.mod.polycraft.inventory.solararray.SolarArrayRenderingHandler;
import edu.utd.minecraft.mod.polycraft.inventory.textwall.TextWallRenderHandler;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapRenderingHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemAirQualityDetector;
import edu.utd.minecraft.mod.polycraft.item.ItemCommunication;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezeRay;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemKnockbackBomb;
import edu.utd.minecraft.mod.polycraft.item.ItemMoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.scoreboards.ClientScoreboard;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;
import edu.utd.minecraft.mod.polycraft.util.TextFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ClientProxy extends CommonProxy {

	private static final int statusOverlayStartX = 5;
	private static final int statusOverlayStartY = 5;
	private static final int statusOverlayDistanceBetweenY = 10;
	private static final int swapCooldownTime = 1200; //60 seconds x 20 ticks per sec
	private static long lastTickTime = 0, tickms = 0;
	private static boolean sentLoadingCompleteMessage = false;

	private Minecraft client;
	private GameSettings gameSettings;
	private KeyBinding keyBindingToggleArmor;
	private KeyBinding keyBindingJ;
	private KeyBinding keyBindingI;
	private KeyBinding keyBindingM;
	private KeyBinding keyBindingN;
	private KeyBinding keyBindingV;
	private KeyBinding keyBindingX;
	private KeyBinding keyBindingP;
	private KeyBinding keyBindingBackspace;
	private KeyBinding keyBindingCheckAir;
	private KeyBinding keyBindingExperiments;
	

	@Override
	public void preInit() {
		super.preInit();
		
		PolycraftRegistry.registerClientSideResources();
		
		client = FMLClientHandler.instance().getClient();
		gameSettings = client.gameSettings;
		keyBindingToggleArmor = new KeyBinding("key.toggle.armor", Keyboard.KEY_F, "key.categories.gameplay");
		keyBindingJ = new KeyBinding("key.cheat.info.1", Keyboard.KEY_J, "key.categories.gameplay");
		keyBindingM = new KeyBinding("key.cheat.info.3", Keyboard.KEY_M, "key.categories.gameplay");
		keyBindingI = new KeyBinding("key.sync.info.1", Keyboard.KEY_I, "key.categories.gameplay");
		keyBindingN = new KeyBinding("key.sync.info.2", Keyboard.KEY_N, "key.categories.gameplay");
		keyBindingV = new KeyBinding("key.sync.info.3", Keyboard.KEY_V, "key.categories.gameplay");

		keyBindingX = new KeyBinding("key.test.info.1", Keyboard.KEY_X, "key.categories.gameplay");
		keyBindingP = new KeyBinding("key.test.info.2", Keyboard.KEY_P, "key.categories.gameplay");

		keyBindingBackspace = new KeyBinding("key.sync.info.4", Keyboard.KEY_BACK, "key.categories.gameplay");
		keyBindingCheckAir = new KeyBinding("key.check.air", Keyboard.KEY_C, "key.categories.gameplay");
		
		keyBindingExperiments = new KeyBinding("key.experiment.list", Keyboard.KEY_X, "key.categories.gameplay");
		
	}

	public void postInit() {
		super.postInit();
		//FMLCommonHandler.instance().bus().register(ClientEnforcer.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ClientEnforcer.INSTANCE);
		
		//register scoreboard handlers
		//FMLCommonHandler.instance().bus().register(ClientScoreboard.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ClientScoreboard.INSTANCE);
		
		// Register respawn desync handler
		FMLCommonHandler.instance().bus().register(ResyncHandler.INSTANCE);
		//TODO: Walter add in 3D rendering code
		registerRenderers();
		
		BotAPI.startAPIThread();
	}
	
	@Override
	public void freeze(EntityPlayer player, boolean flag, int freezeType) {
		this.getPlayerState(player).isFrozen = flag;
		this.getPlayerState(player).freezeType = freezeType;
	}

	private class PlayerState {
		private boolean flashlightEnabled = false;
		private final Collection<PointLightSource> flashlightLightSources;
		private boolean jetPackIsFlying = false;
		private boolean choseToSyncInventory = false;
		private boolean choseToSyncInventoryAgain = false;
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
		private int synchExam = 0;
		private int syncCooldownRemaining = 0;
		private int airQualityTicksRemaining = 0;
		private Map<Ore, Integer> cheatInfoOreBlocksFound = null;
		private boolean airQualityClean = true;
		public boolean isFrozen = false;
		public int freezeType = 0;

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

		private void setFreezeRayLightsEnabled(final boolean enabled) {
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
		if(getPlayerState(player).isFrozen)
			return;
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
		if(getPlayerState(player).isFrozen)
			return;
		if (isKeyDown(gameSettings.keyBindJump)) {
			if (ItemJetPack.isEquipped(player)) {
				if (ItemJetPack.getEquippedItem(player).altitudeMaximum > player.posY)
					player.motionY = velocity;
				else
					player.motionY = 0;
			}

		} else if (isKeyDown(gameSettings.keyBindSneak))
			player.motionY = -velocity;
		else if (cancelGravity && player.motionY < 0)
			player.motionY = 0;
	}

	//	@SubscribeEvent TODO create a message upon logging into the server
	//	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
	//		event.player.addChatMessage(new ChatComponentText("Welcome To Polycraft!"));
	//	}

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
				if (getBlockUnderEntity(player) instanceof BlockBouncy) {
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				} else if (getBlockUnderNorthOfEntity(player) instanceof BlockBouncy) {
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderNorthOfEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				}

				else if (getBlockUnderSouthOfEntity(player) instanceof BlockBouncy) {
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderSouthOfEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				} else if (getBlockUnderEastOfEntity(player) instanceof BlockBouncy) {
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderEastOfEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				} else if (getBlockUnderWestOfEntity(player) instanceof BlockBouncy) {
					final BlockBouncy bouncyBlock = (BlockBouncy) getBlockUnderWestOfEntity(player);
					// if we are actively jumping
					if (noScreenOverlay() && isKeyDown(gameSettings.keyBindJump))
						playerState.bouncyBlockBounceHeight = bouncyBlock.getActiveBounceHeight();
					// if we are supposed to return momentum while not actively jumping (or sneaking)
					else if (bouncyBlock.getMomentumReturnedOnPassiveFall() > 0 && !isKeyDown(gameSettings.keyBindSneak))
						playerState.bouncyBlockBounceHeight = event.distance * bouncyBlock.getMomentumReturnedOnPassiveFall();
				} else if (getBlockUnderEntity(player) instanceof BlockBed) {
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
				onClientTickCommDeviceToggled(player, playerState);
				onClientTickSyncInventory(player, playerState);
				//onClientTickPlasticBrick(player, playerState);
				onClientTickPhaseShifter(player, playerState);
				//onClientTickOpenExperimentsGui(player, playerState); //Was moved to onPlayerTick
				onClientTickMinigame(player,playerState);
				onClientTickBreakBlocks(player, playerState);
			}
			if(BotAPI.apiRunning.get()) {
				BotAPI.onClientTick();
		        if(lastTickTime == 0)
		        	lastTickTime = System.currentTimeMillis();
		        else {
		        	tickms = (System.currentTimeMillis() - lastTickTime);
		        	if(tickms == 0)
		        		tickms = 1;
		        	lastTickTime = System.currentTimeMillis();
		        }
			}
		}
		
		if(!sentLoadingCompleteMessage) {
			if(client.currentScreen instanceof GuiMainMenu) {
				PolycraftMod.logger.info("Minecraft finished loading");
				sentLoadingCompleteMessage = true;
				if(System.getProperty("dev") != null) {
					PolycraftMod.logger.info("dev mode enabled");
				}
			}
		}
	}

	private void onClientTickMinigame(EntityPlayer player, PlayerState playerState) {
		// TODO Auto-generated method stub
		
	}

	private void onClientTickBreakBlocks(EntityPlayer player, PlayerState playerState) {
		// TODO Auto-generated method stub
		if(BotAPI.breakList.size() > 0) {
			Vec3 breakPos = BotAPI.breakList.get(0);
			Block block = player.worldObj.getBlockState(new BlockPos(breakPos)).getBlock();
			Vec3 vector = breakPos.subtract(new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ));
			
			double pitch = ((Math.atan2(vector.zCoord, vector.xCoord) * 180.0) / Math.PI) - 90.0;
			double yaw  = ((Math.atan2(Math.sqrt(vector.zCoord * vector.zCoord + vector.xCoord * vector.xCoord), vector.yCoord) * 180.0) / Math.PI) - 90.0;
			
			//player.addChatComponentMessage(new ChatComponentText("x: " + breakPos.xCoord + " :: Y: " + breakPos.yCoord + " :: Z:" + breakPos.zCoord));
			
			player.setPositionAndRotation(player.posX, player.posY, player.posZ, (float) pitch, (float) yaw);
			
			if(block.getMaterial() != Material.air) {
				KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode(), true);
        		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode());
			}else {
				BotAPI.breakList.remove(0);
				KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode(), false);
			}
		}
	}
	
	private void onClientTickCommDeviceToggled(EntityPlayer player, PlayerState playerState) {
		if (keyBindingToggleArmor.isPressed()) {
			//TODO: add in functionality for pushing F when comm. device is equipped
		}

	}

	private void onClientTickSyncInventory(EntityPlayer player, PlayerState playerState) {
		
		if (playerState.syncCooldownRemaining == 0) {
			final boolean clientWantsToSyncInventory = isKeyDown(keyBindingI) && isKeyDown(keyBindingN) && isKeyDown(keyBindingV); //TODO and in PP
			if (clientWantsToSyncInventory) {
				if(System.getProperty("isExperimentServer") != null || !ExperimentManager.metadata.isEmpty()) { //Does NOT Work in an experiments server.
					player.addChatMessage(new ChatComponentText("INV swap is unavailable on our experiments server"));
					return;
				}
				playerState.syncCooldownRemaining = swapCooldownTime;
				playerState.choseToSyncInventory = true;
				sendMessageToServerClientWantsToSync(playerState.choseToSyncInventory);
			}
			playerState.choseToSyncInventoryAgain = false;

		}

		else {
			playerState.syncCooldownRemaining--;
			if (isKeyDown(keyBindingI) && isKeyDown(keyBindingN) && isKeyDown(keyBindingV) && playerState.syncCooldownRemaining < swapCooldownTime - 100) {
				playerState.choseToSyncInventoryAgain = true;
			}
		}

		if (isKeyDown(keyBindingBackspace)) {
			playerState.choseToSyncInventoryAgain = false;
			playerState.choseToSyncInventory = false;
		}

	}
	
	private void onClientTickFreeze(EntityPlayer player, PlayerState playerState) {

		if (playerState.isFrozen) {
			KeyBinding.unPressAllKeys();
			if(playerState.freezeType == 0) {
				player.motionX = 0;
				player.motionY = 0;
				player.motionZ = 0;
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
					//onPlayerTickClientPhaseShifter(tick.player, playerState);
					DynamicLights.updateLights(client.theWorld);
					onClientTickOpenExperimentsGui(tick.player, playerState);
					onClientTickFreeze(tick.player, playerState);
				}
			}
			if(tick.side == Side.CLIENT) {
				TutorialManager.INSTANCE.onPlayerTick(tick);
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderGuiPre(RenderGameOverlayEvent.Pre event) {
		if(TutorialManager.INSTANCE.getOptions().hideGui || (System.getProperty("dev") == null && TutorialManager.INSTANCE.clientCurrentExperiment != 0)) {
			if(event.type == ElementType.HEALTH) {
				event.setCanceled(true);
			}
			if(event.type == ElementType.EXPERIENCE) {
				event.setCanceled(true);
			}
			if(event.type == ElementType.FOOD) {
				event.setCanceled(true);
			}
			if(event.type == ElementType.CROSSHAIRS) {
				event.setCanceled(true);
			}
			if(event.type == ElementType.CHAT) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		if(TutorialManager.INSTANCE.getOptions().hideGui || (System.getProperty("dev") == null && TutorialManager.INSTANCE.clientCurrentExperiment != 0))
			event.setCanceled(true);
	}
	 
	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event) {
		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		//TutorialRender.renderLoadingScreen(entity);
	}
	
	@SubscribeEvent
    public void onRenderScreenGui(GuiScreenEvent.DrawScreenEvent.Post event) {
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if(entity!=null)
        {
        	//TutorialRender.renderLoadingScreen(entity);
        	 TutorialManager.INSTANCE.renderScreen(entity);
        }
        if(BotAPI.apiRunning.get())
	        if(lastTickTime == 0)
	        	lastTickTime = System.currentTimeMillis();
	        else {
	        	long tickms = (System.currentTimeMillis() - lastTickTime);
	        	if(tickms == 0)
	        		tickms = 1;
//	        	client.fontRendererObj.drawStringWithShadow("Tick ms: " + tickms, 100, 02, 16777215);
//	        	client.fontRendererObj.drawStringWithShadow("FPS: " + (1000 / tickms), 100, 12, 16777215);
	        	lastTickTime = System.currentTimeMillis();
	        }
    }
	

	@SubscribeEvent
	public synchronized void onRenderTick(final TickEvent.RenderTickEvent tick) {
		if (isTickValid(tick)) {
			final EntityPlayer player = client.thePlayer;
			if (player != null && player.isEntityAlive()) {
				onRenderTickItemStatusOverlays(player, getPlayerState(player));
			}

			if(BotAPI.apiRunning.get())
		        if(lastTickTime == 0)
		        	lastTickTime = System.currentTimeMillis();
		        else {
//		        	long tickms = (System.currentTimeMillis() - lastTickTime);
		        	if(tickms == 0)
		        		tickms = 1;
//		        	client.fontRendererObj.drawStringWithShadow("Tick ms: " + tickms, 100, 02, 16777215);
//		        	client.fontRendererObj.drawStringWithShadow("FPS: " + (1000 / tickms), 100, 12, 16777215);
		        	lastTickTime = System.currentTimeMillis();
		        }
		}
	}

	 public static void render(float frame) {
	        GL11.glPushMatrix();
	        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
	        double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * frame;
	        double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * frame;
	        double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * frame;
	        
	        GL11.glTranslated(-interpPosX, -interpPosY, -interpPosZ);
	        if(ClientEnforcer.getShowPP()) {
	        	renderPPBounds(entity);
	        }
//	        if(ClientEnforcer.getShowTutorialRender()) {
//	        	renderTutorial(entity);
//	        }
	        if(entity instanceof EntityPlayer) {
				if(((EntityPlayer)entity).getHeldItem() != null && ((EntityPlayer)entity).getHeldItem().getItem() instanceof ItemKnockbackBomb) {
					((ItemKnockbackBomb)((EntityPlayer)entity).getHeldItem().getItem()).render(entity);
				}
			}
	        if(!ClientEnforcer.INSTANCE.baseList.isEmpty()) {
				if (entity.dimension == 8) {
					for (FeatureBase base :ClientEnforcer.INSTANCE.baseList) {
						base.render(entity);
						base.setRendering(true);
					}
				} else {
					for (FeatureBase base : ClientEnforcer.INSTANCE.baseList) {
						//base.setRendering(false);
					}
				}
			}
	        //Chris or Matthew: this needs more comments.
	        //PolycraftMinigameManager.INSTANCE.render(entity);
	        //renderKillWallBounds(entity);
	       // renderRaceGameGoal(entity);
	        ExperimentManager.INSTANCE.render(entity);
	        TutorialManager.INSTANCE.render(entity);
	        if(PolycraftMinigameManager.INSTANCE!=null)
	        {
	        	if(PolycraftMinigameManager.INSTANCE.active && entity.worldObj.isRemote)
	        	{
	        		PolycraftMinigameManager.INSTANCE.render(entity);
	        	}
	        }
	        ItemDevTool.render(entity);
	        AttackWarning.renderAttackWarnings(entity);
	        //renderKillWallBounds(entity);
	        //renderRaceGameGoal(entity);
	        GL11.glPopMatrix();
	    }
	 
//	 public static void renderLeftArrow(Entity entity)
//	 {
//			GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        GL11.glBegin(GL11.GL_QUADS);
//	        double x = entity.posX;
//	        double y = entity.posY;
//	        double z = entity.posZ;
//	        double ang = entity.rotationYaw;
//	        double ang2= 0;
//	        double r = 3; 
//	        int ticks=0;      //entity.ticksExisted%60;
//	        double degInRad;
//	        double DEG2RAD = Math.PI/180;
//	        for (int i=50-ticks; i<=85; i++)
//	        {
//	           degInRad = (i+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y,(float)(Math.sin(degInRad)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.25F,(float)(Math.sin(degInRad)*r+z));
//	           i++;
//	           degInRad = (i+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.25F,(float)(Math.sin(degInRad)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y,(float)(Math.sin(degInRad)*r+z));
//	           i--;
//	        }
//
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        
//	        
//	        GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        GL11.glFrontFace(GL11.GL_CW);
//	        GL11.glBegin(GL11.GL_TRIANGLES);
//	        
//
//	        int i=40-ticks;
//	        degInRad = (i+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.125F,(float)(Math.sin(degInRad)*r+z));
//	        i+=15;
//	        degInRad = (i+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y+.25F,(float)(Math.sin(degInRad)*r+z));
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.5F,(float)(Math.sin(degInRad)*r+z));
//	        
//	        
//	 
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        GL11.glFrontFace(GL11.GL_CCW);
//	 }
//	 
//	 public static void renderGreenLeftArrow(Entity entity)
//	 {
//			GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        GL11.glColor4f(0F, 1F, 0F,1F);
//	        GL11.glBegin(GL11.GL_QUADS);
//	       
//	        double x = entity.posX;
//	        double y = entity.posY;
//	        double z = entity.posZ;
//	        double ang = entity.rotationYaw;
//	        
//	        int ang1=((int)ang)%360;
//	        if(ang1<0)
//	        	ang1+=360;
//	        double ang2= 0;
//	        double r = 2; 
//	        int ticks=0;      //entity.ticksExisted%60;
//	        double degInRad;
//	        double DEG2RAD = Math.PI/180;
//	        for (int i=ang1; i<=85; i++)
//	        {
//	        
//	           degInRad = (i+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y,(float)(Math.sin(degInRad)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.25F,(float)(Math.sin(degInRad)*r+z));
//	           i++;
//	           degInRad = (i+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.25F,(float)(Math.sin(degInRad)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y,(float)(Math.sin(degInRad)*r+z));
//	           i--;
//	        }
//
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        
//	        
//	        GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        GL11.glFrontFace(GL11.GL_CW);
//	        GL11.glBegin(GL11.GL_TRIANGLES);
//	        
//
//	        int i=40-ticks;
//	        degInRad = (i+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.125F,(float)(Math.sin(degInRad)*r+z));
//	        i+=15;
//	        degInRad = (i+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y+.25F,(float)(Math.sin(degInRad)*r+z));
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.5F,(float)(Math.sin(degInRad)*r+z));
//	        
//	        
//	 
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        GL11.glFrontFace(GL11.GL_CCW);
//	 }
//	 
//	 
//	 public static void renderRightArrow(Entity entity)
//	 {
//	        GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        GL11.glBegin(GL11.GL_QUADS);
//
//	        	
//	        double x = entity.posX;
//	        double y = entity.posY;
//	        double z = entity.posZ;
//	        double ang = entity.rotationYaw;
//	        double ang2= 0;
//	        double r = 3; 
//	        int ticks=entity.ticksExisted%60 ;
//	        double degInRad;
//	        double DEG2RAD = Math.PI/180;
//	        for (int i=94; i<=95+ticks; i++)
//	        {
//	           degInRad = (i+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y,(float)(Math.sin(degInRad)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.25F,(float)(Math.sin(degInRad)*r+z));
//	           i++;
//	           degInRad = (i+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.25F,(float)(Math.sin(degInRad)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y,(float)(Math.sin(degInRad)*r+z));
//	           i--;
//	        }
//
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        
//	        
//	        GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        //GL11.glFrontFace(GL11.GL_CW);
//	        GL11.glBegin(GL11.GL_TRIANGLES);
//
//	       
//	        int i=110+ticks;
//	        degInRad = (i+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.125F,(float)(Math.sin(degInRad)*r+z));
//	        i-=15;
//	        degInRad = (i+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y+.25F,(float)(Math.sin(degInRad)*r+z));
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)y-.5F,(float)(Math.sin(degInRad)*r+z));
//	        
//	        
//	 
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        GL11.glFrontFace(GL11.GL_CCW);  
//	 }
//	 
//	 public static void renderDownArrow(Entity entity)
//	 {
//		  GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        GL11.glBegin(GL11.GL_QUADS);
//
//	        	
//	        double x = entity.posX;
//	        double y = entity.posY;
//	        double z = entity.posZ;
//	        double ang = entity.rotationYaw;
//	        double ang2= 0;
//	        double r = 3; 
//	        int ticks=entity.ticksExisted%40;
//	        double degInRad;
//	        double DEG2RAD = Math.PI/180;
//	        for (int i=95; i<=95+ticks; i++)
//	        {
//	           degInRad = (90+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad+Math.PI/60)*r+x),(float)(Math.cos((i+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad+Math.PI/60)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad-Math.PI/60)*r+x),(float)(Math.cos((i+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad-Math.PI/60)*r+z));
//	           i++;
//	           degInRad = (90+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad-Math.PI/60)*r+x),(float)(Math.cos((i+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad-Math.PI/60)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad+Math.PI/60)*r+x),(float)(Math.cos((i+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad+Math.PI/60)*r+z));
//	           i--;
//	        }
//
//	        
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        
//	        
//	        GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        //GL11.glFrontFace(GL11.GL_CW);
//	        GL11.glBegin(GL11.GL_TRIANGLES);
//
//	       
//	       
//	        degInRad = (100+ang)*DEG2RAD;
//	        //double extra=-Math.PI/60;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)(Math.cos((100+ticks+ang2)*DEG2RAD)*r+y)+.25F,(float)(Math.sin(degInRad)*r+z));
//	        degInRad = (80+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)(Math.cos((100+ticks+ang2)*DEG2RAD)*r+y)+.25F,(float)(Math.sin(degInRad)*r+z));
//	        degInRad = (90+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)(Math.cos((100+ticks+ang2)*DEG2RAD)*r+y)-.75F,(float)(Math.sin(degInRad)*r+z));
//	        
//	        
//	 
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        GL11.glFrontFace(GL11.GL_CCW);
//	 }
//	 
//	 public static void renderUpArrow(Entity entity)
//	 {
//	        GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        GL11.glBegin(GL11.GL_QUADS);
//
//	        	
//	        double x = entity.posX;
//	        double y = entity.posY;
//	        double z = entity.posZ;
//	        double ang = entity.rotationYaw;
//	        double ang2= 0;
//	        double r = 3; 
//	        int ticks=entity.ticksExisted%40;
//	        double degInRad;
//	        double DEG2RAD = Math.PI/180;
//	        for (int i=85-ticks; i<=85; i++)
//	        {
//	           degInRad = (90+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad+Math.PI/60)*r+x),(float)(Math.cos((i+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad+Math.PI/60)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad-Math.PI/60)*r+x),(float)(Math.cos((i+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad-Math.PI/60)*r+z));
//	           i++;
//	           degInRad = (90+ang)*DEG2RAD;
//	           GL11.glVertex3f((float)(Math.cos(degInRad-Math.PI/60)*r+x),(float)(Math.cos((i+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad-Math.PI/60)*r+z));
//	           GL11.glVertex3f((float)(Math.cos(degInRad+Math.PI/60)*r+x),(float)(Math.cos((i+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad+Math.PI/60)*r+z));
//	           i--;
//	        }
//
//	        
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        
//	        
//	        GL11.glDisable(GL11.GL_TEXTURE_2D);
//	        GL11.glEnable(GL11.GL_BLEND);
//	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//	        GL11.glDisable(GL11.GL_LIGHTING);
//	        GL11.glLineWidth(1.5F);
//	        GL11.glFrontFace(GL11.GL_CW);
//	        GL11.glBegin(GL11.GL_TRIANGLES);
//
//	       
//	       
//	        degInRad = (100+ang)*DEG2RAD;
//	        //double extra=-Math.PI/60;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)(Math.cos((86-ticks+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad)*r+z));
//	        degInRad = (80+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)(Math.cos((86-ticks+ang2)*DEG2RAD)*r+y),(float)(Math.sin(degInRad)*r+z));
//	        degInRad = (90+ang)*DEG2RAD;
//	        GL11.glVertex3f((float)(Math.cos(degInRad)*r+x),(float)(Math.cos((86-ticks+ang2)*DEG2RAD)*r+y)+1F,(float)(Math.sin(degInRad)*r+z));
//	        
//	        
//	 
//	        GL11.glEnd();
//	        GL11.glEnable(GL11.GL_LIGHTING);
//	        GL11.glEnable(GL11.GL_TEXTURE_2D);
//	        GL11.glDisable(GL11.GL_BLEND);
//	        GL11.glFrontFace(GL11.GL_CCW);
//	 }

	 private static void renderTutorial(Entity entity) 
	 {
		 
	 }

	@SubscribeEvent
	 public void renderLastEvent(RenderWorldLastEvent event) {
	    render(event.partialTicks);
	    
	 }
	 private static void renderPPBounds(Entity entity) {
		 if (entity.worldObj.isRemote){
			 	GL11.glDisable(GL11.GL_TEXTURE_2D);
		        GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		        GL11.glDisable(GL11.GL_LIGHTING);
		        GL11.glLineWidth(1.5F);
		        GL11.glBegin(GL11.GL_LINES);
		        
		        PrivateProperty playerPP = (Enforcer.findPrivateProperty(entity, (entity.chunkCoordX), (entity.chunkCoordZ)));
		        
		        for (int cx = -4; cx <= 4; cx++)
		            for (int cz = -4; cz <= 4; cz++) {
		            	
		                double x1 = (entity.chunkCoordX + cx) << 4;
		                double z1 = (entity.chunkCoordZ + cz) << 4;
		                double x2 = x1 + 16;
		                double z2 = z1 + 16;
		                if(Enforcer.findPrivateProperty(entity, (entity.chunkCoordX + cx), (entity.chunkCoordZ + cz)) != null) {
			                double dy = 64;
			                double y1 = Math.floor(entity.posY - dy / 2);
			                double y2 = y1 + dy;
			                if (y1 < 0) {
			                    y1 = 0;
			                    y2 = dy;
			                }
			                if (y1 > entity.worldObj.getHeight()) {
			                    y2 = entity.worldObj.getHeight();
			                    y1 = y2 - dy;
			                }
			                double dist = (((double)entity.ticksExisted %120.00));
			                if(dist>=60)
			                {
			                	dist=120-dist;
			                }
			                dist=dist/120+.2;
			                
			                boolean northPP=(Enforcer.findPrivateProperty(entity, (int)(x1/16)+1, (int)(z1/16)) != null);
			                boolean southPP=(Enforcer.findPrivateProperty(entity, (int)(x1/16)-1, (int)(z1/16)) != null);
			                boolean eastPP=(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16)+1) != null);
			                boolean westPP=(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16)-1) != null);
			                
			                
			                boolean northOPP=false;
			                boolean southOPP=false;
			                boolean eastOPP=false;
			                boolean westOPP=false;
			              
			                if(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16))!=null)
			                {
			                	if(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16)).actionEnabled((EntityPlayer) entity, Action.Enter))
			                	{
			                		if(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16)).equals(playerPP))
			                		{
			                			GL11.glColor4d(0.2, 0.2, 0.8, dist);
			                		}else
			                		{
			                			GL11.glColor4d(0.1, 0.7, 0.1, dist);
			                		}
			                		
			                	}
			                	else
			                	{
			                		GL11.glColor4d(0.9, 0, 0, dist);
			                	}
			                	if(northPP){
				                	northOPP =(Enforcer.findPrivateProperty(entity, (int)(x1/16)+1, (int)(z1/16)).equals(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16))));
				                }
			                	if(southPP){
				                southOPP =(Enforcer.findPrivateProperty(entity, (int)(x1/16)-1, (int)(z1/16)).equals(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16))));
			                	}
				                if(eastPP){
				                eastOPP =(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16)+1).equals(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16))));
				                }
				                if(westPP){
				                westOPP =(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16)-1).equals(Enforcer.findPrivateProperty(entity, (int)(x1/16), (int)(z1/16))));
				                }
			                	
			               
			                
				                for (double y = (int) y1; y <= y2; y++) {
			                    	
			                    	
				                	 if ( (!northPP) ) 
				                	 {
			                    		GL11.glVertex3d(x2, y, z1);//north w..
			                    		GL11.glVertex3d(x2, y, z2);//north e..
				                	 }
				                	 else if(!northOPP)
				                	 {
				                		GL11.glVertex3d(x2, y, z1);//north w..
				                    	GL11.glVertex3d(x2, y, z2);//north e..
				                	 }
				                	 if ( (!southPP) ) 
				                	 {
				                		 GL11.glVertex3d(x1, y, z1);//south w..
				                		 GL11.glVertex3d(x1, y, z2);//south e..
				                	 }
				                	 else if(!southOPP)
				                	 {
				                		 GL11.glVertex3d(x1, y, z1);//south w..
				                		 GL11.glVertex3d(x1, y, z2);//south e..
				                	 }
				                	 if ( (!eastPP) ) 
				                	 {
				                		 GL11.glVertex3d(x1, y, z2);//east  s..
				                		 GL11.glVertex3d(x2, y, z2);//east  n..
				                	 }
				                	 else if(!eastOPP)
				                	 {
				                		 GL11.glVertex3d(x1, y, z2);//east  s..
				                		 GL11.glVertex3d(x2, y, z2);//east  n..
				                	 }
				                	 if ( (!westPP) ) 
				                	 {
				                		 GL11.glVertex3d(x1, y, z1);//west  s..
				                		 GL11.glVertex3d(x2, y, z1);//west  n..
				                	 }
				                	 else if(!westOPP)
				                	 {
				                		 GL11.glVertex3d(x1, y, z1);//west  s..
				                		 GL11.glVertex3d(x2, y, z1);//west  n..
				                	 }
			                    }
			                    
				                	
				                for (double h = 1; h <= 15; h++) {
			                    	
			                    	
			                    	if ( (!westPP) ) 
				                	{
			                    		GL11.glVertex3d(x1 + h, y1, z1);
			                    		GL11.glVertex3d(x1 + h, y2, z1);
				                	}
			                    	else if(!westOPP)
				                	 {
			                    		GL11.glVertex3d(x1 + h, y1, z1);
			                    		GL11.glVertex3d(x1 + h, y2, z1);
				                	 }
			                    	if ( (!eastPP) ) 
				                	{
			                    		GL11.glVertex3d(x1 + h, y1, z2);
			                    		GL11.glVertex3d(x1 + h, y2, z2);
				                	}
			                    	else if(!eastOPP)
				                	 {
			                    		GL11.glVertex3d(x1 + h, y1, z2);
			                    		GL11.glVertex3d(x1 + h, y2, z2);
				                	 }
			                        
			                    	if ( (!southPP) ) 
				                	{ 
			                    		GL11.glVertex3d(x1, y1, z1 + h);
			                    		GL11.glVertex3d(x1, y2, z1 + h);
				                	}
			                    	else if(!southOPP)
				                	 {
			                    		GL11.glVertex3d(x1, y1, z1 + h);
			                    		GL11.glVertex3d(x1, y2, z1 + h);
				                	 }
			                    	if ( (!northPP) ) 
				                	{
			                    		GL11.glVertex3d(x2, y1, z1 + h);
			                    		GL11.glVertex3d(x2, y2, z1 + h);
				                	}
			                    	else if(!northOPP)
				                	 {
			                    		GL11.glVertex3d(x2, y1, z1 + h);
			                    		GL11.glVertex3d(x2, y2, z1 + h);
				                	 }
			                    }
			                
			                }
			                GL11.glColor4d(0, 0, 0.9, dist);
			                if ( (!northPP || !eastPP) ) {
			                    GL11.glVertex3d(x2, y1, z2);
			                    GL11.glVertex3d(x2, y2, z2);
			                }
			                if ( (!northPP || !westPP)) {
			                    GL11.glVertex3d(x2, y1, z1);
			                    GL11.glVertex3d(x2, y2, z1);
			                }
			                if ( (!southPP || !eastPP)) {
			                    GL11.glVertex3d(x1, y1, z2);
			                    GL11.glVertex3d(x1, y2, z2);
			                }
			                if ((!southPP || !westPP)) {
			                    GL11.glVertex3d(x1, y1, z1);
			                    GL11.glVertex3d(x1, y2, z1);
			                }
			                
		                } 
		            }   
		        GL11.glEnd();
		        GL11.glEnable(GL11.GL_LIGHTING);
		        GL11.glEnable(GL11.GL_TEXTURE_2D);
		        GL11.glDisable(GL11.GL_BLEND);
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

	private static String getFrequency(final ItemStack itemStack, final double frequency) {
		if (itemStack.getDisplayName().equalsIgnoreCase("voice cone")) {
			return "";
		} else if (itemStack.getDisplayName().equalsIgnoreCase("megaphone")) {
			return "";
		}

		return String.format("%1$s: %2$.1f MHz", itemStack.getItem().getItemStackDisplayName(itemStack), frequency / 10);
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
				client.fontRendererObj.drawStringWithShadow(message, x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			} else if (ItemScubaTank.isEquipped(player)) {
				client.fontRendererObj.drawStringWithShadow(getOverlayStatusPercent(ItemScubaTank.getEquippedItemStack(player), ItemScubaTank.getAirRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}

			else if (ItemAirQualityDetector.isEquipped(player)) {
				if (isKeyDown(keyBindingCheckAir)) {
					if (playerState.airQualityTicksRemaining == 0) {
						playerState.airQualityTicksRemaining = PolycraftMod.convertSecondsToGameTicks(10);
						playerState.airQualityClean = PolycraftCleanroom.isLocationClean(player.worldObj, (int) (player.posX) - 1, (int) (player.posY), (int) (player.posZ) - 1);

					} else {

						client.fontRendererObj.drawStringWithShadow(playerState.airQualityClean ? "Clean" : "Dirty", x, y, 16777215);
						y += statusOverlayDistanceBetweenY;

					}

				} else {
					playerState.airQualityTicksRemaining = 0;

				}
			}

			if (playerState.airQualityTicksRemaining > 0)
				playerState.airQualityTicksRemaining--;

			if (ItemFlameThrower.isEquipped(player)) {
				client.fontRendererObj.drawStringWithShadow(getOverlayStatusPercent(ItemFlameThrower.getEquippedItemStack(player), ItemFlameThrower.getFuelRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			} else if (ItemFreezeRay.isEquipped(player)) {
				client.fontRendererObj.drawStringWithShadow(getOverlayStatusPercent(ItemFreezeRay.getEquippedItemStack(player), ItemFreezeRay.getFuelRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			} else if (ItemWaterCannon.isEquipped(player)) {
				client.fontRendererObj.drawStringWithShadow(getOverlayStatusPercent(ItemWaterCannon.getEquippedItemStack(player), ItemWaterCannon.getFuelRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}

			else if (ItemCommunication.isEquipped(player)) {

				client.fontRendererObj.drawStringWithShadow(getFrequency(ItemCommunication.getEquippedItemStack(player), ItemCommunication.getFrequency(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}

			if (playerState.choseToSyncInventory) {
				if (playerState.syncCooldownRemaining < 1100 && playerState.syncCooldownRemaining > 1000) {
					playerState.choseToSyncInventory = false;
					playerState.choseToSyncInventoryAgain = false;
				}

				else {
					client.fontRendererObj.drawStringWithShadow("Synced Inventory With Portal (" + playerState.syncCooldownRemaining / 20 + " seconds until next sync possible)", x, y, 16777215);
					y += statusOverlayDistanceBetweenY;

				}

			} else if (playerState.choseToSyncInventoryAgain) {
				client.fontRendererObj.drawStringWithShadow("Be patient: (" + playerState.syncCooldownRemaining / 20 + " seconds until next sync possible)", x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}
			
			
			
			// Display shiny text when user hasn't completed tutorial
			if (!ClientEnforcer.INSTANCE.hasCompletedTutorial && TutorialManager.INSTANCE.clientCurrentExperiment == -1) {
				int color = 16777215;
				//client.fontRendererObj.drawStringWithShadow(stringToGolden("Push X to open the tutorial!",5,false),x,y,color);
			}
			
			// Display shiny text after user completes tutorial to play experiments
			if (ClientEnforcer.INSTANCE.hasCompletedTutorial && TutorialManager.INSTANCE.clientCurrentExperiment == -1 
					&& ExperimentManager.INSTANCE.clientCurrentExperiment == -1 ) {
				int color = 16777215;
				client.fontRendererObj.drawStringWithShadow(stringToGolden("Push X to open play an experiment!!",5,false),x,y,color);
			}
			
			if (playerState.cheatInfoTicksRemaining == 0) {
				final boolean cheatInfoActivated = isKeyDown(keyBindingJ) && isKeyDown(keyBindingI) && isKeyDown(keyBindingM);
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
								final BlockPos blockPos = new BlockPos(player.posX + testX,testY, player.posZ + testZ);
								if (!player.worldObj.isAirBlock(blockPos)) {
									final Block testBlock = player.worldObj.getBlockState(blockPos).getBlock();
									if (testBlock instanceof BlockOre) {
										final Ore ore = ((BlockOre) testBlock).ore;
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
			} else {
				client.fontRendererObj.drawStringWithShadow("Cheat Info (" + playerState.cheatInfoTicksRemaining + ")", x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
				for (final Entry<Ore, Integer> foundOre : playerState.cheatInfoOreBlocksFound.entrySet()) {
					client.fontRendererObj.drawStringWithShadow(foundOre.getValue() + " " + foundOre.getKey().name, x, y, 16777215);
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
	
	public static String stringToGolden(String parString, int parShineLocation, boolean parReturnToBlack)
	{
	   int stringLength = parString.length();
	   if (stringLength < 1)
	   {
	      return "";
	   }
	   String outputString = "";
	   TextFormatting[] colorChar = 
	      {
	         TextFormatting.RED,
	         TextFormatting.GOLD,
	         TextFormatting.YELLOW,
	         TextFormatting.GREEN,
	         TextFormatting.AQUA,
	         TextFormatting.BLUE,
	         TextFormatting.LIGHT_PURPLE,
	         TextFormatting.DARK_PURPLE
	      };
	   for (int i = 0; i < stringLength; i++)
	   {
	      if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==0)
	      {
	         outputString = outputString+TextFormatting.WHITE+parString.substring(i, i+1);    
	      }
	      else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==1)
	      {
	          outputString = outputString+TextFormatting.YELLOW+parString.substring(i, i+1);    
	      }
	      else if ((i+parShineLocation+Minecraft.getSystemTime()/20)%88==87)
	      {
	         outputString = outputString+TextFormatting.YELLOW+parString.substring(i, i+1);    
	      }
	      else
	      {
	         outputString = outputString+TextFormatting.GOLD+parString.substring(i, i+1);        
	      }
	   }
	   // return color to a common one after (most chat is white, but for other GUI might want black)
	   if (parReturnToBlack)
	   {
	      return outputString+TextFormatting.BLACK;
	   }
	   return outputString+TextFormatting.WHITE;
	}
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
			if ((jetPackIsFlying) && (ItemJetPack.getEquippedItem(player).altitudeMaximum < player.posY)) {
				player.motionY = 1; // takeoff
			}
		} else if (playerState.jetPackIsFlying) {
			final float velocityInAir = ItemJetPack.getEquippedItem(player).velocityInAir;

			setPlayerVelocityFromInputXZ(player, velocityInAir);
			setPlayerVelocityFromInputY(player, velocityInAir, true);

			// cause an unstable motion to simulate the unpredictability of the exhaust direction
			ItemJetPack.randomizePosition(player, random);
		}
		//TODO: Jim, inspect code to respect the altitudeLimit of the jet pack
		// The jet pack should still be able to move in the XZ plane and in -Y
		// simply disallow climbing 
		// the variable in ItemJetPack is called altitudeMaximum
		// just want you to be aware that I am monkeying with your code
	}

	private void onPlayerTickClientJetPack(final EntityPlayer player, final PlayerState playerState) {
		final boolean jetPackIgnited = ItemJetPack.getIgnited(player);
		if (jetPackIgnited)
			ItemJetPack.createExhaust(player, client.theWorld, playerState.jetPackLightSources, random);
		playerState.setJetPackLightsEnabled(jetPackIgnited);
	}

	private void onPlayerTickClientFlameThrower(final EntityPlayer player, final PlayerState playerState) {
		final boolean playerOnCurrentClient = client.thePlayer.equals(player);
		final boolean flameThrowerIgnited = playerOnCurrentClient ? ItemFlameThrower.allowsActivation(player) && player.isUsingItem() : ItemFlameThrower.getActivated(player);
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
			} else if (player.onGround) {
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
		} else {
			playerState.pogoStickPreviousContinuousActiveBounces = 0;
		}
		playerState.pogoStickLastFallDistance = 0;

		if (player.jumpMovementFactor != jumpMovementFactor)
			player.jumpMovementFactor = jumpMovementFactor;
	}

	private boolean isPlayerLookingAtPogoCancellingBlock() {
		if (client.objectMouseOver != null) {
			final Block block = client.theWorld.getBlockState(client.objectMouseOver.getBlockPos()).getBlock();
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
		//RenderIDs.PolymerBrickID = RenderingRegistry.getNextAvailableRenderId();
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPolymerBrick.class, new TileEntityPolymerBrickRenderer());
		for (final Inventory inventory : Inventory.registry.values()) {
			PolycraftInventoryBlock inventoryBlock = (PolycraftInventoryBlock) PolycraftRegistry.getBlock(inventory);
			//TODO: update to 1.8, now using JSON files for rendering blocks
			/*
			PolycraftInventoryBlock.BasicRenderingHandler renderingHandler;
			if (inventory.render3D)
				inventory.renderID = RenderingRegistry.getNextAvailableRenderId();

			if (GameID.InventoryTreeTap.matches(inventory))
				RenderingRegistry.registerBlockHandler(inventory.renderID, renderingHandler = new TreeTapRenderingHandler(inventory));
			else if (GameID.InventoryOilDerrick.matches(inventory))
				RenderingRegistry.registerBlockHandler(inventory.renderID, renderingHandler = new OilDerrickRenderingHandler(inventory));
			else if (GameID.InventoryCondenser.matches(inventory))
				RenderingRegistry.registerBlockHandler(inventory.renderID, renderingHandler = new CondenserRenderingHandler(inventory));
			else if (GameID.InventorySolarArray.matches(inventory))
				RenderingRegistry.registerBlockHandler(inventory.renderID, renderingHandler = new SolarArrayRenderingHandler(inventory));
			else if (GameID.TextWall.matches(inventory)) {
				renderingHandler = new TextWallRenderHandler(inventory);
				RenderingRegistry.registerBlockHandler(inventory.renderID, renderingHandler);
			}
			else
				RenderingRegistry.registerBlockHandler(inventory.renderID, renderingHandler = new PolycraftInventoryBlock.BasicRenderingHandler(inventory));

			//TODO: figure out why this is commented out and if it is needed 8.5.2015
			if (inventory.render3D)
				ClientRegistry.bindTileEntitySpecialRenderer(inventoryBlock.tileEntityClass, renderingHandler);
*/
		}
		
		for (final PolycraftEntity polycraftEntity : PolycraftEntity.registry.values()) {
            if (GameID.EntityResearchAssistant.matches(polycraftEntity)){
                RenderingRegistry.registerEntityRenderingHandler(ResearchAssistantEntity.class, new RenderPolycraftBiped(new ModelPolycraftBiped(), 0));
            }
            else if (GameID.EntityTerritoryFlag.matches(polycraftEntity)){
                RenderingRegistry.registerEntityRenderingHandler(EntityTerritoryFlag.class, new RenderTerritoryFlag());
            }
            else if (GameID.EntityOilSlime.matches(polycraftEntity)){
                RenderingRegistry.registerEntityRenderingHandler(EntityOilSlime.class, new RenderOilSlime(new ModelPolySlime(16), new ModelPolySlime(0), 0.25F));
            }
            else if (GameID.EntityOilSlimeBall.matches(polycraftEntity)){
                //RenderingRegistry.registerEntityRenderingHandler(EntityOilSlimeBallProjectile.class, new RenderSnowball(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("1hl"))));
            }
            else if (GameID.EntityDummy.matches(polycraftEntity)){
                RenderingRegistry.registerEntityRenderingHandler(EntityDummy.class, new RenderDummy((ModelBase)new ModelIronGolem(), 0.25F));
            }
            else if (GameID.EntityTestTerritoryFlagBoss.matches(polycraftEntity)) {
            	RenderingRegistry.registerEntityRenderingHandler(TestTerritoryFlagBoss.class, new RenderTerritoryFlag2());
            }
            else if (GameID.EntityAndroid.matches(polycraftEntity)){
                RenderingRegistry.registerEntityRenderingHandler(EntityAndroid.class, new RenderPolycraftBiped(new ModelPolycraftBiped(), 0));
            }
            else if (GameID.EntityIronCannonBall.matches(polycraftEntity)) {
            	//RenderingRegistry.registerEntityRenderingHandler(EntityIronCannonBall.class, new RenderCannonBall());
            }
            else if (GameID.EPaintball.matches(polycraftEntity)) {
            	//RenderingRegistry.registerEntityRenderingHandler(EntityPaintBall__Old.class, new RenderPaintball());

            }

        }

	}
	
	public void onClientTickOpenExperimentsGui(EntityPlayer player, PlayerState state) {
		if(keyBindingExperiments.isPressed()) {
			if(this.noScreenOverlay()) {
				if(player.isSneaking()) {
					openExperimentManagerGui(player);
				}else {
					if(ClientEnforcer.INSTANCE.hasCompletedTutorial)
						client.displayGuiScreen(new GuiExperimentList(player));
					else
						client.displayGuiScreen(new GuiTutorialMenu(player));
				}
			}
		}
	}
	
	@Override
	public void openDoorGui(BlockPasswordDoor block, EntityPlayer player, BlockPos blockPos)
	{
		client.displayGuiScreen(new GuiScreenPasswordDoor(block, player, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
	}
	
	@Override
	public void openConsentGui(EntityPlayer player, int x, int y, int z)
	{
		client.displayGuiScreen(new GuiConsent(player, x, y, z));
	}
	
	@Override
	public void openExperimentManagerGui(EntityPlayer player)
	{
		client.displayGuiScreen(new GuiExperimentManager(player));
	}
	
	@Override
	public void openDevToolGui(EntityPlayer player) {
		client.displayGuiScreen(new GuiExpCreator(player));
	}
	
	@Override
	public void openAIToolGui(EntityPlayer player) {
		client.displayGuiScreen(new GuiAITrainingRoom(player));
	}
	
	@Override
	public void openTutorialGui(EntityPlayer player) {
		client.displayGuiScreen(new GuiTutorial(player));
	}
	
	@Override
	public void openHalftimeGui(EntityPlayer player) {
		client.displayGuiScreen(new GuiHalftime(this.client.thePlayer));
	}
	@Override
	public void closeHalftimeGui(EntityPlayer player) {
		client.displayGuiScreen((GuiScreen) null);
		client.setIngameFocus();
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
}