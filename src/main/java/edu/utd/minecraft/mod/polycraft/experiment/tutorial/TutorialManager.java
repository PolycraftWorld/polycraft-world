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
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
		if (entity instanceof EntityPlayer && entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer) entity;
			for(ExperimentTutorial ex: experiments.values()){
				if(ex.isPlayerInExperiment(player.getDisplayName())){
					ex.render(entity);
				}
			}
		}
	}

}
