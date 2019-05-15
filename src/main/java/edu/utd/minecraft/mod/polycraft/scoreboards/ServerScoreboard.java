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

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
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
	
	public CustomScoreboard addNewScoreboard() {
		CustomScoreboard scoreboard = new CustomScoreboard();
		this.managedScoreboards.add(scoreboard);
		return scoreboard;
	}

	
	public void sendGameOverUpdatePacket(final CustomScoreboard board, final String stringToSend) {
		for (EntityPlayer player : board.getPlayersAsEntity()) {
			if (player != null & player.isEntityAlive()) {
				ServerEnforcer.INSTANCE.sendScoreboardUpdatePackets(stringToSend, (EntityPlayerMP)player, DataType.GameOver.ordinal());//metadata is 0 for UpdateScore
			}
		}
	}
	
	

	private void sendDataPackets(final DataType type, CustomScoreboard board) {
		//FMLProxyPacket[] packets = null;
		Gson gson = new Gson();
		Type top = new TypeToken<HashMap<String, Float>>() {}.getType();
		Type playerTeamString = new TypeToken<Team>() {}.getType();
		Type teamMatesStringType = new TypeToken<ArrayList<String>>() {}.getType();
		//DO I need to do it this way?
		//System.out.println("I am able to get here, inside sendDataPackets");
		switch (type) {
		case UpdatePlayerTeam:
			for (EntityPlayer player : board.getPlayersAsEntity()) {
				Team teamPlayerIsOn = board.getPlayerTeam(player.getDisplayNameString());
				final String updateStringJson = gson.toJson(teamPlayerIsOn, playerTeamString);
				if(updateStringJson != null && player != null && player.isEntityAlive()) {
					ServerEnforcer.INSTANCE.sendScoreboardUpdatePackets(updateStringJson, (EntityPlayerMP)player, ScoreboardManager.DataType.UpdatePlayerTeam.ordinal()); //Send 1 for team update
				}
				
			}
			break;
		case UpdateScore:
			HashMap<Team, Float> teamScores = board.getTeamScores();
			HashMap<String, Float> testing = new HashMap<String, Float>();
			for (Team tm : teamScores.keySet()) {
				testing.put(tm.toString(), teamScores.get(tm));
			}
			final String updateScoreJson = gson.toJson(testing, top);

			for (EntityPlayer player : board.getPlayersAsEntity()) {
				if (updateScoreJson != null & player != null & player.isEntityAlive()) {
					ServerEnforcer.INSTANCE.sendScoreboardUpdatePackets(updateScoreJson, (EntityPlayerMP)player, ScoreboardManager.DataType.UpdateScore.ordinal());//metadata is 0 for UpdateScore
				}
			}
			break;
		case UpdateTeammates:
			for (EntityPlayer player : board.getPlayersAsEntity()) {
				Team teamPlayerIsOn = board.getPlayerTeam(player.getDisplayNameString());
				final String updateStringJson = gson.toJson(teamPlayerIsOn.getPlayers(), teamMatesStringType);
				if(updateStringJson != null && player != null && player.isEntityAlive()) {
					ServerEnforcer.INSTANCE.sendScoreboardUpdatePackets(updateStringJson, (EntityPlayerMP)player, ScoreboardManager.DataType.UpdateTeammates.ordinal()); //Send 1 for team update
				}
				
			}
			break;
			
		default:
			break;
		}
	}

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

	public void onServerTick(final TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			for (CustomScoreboard scoreboard : this.managedScoreboards) {
				if (scoreboard.needToSendUpdate) {
					this.sendDataPackets(DataType.UpdateScore, scoreboard);
					this.sendDataPackets(DataType.UpdatePlayerTeam, scoreboard); //TODO: distinguish and send appropriate packet.
					this.sendDataPackets(DataType.UpdateTeammates, scoreboard);
				}
				scoreboard.needToSendUpdate = false;
			}
		}
	}

//	// TODO: Remove this! Testing only.
//	@SubscribeEvent
//	public void onEntityJoinWorld(final EntityJoinWorldEvent event) {
//		if (event.entity instanceof EntityPlayerMP) {
//			final EntityPlayerMP player = (EntityPlayerMP) event.entity;
//		}
//	}

	public void clear() {
		// TODO Auto-generated method stub
		this.managedScoreboards.clear();
		
	}

}
