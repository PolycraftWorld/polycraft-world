/**
 * 
 */
package edu.utd.minecraft.mod.polycraft.scoreboards;

import net.minecraft.world.World;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.client.*;
import net.minecraft.command.server.*;
import net.minecraft.scoreboard.*;

/**
 * @author Dhruv Narayanan
 *
 */
public abstract class ScoreboardManager {

	public enum DataType {
		UpdateScore, UpdateNames, Unknown, UpdatePlayerTeam, GameOver, UpdateTeammates, UpdateTime
	}

	protected ArrayList<CustomScoreboard> managedScoreboards;
	//protected static Map<String, Float> onScreenValues = Maps.newHashMap();

	protected static final int maxPacketSizeBytes = 10;//(int) Math.pow(2, 16) - 1; //TODO: update this?

	protected static final int getPacketsRequired(int bytes) {
		int value =  (int) Math.ceil((double) bytes / (double) maxPacketSizeBytes);
		System.out.println(String.format("Value: %d Bytes: %d", value, bytes));
		return value;
	}

	protected final FMLEventChannel netChannel;
	protected final String netChannelName = "polycraft.scoreboardmanager";

	public static ScoreboardManager getInstance(final World world) {
		if (world.isRemote) {
			return ClientScoreboard.INSTANCE;
		}
		return ServerScoreboard.INSTANCE;
	}

	public ScoreboardManager() {
		netChannel = null;
		//netChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(netChannelName);
		//netChannel.register(this);
		final GsonBuilder gsonBuilder = new GsonBuilder();
		// gsonGeneric = gsonBuilder.create();
	}
	
	public static class ColoredString {
		public String value;
		public Color color;
		public int time;
		
		public ColoredString(String val, Color col) {
			value = val;
			color = col;
		}
		public ColoredString(String val, Color col, int num) {
			value = val;
			color = col;
			time = num;
		}
	}

}
