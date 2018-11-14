package edu.utd.minecraft.mod.polycraft.privateproperty;

import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import scala.util.parsing.json.JSON;
import scala.util.parsing.json.JSONArray;
import scala.util.parsing.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager.ExperimentListMetaData;
import edu.utd.minecraft.mod.polycraft.entity.boss.AttackWarning;
import edu.utd.minecraft.mod.polycraft.minigame.KillWall;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer.DataPacketType;
import edu.utd.minecraft.mod.polycraft.trading.ItemStackSwitch;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import edu.utd.minecraft.mod.polycraft.util.SystemUtil;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;

public class ServerEnforcer extends Enforcer {
	public static final ServerEnforcer INSTANCE = new ServerEnforcer();

	public static final String portalRestUrl = System
			.getProperty("portal.rest.url");
	// refresh once per minecraft day by default
	private static final long portalRefreshTicksPrivateProperties = SystemUtil
			.getPropertyLong("portal.refresh.ticks.private.properties", 24000);
	private static final long portalRefreshTicksWhitelist = SystemUtil
			.getPropertyLong("portal.refresh.ticks.whitelist", 24000);
	private static final long portalRefreshTicksFriends = SystemUtil
			.getPropertyLong("portal.refresh.ticks.friends", 24000);
	private static final long portalRefreshTicksGovernments = SystemUtil
			.getPropertyLong("portal.refresh.ticks.governments", 12000);

	private DataPacketType pendingDataPacketType = DataPacketType.Unknown;
	private int pendingDataPacketTypeMetadata = 0;
	private int pendingDataPacketsBytes = 0;
	private ByteBuffer pendingDataPacketsBuffer = null;
	
	protected final Map<String, Integer> frozenPlayers = Maps.newHashMap();
	
	
	@SubscribeEvent
	public void onWorldTick(final TickEvent.WorldTickEvent event) {
		// TODO not sure why this is getting called multiple times with
		// different world java objects for the same world
		if ((event.phase == TickEvent.Phase.END)
				&& (event.world.provider.dimensionId == 0 || event.world.provider.dimensionId == 8)) { //added properties to challenge dimension --matt
			//System.out.println("I have done this");
			onWorldTickPrivateProperties(event);
			onWorldTickWhitelist(event);
			onWorldTickFriends(event);
			onWorldTickInventories(event);
			//onWorldTickGovernments(event);
			onWorldTickCheckFrozenPlayers(event);

		}
	}

	//Is this supposed to be commented out?? 
	//TODO: recomment this function before pushing to production.
//	 @SubscribeEvent
//	 public void onServerChatEvent(final ServerChatEvent event)
//	 {
//		 if (event.message.startsWith(chatCommandPrefix)) {
//		 return;
//		 }
//		
//		 if (event.message.startsWith("//"))
//		 return;
//		
//		 for (int i = 0; i < 36; i++)
//		 {
//			 ItemStack itemStackSend = event.player.inventory.getStackInSlot(i);
//			
//			 if (itemStackSend != null)
//			 {
//			 if (i < 9)
//			 {
//			 //test if receiving player has walky talky on the hotbar
//			 if (itemStackSend != null &&
//			 ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Walky Talky").getItemStack().getUnlocalizedName())))
//			 broadcastFromSender(event, itemStackSend);
//			
//			 //test if receiving player has cell phone on the hotbar
//			 if (itemStackSend != null &&
//			 ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Cell Phone").getItemStack().getUnlocalizedName())))
//			 broadcastFromSender(event, itemStackSend);
//			
//			 }
//			
//			 //test if sending and receiving player have ham radios on same frequency
//			 if (itemStackSend != null &&
//			 ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("HAM Radio").getItemStack().getUnlocalizedName())))
//			 broadcastFromSender(event, itemStackSend);
//			
//			 //test if sending player holding phone broadcast the message
//			 //send message to a specific user (tell command)
//			 if (itemStackSend != null &&
//			 ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Smart Phone").getItemStack().getUnlocalizedName())))
//			 broadcastFromSender(event, itemStackSend);
//			
//			 }
//		
//		 }
//	 }

	public void broadcastFromSender(ServerChatEvent event, ItemStack itemStack) {
		// somehow we need to send a broadcast event now...
		// int i = 0;
		// ClientBroadcastReceivedEvent broadcast =
		// new ClientBroadcastReceivedEvent(new ChatComponentText("<" +
		// event.username + "> " + event.message),
		// event.player.posX, event.player.posY, event.player.posY,
		// itemStack.getDisplayName(), itemStack.getItemDamage());

		// MinecraftForge.EVENT_BUS.post(broadcast);

		broadcastMessage = String.valueOf(itemStack.getItemDamage()) + ":"
				+ String.valueOf(event.player.posX) + ":"
				+ String.valueOf(event.player.posY) + ":"
				+ String.valueOf(event.player.posZ) + ":"
				+ itemStack.getUnlocalizedName() + ":" + event.username + ":"
				+ event.message;

		sendDataPackets(DataPacketType.Broadcast, 1);

		// option #2: modified form
		// of...sendDataPackets(DataPacketType.Broadcast, 1) which includes
		// message;

	}
	
	@SubscribeEvent
	public void onServerPacket(final ServerCustomPacketEvent event) {
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
					case Challenge:
						onClientExperimentSelection(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case Consent:
						switch(pendingDataPacketTypeMetadata) {
						case 0: //player gives consent
							//TODO: Player gave consent!
							System.out.println("Player Gives Consent");
							break;
						case 1:
							//TODO: Player withdraws consent
							System.out.println("Player Withdraws Consent");
							break;
						default:
							break;
						}
					default:
						break;
					}
				
				//Flush the buffer. and reset for the next message coming from the client.
				pendingDataPacketType = DataPacketType.Unknown;
				pendingDataPacketTypeMetadata = 0; 
				pendingDataPacketsBuffer = null;
					
				}
				
			}
			
			
			
		}catch (Exception e) {
			PolycraftMod.logger.error("Unable to decompress data packetes", e);
		}
	}
	
	@SubscribeEvent
	public void onClientJoinsServer(final PlayerEvent.PlayerLoggedInEvent event) {
		System.out.println("Player Logged in");
		System.out.println("Player Dim:" + event.player.dimension);
		if(event.player.dimension == 8) {//if player is in Experiments dimension, then they should not log in there
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 0,	new PolycraftTeleporter(player.mcServer.worldServerForDimension(0)));	
		}
		
		if(System.getProperty("isExperimentServer") != null) {
			this.shouldClientDisplayConsentGUI(true, (EntityPlayerMP)event.player);
		} else {
			this.shouldClientDisplayConsentGUI(false, (EntityPlayerMP)event.player);
		}
	}
	
	@SubscribeEvent
	public void onEntityRespawn(final PlayerEvent.PlayerRespawnEvent event) {
		System.out.println("I think this is server-side?");
		//if the player dies, let the client know that.
		//this.sendExperimentUpdatePackets(null, (EntityPlayerMP)event.player); //send a packet to the client saying they left the server.
	}
	
	@SubscribeEvent
	public void onPlayerDamage(final LivingHurtEvent event) {
		if(event.entity.dimension == 8 && event.entity instanceof EntityPlayerMP  && (event.entityLiving.getHealth() - event.ammount <= 0)) {
			event.entityLiving.setHealth(1); //prevent death in dimension 8. Set health to the lowest level.
			event.setCanceled(true);
		}
	}
	
	/**
	 * On client disconnect, handle this gracefully by removing them from any experiments
	 * they've joined or have queued for.
	 * This will work even in a mini-game, but results may be un-expected! :) 
	 * @param event the client that logged out (accessed by event.player)
	 */
	@SubscribeEvent
	public void onClientDisconnectFromServer(final PlayerEvent.PlayerLoggedOutEvent event) {
		System.out.println("Client Disconnect from server");
		System.out.println("Player: " + event.player.getDisplayName());
		ExperimentManager.INSTANCE.checkAndRemovePlayerFromExperimentLists(event.player.getDisplayName());
		//clear player inventory, if they disconnected from dimension 8.
		if(event.player.dimension == 8) {
			event.player.inventory.mainInventory = new ItemStack[36];
			event.player.inventory.armorInventory = new ItemStack[4];
		}
		//note: the global player list in ExperimentManager doesn't update fast enough - remove them using their player object.
	}
	
	/**
	 * Triggered when server receives a packet from the client
	 * @param decompressedJson the JSON version of the ExperimentParticipantMetaData
	 * object that indicates to the server which experiment a player wants to join or withdraw from.
	 */
	private void onClientExperimentSelection(final String decompressedJson) {
		Gson gson = new Gson();
		ExperimentManager.ExperimentParticipantMetaData part = gson.fromJson(decompressedJson, new TypeToken<ExperimentManager.ExperimentParticipantMetaData>() {}.getType());
		if(part.wantsToJoin) {
			ExperimentManager.INSTANCE.addPlayerToExperiment(part.experimentID, (EntityPlayerMP)ExperimentManager.INSTANCE.getPlayerEntity(part.playerName));
		}else {
			ExperimentManager.INSTANCE.removePlayerFromExperiment(part.experimentID, (EntityPlayerMP)ExperimentManager.INSTANCE.getPlayerEntity(part.playerName));
		}
		PolycraftMod.logger.debug(part.toString());
//		System.out.println("Receiving new Data...");
//		System.out.println(metadata.toString());
	}

	private void onWorldTickPrivateProperties(
			final TickEvent.WorldTickEvent event) {
		// refresh private property permissions at the start of each day, or if
		// we haven't loaded them yet
		if (portalRestUrl != null
				&& (event.world.getWorldTime()
						% portalRefreshTicksPrivateProperties == 1
						|| privatePropertiesMasterJson == null || privatePropertiesNonMasterJson == null)) {
			try {
				String url = portalRestUrl.startsWith("file:") ? portalRestUrl
						+ "privatepropertiesinclude.json"
				// TODO eventually send a timestamp of the last successful pull,
				// so the server can return no-change (which is probably most of
				// the time)
						: String.format(
								"%s/private_properties/worlds/include/%s/",
								portalRestUrl, event.world.getWorldInfo()
										.getWorldName());
				updatePrivateProperties(NetUtil.getText(url), true, true);
				sendDataPackets(DataPacketType.PrivateProperties, 1);

				url = portalRestUrl.startsWith("file:") ? portalRestUrl
						+ "privatepropertiesexclude.json"
				// TODO eventually send a timestamp of the last successful pull,
				// so the server can return no-change (which is probably most of
				// the time)
						: String.format(
								"%s/private_properties/worlds/exclude/%s/",
								portalRestUrl, event.world.getWorldInfo()
										.getWorldName());
				updatePrivateProperties(NetUtil.getText(url), false, true);
				sendDataPackets(DataPacketType.PrivateProperties, 0);
			} catch (final Exception e) {
				// TODO set up a log4j mapping to send emails on error messages
				// (via mandrill)
				if (privatePropertiesMasterJson == null
						|| privatePropertiesNonMasterJson == null) {
					PolycraftMod.logger.error(
							"Unable to load private properties", e);
					System.exit(-1);
				} else {
					PolycraftMod.logger.error(
							"Unable to refresh private properties", e);
				}
			}
		}
	}
	
	public void sendScoreboardUpdatePackets(final String jsonStringToSend, EntityPlayerMP player, final int metadata) {
		PolycraftMod.logger.debug("Scoreboard update sending...");
		//TODO: add meta-data parsing.
		final FMLProxyPacket[] packetList = getDataPackets(DataPacketType.Scoreboard, metadata, jsonStringToSend);
		if(packetList != null) {
			for (final FMLProxyPacket packet : packetList) {
				netChannel.sendTo(packet, player);
			}
			
		}
	}

	public void sendTempPPDataPackets() {
		sendDataPackets(DataPacketType.TempPrivateProperties, 0, null);
	}
	
	public void sendTempCPDataPackets(EntityPlayerMP player) {
		sendDataPackets(DataPacketType.Challenge, 2, player);
	}
	
	public void minigameUpdate(int meta) {
		sendDataPackets(DataPacketType.GenericMinigame, meta, null);
	}
	
	public void raceGameUpdate() {
		sendDataPackets(DataPacketType.RaceMinigame, 0, null);
	}
	
	public void sendAttackWarning() {
		sendDataPackets(DataPacketType.AttackWarning, 0, null);
	}
	

	
	/**
	 * Send experiment updates to players in the game
	 * Sends 3 cases: 	ExperimentListMetaData updates to the Client (sent to all players in dimension 0)
	 * 					ClientLeavesDimension update (hide scoreboard, reset GUI, etc.)
	 * 					BoundingBox updates for client-side rendering handlers
	 * @param jsonStringToSend if Null, then it is case 2 else, it's case 3
	 * @param player if Null, then it is case 1 else, it's either case 2 or 3.
	 */
	public void sendExperimentUpdatePackets(final String jsonStringToSend, EntityPlayerMP player) {
		//TODO: add meta-data parsing.
		FMLProxyPacket[] packetList = null;
		
		if(player == null) { //case: Send Experiment List Updates
			packetList = getDataPackets(DataPacketType.Challenge, ExperimentsPacketType.ReceiveExperimentsList.ordinal(), jsonStringToSend);
			if(packetList != null) {
				int i = 0;
				for (final FMLProxyPacket packet : packetList) {
					System.out.println("Sending packet " + i);
					netChannel.sendToDimension(packet, 0); //send to all players in dimension 0 (don't send to players in an experiment)
					i++;
				}
			}
			return;
		}
		
		if(jsonStringToSend == null) { //case: Player is leaving dimension
			packetList = getDataPackets(DataPacketType.Challenge, ExperimentsPacketType.PlayerLeftDimension.ordinal(), "PlayerLeavingDimension");
			System.out.println("Player is Leaving Dimension");
			
		} else { //case: Bounding Box updates for client rendering
			packetList = getDataPackets(DataPacketType.Challenge, ExperimentsPacketType.BoundingBoxUpdate.ordinal(), jsonStringToSend);
		}
		if(packetList != null) {
			for (final FMLProxyPacket packet : packetList) {
				netChannel.sendTo(packet, player);
			}				
		}
	}
	/**
	 * Send an updated list of experiments to all players in dimension 0
	 * @param jsonStringToSend the Gson arraylist of ExperimentListMetaData objects. 
	 */
	@Deprecated //TODO: delete this.
	public void sendExperimentListUpdates(final String jsonStringToSend) {
		FMLProxyPacket[] packetList = null;
		packetList = getDataPackets(DataPacketType.Challenge, ExperimentsPacketType.ReceiveExperimentsList.ordinal(), jsonStringToSend);
		System.out.println(packetList.toString());
		if(packetList != null) {
			int i = 0;
			for (final FMLProxyPacket packet : packetList) {
				System.out.println("Sending packet " + i);
				netChannel.sendToDimension(packet, 0); //send to all players in dimension 0 (don't send to players in an experiment)
				i++;
			}
		}
	}
	
	public void freezePlayer(boolean flag, EntityPlayerMP player) {
		//true flag will freeze player, false flag will unfreeze player
		int f = flag? 1: 0; //1 freezes player, 0 thaws player
		sendDataPackets(DataPacketType.FreezePlayer, f, player);
	}
	

	public void shouldClientDisplayConsentGUI(boolean flag, EntityPlayerMP player) {
		int yes = flag ? 0 : 1;
		sendDataPackets(DataPacketType.Consent, yes, player);
	}

	public void freezePlayerForTicks(int ticks, EntityPlayerMP player) {
		//Freeze player for specific number of ticks
		if(frozenPlayers.containsKey(player.getDisplayName())) {
			frozenPlayers.replace(player.getDisplayName(), ticks);
		}else {
			frozenPlayers.put(player.getDisplayName(), ticks);
		}
		sendDataPackets(DataPacketType.FreezePlayer, 2, player);
	}
	
	private void onWorldTickCheckFrozenPlayers(final TickEvent.WorldTickEvent event) {
		if(frozenPlayers.isEmpty())
			return;
		boolean removePlayer = false;
		for(String playerName: frozenPlayers.keySet()) {
			if(frozenPlayers.get(playerName) > 0)
				frozenPlayers.replace(playerName, frozenPlayers.get(playerName) - 1);
			else {	//once the time runs out, we should unfreeze the player
				for(Object obj: MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
					if(obj instanceof EntityPlayerMP) {
						if(((EntityPlayerMP)obj).getDisplayName().equals(playerName))
						{
							sendDataPackets(DataPacketType.FreezePlayer, 0, ((EntityPlayerMP)obj));	//unfreeze player
							removePlayer = true;
							frozenPlayers.remove(playerName);
						}
					}
				}
			}
			if(removePlayer)
				break;
		}
	}

	
	
	//TODO: refactor and remove this.
	private void sendDataPackets(final DataPacketType type) {
		sendDataPackets(type, 0, null);
	}

	//TODO: refactor and safely remove this
	private void sendDataPackets(final DataPacketType type,
			final int typeMetadata) {
		sendDataPackets(type, typeMetadata, null);
	}

	private void sendDataPackets(final DataPacketType type,
			final int typeMetadata, final EntityPlayerMP player) {
		final FMLProxyPacket[] packets = getDataPackets(type, typeMetadata);
		if (packets != null) {
			for (final FMLProxyPacket packet : packets) {
				if (player == null) {
					netChannel.sendToAll(packet);
				} else {
					netChannel.sendTo(packet, player);
				}
			}
		}
	}
	
	private FMLProxyPacket[] getDataPackets(final DataPacketType type,
			final int typeMetadata) {
		try {
			Gson gson = new Gson();
			// we have to split these up into smaller packets due to this issue:
			// https://github.com/MinecraftForge/MinecraftForge/issues/1207#issuecomment-48870313
			final byte[] dataBytes = CompressUtil
					.compress(type == DataPacketType.PrivateProperties ? (typeMetadata == 1 ? privatePropertiesMasterJson
																		: privatePropertiesNonMasterJson)
							: type == DataPacketType.Broadcast ? broadcastMessage
							: type == DataPacketType.Friends ? friendsJson	
							: type == DataPacketType.Governments ? GovernmentsJson 
//							: type == DataPacketType.Challenge ? gson.toJson(typeMetadata == 1 ? gson.toJson(ExperimentManager.INSTANCE)
//																		:tempChallengeProperties) 
							: type == DataPacketType.TempPrivateProperties ? gson.toJson(tempPrivateProperties)
							: type == DataPacketType.GenericMinigame ? gson.toJson(PolycraftMinigameManager.INSTANCE)//get through manager
//							: type == DataPacketType.RaceMinigame ? gson.toJson(RaceGame.INSTANCE)
							: type == DataPacketType.AttackWarning ? gson.toJson(AttackWarning.toSend)
							: type == DataPacketType.playerID ? gson.toJson(this.playerID)
									: gson.toJson(""));	//default packet should be blank 
			//System.out.println("type: " + DataPacketType.);

			final int payloadPacketsRequired = getPacketsRequired(dataBytes.length);
			final int controlPacketsRequired = 1;
			final FMLProxyPacket[] packets = new FMLProxyPacket[controlPacketsRequired
					+ payloadPacketsRequired];
			packets[0] = new FMLProxyPacket(Unpooled.buffer()
					.writeInt(type.ordinal()).writeInt(typeMetadata)
					.writeInt(dataBytes.length).copy(), netChannelName);
			for (int payloadIndex = 0; payloadIndex < payloadPacketsRequired; payloadIndex++) {
				int startDataIndex = payloadIndex * maxPacketSizeBytes;
				int length = Math.min(dataBytes.length - startDataIndex,
						maxPacketSizeBytes);
				packets[controlPacketsRequired + payloadIndex] = new FMLProxyPacket(
						Unpooled.buffer()
								.writeBytes(dataBytes, startDataIndex, length)
								.copy(), netChannelName);
			}
			return packets;
		} catch (IOException e) {
			PolycraftMod.logger.error("Unable to compress packet data", e);
			return null;
		}
	}
	
	public String forceUpdateWhitelist(final World world) {
		// refresh the whitelist at the start of each day, or if we haven't it
		// yet
		String result = "added ";
		if (portalRestUrl != null) {
			try {
				final String url = portalRestUrl.startsWith("file:") ? portalRestUrl
						+ "whitelist.json"
						: String.format("%s/worlds/%s/whitelist/",
								portalRestUrl, world.getWorldInfo()
										.getWorldName());
				final Set<String> previousWhitelist = Sets.newHashSet(whitelist
						.keySet());
				updateWhitelist(NetUtil.getText(url));

				final String url_uuid = portalRestUrl.startsWith("file:") ? portalRestUrl
						+ "whitelist.json"
						: String.format("%s/worlds/%s/whitelist_uuid/",
								portalRestUrl, world.getWorldInfo()
										.getWorldName());
				updateUUIDWhitelist(NetUtil.getText(url_uuid));

				// reconcile whitelists
				final MinecraftServer minecraftserver = MinecraftServer
						.getServer();
				UUID userID;

				for (final String usernameToAdd : whitelist.keySet()) {
					// if the user is new, add to the whitelist
					if (!previousWhitelist.remove(usernameToAdd)) {
						// final GameProfile gameprofile =
						// minecraftserver.func_152358_ax().func_152655_a(usernameToAdd);
						try {
							userID = UUID.fromString(whitelist_uuid
									.get(usernameToAdd));
							final GameProfile gameprofile = new GameProfile(
									userID, usernameToAdd);
							if (gameprofile != null)
								minecraftserver.getConfigurationManager()
										.func_152601_d(gameprofile);
							result += usernameToAdd + ",";
						} catch (IllegalArgumentException e) {
							System.out.println("Could not add to whitelist: "
									+ usernameToAdd);
						}

					}
				}
				result += ",,, Removed";
				// remove users from the whitelist that were not in the new
				// whitelist
				for (final String usernameToRemove : previousWhitelist) {
					final GameProfile gameprofile = minecraftserver
							.getConfigurationManager().func_152599_k()
							.func_152706_a(usernameToRemove);
					if (gameprofile != null)
						minecraftserver.getConfigurationManager()
								.func_152597_c(gameprofile);
					result += usernameToRemove + ",";
					// TODO don't worry about kicking them right now
				}
			} catch (final Exception e) {
				// TODO set up a log4j mapping to send emails on error messages
				// (via mandrill)
				if (whitelistJson == null) {
					PolycraftMod.logger.error("Unable to load whitelist", e);
					System.exit(-1);
				} else {
					PolycraftMod.logger.error("Unable to refresh whitelist", e);
				}
			}
		}
		return result;
	}
	

	private void onWorldTickWhitelist(final TickEvent.WorldTickEvent event) {
		// refresh the whitelist at the start of each day, or if we haven't it
		// yet
		if (portalRestUrl != null
				&& (event.world.getWorldTime() % portalRefreshTicksWhitelist == 0 || whitelistJson == null)) {
			try {
				final String url = portalRestUrl.startsWith("file:") ? portalRestUrl
						+ "whitelist.json"
						: String.format("%s/worlds/%s/whitelist/",
								portalRestUrl, event.world.getWorldInfo()
										.getWorldName());
				final Set<String> previousWhitelist = Sets.newHashSet(whitelist
						.keySet());
				updateWhitelist(NetUtil.getText(url));

				final String url_uuid = portalRestUrl.startsWith("file:") ? portalRestUrl
						+ "whitelist.json"
						: String.format("%s/worlds/%s/whitelist_uuid/",
								portalRestUrl, event.world.getWorldInfo()
										.getWorldName());
				updateUUIDWhitelist(NetUtil.getText(url_uuid));

				// reconcile whitelists
				final MinecraftServer minecraftserver = MinecraftServer
						.getServer();
				UUID userID;

				for (final String usernameToAdd : whitelist.keySet()) {
					// if the user is new, add to the whitelist
					if (!previousWhitelist.remove(usernameToAdd)) {
						// final GameProfile gameprofile =
						// minecraftserver.func_152358_ax().func_152655_a(usernameToAdd);
						try {
							userID = UUID.fromString(whitelist_uuid
									.get(usernameToAdd));
							final GameProfile gameprofile = new GameProfile(
									userID, usernameToAdd);
							if (gameprofile != null)
								minecraftserver.getConfigurationManager()
										.func_152601_d(gameprofile);
						} catch (IllegalArgumentException e) {
							System.out.println("Could not add to whitelist: "
									+ usernameToAdd);
						}

					}
				}
				// remove users from the whitelist that were not in the new
				// whitelist
				for (final String usernameToRemove : previousWhitelist) {
					final GameProfile gameprofile = minecraftserver
							.getConfigurationManager().func_152599_k()
							.func_152706_a(usernameToRemove);
					if (gameprofile != null)
						minecraftserver.getConfigurationManager()
								.func_152597_c(gameprofile);
					// TODO don't worry about kicking them right now
				}
			} catch (final Exception e) {
				// TODO set up a log4j mapping to send emails on error messages
				// (via mandrill)
				if (whitelistJson == null) {
					PolycraftMod.logger.error("Unable to load whitelist", e);
					System.exit(-1);
				} else {
					PolycraftMod.logger.error("Unable to refresh whitelist", e);
				}
			}
		}
	}
	
	private void addUserToWhitelist(long ID, String usernameToAdd, String uuid) {
		final MinecraftServer minecraftserver = MinecraftServer
				.getServer();
		UUID userID;
		whitelist.put(usernameToAdd, ID);
		whitelist_uuid.put(usernameToAdd, uuid);
		
		try {
			userID = UUID.fromString(whitelist_uuid
					.get(usernameToAdd));
			final GameProfile gameprofile = new GameProfile(
					userID, usernameToAdd);
			if (gameprofile != null)
				minecraftserver.getConfigurationManager()
						.func_152601_d(gameprofile);
		} catch (IllegalArgumentException e) {
			System.out.println("Could not add to whitelist: "
					+ usernameToAdd);
		}
	}

	private void onWorldTickFriends(final TickEvent.WorldTickEvent event) {
		// refresh the friends at the start of each day, or if we haven't it yet
		if (portalRestUrl != null
				&& (event.world.getWorldTime() % portalRefreshTicksFriends == 0 || friendsJson == null)) {
			try {
				final String url = portalRestUrl.startsWith("file:") ? portalRestUrl
						+ "friends.json"
						: String.format("%s/friends/", portalRestUrl);
				updateFriends(NetUtil.getText(url));
				sendDataPackets(DataPacketType.Friends);
			} catch (final Exception e) {
				// TODO set up a log4j mapping to send emails on error messages
				// (via mandrill)
				if (friendsJson == null) {
					PolycraftMod.logger.error("Unable to load friends", e);
				} else {
					PolycraftMod.logger.error("Unable to refresh friends", e);
				}
			}
		}
	}

	private void onWorldTickInventories(final TickEvent.WorldTickEvent event) {
		// if (portalRestUrl != null &&

	}
	
	private void onWorldTickGovernments(final TickEvent.WorldTickEvent event) {	
		//refresh private property permissions at the start of each day, or if we haven't loaded them yet	
		if (portalRestUrl != null && (event.world.getWorldTime() % portalRefreshTicksGovernments == 1 || GovernmentsJson == null)) {	
			try {	
				String url = portalRestUrl.startsWith("file:")	
						? portalRestUrl + "Governments.json"	
						//TODO eventually send a timestamp of the last successful pull, so the server can return no-change (which is probably most of the time)	
						: String.format("%s/governments", portalRestUrl);	
				updateGovernments(NetUtil.getText(url), true);	
				sendDataPackets(DataPacketType.Governments);	
	
			} catch (final Exception e) {	
				//TODO set up a log4j mapping to send emails on error messages (via mandrill)	
				if (GovernmentsJson == null) {	
					PolycraftMod.logger.error("Unable to load Governments", e);	
					System.exit(-1);
				} else {	
					PolycraftMod.logger.error("Unable to refresh Governments", e);	
		 				}
			}
		}
	}
	
	@SubscribeEvent
	public void ClientConnectedToServerEvent(final EntityJoinWorldEvent event) {
		//Entity wolf cannot cast to player
		if(event.entity instanceof EntityPlayerMP) {
			System.out.println(event.entity.getCommandSenderName());
			ExperimentManager.INSTANCE.onEntityJoinWorldEventSendUpdates((EntityPlayer)event.entity); //send newly joined players an update
			if(event.entity.dimension == 8) {
				
			}
		}
		if (portalRestUrl != null && event.entity instanceof EntityPlayerMP) {
			final EntityPlayerMP player = (EntityPlayerMP) event.entity;
			player.addChatMessage(new ChatComponentText("Welcome to PolycraftWorld!"));
			player.addChatMessage(new ChatComponentText("Type \"/help\" for a list of commands"));
			player.addChatMessage(new ChatComponentText("By playing on our servers, you accept our TOS and privacy policy available on polycraftworld.com"));
			sendDataPackets(DataPacketType.PrivateProperties, 1, player);
			sendDataPackets(DataPacketType.PrivateProperties, 0, player);
			sendDataPackets(DataPacketType.Friends);
			 //send updated experiments available to everyone
			//sendDataPackets(DataPacketType.Governments);
			if(this.whitelist.containsKey(player.getDisplayName().toLowerCase())) {
				this.playerID = this.whitelist.get(player.getDisplayName().toLowerCase()); //unexpected conflict with upper and lower case. may need to be looked at later.
				sendDataPackets(DataPacketType.playerID, 0, player);
			}else {
				if (!portalRestUrl.startsWith("file:")) {
					try {
						String response = NetUtil.post(String.format("%s/create_player/", portalRestUrl),
								ImmutableMap.of("mincraft_user_name", player.getDisplayName().toLowerCase()));
						
						final GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.registerTypeAdapter(PlayerHelper.class,
								new PlayerHelper.Deserializer());

						final Gson gson = gsonBuilder.create();
						final PlayerHelper newPlayer = gson.fromJson(
								response,
								new TypeToken<PlayerHelper>() {
								}.getType());
						addUserToWhitelist(newPlayer.id, newPlayer.minecraft_user_name, newPlayer.uuid);
						sendDataPackets(DataPacketType.playerID, 0, player);
					} catch (final IOException e) {
						PolycraftMod.logger.error(
								"Unable to create new player account or get player data", e);
					}
				}
			}
		}
	}
	
	public String AddEmail(String minecraftUserName, String email) {
		try {
			Map<String, String> params = Maps.newHashMap();
			params.put("minecraft_user_name", minecraftUserName);
			params.put("email", email);
			String response = NetUtil.post(String.format("%s/add_email/", portalRestUrl),
					params);
			if(response.length() > 500)
				response = "Error processing command";
			return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error";
		}
	}
}
