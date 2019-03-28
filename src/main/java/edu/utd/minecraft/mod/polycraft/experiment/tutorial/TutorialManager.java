package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager.ExperimentListMetaData;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TutorialManager {

	public static TutorialManager INSTANCE = new TutorialManager();
	private static int nextAvailableExperimentID = 1; 	//one indexed
	private static Hashtable<Integer, ExperimentTutorial> experiments = new Hashtable<Integer, ExperimentTutorial>();
	
	private List<EntityPlayer> globalPlayerList;
	
	public int clientCurrentExperiment = -1; //Variable held in the static instance for memory purposes. In the future, this may need to be moved somewhere else 
	
	
	public enum PacketMeta{
		Features,
		ActiveFeatures,
		Feature
	}
	
	public TutorialManager() {
		try {
			globalPlayerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			//MinecraftServer.getServer().getConfigurationManager().
		}catch (NullPointerException e) {
			//e.printStackTrace();//TODO: Remove this.
			globalPlayerList = null;
		}
		
	}
	
	
	public void onServerTickUpdate(final TickEvent.ServerTickEvent tick) {
		if(tick.phase == Phase.END) {
			for(ExperimentTutorial ex: experiments.values()){
				if(ex.currentState != ExperimentTutorial.State.Done) {
					ex.onServerTickUpdate();
				}
			}
		}
	}
	
	/**
	 * This is called from Common Proxy
	 * @param tick the input tick.
	 */
	public void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		if(tick.side == Side.CLIENT && tick.phase == Phase.END){ //I think these are always true?
			for(ExperimentTutorial ex: experiments.values()){
				if(ex.currentState != ExperimentTutorial.State.Done) {
					ex.onClientTickUpdate();
				}
			}
		}
	}
	
	//@SideOnly(Side.SERVER)
	public static void sendTutorialFeatures(int id) {
		Gson gson = new Gson();
		Type gsonType = new TypeToken<ArrayList<TutorialFeature>>(){}.getType();
		final String experimentUpdates = gson.toJson(getExperiment(id).features, gsonType);
		for(EntityPlayer player: getExperiment(id).scoreboard.getPlayersAsEntity()) {
			ServerEnforcer.INSTANCE.sendTutorialUpdatePackets(experimentUpdates,PacketMeta.Features.ordinal(), (EntityPlayerMP)player);
		}
		System.out.println("Sending Update...");
	}
	
	//@SideOnly(Side.SERVER)
	public static void sendTutorialActiveFeatures(int id) {
		Gson gson = new Gson();
		Type gsonType = new TypeToken<ArrayList<TutorialFeature>>(){}.getType();
		final String experimentUpdates = gson.toJson(getExperiment(id).activeFeatures, gsonType);
		for(EntityPlayer player: getExperiment(id).scoreboard.getPlayersAsEntity()) {
			ServerEnforcer.INSTANCE.sendTutorialUpdatePackets(experimentUpdates,PacketMeta.ActiveFeatures.ordinal(), (EntityPlayerMP)player);
		}
		System.out.println("Sending Update...");
	}
	
	
	public void sendFeatureUpdate(int id, int index, TutorialFeature feature, World world) {
		Gson gson = new Gson();
		Type gsonType = new TypeToken<NBTTagCompound>(){}.getType();
		NBTTagCompound tempNBT = feature.save();
		tempNBT.setInteger("index", index);
		String experimentUpdates;
		
		if(world.isRemote) {
			tempNBT.setString("player", Minecraft.getMinecraft().thePlayer.getDisplayName());
			experimentUpdates = gson.toJson(tempNBT, gsonType);
			ClientEnforcer.INSTANCE.sendTutorialUpdatePackets(experimentUpdates,PacketMeta.Feature.ordinal());
		}else {
			experimentUpdates = gson.toJson(tempNBT, gsonType);
			for(EntityPlayer player: getExperiment(id).scoreboard.getPlayersAsEntity()) {
				ServerEnforcer.INSTANCE.sendTutorialUpdatePackets(experimentUpdates,PacketMeta.Feature.ordinal(), (EntityPlayerMP)player);
			}
		}
		
		System.out.println("Sending Update...");
	}


	//@SideOnly(Side.CLIENT)
	public void resetClientExperimentManager() {
		//clientCurrentExperiment = -1;
	}
	
	public void addExperiment(ExperimentTutorial exp) {
		this.experiments.put(nextAvailableExperimentID++, exp);
	}

	
	public EntityPlayer getPlayerEntity(String playerName) {
		try {
			if(this.globalPlayerList.isEmpty()) {
				this.globalPlayerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			} else if(this.globalPlayerList == null) {
				System.out.println("List is null, help pls");
			}
			for (EntityPlayer player : this.globalPlayerList) {
				if(player.getDisplayName().equals(playerName)) {
					return player;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}

	
	public ExperimentTutorial.State getExperimentStatus(int id){
		return experiments.get(id).currentState;
	}
	
	public static ExperimentTutorial getExperiment(int id){
		return experiments.get(id);
	}
	
	@SideOnly(Side.CLIENT)
	public void updateExperimentFeatures(int experimentID, ArrayList<TutorialFeature> features) {
		//This should only be sent once
		experiments.put(1, new ExperimentTutorial(1, Minecraft.getMinecraft().theWorld, features));
	}
	
	public void updateExperimentFeature(int experimentID, NBTTagCompound featureNBT, boolean isRemote) {
		try {
			int index = featureNBT.getInteger("index");
			String playerName = featureNBT.getString("player");
			TutorialFeature test;
		
			test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(featureNBT.getString("type")).className).newInstance();
		
			test.load(featureNBT);
			if(isRemote) {	//client side
				experiments.get(1).features.set(index, test);
			}else {
				for(ExperimentTutorial exp : experiments.values()) {
					if(exp.isPlayerInExperiment(playerName))
						experiments.get(1).features.set(index, test);
				}
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.out.println("Cannot load Feature: " + e.getMessage());
		}
	}
	
	public void updateExperimentActiveFeatures(int experimentID, ArrayList<TutorialFeature> activeFeatures) {
		this.experiments.get(1).updateActiveFeatures(activeFeatures);
	}
	
	
	/**
	 * For getting experiment id of running experiment
	 * @return First currently running experiment or -1 if there are no running experiments
	 */
	public static int getRunningExperiment() {
		for(int expID: experiments.keySet()) {
			if(experiments.get(expID).currentState == ExperimentTutorial.State.Starting || experiments.get(expID).currentState == ExperimentTutorial.State.Running)
			{
				return expID;
			}
		}
		return -1;
	}

	
	public static int getNextID(){
		return nextAvailableExperimentID;
	}
	
	public static void registerExperimentTypes()
	{
		//experimentTypes.put(1, ExperimentCTB.class);
	}
	
	public static void render(Entity entity) {
//		if (entity instanceof EntityPlayer && entity.worldObj.isRemote) {
//			EntityPlayer player = (EntityPlayer) entity;
//			for(ExperimentTutorial ex: experiments.values()){
//				if(ex.isPlayerInExperiment(player.getDisplayName())){
//					ex.render(entity);
//				}
//			}
//		}
		if(!experiments.isEmpty())
			if(experiments.containsKey(1))
				experiments.get(1).render(entity);
	}

}
