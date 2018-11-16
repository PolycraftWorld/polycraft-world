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
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
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

public class ExperimentManager {

	public static ExperimentManager INSTANCE = new ExperimentManager();
	private static int nextAvailableExperimentID = 1; 	//one indexed
	private static Hashtable<Integer, Experiment> experiments = new Hashtable<Integer, Experiment>();
	private static Hashtable<Integer, Class<? extends Experiment>> experimentTypes = new Hashtable<Integer, Class <? extends Experiment>>();
	private List<EntityPlayer> globalPlayerList;
	public static ArrayList<ExperimentListMetaData> metadata = new ArrayList<ExperimentListMetaData>(); 
	public int clientCurrentExperiment = -1; //Variable held in the static instance for memory purposes. In the future, this may need to be moved somewhere else
	
	//read the schematic file only once.
	short n = 0;
	Schematic sch = new Schematic(new NBTTagList(), n, n, n, new int[] {0}, new byte[] {0});
	public Schematic stoop = sch.get("stoopUpdated.psm");
	
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
		public String instructions = "";
		public int playersNeeded;
		public int currentPlayers;
		private boolean available = true;
		private ExperimentParameters parameters;
		
		public ExperimentListMetaData(String name, int maxPlayers, int currPlayers, String instructions) {
			expName = name;
			playersNeeded = maxPlayers;
			currentPlayers = currPlayers;
			this.instructions = instructions; 
		}
		public ExperimentListMetaData(String name, int maxPlayers, int currPlayers, String instructions, ExperimentParameters params) {
			expName = name;
			playersNeeded = maxPlayers;
			currentPlayers = currPlayers;
			this.instructions = instructions; 
			this.parameters = params;
		}
		
		public void updateCurrentPlayers(int newPlayerCount) {
			currentPlayers = newPlayerCount;
		}
		
		public void updateParams(ExperimentParameters params) {
			this.parameters = params;
		}
		
		public void updateParams(int ExpID) {
			this.parameters = new ExperimentParameters(ExperimentManager.experiments.get(ExpID));
		}
		
		public void deactivate() {
			available = false;
		}
		
		public boolean isAvailable() {
			return available;
		}
		
		public ExperimentParameters getParams() {
			return parameters;
		}

		@Override
		public String toString() {
			return String.format("Name: %s\tPlayers Needed: %d\t Current Players: %d Available? %s", expName, playersNeeded, currentPlayers, available);
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
		public ExperimentParameters params;
		public ExperimentParticipantMetaData(String playerName, int expID, boolean join) {
			this.playerName = playerName;
			this.experimentID = expID;
			wantsToJoin = join;
		}
		
		public ExperimentParticipantMetaData(String playerName, int expID, ExperimentParameters param) {
			this.playerName = playerName;
			this.experimentID = expID;
			this.params = param;
		}
	}
	
	public ExperimentManager() {
		try {
			globalPlayerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			//MinecraftServer.getServer().getConfigurationManager().
		}catch (NullPointerException e) {
			//e.printStackTrace();//TODO: Remove this.
			globalPlayerList = null;
		}
		
		clientCurrentExperiment = -1;
		
	}
	
	//initialize new experiments before players join
	@Deprecated
	public static void init(){					
		World world = DimensionManager.getWorld(8);
		for(Experiment ex: experiments.values()){
			ex.init(); //this is not needed anymore, as Experiment sets this in its constructor (i.e. when creating a new experiment)
		}
	}
	
	
	public void onServerTickUpdate(final TickEvent.ServerTickEvent tick) {
		if(tick.phase == Phase.END) {
		//	boolean areAnyActive1x = false;
			boolean areAnyActive = false;
			boolean areAnyActive4x = false;
			boolean areAnyActive8x = false;
			for(Experiment ex: experiments.values()){
				if(ex.currentState != Experiment.State.Done) {
					ex.onServerTickUpdate();
				}
			}
			for(ExperimentListMetaData ex2 : metadata) {
				if(ex2.isAvailable()) {
					switch(ex2.playersNeeded) {
//					case 1:
//						areAnyActive1x = true;
//						break;
					case 2:
						areAnyActive = true;
						break;
					case 4:
						areAnyActive4x = true;
						break;
					case 8:
						areAnyActive8x = true;
						break;
					default:
						//areAnyActive1x = true;
						areAnyActive = true;
						areAnyActive4x = true;
						areAnyActive8x = true;
						break;
					}
					
				}
			}
			//TODO: remove this.
//			if(!areAnyActive1x) {
//				int nextID = this.getNextID();
//				int numChunks = 8;
//				ExperimentCTB newExpCTB1 = new ExperimentCTB(nextID, numChunks, nextID*16*numChunks + 16, nextID*16*numChunks + 144,DimensionManager.getWorld(8), 1, 1);
//				//newExpCTB1.setTeamsNeeded(1);
//				//newExpCTB1.setTeamSize(1);
//				this.registerExperiment(nextID, newExpCTB1);
//				//sendExperimentUpdates();
//			}
			if(!areAnyActive) {
				int nextID = this.getNextID();
				int numChunks = 8;
				//TODO Change multiplier to nextID to spawn a new field per experiment
				int multiplier = 1;
				ExperimentCTB newExpCTB2x = new ExperimentCTB(nextID, numChunks, multiplier*16*numChunks + 16, multiplier*16*numChunks + 144,DimensionManager.getWorld(8), 2, 1);
				//newExpCTB1.setTeamsNeeded(1);
				//newExpCTB1.setTeamSize(1);
				this.registerExperiment(nextID, newExpCTB2x);
				//sendExperimentUpdates();
			}
			if(!areAnyActive4x) {
				int nextID = this.getNextID();
				int numChunks = 8;
				ExperimentCTB newExpCTB4x = new ExperimentCTB(nextID, numChunks, nextID*16*numChunks + 16, nextID*16*numChunks + 144,DimensionManager.getWorld(8), 2, 2);
				//newExpCTB1.setTeamsNeeded(1);
				//newExpCTB1.setTeamSize(1);
				this.registerExperiment(nextID, newExpCTB4x);
				//sendExperimentUpdates();
			}
			if(!areAnyActive8x) {
				int nextID = this.getNextID();
				int numChunks = 8;
				ExperimentCTB newExpCTB8x = new ExperimentCTB(nextID, numChunks, nextID*16*numChunks + 16, nextID*16*numChunks + 144,DimensionManager.getWorld(8), 2, 4);
				//newExpCTB1.setTeamsNeeded(1);
				//newExpCTB1.setTeamSize(1);
				this.registerExperiment(nextID, newExpCTB8x);
				//sendExperimentUpdates(); //do we need this??
			}
		}
	}
	
	/**
	 * This is called from Common Proxy
	 * @param tick the input tick.
	 */
	public void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		if(tick.side == Side.CLIENT && tick.phase == Phase.END){ //I think these are always true?
			for(Experiment ex: experiments.values()){
				if(ex.currentState != Experiment.State.Done) {
					ex.onClientTickUpdate();
				}
			}
		}
	}
	
	public boolean addPlayerToExperiment(int expID, EntityPlayerMP player){
		boolean value = experiments.get(expID).addPlayer(player);
		ExperimentManager.metadata.get(expID - 1).updateCurrentPlayers(experiments.get(expID).getMaxPlayers() - experiments.get(expID).getNumPlayersAwaiting());		
		sendExperimentUpdates();
		return value;
	}
	
	//TODO: consolidate these functions and use only the display name OR the player object. I think it'd be cleaner to pass Player Objects?
	public boolean removePlayerFromExperiment(int expID, EntityPlayerMP player){
		boolean value = experiments.get(expID).removePlayer(player);
		ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(null, player);
		ExperimentManager.metadata.get(expID - 1).updateCurrentPlayers(experiments.get(expID).getMaxPlayers() - experiments.get(expID).getNumPlayersAwaiting());		
		sendExperimentUpdates();
		return value;
	}
	
	/**
	 * Removes the player from the given experiment. Sends an update packet to the player requesting the player
	 * to clear their scoreboard display, if it's active, and to reset their Experiment Manager object
	 * Sends experimentUpdates to re-populate their experiment manager object
	 * @param expID the experiment to withdraw the player from
	 * @param player the player in question
	 * @return True if a player was successfully removed. False if not.
	 */
	private boolean removePlayerFromExperiment(int expID, String player){
		boolean value = experiments.get(expID).removePlayer(player);
		if(value) ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(null, (EntityPlayerMP)this.getPlayerEntity(player));
		ExperimentManager.metadata.get(expID - 1).updateCurrentPlayers(experiments.get(expID).getMaxPlayers() - experiments.get(expID).getNumPlayersAwaiting());		
		sendExperimentUpdates();
		return value;
	}
	
	
	@Deprecated
	public boolean checkAndRemovePlayerFromExperimentLists(EntityPlayer player) {
		for(Experiment ex : experiments.values()) {
			//skip the experiment if it's not waiting to start. We don't wanna accidentally remove a player...
			//or... what if we need to when a player leaves?
			//if(ex.currentState != Experiment.State.WaitingToStart) continue;
			try {
				for(String play : ex.scoreboard.getPlayers()) {
					if(play.equals(player.getDisplayName())) {
						removePlayerFromExperiment(ex.id, (EntityPlayerMP)player);
						//sendExperimentUpdates();
						return true;
					}
				}
			}catch(NullPointerException e) {
				e.printStackTrace();
				//continue();
			}
		}
		sendExperimentUpdates();
		return false;
	}
	
	/**
	 * If player asks to be removed (using a command, withdrawing from the queue, disconnecting from the server)
	 * Enable them to be removed from the queue. This method uses their display name to do so.
	 * @param playerName
	 * @return True if the player was successfully removed from an experiment
	 */
	public boolean checkAndRemovePlayerFromExperimentLists(String playerName) {
		for(Experiment ex : experiments.values()) {
			 
			try {
				if(ex.scoreboard.getPlayers().isEmpty()) continue;//skip experiments that are completed or have no players in them.
				for(String play : ex.scoreboard.getPlayers()) {
					if(play.equals(playerName)) {
						removePlayerFromExperiment(ex.id, playerName);
						//sendExperimentUpdates();
						return true;
					}
				}
			}catch(NullPointerException e) {
				e.printStackTrace();
			}
		}
		sendExperimentUpdates();
		return false;
	}
	
	/**
	 * When a player joins the World, send Experiment Updates to all players (doesn't hurt)
	 * @param player
	 * @return
	 */
	public boolean onEntityJoinWorldEventSendUpdates(EntityPlayer player) {
		sendExperimentUpdates();
		return false;
	}
	
	
	//@SideOnly(Side.SERVER)
	@Deprecated
	public boolean checkGlobalPlayerListAndUpdate() {
		for(Experiment ex : experiments.values()) {
			for(String player : ex.scoreboard.getPlayers()) {
				if(this.getPlayerEntity(player) == null) {
					removePlayerFromExperiment(ex.id, player);
					return true;
				}
			}
		}
		sendExperimentUpdates();
		return true;
	}
	
	//@SideOnly(Side.CLIENT)
	public void resetClientExperimentManager() {
		clientCurrentExperiment = -1;
	}
	
	//@SideOnly(Side.SERVER)
	static void sendExperimentUpdates() {
		Gson gson = new Gson();
		Type gsonType = new TypeToken<ArrayList<ExperimentListMetaData>>(){}.getType();
		final String experimentUpdates = gson.toJson(ExperimentManager.metadata, gsonType);
		ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(experimentUpdates, null);
		System.out.println("Sending Update...");
	}
	
	//@SideOnly(Side.CLIENT)
	public static void updateExperimentMetadata(String experimentMetaDataJson) {
		Gson gson = new Gson();
		ExperimentManager.metadata = gson.fromJson(experimentMetaDataJson, new TypeToken<ArrayList<ExperimentListMetaData>>() {}.getType());
		PolycraftMod.logger.debug(metadata.toString());
		System.out.println("Receiving new Data...");
		System.out.println(metadata.toString());
	}
	
	/**
	 * This is ONLY RUN by the command //challenge start
	 * Finds the experiment id passed in by the user and tries to start.
	 * @param expID the experiment to start.
	 */
	public void commandStart(int expID){
		experiments.get(expID).start();
		
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
	
	/**
	 * Stop the experiment in question, transfer players back to UTD, and send Experiment Updates
	 * to all players.
	 * @param id
	 */
	public void stop(int id){
		Experiment ex = experiments.get(id);
		for(Team team: ex.scoreboard.getTeams()) {
			for(String player: team.getPlayers()){
				EntityPlayerMP playerEntity = (EntityPlayerMP) this.getPlayerEntity(player);
				playerEntity.addChatMessage(new ChatComponentText("Experiment Complete. Teleporting to UTD..."));
				playerEntity.mcServer.getConfigurationManager().transferPlayerToDimension(playerEntity, 0,	new PolycraftTeleporter(playerEntity.mcServer.worldServerForDimension(0)));
			}
		}
		//
		ex.stop(); //this clears the scoreboard (removes players)
		//reset(); //don't delete the scoreboard from the manager just yet.
		sendExperimentUpdates();
		System.out.println("Experiment Is Stopped. We're NOT adding a new Experiment");
		//Add a new experiment to the experiments List.
		int nextID = this.getNextID();
		int numChunks = 8;
		//this.registerExperiment(nextID, new ExperimentCTB(nextID, numChunks, nextID*16*numChunks + 16, nextID*16*numChunks + 144,DimensionManager.getWorld(8)));
	}
	
	@Deprecated
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
	
	
	/**
	 * Adds a new experiment to the experiment manager, increments the nextAvailableExperimentID and sends 
	 * updates to all of the clients on world 0 (so that they can see the new experiment available on their GUI)
	 * @param id the ID to add TODO: Should we hide this field and have the ID's be auto-incrementing??
	 * @param ex the experiment to add 
	 */
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
			ExperimentManager.metadata.add(INSTANCE.new ExperimentListMetaData("Experiment " + ex.id, ex.getMaxPlayers(), 0, ex.getInstructions(), new ExperimentParameters(ex)));
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

	public void updateExperimentParameters(int experimentID, ExperimentParameters params) {
		this.experiments.get(experimentID).updateParams(params);
		
	}
}
