package edu.utd.minecraft.mod.polycraft.privateproperty;

import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.minigame.KillWall;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import edu.utd.minecraft.mod.polycraft.util.SystemUtil;

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

	@SubscribeEvent
	public void onWorldTick(final TickEvent.WorldTickEvent event) {
		// TODO not sure why this is getting called multiple times with
		// different world java objects for the same world
		if ((event.phase == TickEvent.Phase.END)
				&& (event.world.provider.dimensionId == 0)) {
			onWorldTickPrivateProperties(event);
			onWorldTickWhitelist(event);
			onWorldTickFriends(event);
			onWorldTickInventories(event);
			onWorldTickGovernments(event);

		}
	}

	// @SubscribeEvent
	// public void onServerChatEvent(final ServerChatEvent event)
	// {
	// if (event.message.startsWith(chatCommandPrefix)) {
	// return;
	// }
	//
	// if (event.message.startsWith("//"))
	// return;
	//
	// for (int i = 0; i < 36; i++)
	// {
	// ItemStack itemStackSend = event.player.inventory.getStackInSlot(i);
	//
	// if (itemStackSend != null)
	// {
	// if (i < 9)
	// {
	// //test if receiving player has walky talky on the hotbar
	// if (itemStackSend != null &&
	// ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Walky Talky").getItemStack().getUnlocalizedName())))
	// broadcastFromSender(event, itemStackSend);
	//
	// //test if receiving player has cell phone on the hotbar
	// if (itemStackSend != null &&
	// ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Cell Phone").getItemStack().getUnlocalizedName())))
	// broadcastFromSender(event, itemStackSend);
	//
	// }
	//
	// //test if sending and receiving player have ham radios on same frequency
	// if (itemStackSend != null &&
	// ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("HAM Radio").getItemStack().getUnlocalizedName())))
	// broadcastFromSender(event, itemStackSend);
	//
	// //test if sending player holding phone broadcast the message
	// //send message to a specific user (tell command)
	// if (itemStackSend != null &&
	// ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Smart Phone").getItemStack().getUnlocalizedName())))
	// broadcastFromSender(event, itemStackSend);
	//
	// }
	//
	// }
	//
	// }

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
				+ itemStack.getDisplayName() + ":" + event.username + ":"
				+ event.message;

		sendDataPackets(DataPacketType.Broadcast, 1);

		// option #2: modified form
		// of...sendDataPackets(DataPacketType.Broadcast, 1) which includes
		// message;

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

	public void sendTempPPDataPackets() {
		sendDataPackets(DataPacketType.TempPrivatProperties, 0, null);
	}
	
	public void minigameStart() {
		sendDataPackets(DataPacketType.GenericMinigame, 0, null);
	}
	
	private void sendDataPackets(final DataPacketType type) {
		sendDataPackets(type, 0, null);
	}

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
							: type == DataPacketType.TempPrivatProperties ? gson.toJson(tempPrivateProperties)
							: type == DataPacketType.GenericMinigame ? gson.toJson(KillWall.INSTANCE)
									: gson.toJson(this.playerID)); 
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
	public void onEntityJoinWorld(final EntityJoinWorldEvent event) {
		//TODO: change to ClientConnectedToServerEvent instead of onEntityJoinWorld
		if (portalRestUrl != null && event.entity instanceof EntityPlayerMP) {
			final EntityPlayerMP player = (EntityPlayerMP) event.entity;
			player.addChatMessage(new ChatComponentText("Welcome to PolycraftWorld!"));
			player.addChatMessage(new ChatComponentText("Type \"/help\" for a list of commands"));
			sendDataPackets(DataPacketType.PrivateProperties, 1, player);
			sendDataPackets(DataPacketType.PrivateProperties, 0, player);
			sendDataPackets(DataPacketType.Friends);
			sendDataPackets(DataPacketType.Governments);
			this.playerID = this.whitelist.get(player.getDisplayName().toLowerCase()); //unexpected conflict with upper and lower case. may need to be looked at later.
			sendDataPackets(DataPacketType.playerID, 0, player);
			if (!portalRestUrl.startsWith("file:")) {
				try {
					NetUtil.post(String.format("%s/players/%s/", portalRestUrl,
							player.getDisplayName().toLowerCase()),
							ImmutableMap.of("last_world_seen", player.worldObj
									.getWorldInfo().getWorldName()));
				} catch (final IOException e) {
					PolycraftMod.logger.error(
							"Unable to log player last world seen", e);
				}
			}
		}
	}
}
