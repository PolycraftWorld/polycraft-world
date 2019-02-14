package edu.utd.minecraft.mod.polycraft.scoreboards;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
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
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer.DataPacketType;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ClientScoreboard extends ScoreboardManager {

	// every client only gets one!
	public static final ClientScoreboard INSTANCE = new ClientScoreboard();

	// rendering params
	private static int overlayStartX = 175;
	private static int overlayStartY = 5;
	private static final int overlayDistanceBetweenX = 125;
	private static final int overlayDistanceBetweenY = 10;
	private static final int overlayMaxY = 500;
	private static final int overlayColor = 16777215;
	private static final String title = "Scoreboard";
	private static final String separator = "------------";

	private boolean isTimeExpiredForScoreUpdates = true;

	//scoreboard formatting
	private final String teamFormat = "|%1$-10s | %2$ 4d seconds";
	private final String scoreFormat = "|%-12.11s | %3.0f";
	//scoreboard params;
	private String playerTeam = "TRIFORCE";
	public Team currentTeam = null;
	public ArrayList<String> teammates;
	private HashMap<String, Float> teamList;
	private ScoreboardManager.ColoredString secondsRemaining;
	private boolean DisplayScoreboard = false;
	

	// update packet params
	private DataType pendingDataPacketType = DataType.Unknown;
	private int pendingDataPacketsBytes = 0;
	private ByteBuffer pendingDataPacketsBuffer = null;

	public final Minecraft client;

	private int hideDisplayTimer = -1;

	public ClientScoreboard() {
		client = FMLClientHandler.instance().getClient();
		this.teamList = new HashMap<String, Float>();
		this.teammates = new ArrayList<String>();
		overlayStartX = new Integer(client.displayWidth / 4);
	}
	
	public void updateTeam(final String decompressedJson) {
		Gson gson = new Gson();
		this.currentTeam = gson.fromJson(decompressedJson, new TypeToken<Team>() {}.getType());
		if(!this.DisplayScoreboard) {
			this.DisplayScoreboard = true;
			ClientEnforcer.INSTANCE.openExperimentsGui(); //open it for the first time.
		
		}
		//PolycraftMod.logger.debug(this.currentTeam.toString());
	}
	
	public void updateTime(final String decompressedJson) {
		Gson gson = new Gson();
		this.secondsRemaining = gson.fromJson(decompressedJson, new TypeToken<ScoreboardManager.ColoredString>() {}.getType()); 
	}
	
	public void updateTeamMates(final String decompressedJson) {
		Gson gson = new Gson();
		this.teammates.clear();
		this.teammates.addAll((ArrayList<String>) (gson.fromJson(decompressedJson, new TypeToken<ArrayList<String>>() {}.getType())));
	}

	public void updateScore(final String decompressedJson) {
		Gson gson = new Gson();
		this.teamList = gson.fromJson(decompressedJson, new TypeToken<HashMap<String, Float>>() {}.getType());
		PolycraftMod.logger.debug(this.teamList.toString());
	}
	
	private String getBinaryFromInt(int n) {
		//Integer.parseInt("1001", 2);
		String finalResult = "";
		while(n > 0)
	       {
	           int a = n % 2;
	           finalResult = a + finalResult;
	           n = n / 2;
	       }
		return finalResult;
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
						if(hideDisplayTimer > 0) {
							hideDisplayTimer--;
						}else if(hideDisplayTimer == 0) {
							DisplayScoreboard = false;
							hideDisplayTimer = -1;
						}
						
						client.fontRenderer.drawStringWithShadow(title, x, y, overlayColor);
						y += overlayDistanceBetweenY;//line break
						//Gui.drawCenteredString(client.fontRenderer, "test", client.displayWidth, 2, overlayColor);
						
						
						
						if(this.currentTeam != null) {
							try {
								int finalColor = Format.getIntegerFromColor(this.currentTeam.getColor());
								client.fontRenderer.drawString(this.currentTeam.toString(), x, y, finalColor);
								if(this.secondsRemaining != null) {
									int mins = this.secondsRemaining.time/60;
									int secs = this.secondsRemaining.time%60;
									String fmt = "%02d:%02d";
									client.fontRenderer.drawString(
										this.secondsRemaining.value + String.format(fmt, mins,secs), 
										x + client.fontRenderer.getStringWidth(this.currentTeam.toString()) + 3,
										y,
										Format.getIntegerFromColor(this.secondsRemaining.color));
								}
							}catch (Exception e){
								System.out.println("oops");
							}
						}else {
							client.fontRenderer.drawString(playerTeam, x, y, overlayColor);	
						}
						y += overlayDistanceBetweenY;//line break
						
						client.fontRenderer.drawStringWithShadow(separator, x, y, overlayColor);
						y += overlayDistanceBetweenY;//line break
						for (String st : teamList.keySet()) {
							client.fontRenderer.drawString(String.format(this.scoreFormat, st, teamList.get(st)), x,
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
//						int x = overlayStartX;
//						int y = overlayStartY;
//						client.fontRenderer.drawStringWithShadow(title, x, y, overlayColor);
//						y += overlayDistanceBetweenY;
//						client.fontRenderer.drawStringWithShadow(playerTeam, x, y, overlayColor);
//						y += overlayDistanceBetweenY;
//						client.fontRenderer.drawStringWithShadow(separator, x, y, overlayColor);
//						y += overlayDistanceBetweenY;
					}
					
				}

			}

		}
	}
	
	public Team getCurrentTeam() {
		return currentTeam;
	}
	
	public void clearDisplay() {
		this.teamList = new HashMap<String, Float>();
		this.teammates.clear();
		this.secondsRemaining = null;
		this.currentTeam = null;
		this.DisplayScoreboard = false;
	}

	public void gameOver(String decompress) {
		
		this.teamList = new HashMap<String, Float>();
		this.teammates.clear();
		this.secondsRemaining = null;
		this.currentTeam = new Team(decompress);
		this.currentTeam.setColor(Color.WHITE);
		//this.currentTeam.setColor(Color.web(decompress.split("\\s")[0])))));
		ExperimentManager.INSTANCE.clientCurrentExperiment = -1; //client is no longer in an experiment
		startHideDisplayTimer();
		
	}

	private void startHideDisplayTimer() {
		this.hideDisplayTimer  = 600;
		
	}

}
