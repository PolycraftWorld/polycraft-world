package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.entity.boss.AttackWarning;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentOld;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.IntegerMessage;
import edu.utd.minecraft.mod.polycraft.util.Analytics;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import edu.utd.minecraft.mod.polycraft.util.PlayerHalfTimeGUIEvent;
import edu.utd.minecraft.mod.polycraft.util.SystemUtil;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

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
	public boolean placeBlock;
	public int placeX;
	public int placeY;
	public int placeZ;
	public int placeBlockID;
	public int placeBlockMeta;
	public EntityPlayer placeblockPlayer;
	public ItemStack placeblockItemStack;
	
	
	
	@SubscribeEvent
	public void onWorldTick(final TickEvent.WorldTickEvent event) {
		// TODO not sure why this is getting called multiple times with
		// different world java objects for the same world
		if ((event.phase == TickEvent.Phase.END)
				&& (event.world.provider.getDimensionId() == 0 || event.world.provider.getDimensionId() == 8)) { //added properties to challenge dimension --matt
			//System.out.println("I have done this");
			onWorldTickPrivateProperties(event);
			onWorldTickWhitelist(event);
			onWorldTickFriends(event);
			onWorldTickInventories(event);
			//onWorldTickGovernments(event);
			onWorldTickCheckFrozenPlayers(event);
			if(placeBlock)
			{
				event.world.setBlockState(new BlockPos(this.placeX, this.placeY, this.placeZ), Block.getBlockById(this.placeBlockID).getStateFromMeta(this.placeBlockMeta), 2);
				Block.getBlockById(this.placeBlockID).onBlockPlacedBy(event.world, new BlockPos(this.placeX, this.placeY, this.placeZ), Block.getBlockById(this.placeBlockID).getStateFromMeta(this.placeBlockMeta), this.placeblockPlayer, this.placeblockItemStack);
				Block.getBlockById(this.placeBlockID).onBlockAdded(event.world, new BlockPos(this.placeX, this.placeY, this.placeZ), Block.getBlockById(this.placeBlockID).getStateFromMeta(this.placeBlockMeta));
				this.placeBlock=false;
			}
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
	public void onServerPacket(final FMLNetworkEvent.ServerCustomPacketEvent event) {
		// THIS CODE SHOULD NOT RUN IN 1.8.9
//		try {
//			final ByteBuffer payload = ByteBuffer.wrap(event.packet.payload().array());
//			if (pendingDataPacketType == DataPacketType.Unknown) {
//				pendingDataPacketType = DataPacketType.values()[payload.getInt()];
//				pendingDataPacketTypeMetadata = payload.getInt();
//				pendingDataPacketsBytes = payload.getInt();
//				pendingDataPacketsBuffer = ByteBuffer.allocate(pendingDataPacketsBytes);
//			}
//			else {
//				String playerDisplayName;
//				pendingDataPacketsBytes -= payload.array().length;
//				pendingDataPacketsBuffer.put(payload);
//				if (pendingDataPacketsBytes == 0 && !isByteArrayEmpty(pendingDataPacketsBuffer.array())) {
//					switch (pendingDataPacketType) {
//					case Experiment:
//						switch(ExperimentsPacketType.values()[pendingDataPacketTypeMetadata]) {
//							case BoundingBoxUpdate:
//								break;
//							case PlayerLeftDimension:
//								break;
//							case ReceiveExperimentsList:
//								break;
//							case RequestJoinExperiment:
//								onClientExperimentSelection(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//								break;
//							case SendParameterUpdates:
//								onClientUpdateExperimentParameters(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//								break;
//							case ExpDefGet:
//								playerDisplayName = gsonGeneric.fromJson(CompressUtil.decompress(pendingDataPacketsBuffer.array()),
//										new TypeToken<String>() {}.getType());
//								ExperimentManager.INSTANCE.sendExperimentDefs(playerDisplayName);
//								break;
//							case ExpDefUpdate:
//								updateExpDef(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//								break;
//							case ExpDefRemove:
//								removeExpDef(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//								break;
//							default:
//								break;
//							}
//						break;
//					case Consent:
//						playerDisplayName = gsonGeneric.fromJson(CompressUtil.decompress(pendingDataPacketsBuffer.array()),
//								new TypeToken<String>() {}.getType());
//						switch(pendingDataPacketTypeMetadata) {
//						case 0: //player gives consent
//							ServerEnforcer.INSTANCE.IRBTest(playerDisplayName.toLowerCase(), "set", true);
//							break;
//						case 1:
//							//Player withdraws consent
//							ServerEnforcer.INSTANCE.IRBTest(playerDisplayName.toLowerCase(), "set", false);
//							break;
//						default:
//							break;
//						}
//						break;
//					case Halftime: // decompress json array with halftime answers
//						final String[] halftimeAnswers = gsonGeneric.fromJson(CompressUtil.decompress(pendingDataPacketsBuffer.array()), String[].class);
//						//Experiment.inputAnswers(halftimeAnswers);
//						ExperimentOld.halftimeAnswers.inputAnswers(halftimeAnswers);
//						String[] half_time_Answers1 = Arrays.copyOfRange(halftimeAnswers, 1, halftimeAnswers.length);	//Removing player name from answers (the first element in array)
//						String half_time_Answers = String.join(",", half_time_Answers1);
//						PlayerHalfTimeGUIEvent event1 = new PlayerHalfTimeGUIEvent(halftimeAnswers[0],half_time_Answers);
//						Analytics.onHalfTimeGUIEvent(event1);
//						break;
//					case Tutorial:
//						switch(TutorialManager.PacketMeta.values()[pendingDataPacketTypeMetadata]) {
//							case Features:	//Experiment Features update
//								PolycraftMod.logger.debug("Why is client sending Features List?");
//								break;
//							case ActiveFeatures:	//Experiment Active Features update
//								PolycraftMod.logger.debug("Receiving client Active Features update request");
//								playerDisplayName = gsonGeneric.fromJson(CompressUtil.decompress(pendingDataPacketsBuffer.array()),
//										new TypeToken<String>() {}.getType());
//								int expID = TutorialManager.isPlayerinExperiment(playerDisplayName.toLowerCase());
//								if(expID > -1)
//									TutorialManager.INSTANCE.sendTutorialActiveFeatures(expID);
//								break;
//							case Feature:	//Experiment single featuer update
//								PolycraftMod.logger.debug("Receiving experiment feature...");
//								this.updateTutorialFeature(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//								break;
//							case JoinNew:	//Client requesting to join new tutorial
//								PolycraftMod.logger.debug("Receiving experiment feature...");
//								playerDisplayName = gsonGeneric.fromJson(CompressUtil.decompress(pendingDataPacketsBuffer.array()),
//										new TypeToken<String>() {}.getType());
//								TutorialManager.INSTANCE.addPlayerToExperiment(TutorialManager.INSTANCE.createExperiment(),
//										MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(playerDisplayName));	// func_152612_a: get EntityPlayerMP by username
//								break;
//							default:
//								break;
//						}
//						break;
//					case PlaceBlock:
//						this.updatePlaceBlock(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//						break;
//					case AIAPI:
//						this.AIAPICollect(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//						break;
//					default:
//						break;
//					}
//				
//				//Flush the buffer. and reset for the next message coming from the client.
//				pendingDataPacketType = DataPacketType.Unknown;
//				pendingDataPacketTypeMetadata = 0; 
//				pendingDataPacketsBuffer = null;
//					
//				}
//				
//			}
//			
//			
//			
//		}catch (Exception e) {
//			PolycraftMod.logger.error("Unable to decompress data packetes", e);
//			//Flush the buffer. and reset for the next message coming from the client.
//			pendingDataPacketType = DataPacketType.Unknown;
//			pendingDataPacketTypeMetadata = 0; 
//			pendingDataPacketsBuffer = null;
//		}
	}
	
	
	private void updateTutorialFeature(String decompressedJson) {
		Gson gson = new Gson();
		TutorialManager.INSTANCE.updateExperimentFeature(TutorialManager.INSTANCE.clientCurrentExperiment, 
				(ByteArrayOutputStream) gson.fromJson(decompressedJson, new TypeToken<ByteArrayOutputStream>() {}.getType()), false);
	}
	
	private void updateExpDef(String decompressedJson) {
		Gson gson = new Gson();
		ExperimentManager.INSTANCE.setExperimentDef(
				(ByteArrayOutputStream) gson.fromJson(decompressedJson, new TypeToken<ByteArrayOutputStream>() {}.getType()), false);
	}
	
	private void removeExpDef(String decompressedJson) {
		Gson gson = new Gson();
		ExperimentManager.INSTANCE.setExperimentDef(
				(ByteArrayOutputStream) gson.fromJson(decompressedJson, new TypeToken<ByteArrayOutputStream>() {}.getType()), true);
	}


	private void updatePlaceBlock(String decompressedJson) {
		Gson gson = new Gson();
		ServerEnforcer.INSTANCE.placeBlock((ByteArrayOutputStream) gson.fromJson(decompressedJson, new TypeToken<ByteArrayOutputStream>() {}.getType()), false);
	}
	
	public void placeBlock(ByteArrayOutputStream featuresStream, boolean isRemote) {
			
		NBTTagCompound NBT = new NBTTagCompound();
		try {
			NBT = CompressedStreamTools.readCompressed(new ByteArrayInputStream(featuresStream.toByteArray()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		this.placeX=NBT.getInteger("x");
		this.placeY=NBT.getInteger("y");
		this.placeZ=NBT.getInteger("z");
		this.placeBlockID=NBT.getInteger("blockid");
		this.placeBlockMeta=NBT.getInteger("meta");
		this.placeblockItemStack=ItemStack.loadItemStackFromNBT((NBTTagCompound) NBT.getTag("itemstack"));
		this.placeblockPlayer=MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(NBT.getString("player"));
		this.placeBlock=true;
		
	}
	
	private void AIAPICollect(String decompressedJson) {
		Gson gson = new Gson();
		ByteArrayOutputStream featuresStream = (ByteArrayOutputStream) gson.fromJson(decompressedJson, new TypeToken<ByteArrayOutputStream>() {}.getType());
		
		NBTTagCompound NBT = new NBTTagCompound();
		try {
			NBT = CompressedStreamTools.readCompressed(new ByteArrayInputStream(featuresStream.toByteArray()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		BlockPos invPos = new BlockPos(NBT.getInteger("x"), NBT.getInteger("y"), NBT.getInteger("z"));
		EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(NBT.getString("player"));
		for(int x = 0; x < 1; x++) {
			player.inventory.addItemStackToInventory(((TreeTapInventory)player.worldObj.getTileEntity(invPos)).removeStackFromSlot(x));
        }
	}
		
	
	/**
	 * Client sends updated parameters inside an ExperimentParticipantMetaData object that contains Client ID and experiment ID.
	 * @param decompress the ExperimentParticipantMetaData object with new client parameters
	 */
	private void onClientUpdateExperimentParameters(String decompress) {
		Gson gson = new Gson();
		ExperimentManager.ExperimentParticipantMetaData part = gson.fromJson(decompress, new TypeToken<ExperimentManager.ExperimentParticipantMetaData>() {}.getType());
		
		//checks if the player is opped. If so, then it updates Experiment parameters. Otherwise, nothing happens.
		if(MinecraftServer.getServer().getConfigurationManager().getOppedPlayers().getGameProfileFromName(part.playerName) != null)
			ExperimentManager.INSTANCE.updateExperimentParameters(part.experimentID, part.params);
		else {
			//send a chat message for now:
			EntityPlayerMP playerEntity = (EntityPlayerMP) ExperimentManager.INSTANCE.getPlayerEntity(part.playerName);
			playerEntity.addChatMessage(new ChatComponentText("You are not authorized to adjust parameters. \u00A7cThis incident \u00A7cwill be reported."));
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
			this.shouldClientDisplayConsentGUI((EntityPlayerMP)event.player);
			this.UpdateClientTutorialCompleted((EntityPlayerMP)event.player);
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
		ExperimentManager.INSTANCE.checkAndRemovePlayerFromExperimentLists(event.player.getDisplayNameString());
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
				
				NetUtil util = new NetUtil();
				NetUtil.ThreadedNetUtil masterThread = util.getNewThreadUtil();
				masterThread.getPrivateProperties(url, true);
				
				
				
//				updatePrivateProperties(NetUtil.getText(url), true, true);
//				sendDataPackets(DataPacketType.PrivateProperties, 1);

				url = portalRestUrl.startsWith("file:") ? portalRestUrl
						+ "privatepropertiesexclude.json"
				// TODO eventually send a timestamp of the last successful pull,
				// so the server can return no-change (which is probably most of
				// the time)
						: String.format(
								"%s/private_properties/worlds/exclude/%s/",
								portalRestUrl, event.world.getWorldInfo()
										.getWorldName());
				
				NetUtil.ThreadedNetUtil nonMasterThread = util.getNewThreadUtil();
				nonMasterThread.getPrivateProperties(url, false);
				
//				updatePrivateProperties(NetUtil.getText(url), false, true);
//				sendDataPackets(DataPacketType.PrivateProperties, 0);
			} catch (final Exception e) {
				// TODO set up a log4j mapping to send emails on error messages
				// (via mandrill)
				if (privatePropertiesMasterJson == null
						|| privatePropertiesNonMasterJson == null) {
					PolycraftMod.logger.error(
							"Unable to load private properties", e);
					FMLCommonHandler.instance().exitJava(-1, false);
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
	
	public void sendExpPPDataPackets() {
		sendDataPackets(DataPacketType.ExpPrivateProperties, 0, null);
	}
	
	public void sendTempCPDataPackets(EntityPlayerMP player) {
		sendDataPackets(DataPacketType.Experiment, 2, player);
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
	
	public void sendCannonInputs()
	{
		sendDataPackets(DataPacketType.Cannon, 0, null);
	}
	
	public void sendRespawnSync(EntityPlayerMP player) {
		sendDataPackets(DataPacketType.RespawnSync, 0, player);
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
			packetList = getDataPackets(DataPacketType.Experiment, ExperimentsPacketType.ReceiveExperimentsList.ordinal(), jsonStringToSend);
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
			packetList = getDataPackets(DataPacketType.Experiment, ExperimentsPacketType.PlayerLeftDimension.ordinal(), "PlayerLeavingDimension");
			System.out.println("Player is Leaving Dimension");
			
		} 
		else if(jsonStringToSend.compareTo("OpenHaltimeGUI") == 0) { //case:  open halftime gui
			packetList = getDataPackets(DataPacketType.Experiment, ExperimentsPacketType.OpenHalftimeGUI.ordinal(), "OpenHalftimeGUI");
		}		
		else if(jsonStringToSend.compareTo("CloseHaltimeGUI") == 0) { //case:  close halftime gui
			packetList = getDataPackets(DataPacketType.Experiment, ExperimentsPacketType.CloseHalftimeGUI.ordinal(), "CloseHalftimeGUI");
		}		
		else { //case: Bounding Box updates for client rendering
			packetList = getDataPackets(DataPacketType.Experiment, ExperimentsPacketType.BoundingBoxUpdate.ordinal(), jsonStringToSend);
		}		
		if(packetList != null) {
			for (final FMLProxyPacket packet : packetList) {
				netChannel.sendTo(packet, player);
			}				
		}
	}
	
	
	/**
	 * Send Tutorial updates to players in the game
	 * @param jsonStringToSend if Null, then it is case 2 else, it's case 3
	 * @param player if Null, then it is case 1 else, it's either case 2 or 3.
	 */
	public void sendTutorialUpdatePackets(final String jsonStringToSend, int meta, EntityPlayerMP player) {
		//TODO: add meta-data parsing.
		FMLProxyPacket[] packets = null;
		packets = getDataPackets(DataPacketType.Tutorial, meta, jsonStringToSend);
		
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
	
	/**
	 * Send Experiment Definitions to players in the game
	 * @param jsonStringToSend 
	 * @param player 
	 */
	public void sendExpDefUpdatePackets(final String jsonStringToSend, EntityPlayerMP player) {
		//TODO: add meta-data parsing.
		FMLProxyPacket[] packets = null;
		packets = getDataPackets(DataPacketType.Experiment, ExperimentsPacketType.ExpDefGet.ordinal(), jsonStringToSend);
		
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
	
	/**
	 * Send an updated list of experiments to all players in dimension 0
	 * @param jsonStringToSend the Gson arraylist of ExperimentListMetaData objects. 
	 */
	@Deprecated //TODO: delete this.
	public void sendExperimentListUpdates(final String jsonStringToSend) {
		FMLProxyPacket[] packetList = null;
		packetList = getDataPackets(DataPacketType.Experiment, ExperimentsPacketType.ReceiveExperimentsList.ordinal(), jsonStringToSend);
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
	

	public void shouldClientDisplayConsentGUI(EntityPlayerMP player) {
		if (portalRestUrl != null) {
			try {
				String result = ServerEnforcer.INSTANCE.IRBTest(player.getCommandSenderEntity().getName().toLowerCase(), "get");
				if(result.equals("\"valid\"")) {
					sendDataPackets(DataPacketType.Consent, 1, player);	//sending 1 will not show consent form
				}else {
					sendDataPackets(DataPacketType.Consent, 0, player);	//sending 0 will show consent form
				}
			}
			catch(Exception e){
				//TODO: something?
			}
		}
	}
	
	public void UpdateClientTutorialCompleted(EntityPlayerMP player) {
		if (portalRestUrl != null) {
			try {
				String result = ServerEnforcer.INSTANCE.skillLevelCheck(player.getCommandSenderEntity().getName());
				if(result.equals("Error")) {
					sendDataPackets(DataPacketType.Tutorial, TutorialManager.PacketMeta.CompletedTutorialFalse.ordinal(), player);	//Update clients tutorial completion to False
				}else {
					sendDataPackets(DataPacketType.Tutorial, TutorialManager.PacketMeta.CompletedTutorialTrue.ordinal(), player);	//Update clients tutorial completion to True
				}
			}
			catch(Exception e){
				sendDataPackets(DataPacketType.Tutorial, TutorialManager.PacketMeta.CompletedTutorialFalse.ordinal(), player);	//Update clients tutorial completion to False
			}
		}
	}

	public void freezePlayerForTicks(int ticks, EntityPlayerMP player) {
		//Freeze player for specific number of ticks
		if(frozenPlayers.containsKey(player.getDisplayName())) {
			frozenPlayers.replace(player.getDisplayNameString(), ticks);
		}else {
			frozenPlayers.put(player.getDisplayNameString(), ticks);
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
							: type == DataPacketType.ExpPrivateProperties ? gson.toJson(expPrivateProperties)
							: type == DataPacketType.GenericMinigame ? gson.toJson(PolycraftMinigameManager.INSTANCE)//get through manager
//							: type == DataPacketType.RaceMinigame ? gson.toJson(RaceGame.INSTANCE)
							: type == DataPacketType.AttackWarning ? gson.toJson(AttackWarning.toSend)
							//: type == DataPacketType.Cannon ? gson.toJson(CannonBlock.INSTANCE)
							: type == DataPacketType.playerID ? gson.toJson(this.playerID)
									: gson.toJson(""));	//default packet should be blank 
			//System.out.println("type: " + DataPacketType.);

			final int payloadPacketsRequired = getPacketsRequired(dataBytes.length);
			final int controlPacketsRequired = 1;
			final FMLProxyPacket[] packets = new FMLProxyPacket[controlPacketsRequired
					+ payloadPacketsRequired];
			packets[0] = new FMLProxyPacket(new PacketBuffer(Unpooled.buffer()
					.writeInt(type.ordinal()).writeInt(typeMetadata)
					.writeInt(dataBytes.length).copy()), netChannelName);
			for (int payloadIndex = 0; payloadIndex < payloadPacketsRequired; payloadIndex++) {
				int startDataIndex = payloadIndex * maxPacketSizeBytes;
				int length = Math.min(dataBytes.length - startDataIndex,
						maxPacketSizeBytes);
				packets[controlPacketsRequired + payloadIndex] = new FMLProxyPacket(
						new PacketBuffer(Unpooled.buffer()
								.writeBytes(dataBytes, startDataIndex, length)
								.copy()), netChannelName);
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
										.addWhitelistedPlayer(gameprofile);
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
							.getConfigurationManager().getWhitelistedPlayers()
							.getBannedProfile(usernameToRemove);
					if (gameprofile != null)
						minecraftserver.getConfigurationManager()
								.removePlayerFromWhitelist(gameprofile);
					result += usernameToRemove + ",";
					// TODO don't worry about kicking them right now
				}
			} catch (final Exception e) {
				// TODO set up a log4j mapping to send emails on error messages
				// (via mandrill)
				if (whitelistJson == null) {
					PolycraftMod.logger.error("Unable to load whitelist", e);
					FMLCommonHandler.instance().exitJava(-1, false);
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
										.addWhitelistedPlayer(gameprofile);
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
							.getConfigurationManager().getWhitelistedPlayers()
							.getBannedProfile(usernameToRemove);
					if (gameprofile != null)
						minecraftserver.getConfigurationManager()
								.removePlayerFromWhitelist(gameprofile);
					// TODO don't worry about kicking them right now
				}
			} catch (final Exception e) {
				// TODO set up a log4j mapping to send emails on error messages
				// (via mandrill)
				if (whitelistJson == null) {
					PolycraftMod.logger.error("Unable to load whitelist", e);
					FMLCommonHandler.instance().exitJava(-1, false);
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
						.addWhitelistedPlayer(gameprofile);
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
					FMLCommonHandler.instance().exitJava(-1, false);
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
			System.out.println(event.entity.getCommandSenderEntity().getName());
			ExperimentManager.INSTANCE.onEntityJoinWorldEventSendUpdates((EntityPlayer)event.entity); //send newly joined players an update
			if(event.entity.dimension == 8) {
				
			}
			final EntityPlayerMP player = (EntityPlayerMP) event.entity;
			PolycraftMod.SChannel.sendTo(new IntegerMessage(1337), player);
		}
		if (portalRestUrl != null && event.entity instanceof EntityPlayerMP) {
			final EntityPlayerMP player = (EntityPlayerMP) event.entity;
			PolycraftMod.SChannel.sendTo(new IntegerMessage(1337), player);
			player.addChatMessage(new ChatComponentText("Welcome to PolycraftWorld!"));
			player.addChatMessage(new ChatComponentText("Type \"/help\" for a list of commands"));
			player.addChatMessage(new ChatComponentText("By playing on our servers, you accept our TOS and privacy policy available on polycraftworld.com"));
			sendDataPackets(DataPacketType.PrivateProperties, 1, player);
			sendDataPackets(DataPacketType.PrivateProperties, 0, player);
			sendDataPackets(DataPacketType.Friends);
			 //send updated experiments available to everyone
			//sendDataPackets(DataPacketType.Governments);
			if(this.whitelist.containsKey(player.getDisplayNameString().toLowerCase())) {
				this.playerID = this.whitelist.get(player.getDisplayNameString().toLowerCase()); //unexpected conflict with upper and lower case. may need to be looked at later.
				sendDataPackets(DataPacketType.playerID, 0, player);
			}else {
				if (!portalRestUrl.startsWith("file:")) {
					try {
						String response = NetUtil.post(String.format("%s/create_player/", portalRestUrl),
								ImmutableMap.of("mincraft_user_name", player.getDisplayNameString().toLowerCase()));
						
						final GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.registerTypeAdapter(PlayerHelper.class,
								new PlayerHelper.Deserializer());

						final Gson gson = gsonBuilder.create();
						final PlayerHelper newPlayer = gson.fromJson(
								response,
								new TypeToken<PlayerHelper>() {
								}.getType());
						addUserToWhitelist(newPlayer.id, newPlayer.minecraft_user_name.toLowerCase(), newPlayer.uuid);
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
	
	public String IRBTest(String minecraftUserName, String option) {
		return IRBTest(minecraftUserName, option, false);
	}
	
	public String IRBTest(String minecraftUserName, String option, boolean flag) {
		try {
			Map<String, String> params = Maps.newHashMap();
			params.put("minecraft_user_name", minecraftUserName);
			params.put("option", option);
			if(flag) 
				params.put("flag", "True");
			else
				params.put("flag", "False");
			String response = NetUtil.post(String.format("%s/irb/", portalRestUrl),
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
	
	public String skillLevelCheck(String minecraftUserName) {
		try {
			String response = NetUtil.post(String.format("%s/skill_level_get/%s/", ServerEnforcer.portalRestUrl, minecraftUserName),null);
			JsonParser parser = new JsonParser();
			JsonObject jsonObj = (JsonObject) parser.parse(response);
			PolycraftMod.logger.debug("Skill Level Check response: " + response);
			if(!jsonObj.get("skill_level").getAsString().matches("-?\\d+")) {
				response = "Error";
			}
			return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error";
		}
	}
	
	public int skillLevelGet(String minecraftUserName) {
		try {
			String response = NetUtil.post(String.format("%s/skill_level_get/%s/", ServerEnforcer.portalRestUrl, minecraftUserName),null);
			JsonParser parser = new JsonParser();
			JsonObject jsonObj = (JsonObject) parser.parse(response);
			PolycraftMod.logger.debug("Skill Level Check response: " + response);
			if(!jsonObj.get("skill_level").getAsString().matches("-?\\d+")) {
				return -1;	//error getting skill level
			}else {
				return jsonObj.get("skill_level").getAsInt();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	
	public String updateSkillLevel(String minecraftUserName, int skillLevel) {
		try {
			Map<String, String> params = Maps.newHashMap();
			params.put("minecraft_user_name", minecraftUserName);
			params.put("skill_level", Integer.toString(skillLevel));
			String response = NetUtil.post(String.format("%s/skill_level_set/%s/", ServerEnforcer.portalRestUrl, minecraftUserName),params);
			PolycraftMod.logger.debug("Skill Level Check response: " + response);
			if(!response.matches("-?\\d+")) {
				response = "Error";
			}
			return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error";
		}
	}

	public void sendPrivateProperties(String privatePropertyJSON, boolean isMasterWorld) {
		// TODO Auto-generated method stub
		this.updatePrivateProperties(privatePropertyJSON, isMasterWorld, true);
		sendDataPackets(DataPacketType.PrivateProperties, isMasterWorld ? 1 : 0);
	}
}
