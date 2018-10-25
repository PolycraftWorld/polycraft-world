package edu.utd.minecraft.mod.polycraft.experiment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ExperimentManager {

	public static ExperimentManager INSTANCE = new ExperimentManager();
	private static int nextAvailableExperimentID = 1; 	//one indexed
	private static Hashtable<Integer, Experiment> experiments = new Hashtable<Integer, Experiment>();
	private static Hashtable<Integer, Class<? extends Experiment>> experimentTypes = new Hashtable<Integer, Class <? extends Experiment>>();
	private static List<EntityPlayer> globalPlayerList;
	public static ArrayList<ExperimentListMetaData> metadata = new ArrayList<ExperimentListMetaData>(); 
	
	/**
	 * Internal class that keeps track of all experiments
	 * A static arraylist of this class, called #metadata is transferred between Server & Client
	 * for synchronization. This contains relevant, condensed information enabling efficient info
	 * transfer across the network. 
	 * The information is fired from {@link #ExperimentManager.sendExperimentUpdates} and received by
	 * {@link ClientEnforcer}. It is currently rendered on {@link GuiExperimentList}
	 * @author dnarayanan
	 *
	 */
	public class ExperimentListMetaData {	
		public String expName;
		public int playersNeeded;
		public int currentPlayers;
		private boolean available = true;
		
		public ExperimentListMetaData(String name, int maxPlayers, int currPlayers) {
			expName = name;
			playersNeeded = maxPlayers;
			currentPlayers = currPlayers;
		}
		
		public void updateCurrentPlayers(int newPlayerCount) {
			currentPlayers = newPlayerCount;
		}
		
		public void deactivate() {
			available = false;
		}
		
		@Override
		public String toString() {
			return String.format("Name: %s\tPlayers Needed: %d\t Current Players: %d", expName, playersNeeded, currentPlayers);
		}	
	}
	
	/**
	 * This class contains a request from the Client to the Server of a player wanting to join
	 * or of a player wanting to be removed from a particular experiment list.
	 * this is transmitted through the network (fired currently from {@link GuiExperimentList} and received
	 * by {@link ServerEnforcer}).
	 * @author dnarayanan
	 *
	 */
	public class ExperimentParticipantMetaData {
		public String playerName;
		public int experimentID;
		public boolean wantsToJoin;
		public ExperimentParticipantMetaData(String playerName, int expID, boolean join) {
			this.playerName = playerName;
			this.experimentID = expID;
			wantsToJoin = join;
		}
	}
	
	public ExperimentManager() {
		try {
			globalPlayerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		}catch (NullPointerException e) {
			globalPlayerList = null;
		}
	}
	//initialize new experiments before players join
	public static void init(){					
		World world = DimensionManager.getWorld(8);
		for(Experiment ex: experiments.values()){
			ex.init();
		}
	}
	
	public void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		if(tick.side == Side.SERVER){
			for(Experiment ex: experiments.values()){
				ex.onServerTickUpdate();
//				if(ex.currentState == State.Done){
//					stop(ex.id);
//				}
			}
		}else{
			for(Experiment ex: experiments.values()){
				ex.onClientTickUpdate();
			}
		}
	}
	
	public boolean addPlayerToExperiment(int expID, EntityPlayerMP player){
		boolean value = experiments.get(expID).addPlayer(player);
		ExperimentManager.metadata.get(expID - 1).updateCurrentPlayers(experiments.get(expID).getMaxPlayers() - experiments.get(expID).getNumPlayersAwaiting());		
		sendExperimentUpdates();
		return value;
	}
	
	public boolean removePlayerFromExperiment(int expID, EntityPlayerMP player){
		boolean value = experiments.get(expID).removePlayer(player);
		ExperimentManager.metadata.get(expID - 1).updateCurrentPlayers(experiments.get(expID).getMaxPlayers() - experiments.get(expID).getNumPlayersAwaiting());		
		sendExperimentUpdates();
		return value;
	}
	
	@SideOnly(Side.SERVER)
	private static void sendExperimentUpdates() {
		Gson gson = new Gson();
		Type gsonType = new TypeToken<ArrayList<ExperimentListMetaData>>(){}.getType();
		final String experimentUpdates = gson.toJson(ExperimentManager.metadata, gsonType);
		ServerEnforcer.INSTANCE.sendExperimentListUpdates(experimentUpdates);
		System.out.println("Sending Update...");
		//return updateScoreJson;
	}
	
	@SideOnly(Side.CLIENT)
	public static void updateExperimentMetadata(String experimentMetaDataJson) {
		Gson gson = new Gson();
		ExperimentManager.metadata = gson.fromJson(experimentMetaDataJson, new TypeToken<ArrayList<ExperimentListMetaData>>() {}.getType());
		PolycraftMod.logger.debug(metadata.toString());
		System.out.println("Receiving new Data...");
		System.out.println(metadata.toString());
	}
	
	public void start(int expID){
		experiments.get(expID).start();
		metadata.get(expID-1).deactivate(); //prevents this experiment from showing up on the list.
	}
	
	public EntityPlayer getPlayerEntity(String playerName) {
		for (EntityPlayer player : this.globalPlayerList) {
			if(player.getDisplayName().equals(playerName)) return player;
		}
		
		return null;
	}
	
	public void stop(int id){
		Experiment ex = experiments.get(id);
		for(Team team: ex.scoreboard.getTeams()) {
			for(String player: team.getPlayers()){
				EntityPlayerMP playerEntity = (EntityPlayerMP) this.getPlayerEntity(player);
				playerEntity.addChatMessage(new ChatComponentText("Experiment Complete. Teleporting to UTD..."));
				playerEntity.mcServer.getConfigurationManager().transferPlayerToDimension(playerEntity, 0,	new PolycraftTeleporter(playerEntity.mcServer.worldServerForDimension(0)));
			}
		}
		//TODO: clear the scoreboard.
		ex.stop();
		
		
		//experiments.remove(id);
		//TODO: fix this:
		reset(); //TODO: remove the above experiment.
	}
	
	public void reset(){
		ServerScoreboard.INSTANCE.clear();
		 //reset this to 1 for the polyBlockPortal to work again. 
		//TODO: allow the polyblockportal to create new experiments in the future.
	}
	//Use to sync with minigame clients
	public void timer(){
		
	}
	
	//I need things.
	public int[] playerAttemptToConnect(EntityPlayerMP player, int experimentID) {
		//check if experimentId is valid
		if(experiments.containsKey(experimentID)){
			//check if experimentId can take in player
			if(ExperimentManager.INSTANCE.getExperimentStatus(experimentID) == Experiment.State.WaitingToStart){
				return experiments.get(experimentID).getSpectatorLocation();
			}
		}
		return null;
	}
	
	public Experiment.State getExperimentStatus(int id){
		return experiments.get(id).currentState;
	}
	
	
	 
	public static void registerExperiment(int id, Experiment ex)
	{
		if (experiments.containsKey(id))
		{
			//for debugging purposes as errors in commands do not natively print to console
		    throw new IllegalArgumentException(String.format("Failed to register experiment for id %d, One is already registered", id));
		}
		if(id == nextAvailableExperimentID){
			experiments.put(id, ex);
			nextAvailableExperimentID++;
			ExperimentManager.metadata.add(INSTANCE.new ExperimentListMetaData("Experiment " + ex.id, ex.getMaxPlayers(), 0));
			sendExperimentUpdates();
		}else{
			throw new IllegalArgumentException(String.format("Failed to register experiment for id %d, Must use getNextID()", id));
		}
	}
	
	public static int getNextID(){
		return nextAvailableExperimentID;
	}
	
	public static void registerExperimentTypes()
	{
		experimentTypes.put(1, ExperimentCTB.class);
	}
	
	public static void render(Entity entity) {
		if (entity instanceof EntityPlayer && entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer) entity;
			for(Experiment ex: experiments.values()){
				if(ex.isPlayerInExperiment(player.getDisplayName())){
					ex.render(entity);
				}
			}
		}
	}
}
