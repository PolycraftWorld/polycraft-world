package edu.utd.minecraft.mod.polycraft.scoreboards;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;

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

	// private ByteBuffer pendingDataPacketsBuffer = null;

	public final Minecraft client;

	public ClientScoreboard() {
		client = FMLClientHandler.instance().getClient();
		this.teamList = new HashMap<String, Float>();
	}

	@SubscribeEvent
	public void onClientPacket(final ClientCustomPacketEvent event) {
		try {
			final ByteBuffer payload = ByteBuffer.wrap(event.packet.payload().array());
			pendingDataPacketType = DataType.values()[payload.getInt()];
			switch (pendingDataPacketType) {
			case UpdatePlayer:
				byte[] b = new byte[payload.remaining()];

				playerTeam = CompressUtil.decompress(payload.get(b).array());
				System.out.println("incoming data: " + playerTeam); // TODO: comment this.
				DisplayScoreboard = true;
				break;
			case UpdateScore:
				float val = payload.getFloat();
				byte[] c = new byte[payload.remaining()];
				String currentTeamName = CompressUtil.decompress(payload.get(c).array());
				teamList.put(currentTeamName, val);
				DisplayScoreboard = true;
				break;
			default:
				DisplayScoreboard = false;
				break;
			}

		} catch (IOException e) {
			PolycraftMod.logger.error("Unable to decompress data packetes", e);
		}
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
							client.fontRenderer.drawStringWithShadow(String.format("%s\t\t%f", st, teamList.get(st)), x,
									y, overlayColor);
							y += overlayDistanceBetweenY;
						}

					}
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
