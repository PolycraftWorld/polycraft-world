package edu.utd.minecraft.mod.polycraft.scoreboards;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class ServerScoreboard extends ScoreboardManager {

	public static final ServerScoreboard INSTANCE = new ServerScoreboard();

	public ServerScoreboard() {
		this.managedScoreboards = new ArrayList<CustomScoreboard>();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Other applications can catch a handle on the scoreboard and update it!
	 * 
	 * @param teamNames
	 *            an arraylist of team names to add to the scoreboard
	 * @return the object reference of the scoreboard so it can be updated.
	 */
	public CustomScoreboard addNewScoreboard(ArrayList<String> teamNames) {
		CustomScoreboard scoreboard = new CustomScoreboard(teamNames);
		this.managedScoreboards.add(scoreboard);
		return scoreboard;
	}

	// private void sendDataPackets(final DataType type, final EntityPlayerMP
	// player, final String data) {
	// final FMLProxyPacket[] packets = getDataPackets(type, data);
	// if (packets != null) {
	// for (final FMLProxyPacket packet : packets) {
	// if (player == null) {
	// System.out.println("Potato.");//netChannel.sendToAll(packet); //TODO: Remove
	// this.
	// } else {
	// netChannel.sendTo(packet, player);
	// }
	// }
	// }
	// }

	private void sendDataPackets(final DataType type, CustomScoreboard board) {
		//FMLProxyPacket[] packets = null;
		Gson gson = new Gson();
		Type top = new TypeToken<HashMap<String, Float>>() {}.getType();
		System.out.println("I am able to get here, inside sendDataPackets");
		switch (type) {
		case UpdatePlayer:
			for (EntityPlayerMP player : board.getPlayers()) {
//					packets = new FMLProxyPacket[1];
//					String teamPlayerIsOn = board.getPlayerTeam(player).toString();
//					packets[0] = new FMLProxyPacket(Unpooled.buffer().writeInt(type.ordinal())
//							.writeBytes(CompressUtil.compress(teamPlayerIsOn)).copy(), netChannelName);
//					if (packets != null) {
//						for (final FMLProxyPacket pkt : packets) {
//							netChannel.sendTo(pkt, player);
//						}
//					}
				
			}
			break;
		case UpdateScore:
			HashMap<Team, Float> teamScores = board.getTeamScores();
			HashMap<String, Float> testing = new HashMap<String, Float>();
			for (Team tm : teamScores.keySet()) {
				testing.put(tm.toString(), teamScores.get(tm));
			}
			final String updateScoreJson = gson.toJson(testing, top);
		//	System.out.println("This is the value of the encoded String" + updateScoreJson);
		//	final FMLProxyPacket[] packets = getDataPackets(type, updateScoreJson);
			
//				final int payloadPacketsRequired = teamScores.size();
//
//				packets = new FMLProxyPacket[payloadPacketsRequired];
//
//				// send the string of data to the client.
//				int counter = 0;
//				for (Team tm : teamScores.keySet()) {
//					packets[counter] = new FMLProxyPacket(Unpooled.buffer().writeInt(type.ordinal())
//							.writeFloat(teamScores.get(tm)).writeBytes(CompressUtil.compress(tm.toString())).copy(),
//							netChannelName);
//					counter++;
//				}
			for (EntityPlayerMP player : board.getPlayers()) {
				if (updateScoreJson != null & player != null & player.isEntityAlive()) {
					ServerEnforcer.INSTANCE.sendScoreboardUpdatePackets(updateScoreJson, player);
//					for (final FMLProxyPacket pkt : packets) {
//						//System.out.println(pkt.payload().toString());
//						//ServerEnforcer.INSTANCE.sendScoreboardUpdatePackets(jsonStringToSend, player);
//						//netChannel.sendTo(pkt, player);
//					}
				}
			}
			break;
		default:
			break;
		}
	}

//	private FMLProxyPacket[] buildDataPackets(final DataType type, final HashMap<Team, Float> teamScores,
//			final String teamPlayerIsOn) {
//		final int payloadPacketsRequired = teamScores.size();
//		final int controlPacketsRequired = 1;
//
//		final FMLProxyPacket[] packets = new FMLProxyPacket[controlPacketsRequired + payloadPacketsRequired];
//
//		try {
//			// send the control packet containing meta-data
//			packets[0] = new FMLProxyPacket(Unpooled.buffer().writeInt(type.ordinal()).writeInt(payloadPacketsRequired)
//					.writeBytes(CompressUtil.compress(teamPlayerIsOn)).copy(), netChannelName);
//
//			// send the string of data to the client.
//			int counter = 0;
//			for (Team tm : teamScores.keySet()) {
//				packets[controlPacketsRequired + counter] = new FMLProxyPacket(Unpooled.buffer()
//						.writeFloat(teamScores.get(tm)).writeBytes(CompressUtil.compress(tm.toString())).copy(),
//						netChannelName);
//				counter++;
//			}
//			return packets;
//		} catch (IOException e) {
//			PolycraftMod.logger.error("Unable to compress scoreboard packets", e);
//			return null;
//		}
//	}

//	private void sendDataPackets(final DataType type, final ArrayList<EntityPlayerMP> playerList,
//			final ArrayList<Float> scoreSet) {
//		final FMLProxyPacket[] packets = getDataPackets(type, scoreSet);
//		if (packets != null) {
//			for (EntityPlayerMP player : playerList) {
//				for (final FMLProxyPacket packet : packets) {
//					if (player == null) {
//						System.out.println("Potato.");// netChannel.sendToAll(packet); //TODO: Remove this.
//					} else {
//						netChannel.sendTo(packet, player);
//					}
//				}
//			}
//		}
//	}

//	private FMLProxyPacket[] getDataPackets(final DataType type, final ArrayList<Float> scoreSet) {
//		final int payloadPacketsRequired = scoreSet.size();
//		final int controlPacketsRequired = 1;
//
//		final FMLProxyPacket[] packets = new FMLProxyPacket[controlPacketsRequired + payloadPacketsRequired];
//
//		// send the control packet containing meta-data
//		packets[0] = new FMLProxyPacket(
//				Unpooled.buffer().writeInt(type.ordinal()).writeInt(payloadPacketsRequired).copy(), netChannelName);
//
//		// send the string of data to the client.
//		for (int payloadIndex = 0; payloadIndex < payloadPacketsRequired; payloadIndex++) {
//			packets[controlPacketsRequired + payloadIndex] = new FMLProxyPacket(
//					Unpooled.buffer().writeFloat(scoreSet.get(payloadIndex)).copy(), netChannelName);
//		}
//		return packets;
//	}

	 private FMLProxyPacket[] getDataPackets(final DataType type, final String data) {
		 try {
			 //we have to split these up into smaller packets due to this issue:
			 //https://github.com/MinecraftForge/MinecraftForge/issues/1207#issuecomment-48870313
			 final byte[] dataBytes = CompressUtil.compress(data);
			 final int payloadPacketsRequired = getPacketsRequired(dataBytes.length);
			 final int controlPacketsRequired = 1;
			 final FMLProxyPacket[] packets = new FMLProxyPacket[controlPacketsRequired + payloadPacketsRequired];
			
			 //send the control packet containing meta-data
			 packets[0] = new FMLProxyPacket(Unpooled.buffer().writeInt(type.ordinal()).writeInt(dataBytes.length).copy(),netChannelName);
			
			 //send the string of data to the client.
			 for (int payloadIndex = 0; payloadIndex < payloadPacketsRequired; payloadIndex++) {
				 int startDataIndex = payloadIndex * maxPacketSizeBytes;
				 int length = Math.min(dataBytes.length - startDataIndex, maxPacketSizeBytes);
				 packets[controlPacketsRequired + payloadIndex] = new FMLProxyPacket(Unpooled.buffer().writeBytes(dataBytes, startDataIndex,length).copy(), netChannelName);
			 }
			 //TODO: Remove.
			 System.out.println("Size of Total packets: " + packets.length);
			 return packets;
			} catch (IOException e) {
				 PolycraftMod.logger.error("Unable to compress packet data", e);
				 return null;
			}
	 	}

	@SubscribeEvent
	public void onPlayerTick(final TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			for (CustomScoreboard scoreboard : this.managedScoreboards) {
				if (scoreboard.needToSendUpdate) {
					this.sendDataPackets(DataType.UpdateScore, scoreboard);
				}
				scoreboard.needToSendUpdate = false;
			}
		}
	}

	// TODO: Remove this! Testing only.
	@SubscribeEvent
	public void onEntityJoinWorld(final EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			final EntityPlayerMP player = (EntityPlayerMP) event.entity;
			ArrayList<String> demo = new ArrayList<String>();
			demo.add("Dhruv");
			demo.add("Eric");
			CustomScoreboard abc = addNewScoreboard(demo);
			try {
				abc.addPlayer(player, "Dhruv");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR.");
			}
			abc.updateScore("Dhruv", 100f);
			// sendDataPackets(DataType.PrivateProperties, 1);

		}
	}

}
