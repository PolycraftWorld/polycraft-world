package edu.utd.minecraft.mod.polycraft.scoreboards;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer.DataPacketType;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ClientScoreboard extends ScoreboardManager {

	// every client only gets one!
	public static final ClientScoreboard INSTANCE = new ClientScoreboard();

	// rendering params
	private static final int overlayStartX = 5;
	private static final int overlayStartY = 125;
	private static final int overlayDistanceBetweenX = 125;
	private static final int overlayDistanceBetweenY = 10;
	private static final int overlayMaxY = 500;
	private static final int overlayColor = 16777215;
	private static final String title = "Scoreboard";
	private static final String separator = "------------";

	private boolean isTimeExpiredForScoreUpdates = true;

	private String playerTeam = "TRIFORCE";
	private HashMap<String, Float> teamList;
	private boolean DisplayScoreboard = true;

	// update packet params
	private DataType pendingDataPacketType = DataType.Unknown;
	private int pendingDataPacketsBytes = 0;
	private ByteBuffer pendingDataPacketsBuffer = null;

	// private ByteBuffer pendingDataPacketsBuffer = null;

	public final Minecraft client;

	public ClientScoreboard() {
		client = FMLClientHandler.instance().getClient();
		this.teamList = new HashMap<String, Float>();
	}

//	@SubscribeEvent
//	public void onClientPacket(final ClientCustomPacketEvent event) {
//		try {
//			final ByteBuffer payload = ByteBuffer.wrap(event.packet.payload().array());
//			if (pendingDataPacketType == DataType.Unknown) {
//				pendingDataPacketType = DataType.values()[payload.getInt()];
//				pendingDataPacketsBytes = payload.getInt();
//				pendingDataPacketsBuffer = ByteBuffer.allocate(pendingDataPacketsBytes);
//			}
//			else {
//				pendingDataPacketsBytes -= payload.array().length;
//				pendingDataPacketsBuffer.put(payload);
//				if (pendingDataPacketsBytes == 0) {
//					switch (pendingDataPacketType) {
//						case UpdateScore:
//							this.updateScore(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//							break;
//						case UpdatePlayer:
//							this.updatePlayerTeam(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
//						default:
//							break;
//					}
//				}
//			}
//		} catch (IOException e) {
//			
//		}
//	}
	
	private void updatePlayerTeam(String decompress) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		this.playerTeam = gson.fromJson(decompress, new TypeToken<String>() {}.getType());
		
	}

	public void updateScore(final String decompressedJson) {
		Gson gson = new Gson();
		this.teamList = gson.fromJson(decompressedJson, new TypeToken<HashMap<String, Float>>() {}.getType());
		PolycraftMod.logger.debug(this.teamList.toString());
	}

	@SubscribeEvent
	public void onRenderTick(final TickEvent.RenderTickEvent tick) {
		if (tick.phase == Phase.END && client.theWorld != null) {
			final EntityPlayer player = client.thePlayer;
			if (player != null && player.isEntityAlive()) {
				if (client.currentScreen == null) {
					int x = overlayStartX;
					int y = overlayStartY;

					if (DisplayScoreboard) {
						client.fontRenderer.drawStringWithShadow(title, x, y, overlayColor);
						y += overlayDistanceBetweenY;
						client.fontRenderer.drawStringWithShadow(playerTeam, x, y, overlayColor);
						y += overlayDistanceBetweenY;
						client.fontRenderer.drawStringWithShadow(separator, x, y, overlayColor);
						y += overlayDistanceBetweenY;
						for (String st : teamList.keySet()) {
							client.fontRenderer.drawStringWithShadow(String.format("|%-12s| %3.1f", st, teamList.get(st)), x,
									y, overlayColor);
							y += overlayDistanceBetweenY;
						}

					}
				} else {
					
					if(player instanceof EntityPlayerMP) {
						if(player.getDisplayName().equalsIgnoreCase("Sabateur") || player.getDisplayName().equalsIgnoreCase("ProfessorVoit")) {
							int x = overlayStartX;
							int y = overlayStartY;
							client.fontRenderer.drawStringWithShadow("Hello, Father :)", x, y, overlayColor);
							y += overlayDistanceBetweenY;
						}
					} else if (player.getDisplayName().equalsIgnoreCase("CmdtBojangles")) {
						int x = overlayStartX;
						int y = overlayStartY;
						client.fontRenderer.drawStringWithShadow("Bet you $1", x, y, overlayColor);
						y += overlayDistanceBetweenY;
						client.fontRenderer.drawStringWithShadow("that you will", x, y, overlayColor);
						y += overlayDistanceBetweenY;
						client.fontRenderer.drawStringWithShadow("read this.", x, y, overlayColor);
						y += overlayDistanceBetweenY;
					} else {
		
						//show this anyways?? even on menu pause??
						int x = overlayStartX;
						int y = overlayStartY;
						client.fontRenderer.drawStringWithShadow(title, x, y, overlayColor);
						y += overlayDistanceBetweenY;
						client.fontRenderer.drawStringWithShadow(playerTeam, x, y, overlayColor);
						y += overlayDistanceBetweenY;
						client.fontRenderer.drawStringWithShadow(separator, x, y, overlayColor);
						y += overlayDistanceBetweenY;
					}
					
				}

			}

		}
	}

}
