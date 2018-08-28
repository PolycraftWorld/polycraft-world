package edu.utd.minecraft.mod.polycraft.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import codechicken.lib.render.RenderUtils;
import codechicken.nei.NEIClientConfig;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockBouncy;
import edu.utd.minecraft.mod.polycraft.block.BlockOre;
import edu.utd.minecraft.mod.polycraft.block.material.PolycraftMaterial;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.GameID;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;
import edu.utd.minecraft.mod.polycraft.config.Ore;
import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityDummy;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityOilSlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.model.ModelPolySlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.model.ModelPolycraftBiped;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.model.ModelTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderDummy;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderOilSlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderPolycraftBiped;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.render.RenderTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftCleanroom;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.condenser.CondenserRenderingHandler;
import edu.utd.minecraft.mod.polycraft.inventory.oilderrick.OilDerrickRenderingHandler;
import edu.utd.minecraft.mod.polycraft.inventory.solararray.SolarArrayRenderingHandler;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapRenderingHandler;
import edu.utd.minecraft.mod.polycraft.item.ItemAirQualityDetector;
import edu.utd.minecraft.mod.polycraft.item.ItemCommunication;
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemFlashlight;
import edu.utd.minecraft.mod.polycraft.item.ItemFreezeRay;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemMoldedItem;
import edu.utd.minecraft.mod.polycraft.item.ItemOilSlimeBall;
import edu.utd.minecraft.mod.polycraft.item.ItemParachute;
import edu.utd.minecraft.mod.polycraft.item.ItemPhaseShifter;
import edu.utd.minecraft.mod.polycraft.item.ItemPogoStick;
import edu.utd.minecraft.mod.polycraft.item.ItemRunningShoes;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaFins;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.item.ItemWaterCannon;
import edu.utd.minecraft.mod.polycraft.minigame.BlockGameBlock;
import edu.utd.minecraft.mod.polycraft.minigame.KillWall;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.DynamicLights;
import edu.utd.minecraft.mod.polycraft.transformer.dynamiclights.PointLightSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class ClientProxy extends CommonProxy {

	private static final int statusOverlayStartX = 5;
	private static final int statusOverlayStartY = 5;
	private static final int statusOverlayDistanceBetweenY = 10;
	private static final int swapCooldownTime = 1200; //60 seconds x 20 ticks per sec

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
	

	@Override
	public void preInit() {
		super.preInit();
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
		
		
	}

	public void postInit() {
		super.postInit();
		FMLCommonHandler.instance().bus().register(ClientEnforcer.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ClientEnforcer.INSTANCE);
		//TODO: Walter add in 3D rendering code
		registerRenderers();
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
	 public void renderLastEvent(RenderWorldLastEvent event) {
	     if (NEIClientConfig.isEnabled()) {
	    	 if(ClientEnforcer.getShowPP()) {
		    		render(event.partialTicks);
		     }
	     }

	 }
	 
	 public static void render(float frame) {
	        GL11.glPushMatrix();
	        Entity entity = Minecraft.getMinecraft().renderViewEntity;
	        RenderUtils.translateToWorldCoords(entity, frame);
	        
	        renderKillWallBounds(entity);
	        
	        GL11.glPopMatrix();
	    }
	 private static void renderKillWallBounds(Entity entity) {
		 if (entity.worldObj.isRemote){
			 	GL11.glDisable(GL11.GL_TEXTURE_2D);
		        GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		        GL11.glDisable(GL11.GL_LIGHTING);
		        GL11.glLineWidth(1.5F);
		        GL11.glBegin(GL11.GL_LINES);//Gl_Line_Loop
                double dy = 16;
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
                double radius;
                if(entity.worldObj.getBlock(0, 0, 0) instanceof BlockGameBlock)
                {
                	radius=((BlockGameBlock) entity.worldObj.getBlock(0, 0, 0)).getRadius();
                }
                else
                {
                	radius=0;
                }
                
                
                GL11.glColor4d(0.9, 0, 0, .5);
//                for (double y = (int) y1; y <= y2; y++) {
//                	
//                	for (int i=0; i<360 ; i+=4)
//	                {
//	                	double degInRad = i*DEG2RAD;
//	                	GL11.glVertex3f((float)Math.cos(degInRad)*radius, (float) y,(float)Math.sin(degInRad)*radius);
//	                }
//	                
//                }
                
                for (double y = (int) y1; y <= y2; y++) {
                	GL11.glVertex3d(radius, y, radius);
                	GL11.glVertex3d(-radius, y, radius);
                	
                	GL11.glVertex3d(-radius, y, radius);
                	GL11.glVertex3d(-radius, y, -radius);
                	
                	GL11.glVertex3d(-radius, y, -radius);
                	GL11.glVertex3d(radius, y, -radius);
                	
                	GL11.glVertex3d(radius, y, -radius);
                	GL11.glVertex3d(radius, y, radius);
                	
                }
		 
		        GL11.glEnd();
		        GL11.glEnable(GL11.GL_LIGHTING);
		        GL11.glEnable(GL11.GL_TEXTURE_2D);
		        GL11.glDisable(GL11.GL_BLEND);
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
				client.fontRenderer.drawStringWithShadow(message, x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			} else if (ItemScubaTank.isEquipped(player)) {
				client.fontRenderer.drawStringWithShadow(getOverlayStatusPercent(ItemScubaTank.getEquippedItemStack(player), ItemScubaTank.getAirRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}

			else if (ItemAirQualityDetector.isEquipped(player)) {
				if (isKeyDown(keyBindingCheckAir)) {
					if (playerState.airQualityTicksRemaining == 0) {
						playerState.airQualityTicksRemaining = PolycraftMod.convertSecondsToGameTicks(10);
						playerState.airQualityClean = PolycraftCleanroom.isLocationClean(player.worldObj, (int) (player.posX) - 1, (int) (player.posY), (int) (player.posZ) - 1);

					} else {

						client.fontRenderer.drawStringWithShadow(playerState.airQualityClean ? "Clean" : "Dirty", x, y, 16777215);
						y += statusOverlayDistanceBetweenY;

					}

				} else {
					playerState.airQualityTicksRemaining = 0;

				}
			}

			if (playerState.airQualityTicksRemaining > 0)
				playerState.airQualityTicksRemaining--;

			if (ItemFlameThrower.isEquipped(player)) {
				client.fontRenderer.drawStringWithShadow(getOverlayStatusPercent(ItemFlameThrower.getEquippedItemStack(player), ItemFlameThrower.getFuelRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			} else if (ItemFreezeRay.isEquipped(player)) {
				client.fontRenderer.drawStringWithShadow(getOverlayStatusPercent(ItemFreezeRay.getEquippedItemStack(player), ItemFreezeRay.getFuelRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			} else if (ItemWaterCannon.isEquipped(player)) {
				client.fontRenderer.drawStringWithShadow(getOverlayStatusPercent(ItemWaterCannon.getEquippedItemStack(player), ItemWaterCannon.getFuelRemainingPercent(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}

			else if (ItemCommunication.isEquipped(player)) {

				client.fontRenderer.drawStringWithShadow(getFrequency(ItemCommunication.getEquippedItemStack(player), ItemCommunication.getFrequency(player)), x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
			}

			if (playerState.choseToSyncInventory) {
				if (playerState.syncCooldownRemaining < 1100 && playerState.syncCooldownRemaining > 1000) {
					playerState.choseToSyncInventory = false;
					playerState.choseToSyncInventoryAgain = false;
				}

				else {
					client.fontRenderer.drawStringWithShadow("Synced Inventory With Portal (" + playerState.syncCooldownRemaining / 20 + " seconds until next sync possible)", x, y, 16777215);
					y += statusOverlayDistanceBetweenY;

				}

			} else if (playerState.choseToSyncInventoryAgain) {
				client.fontRenderer.drawStringWithShadow("Be patient: (" + playerState.syncCooldownRemaining / 20 + " seconds until next sync possible)", x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
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
								final int blockX = (int) (player.posX + testX);
								final int blockY = testY;
								final int blockZ = (int) (player.posZ + testZ);
								if (!player.worldObj.isAirBlock(blockX, blockY, blockZ)) {
									final Block testBlock = player.worldObj.getBlock(blockX, blockY, blockZ);
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
				client.fontRenderer.drawStringWithShadow("Cheat Info (" + playerState.cheatInfoTicksRemaining + ")", x, y, 16777215);
				y += statusOverlayDistanceBetweenY;
				for (final Entry<Ore, Integer> foundOre : playerState.cheatInfoOreBlocksFound.entrySet()) {
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
		//RenderIDs.PolymerBrickID = RenderingRegistry.getNextAvailableRenderId();
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPolymerBrick.class, new TileEntityPolymerBrickRenderer());
		for (final Inventory inventory : Inventory.registry.values()) {
			PolycraftInventoryBlock inventoryBlock = (PolycraftInventoryBlock) PolycraftRegistry.getBlock(inventory);
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
			else
				RenderingRegistry.registerBlockHandler(inventory.renderID, renderingHandler = new PolycraftInventoryBlock.BasicRenderingHandler(inventory));

			//TODO: figure out why this is commented out and if it is needed 8.5.2015
			if (inventory.render3D)
				ClientRegistry.bindTileEntitySpecialRenderer(inventoryBlock.tileEntityClass, renderingHandler);

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
                RenderingRegistry.registerEntityRenderingHandler(EntityOilSlimeBallProjectile.class, new RenderSnowball(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("1hl"))));
            }
            else if (GameID.EntityDummy.matches(polycraftEntity)){
                RenderingRegistry.registerEntityRenderingHandler(EntityDummy.class, new RenderDummy((ModelBase)new ModelIronGolem(), 0.25F));
            }

        }

	}
}