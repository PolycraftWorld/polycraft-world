/**
 * 
 */
package edu.utd.minecraft.mod.polycraft.scoreboards;

import net.minecraft.world.World;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;

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
		UpdateScore, UpdateNames, Unknown, UpdatePlayer
	}

	protected ArrayList<CustomScoreboard> managedScoreboards;

	protected static final int maxPacketSizeBytes = (int) Math.pow(2, 16) - 1;

	protected static final int getPacketsRequired(int bytes) {
		return (int) Math.ceil((double) bytes / (double) maxPacketSizeBytes);
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
		netChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(netChannelName);
		netChannel.register(this);
		// final GsonBuilder gsonBuilder = new GsonBuilder();
		// gsonGeneric = gsonBuilder.create();
	}

}
