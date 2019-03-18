package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiConsent;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentList;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureBase;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonBlock;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.CannonInventory;
import edu.utd.minecraft.mod.polycraft.handler.ResyncHandler;
import edu.utd.minecraft.mod.polycraft.entity.boss.AttackWarning;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool;
import edu.utd.minecraft.mod.polycraft.item.ItemFueledProjectileLauncher;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer.DataPacketType;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer.ExperimentsPacketType;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.scoreboards.ClientScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.ScoreboardManager;
import edu.utd.minecraft.mod.polycraft.trading.ItemStackSwitch;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class ClientEnforcer extends Enforcer {
	public static final ClientEnforcer INSTANCE = new ClientEnforcer();

	private static final int overlayStartX = 5;
	private static final int overlayStartY = 5;
	private static final int overlayDistanceBetweenX = 125;
	private static final int overlayDistanceBetweenY = 10;
	private static final int overlayMaxY = 175;
	private static final int overlayColor = 16777215;
	private static final KeyBinding keyBindingPrivateProperty = new KeyBinding("key.private.property", Keyboard.KEY_P, "key.categories.gameplay");
	private static final KeyBinding keyBindingPrivatePropertyRights = new KeyBinding("key.private.property.rights", Keyboard.KEY_O, "key.categories.gameplay");
	private static final KeyBinding keyBindingAIControls = new KeyBinding("key.private.property", Keyboard.KEY_ADD, "key.categories.gameplay");
	private static final KeyBinding keyBindingAIControlsLeft = new KeyBinding("key.private.property", Keyboard.KEY_LEFT, "key.categories.gameplay");
	private static final KeyBinding keyBindingAIControlsRight = new KeyBinding("key.private.property", Keyboard.KEY_RIGHT, "key.categories.gameplay");
	private static int actionPreventedWarningMessageTicks = 0;
	private static final int actionPreventedWarningMessageMaxTicks = PolycraftMod.convertSecondsToGameTicks(4);
	
	public ArrayList<FeatureBase> baseList = new ArrayList<FeatureBase>();
	
	private final Minecraft client;

	private static class StatusMessage {
		public final String text;
		public int ticksRemaining;

		public StatusMessage(final String text, final int ticksRemaining) {
			this.text = text;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private List<StatusMessage> statusMessages = Lists.newArrayList();
	private static boolean showTutorialRender = false;
	private static boolean showPrivateProperty = false;
	private static boolean showAIControls = false;
	private static int behaviorAI=1;
	private DataPacketType pendingDataPacketType = DataPacketType.Unknown;
	private int pendingDataPacketTypeMetadata = 0;
	private int pendingDataPacketsBytes = 0;
	private ByteBuffer pendingDataPacketsBuffer = null;
	
	public static boolean viewedConsentGUI = false;
	private static boolean needsToSeeConsentForm = false;

	public ClientEnforcer() {
		client = FMLClientHandler.instance().getClient();
	}
	
	@SubscribeEvent
	public void onClientDies(final PlayerEvent.PlayerRespawnEvent event) {
		System.out.println("this is a test event - when does this fire?");
		
	}
	
	
	@SubscribeEvent
	public void KeyInputEvent(cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent event) {
		if (keyBindingPrivateProperty.isPressed()) {
			showPrivateProperty = !showPrivateProperty;
		}
		if (keyBindingAIControls.isPressed()) {
			showAIControls = !showAIControls;
		}
		if(showAIControls)
		{
			if(keyBindingAIControlsLeft.isPressed() && this.behaviorAI>0)
			{
				this.behaviorAI-=1;
			}
			if(keyBindingAIControlsRight.isPressed() && this.behaviorAI<2)
			{
				this.behaviorAI+=1;
			}
		}
	}
	
	

	@Override
	protected void setActionPrevented(final Action action, final PrivateProperty privateProperty) {
		super.setActionPrevented(action, privateProperty);
		actionPreventedWarningMessageTicks = 0;
	}

	private void showStatusMessage(final String message, final int seconds) {
		statusMessages.add(new StatusMessage(message, PolycraftMod.convertSecondsToGameTicks(seconds)));
	}
	
	@SubscribeEvent
	public void onClientDisconnectsFromServer(final ClientDisconnectionFromServerEvent event) {
		System.out.println("This is a client-side test. Does this server trip this??");
		ExperimentManager.INSTANCE = new ExperimentManager();
		ExperimentManager.metadata.clear();
		ClientScoreboard.INSTANCE.clearDisplay();
		this.privatePropertiesMasterJson = null;
		this.privatePropertiesNonMasterJson = null;
		this.playerItemstackSwitchJson = null;
		this.broadcastMessage = null;
		this.whitelistJson = null;
		this.friendsJson = null;
		this.GovernmentsJson = null;
		this.privateProperties.clear();
		this.governments.clear();
		//this.tempChallengeProperties.clear();
		this.tempPrivateProperties.clear();
		this.itemsToSwitch.clear();
		this.privatePropertiesByChunk.clear();
		//this.challengePropertiesByChunk.clear();
		this.privatePropertiesByOwner.clear();
		this.viewedConsentGUI = false;
		this.needsToSeeConsentForm = false;
		GuiConsent.consent = false;
	}
	
	@SubscribeEvent
	public void onClientPacket(final ClientCustomPacketEvent event) {
		try {
			final ByteBuffer payload = ByteBuffer.wrap(event.packet.payload().array());
			if (pendingDataPacketType == DataPacketType.Unknown) {
				pendingDataPacketType = DataPacketType.values()[payload.getInt()];
				pendingDataPacketTypeMetadata = payload.getInt();
				pendingDataPacketsBytes = payload.getInt();
				pendingDataPacketsBuffer = ByteBuffer.allocate(pendingDataPacketsBytes);
			}
			else {
				pendingDataPacketsBytes -= payload.array().length;
				pendingDataPacketsBuffer.put(payload);
				if (pendingDataPacketsBytes == 0 && !isByteArrayEmpty(pendingDataPacketsBuffer.array())) {
					switch (pendingDataPacketType) {
					case PrivateProperties:
						final int count = updatePrivateProperties(CompressUtil.decompress(pendingDataPacketsBuffer.array()), pendingDataPacketTypeMetadata == 1, false);
						final NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
						showStatusMessage("Received " + format.format(count) + " " + (pendingDataPacketTypeMetadata == 1 ? "master" : "other") + " private properties (" + format.format(privatePropertiesByOwner.size()) + " players / "
								+ format.format(privatePropertiesByChunk.size()) + " chunks)", 10);
						break;
					case Friends:
						updateFriends(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case Broadcast:
						onClientBroadcastReceivedEvent(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case TempPrivateProperties:
						final int countPP = updateTempPrivateProperties(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						final NumberFormat formatPP = NumberFormat.getNumberInstance(Locale.getDefault());
						showStatusMessage("Received " + formatPP.format(countPP) + " " + (pendingDataPacketTypeMetadata == 1 ? "master" : "other") + " private properties (" + formatPP.format(privatePropertiesByOwner.size()) + " players / "
								+ formatPP.format(privatePropertiesByChunk.size()) + " chunks)", 10);
						break;
					case Governments:	
						//final int govCount = updateGovernments(CompressUtil.decompress(pendingDataPacketsBuffer.array()), false);	
						//final NumberFormat govformat = NumberFormat.getNumberInstance(Locale.getDefault());	
						//showStatusMessage("Received " + govformat.format(govCount) + "::roles:" + ((Government) governments.toArray()[0]).getRoles()[0], 10);	// commited out for a second -matt
						break;
					case Consent:
						switch(pendingDataPacketTypeMetadata) {
						case 0:	//Player SHOULD see the gui
							this.needsToSeeConsentForm = true;
							GuiConsent.consent = false;
							break;
						case 1:	//Player should not see the gui
							this.needsToSeeConsentForm = false;
							GuiConsent.consent = true;
							break;
						default:
							break;
						}
						break;
					case Challenge:
						System.out.println("Packet Received");
						ExperimentsPacketType tempMetaData = ExperimentsPacketType.values()[pendingDataPacketTypeMetadata];
						switch(tempMetaData) {
							case BoundingBoxUpdate:
								PolycraftMod.logger.debug("Receiving bounding box data...");
								this.updateExperimentalBoundingBox(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
								break;
							case PlayerLeftDimension:
								PolycraftMod.logger.info("User has left the dimension");
								this.baseList.clear();
								ExperimentManager.INSTANCE = new ExperimentManager();
								ExperimentManager.metadata.clear();
								ClientScoreboard.INSTANCE.clearDisplay();
								
								break;
							case ReceiveExperimentsList:
								System.out.println("Receiving experiments list...");
								ExperimentManager.updateExperimentMetadata(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
								//
								break;
							
							default:
							break;
						}
						break;
					case Scoreboard:
						//System.out.println("Packets have all been sent to the client!");
						if(this.pendingDataPacketTypeMetadata == ScoreboardManager.DataType.UpdateScore.ordinal()) { //update the scoreboard
							ClientScoreboard.INSTANCE.updateScore(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						}else if(this.pendingDataPacketTypeMetadata == ScoreboardManager.DataType.UpdatePlayerTeam.ordinal()) { //update the player team
							ClientScoreboard.INSTANCE.updateTeam(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						}else if(this.pendingDataPacketTypeMetadata == ScoreboardManager.DataType.GameOver.ordinal()) {
							ClientScoreboard.INSTANCE.gameOver(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						}else if(this.pendingDataPacketTypeMetadata == ScoreboardManager.DataType.UpdateTeammates.ordinal()) {
							ClientScoreboard.INSTANCE.updateTeamMates(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						}else if(this.pendingDataPacketTypeMetadata == ScoreboardManager.DataType.UpdateTime.ordinal()) {
							ClientScoreboard.INSTANCE.updateTime(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						}
						
						break;
					case playerID:
						this.playerID = updatePlayerID(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case GenericMinigame:
						PolycraftMinigameManager.UpdatePackets(CompressUtil.decompress(pendingDataPacketsBuffer.array()),pendingDataPacketTypeMetadata);
						break;
					case RaceMinigame:
						//RaceGame.INSTANCE.updateRaceGame(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case FreezePlayer:
						switch(pendingDataPacketTypeMetadata) {
						case 0:	//unfreeze the player
							PolycraftMod.proxy.freeze(Minecraft.getMinecraft().thePlayer, false, 0);
							break;
						case 1:	//freeze the player
							PolycraftMod.proxy.freeze(Minecraft.getMinecraft().thePlayer, true, 0);
							break;
						case 2:	//freeze the player controls
							PolycraftMod.proxy.freeze(Minecraft.getMinecraft().thePlayer, true, 1);
							break;
						default:
							break;
						}
					case AttackWarning:
						//AttackWarning.receivePackets(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case Cannon:
						//CannonBlock.UpdatePackets(CompressUtil.decompress(pendingDataPacketsBuffer.array()),pendingDataPacketTypeMetadata);
						break;
					case RespawnSync:
						ResyncHandler.INSTANCE.setResync();
						break;
					case Unknown:
					default:
						break;
					}
					pendingDataPacketType = DataPacketType.Unknown;
					pendingDataPacketTypeMetadata = 0; //is this a problem?
					pendingDataPacketsBuffer = null;
				}
			}
		} catch (IOException e) {
			PolycraftMod.logger.error("Unable to decompress data packetes", e);
		}
	}
	
	public void openExperimentsGui() {
		client.displayGuiScreen(new GuiExperimentList(this.client.thePlayer));
	}
	
	
	@Deprecated
	private void openConsentGui() {
		if(client.theWorld != null) {
			client.displayGuiScreen(new GuiConsent(this.client.thePlayer, (int)this.client.thePlayer.posX, (int) this.client.thePlayer.posY, (int) this.client.thePlayer.posZ));
			this.viewedConsentGUI = true;
		}
	}

	@SubscribeEvent
	public synchronized void onClientChatReceivedEvent(final ClientChatReceivedEvent event) {
		final String message = event.message.getUnformattedText();
		final int usernameIndex = message.indexOf("<");
		if (usernameIndex > -1) {
			final String username = message.substring(usernameIndex + 1, message.indexOf('>', usernameIndex + 1));
			
			//final String sendingPlayer = ClientScoreboard.INSTANCE.teammates.
			
			final EntityPlayer sendingPlayer = client.theWorld.getPlayerEntityByName(username);
			
			//			EntityPlayer sendingPlayer = null;
//			//int ind = playerNames.indexOf(username);
//			for(EntityPlayer player : (List<EntityPlayer>) client.theWorld.playerEntities) {
//				if(username.equals(player.getDisplayName())) {
//					sendingPlayer = player;
//					break;
//				}
//			}
			final EntityPlayer receivingPlayer = Minecraft.getMinecraft().thePlayer;
			if (receivingPlayer.capabilities.isCreativeMode || ExperimentManager.metadata.isEmpty() || ClientScoreboard.INSTANCE.teammates.size() == 0) {
				return; //enable global chat for creative mode players or if there are no experiments on the server.
			} 
			
			else if(ClientScoreboard.INSTANCE.teammates.contains(username)) {
				if(receivingPlayer != null && receivingPlayer.isEntityAlive()) {
					return;
				}
			}else {
				event.setCanceled(true);
				
			}
			
			
			
			
//			if(receivingPlayer.dimension != 8) {
//				//only mess with this if the client is in dimension 8 (running experiments), otherwise, enable global chat.
//				//System.out.println("receiving player: " + receivingPlayer.getDisplayName() + " " + receivingPlayer.dimension);
//				if(sendingPlayer != null && sendingPlayer.dimension != 8) {
//					//System.out.println("Sending Player: " + sendingPlayer.getDisplayName() + " " + sendingPlayer.dimension);
//					return; //send the message to the player if the sender is rendered
//				}else {
//					//System.out.println("is sendingPlayer null? " + (sendingPlayer==null));
//					event.setCanceled(true); // Sender & Receiver are in different dimensions: prevent the message from coming to the player
//					return;
//				}
//			}
//			
//			if (sendingPlayer != null && sendingPlayer.dimension == 8)
//			{
//				//calculate distance and save
//				if (arePlayersWithinDistance(sendingPlayer, receivingPlayer, PolycraftMod.maxChatBlockProximity))
//				{
//					return;
//				}
//
//				final ItemStack itemStackSend = sendingPlayer.inventory.getCurrentItem();
//
//				//is the sender holding a voice cone
//				if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Voice Cone").getItemStack().getUnlocalizedName())))
//				{
//					if (arePlayersWithinDistance(sendingPlayer, receivingPlayer, PolycraftMod.maxChatBlockProximityVoiceCone))
//					{
//						return;
//					}
//				}
//
//				//is the sender holding a megaphone
//				if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Megaphone").getItemStack().getUnlocalizedName())))
//				{
//					if (arePlayersWithinDistance(sendingPlayer, receivingPlayer, PolycraftMod.maxChatBlockProximityMegaphone))
//					{
//						return;
//					}
//				}
//
//			}
//			event.setCanceled(true);

		}
	}

	public void onClientBroadcastReceivedEvent(String complexMessage) throws IOException
	{
		String[] parsed = complexMessage.split(":");
		if (parsed.length > 5)
		{
			final int frequency = Integer.parseInt(parsed[0]);
			final double sourceX = Double.parseDouble(parsed[1]);
			final double sourceY = Double.parseDouble(parsed[2]);
			final double sourceZ = Double.parseDouble(parsed[3]);
			final String itemName = parsed[4].trim();
			ItemStack itemStackSend = null;
			try {
				itemStackSend = CustomObject.registry.get(itemName).getItemStack();
			}catch(NullPointerException e) {
				//User used a custom name for their HAM radio (why would you do this....)
				System.out.println("Why do you do this");
				return; //DO NOTHING.
			}
			final String usernameSender = parsed[5];
			String message = "";
			for (int i = 6; i < parsed.length; i++)
			{
				message += parsed[i];
				if (i < parsed.length - 1)
					message += ":";
			}

			final EntityPlayer receivingPlayer = Minecraft.getMinecraft().thePlayer;
			if (receivingPlayer.capabilities.isCreativeMode) //dont need to broadcast in creative mode, because normal chat will
				return;

			if (receivingPlayer.getDisplayName().equalsIgnoreCase(usernameSender)) //don't need to broadcast to yourself
				return;

			//see what the receiver is holding

			for (int i = 0; i < 36; i++)
			{
				int recFreq = -1;
				ItemStack itemStackRec = receivingPlayer.inventory.getStackInSlot(i);

				if (i < 9)
				{
					//test if  receiving player has walky talky on the hotbar	
					if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Walky Talky").getItemStack().getUnlocalizedName())))
					{
						recFreq = itemStackRec.getItemDamage();
						if (recFreq == frequency)
							if (arePlayersWithinBroadcastRange(sourceX, sourceY, sourceZ, receivingPlayer, PolycraftMod.maxChatBlockProximityWalkyTalky))
								printBroadcastOnClient(receivingPlayer, usernameSender, message);
					}
				}

				//test if sending and receiving player have ham radios on same frequency
				if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("HAM Radio").getItemStack().getUnlocalizedName())))
				{
					recFreq = itemStackRec.getItemDamage();
					if (recFreq == frequency)
						if (arePlayersWithinBroadcastRange(sourceX, sourceY, sourceZ, receivingPlayer, PolycraftMod.maxChatBlockProximityHAMRadio))
							printBroadcastOnClient(receivingPlayer, usernameSender, message);
				}

				//test if sending player holding phone and receiving player has a cell phone on hotbar and they are friends
				//send message to a specific user (tell command)
				//if (friends.contains(getFriendPairKey(whitelist.get(usernameSender), whitelist.get(receivingPlayer.getDisplayName()))))
				//{
				if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Cell Phone").getItemStack().getUnlocalizedName())))
				{
					if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Cell Phone").getItemStack().getUnlocalizedName())))
					{
						final int beginIndex = message.indexOf(":") + 1;
						final String[] textStream = message.split(":");
						if (textStream != null && textStream.length > 1)
						{
							if (receivingPlayer.getDisplayName().equalsIgnoreCase(textStream[0]))
							{
								printBroadcastOnClient(receivingPlayer, usernameSender, message.substring(beginIndex).trim());
							}
						}
					}
				}
				//}

				//if (friends.contains(getFriendPairKey(whitelist.get(usernameSender), whitelist.get(receivingPlayer.getDisplayName()))))
				//{
				if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Smart Phone").getItemStack().getUnlocalizedName())))
				{
					if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Smart Phone").getItemStack().getUnlocalizedName())))
					{
						printBroadcastOnClient(receivingPlayer, usernameSender, message);
					}
				}
				//}
			}
		}

	}

	private void updateExperimentalBoundingBox(String decompressedJson) {
		Gson gson = new Gson();
		this.baseList = new ArrayList<FeatureBase>(Arrays.asList((FeatureBase[]) gson.fromJson(decompressedJson, new TypeToken<FeatureBase[]>() {}.getType())));
		PolycraftMod.logger.debug(this.baseList.toString());
		final EntityPlayer player = client.thePlayer;
		if(!baseList.isEmpty() && player.dimension == 8) {
			//
			for(FeatureBase base: this.baseList){
				if(base.isInBase(client.thePlayer)){
					base.setColor(base.getColor());
				}else{
					base.resetColor();
				}
				base.render(client.thePlayer);
				base.setRendering(true);
			}
		}
	}
	
	private void printBroadcastOnClient(EntityPlayer receivingPlayer, String username, String message) {

		receivingPlayer.addChatMessage(new ChatComponentText("<" + username + "> " + message));
	}

	private boolean arePlayersWithinBroadcastRange(double sourceX, double sourceY, double sourceZ, EntityPlayer receivingPlayer, int distanceBlocks) {

		if (Math.sqrt(Math.pow(sourceX - receivingPlayer.posX, 2) +
				Math.pow(sourceZ - receivingPlayer.posZ, 2) +
				Math.pow(sourceY - receivingPlayer.posY, 2)) <= distanceBlocks)
			return true;

		return false;
	}

	private boolean arePlayersWithinDistance(EntityPlayer sendingPlayer, EntityPlayer receivingPlayer, int distanceBlocks) {

		if (Math.sqrt(Math.pow(sendingPlayer.posX - receivingPlayer.posX, 2) +
				Math.pow(sendingPlayer.posZ - receivingPlayer.posZ, 2) +
				Math.pow(sendingPlayer.posY - receivingPlayer.posY, 2)) <= distanceBlocks)
			return true;

		return false;
	}
	
	public void sendExperimentSelectionUpdate(String jsonData, int metadata) {
		FMLProxyPacket[] packetList = null;
		packetList = getDataPackets(DataPacketType.Challenge, metadata, jsonData);
		System.out.println(packetList.toString());
		if(packetList != null) {
			int i = 0;
			for (final FMLProxyPacket packet : packetList) {
				System.out.println("Sending packet " + i);
				netChannel.sendToServer(packet); 
			}
		}
	}
	
	public void sendGuiConsentUpdate(boolean playerGivesConsent) {
		FMLProxyPacket[] packetList = null;
		int flag = playerGivesConsent ? 0 : 1;
		Gson gson = new Gson();
		packetList = getDataPackets(DataPacketType.Consent, flag, gson.toJson(Minecraft.getMinecraft().thePlayer.getDisplayName()));
		if(packetList != null) {
			int i = 0;
			for (final FMLProxyPacket packet : packetList) {
				System.out.println("Sending packet " + i);
				netChannel.sendToServer(packet); 
			}
		}
	}

	@SubscribeEvent
	public void onRenderTick(final TickEvent.RenderTickEvent tick) {
		if (tick.phase == Phase.END && client.theWorld != null) {
			final EntityPlayer player = client.thePlayer;
			if (player != null && player.isEntityAlive()) {
//				if(!this.baseList.isEmpty()) {
//					if (player.dimension == 8) {
//						for (Base base : baseList) {
//							base.render((Entity)player);
//							base.setRendering(true);
//						}
//					} else {
//						for (Base base : baseList) {
//							//base.setRendering(false);
//						}
//					}
//				}
				final PrivateProperty insidePrivateProperty = findPrivateProperty(player);
				
				if (client.currentScreen == null)
				{
					final PrivateProperty targetPrivateProperty = actionPreventedPrivateProperty == null ? insidePrivateProperty : actionPreventedPrivateProperty;
					int x = overlayStartX;
					int y = overlayStartY;
					//offset if the item overlays are displayed
					if (ItemJetPack.isEquipped(player) || ItemScubaTank.isEquipped(player)) {
						y += overlayDistanceBetweenY;
					}
					if (ItemFueledProjectileLauncher.isEquipped(player)) {
						y += overlayDistanceBetweenY;
					}
					if (statusMessages.size() > 0) {
						for (int i = statusMessages.size() - 1; i >= 0; i--) {
							if (--statusMessages.get(i).ticksRemaining <= 0) {
								statusMessages.remove(i);
							}
						}
						for (final StatusMessage statusMessage : statusMessages) {
							client.fontRenderer.drawStringWithShadow(statusMessage.text, x, y, overlayColor);
							y += overlayDistanceBetweenY;
						}
					}
					if (targetPrivateProperty != null && (showPrivateProperty || actionPreventedPrivateProperty != null)) {
						if (insidePrivateProperty != null) {
							client.fontRenderer.drawStringWithShadow("Private Property - Inside", x, y, overlayColor);
							client.fontRenderer.drawStringWithShadow(insidePrivateProperty.name, x, y += overlayDistanceBetweenY, overlayColor);
						}
						if (actionPreventedPrivateProperty != null && insidePrivateProperty != actionPreventedPrivateProperty) {
							if (insidePrivateProperty != null) {
								y += overlayDistanceBetweenY + overlayDistanceBetweenY;
							}
							client.fontRenderer.drawStringWithShadow("Private Property - Beside", x, y, overlayColor);
							client.fontRenderer.drawStringWithShadow(targetPrivateProperty.name, x, y += overlayDistanceBetweenY, overlayColor);
						}
						client.fontRenderer.drawStringWithShadow("Owned by " + targetPrivateProperty.owner, x, y += overlayDistanceBetweenY, overlayColor);
						client.fontRenderer.drawStringWithShadow("Posted: " + targetPrivateProperty.message, x, y += overlayDistanceBetweenY, overlayColor);
						client.fontRenderer.drawStringWithShadow(String.format("Position: %d,%d,%d (%d, %d)", (int) player.posX, (int) player.posY, (int) player.posZ, player.chunkCoordX, player.chunkCoordZ), x,
								y += overlayDistanceBetweenY, overlayColor);
						y += overlayDistanceBetweenY;
						if (GameSettings.isKeyDown(keyBindingPrivatePropertyRights)) {
							client.fontRenderer.drawStringWithShadow("Property Rights:", x, y += overlayDistanceBetweenY, overlayColor);
							final int startY = y;
							boolean any = false;
							for (final Action action : Action.values()) {
								if (targetPrivateProperty.actionEnabled(client.thePlayer, action)) {
									client.fontRenderer.drawStringWithShadow(action.toString(), x, y += overlayDistanceBetweenY, overlayColor);
									any = true;
								}
								//move over to the next column
								if (y > overlayMaxY) {
									x += overlayDistanceBetweenX;
									y = startY;
								}
							}
							if (!any) {
								client.fontRenderer.drawStringWithShadow("None", x, y += overlayDistanceBetweenY, overlayColor);
							}
						}
						else if (actionPrevented != null) {
							client.fontRenderer.drawStringWithShadow("Action Prevented: " + actionPrevented, x, y += overlayDistanceBetweenY, overlayColor);
							if (actionPreventedWarningMessageTicks++ == actionPreventedWarningMessageMaxTicks) {
								setActionPrevented(null, null);
							}
						}
					}
					else if (showPrivateProperty) {
						client.fontRenderer.drawStringWithShadow("Private Property - None", x, y, overlayColor);
					}
					if(showAIControls)
					{
						client.fontRenderer.drawStringWithShadow("AIControls Test", x, y, overlayColor);
						if(behaviorAI==0)
						{
							client.ingameGUI.drawRect(x+6, y+14, x+26, y+34, 0x66CC0011);
							
							
						}
						if(behaviorAI==1)
						{
							client.ingameGUI.drawRect(x+38, y+14, x+58, y+34, 0x6611CC00);
							//â˜¯
						}
						if(behaviorAI==2)
						{
							client.ingameGUI.drawRect(x+70, y+14, x+90, y+34, 0x660011CC);
							//â™¥
						}
						
						client.ingameGUI.drawRect(x+8, y+16, x+24, y+32, 0xFFCC0011);
						client.ingameGUI.drawRect(x+40, y+16, x+56, y+32, 0xFF11CC00);
						client.ingameGUI.drawRect(x+72, y+16, x+88, y+32, 0xFF0011CC);
						
						client.fontRenderer.drawStringWithShadow("☢",x+12, y+20,  0xFF000000);
						client.fontRenderer.drawStringWithShadow("☯", x+44, y+20,  0xFF000000);
						client.fontRenderer.drawStringWithShadow("♥", x+78, y+20,  0xFF000000);
					}
				}
				else
				{
					client.fontRenderer.drawStringWithShadow(String.format("Position: %d,%d,%d (%d, %d)", (int) player.posX, (int) player.posY, (int) player.posZ, player.chunkCoordX, player.chunkCoordZ), overlayStartX,
							overlayStartY, overlayColor);
				}

			}
			
			if(this.needsToSeeConsentForm && !this.viewedConsentGUI) {
				PolycraftMod.proxy.openConsentGui(client.thePlayer, (int)client.thePlayer.posX, (int)client.thePlayer.posY, (int)client.thePlayer.posZ);
				this.viewedConsentGUI = true;
			}

		}
	}

	
	public static boolean getShowTutorialRender() {
		
		return showTutorialRender;
	}
	
	public static void setShowTutorialRender(boolean render) {
		
		showTutorialRender=render;
	}
	
	public static boolean getShowPP() {
		
		return showPrivateProperty;
	}
	
	public static boolean getShowAIC() {
		
		return showAIControls;
	}
}
