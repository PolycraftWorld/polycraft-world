package edu.utd.minecraft.mod.polycraft.experiment.old;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.experiment.ExperimentDef;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentOld.State;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureEnd;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureGuide;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureInstruction;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureScore;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureStart;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager.PacketMeta;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer.ExperimentsPacketType;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.util.ScoreEvent;
import edu.utd.minecraft.mod.polycraft.util.PlayerRegisterEvent;
import edu.utd.minecraft.mod.polycraft.util.PlayerExitEvent;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ExperimentManager {

	private static final String OUTPUT_FILE_NAME = "ExperimentDefs.nbt";
	public static ExperimentManager INSTANCE = new ExperimentManager();
	private static int nextAvailableExperimentID = 1; 	//one indexed
	private static Hashtable<Integer, ExperimentOld> experiments = new Hashtable<Integer, ExperimentOld>();
	private static Hashtable<Integer, Class<? extends ExperimentOld>> experimentTypes = new Hashtable<Integer, Class <? extends ExperimentOld>>();
	private static Hashtable<Integer, ExperimentDef> experimentDefinitions;	//table of all available defined experiments with set parameters
	
	private List<EntityPlayerMP> globalPlayerList;
	public static ArrayList<ExperimentListMetaData> metadata = new ArrayList<ExperimentListMetaData>(); 
	public int clientCurrentExperiment = -1; //Variable held in the static instance for memory purposes. In the future, this may need to be moved somewhere else
	
	//read the schematic file only once.
	short n = 0;
	Schematic sch = new Schematic(new NBTTagList(), n, n, n, new int[] {0}, new byte[] {0});
	public Schematic stoop = sch.get("stoopWithCrafting.psm");
	public Schematic flat_field = sch.get("flatWithCrafting.psm");
	
	public static Hashtable<Integer, ExperimentOld> getExperiments() {
		return experiments;
	}

	public ExperimentManager() {
		try {
			globalPlayerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			//MinecraftServer.getServer().getConfigurationManager().
		}catch (NullPointerException e) {
			//e.printStackTrace();//TODO: Remove this.
			globalPlayerList = null;
		}
		experimentDefinitions = new Hashtable<Integer, ExperimentDef>();
		if(System.getProperty("isExperimentServer") != null)	//don't need to run this if it isn't an experiment server
			this.loadExpDefs();	//attempt to load experiment defs
		
		clientCurrentExperiment = -1;
		
	}
	
	//initialize new experiments before players join
	@Deprecated
	public static void init(){					
		World world = DimensionManager.getWorld(8);
		for(ExperimentOld ex: experiments.values()){
			ex.init(); //this is not needed anymore, as Experiment sets this in its constructor (i.e. when creating a new experiment)
		}
	}
	
	
	public void onServerTickUpdate(final TickEvent.ServerTickEvent tick) {
		if(tick.phase == Phase.END) {
			for(ExperimentOld ex: experiments.values()){
				if(ex.currentState != ExperimentOld.State.Done) {
					ex.onServerTickUpdate();
				}
			}
			
			int posOffset = 10000;
			int multiplier = 1;
			
			//setup experiments
			expDefLoop: for(ExperimentDef expDef: experimentDefinitions.values()) {
				if(expDef.isEnabled()) {
					for(ExperimentOld ex: experiments.values()) {
						if(ex.expDefID == expDef.getID() && ex.currentState == State.WaitingToStart)
							continue expDefLoop;	//if we find a match that is not currently running, then continue
					}
					int nextID = this.getNextID();
					int numChunks = 20;
					multiplier = nextID;
					System.out.println("Creating new Exp");
					switch(expDef.getExpType()) {
						case CTB_FLAT_1_PLAYER:
							posOffset = 500;
							Experiment1PlayerCTB newExp1Player = new Experiment1PlayerCTB(nextID, numChunks, 16*numChunks + 16 + posOffset, 
									multiplier*16*numChunks + 144,DimensionManager.getWorld(8), 1, expDef.getPlayersPerTeam());
							this.registerExperiment(nextID, newExp1Player, expDef.getName());
							newExp1Player.updateParams(expDef);
							break;
						case CTB_FLAT:
							posOffset = 820;
							ExperimentFlatCTB newExpFlat = new ExperimentFlatCTB(nextID, numChunks, 16*numChunks + 16 + posOffset, 
									multiplier*16*numChunks + 144,DimensionManager.getWorld(8), expDef.getTeamCount(), expDef.getPlayersPerTeam());
							this.registerExperiment(nextID, newExpFlat, expDef.getName());
							newExpFlat.updateParams(expDef);
							break;
						case CTB_STOOP:
							posOffset = 1140;
							ExperimentCTB newExpCTB = new ExperimentCTB(nextID, numChunks, 16*numChunks + 16, 
									multiplier*16*numChunks + 144,DimensionManager.getWorld(8), expDef.getTeamCount(), expDef.getPlayersPerTeam());
							this.registerExperiment(nextID, newExpCTB, expDef.getName());
							newExpCTB.updateParams(expDef);
							break;
						default:
							break;
					}
					sendExperimentUpdates();
				}else {
					for(ExperimentOld ex: experiments.values()) {
						if(ex.expDefID == expDef.getID() && ex.currentState == State.WaitingToStart) {
							this.stop(ex.id);
							System.out.println("Deleting Exp");
							sendExperimentUpdates();
						}
					}
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
			for(ExperimentOld ex: experiments.values()){
				if(ex.currentState != ExperimentOld.State.Done) {
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
		for(ExperimentOld ex : experiments.values()) {
			//skip the experiment if it's not waiting to start. We don't wanna accidentally remove a player...
			//or... what if we need to when a player leaves?
			//if(ex.currentState != Experiment.State.WaitingToStart) continue;
			try {
				for(String play : ex.scoreboard.getPlayers()) {
					if(play.equals(player.getDisplayNameString())) {
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
		for(ExperimentOld ex : experiments.values()) {
			 
			try {
				if(ex.scoreboard.getPlayers().isEmpty()) continue;//skip experiments that are completed or have no players in them.
				for(String play : ex.scoreboard.getPlayers()) {
					if(play.equals(playerName)) {
						removePlayerFromExperiment(ex.id, playerName);
						PlayerExitEvent event = new PlayerExitEvent(ex.id,playerName);
						edu.utd.minecraft.mod.polycraft.util.Analytics.onPlayerExitEvent(event);
						System.out.println(playerName+"Player is removed from experiment"+ex.id);
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
		for(ExperimentOld ex : experiments.values()) {
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
	public static void sendExperimentUpdates() {
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
	
	public EntityPlayerMP getPlayerEntity(String playerName) {
		try {
			if(this.globalPlayerList == null || this.globalPlayerList.isEmpty()) {
//				if(FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer())
//					this.globalPlayerList = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList;
//				else
				this.globalPlayerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			} else if(this.globalPlayerList == null) {
				System.out.println("List is null, help pls");
			}
			for (EntityPlayerMP player : this.globalPlayerList) {
				if(player.getDisplayNameString().equals(playerName)) {
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
		ExperimentOld ex = experiments.get(id);
		Map.Entry<Team, Float> maxEntry = null;
		for (Map.Entry<Team, Float> entry : ex.scoreboard.getTeamScores().entrySet()) {
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)  {
		        maxEntry = entry;
		    }
		}
		
		for(EntityPlayer player : ex.scoreboard.getPlayersAsEntity()) {
			if(ex.currentState != State.Ending) {
				EntityPlayerMP playerEntity = (EntityPlayerMP) player;
				playerEntity.addChatMessage(new ChatComponentText("Experiment is no longer available"));
			}else {
				if(ex.scoreboard.getPlayerTeam(player.getDisplayNameString()).equals(maxEntry.getKey())) {
					EntityPlayerMP playerEntity = (EntityPlayerMP) player;
					playerEntity.addChatMessage(new ChatComponentText("Experiment Complete. Teleporting to Winner's Podium"));
					playerEntity.mcServer.getConfigurationManager().transferPlayerToDimension(playerEntity, 0,	new PolycraftTeleporter(playerEntity.mcServer.worldServerForDimension(0), -16, 71, 10));

				} else {
					EntityPlayerMP playerEntity = (EntityPlayerMP) player;
					playerEntity.addChatMessage(new ChatComponentText("Experiment Complete. Teleporting to UTD..."));
					playerEntity.mcServer.getConfigurationManager().transferPlayerToDimension(playerEntity, 0,	new PolycraftTeleporter(playerEntity.mcServer.worldServerForDimension(0)));
				}
			}
		}
		
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
			if(ExperimentManager.INSTANCE.getExperimentStatus(experimentID) == ExperimentOld.State.WaitingToStart){
				return experiments.get(experimentID).getSpectatorLocation();
			}
		}
		return null;
	}
	
	public ExperimentOld.State getExperimentStatus(int id){
		return experiments.get(id).currentState;
	}
	
	public static ExperimentOld getExperiment(int id){
		return experiments.get(id);
	}
	
	
	/**
	 * For getting experiment id of running experiment
	 * @return First currently running experiment or -1 if there are no running experiments
	 */
	public static int getRunningExperiment() {
		for(int expID: experiments.keySet()) {
			if(experiments.get(expID).currentState == ExperimentOld.State.Starting || experiments.get(expID).currentState == ExperimentOld.State.Running || experiments.get(expID).currentState == ExperimentOld.State.Halftime)
			{
				return expID;
			}
		}
		return -1;
	}
	
	public static List<Integer> getRunningExperiments() {
		List<Integer> list_of_running_experiments=new ArrayList<Integer>();  
		for(int expID: experiments.keySet()) {
			if(experiments.get(expID).currentState == ExperimentOld.State.Starting || experiments.get(expID).currentState == ExperimentOld.State.Running || experiments.get(expID).currentState == ExperimentOld.State.Halftime)
			{
				list_of_running_experiments.add(expID);
			}
		}
		return list_of_running_experiments;
	}
	
	/**
	 * Adds a new experiment to the experiment manager, increments the nextAvailableExperimentID and sends 
	 * updates to all of the clients on world 0 (so that they can see the new experiment available on their GUI)
	 * @param id the ID to add TODO: Should we hide this field and have the ID's be auto-incrementing??
	 * @param ex the experiment to add 
	 */
	public static void registerExperiment(int id, ExperimentOld ex, String name)
	{
		if (experiments.containsKey(id))
		{
			//for debugging purposes as errors in commands do not natively print to console
		    throw new IllegalArgumentException(String.format("Failed to register experiment for id %d, One is already registered", id));
		}
		if(id == nextAvailableExperimentID){
			experiments.put(id, ex);
			nextAvailableExperimentID++;
			ExperimentManager.metadata.add(INSTANCE.new ExperimentListMetaData(ex, name));
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
			for(ExperimentOld ex: experiments.values()){
				if(ex.isPlayerInExperiment(player.getDisplayNameString())){
					ex.render(entity);
				}
			}
		}
	}

	public void updateExperimentParameters(int experimentID, ExperimentParameters params) {
		this.experiments.get(experimentID).updateParams(params);
		
	}
	
	public void requestExpDefs(String playerName) {
		ClientEnforcer.INSTANCE.sendExperimentPacket(playerName, ExperimentsPacketType.ExpDefGet.ordinal());
	}

	/**
	 * Used for updating/adding/removing experiment definitions on server side. Sent from client side.
	 * @param index	index of experiemnt definition. set to -1 to add a new expDef
	 * @param expDef experiement definition to update on server side
	 * @param removeExpDef set to true if removing experiment.
	 */
	public void sendExpDefUpdate(int index, ExperimentDef expDef, boolean removeExpDef) {
		try {
			Gson gson = new Gson();
			Type gsonType = new TypeToken<ByteArrayOutputStream>(){}.getType();
			NBTTagCompound tempNBT = expDef.save();
			String experimentUpdates;
			
			final ByteArrayOutputStream experimentUpdatesTemp = new ByteArrayOutputStream();	//must convert into ByteArray because converting with just Gson fails on receiving end
			tempNBT.setString("player", Minecraft.getMinecraft().thePlayer.getDisplayNameString());
			CompressedStreamTools.writeCompressed(tempNBT, experimentUpdatesTemp);
			experimentUpdates = gson.toJson(experimentUpdatesTemp, gsonType);
			if(removeExpDef) {
				ClientEnforcer.INSTANCE.sendExperimentPacket(experimentUpdates,ExperimentsPacketType.ExpDefRemove.ordinal());
			}else {
				ClientEnforcer.INSTANCE.sendExperimentPacket(experimentUpdates, ExperimentsPacketType.ExpDefUpdate.ordinal());
			}
			
		}catch(Exception e) {
			PolycraftMod.logger.debug("Cannot send Feature Update: " + e.toString() );
		}
	}

	/**
	 * Function for updating experiment definitions on client side
	 * @param playerName
	 */
	public static void sendExperimentDefs(String playerName) {
		try {
			NBTTagCompound nbtFeatures = new NBTTagCompound();
			NBTTagList nbtList = new NBTTagList();
			
			for(ExperimentDef expDef: experimentDefinitions.values()) {
				nbtList.appendTag(expDef.save());
			}
			nbtFeatures.setTag("expDefs", nbtList);
			
			final ByteArrayOutputStream experimentUpdatesTemp = new ByteArrayOutputStream();	//must convert into ByteArray becuase converting with just Gson fails on reveiving end
			CompressedStreamTools.writeCompressed(nbtFeatures, experimentUpdatesTemp);
			
			Gson gson = new Gson();
			Type gsonType = new TypeToken<ByteArrayOutputStream>(){}.getType();
			final String experimentUpdates = gson.toJson(experimentUpdatesTemp, gsonType);
			
			ServerEnforcer.INSTANCE.sendExpDefUpdatePackets(experimentUpdates, 
					(EntityPlayerMP)ExperimentManager.INSTANCE.getPlayerEntity(playerName));
			System.out.println("Sending Update...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Used to update Experiment definition on Server side from client
	 * @param featuresStream
	 * @param removeExpDef
	 */
	public void setExperimentDef(ByteArrayOutputStream featuresStream, boolean removeExpDef) {
		try {
			
			NBTTagCompound expDefNBT = CompressedStreamTools.readCompressed(new ByteArrayInputStream(featuresStream.toByteArray()));
			
			int index = expDefNBT.getInteger("id");
			ExperimentDef temp = new ExperimentDef();

			temp.load(expDefNBT);
			
			if(removeExpDef) {
				experimentDefinitions.remove(temp.getID());
			}else {
				//get next unused key for index if index is -1
				if(index == -1) {
					index = 0;	//should start at initial value greater than -1
					for(Integer key: experimentDefinitions.keySet()) {
						if(index <= key) {
							index = key + 1;
						}
					}
				}
				temp.setID(index);
				experimentDefinitions.put(index, temp);
			}
			
			saveExpDefs();	//attempt to save experiment defs
		} catch (Exception e) {
			System.out.println("Cannot update ExpDef: " + e.getMessage());
		}
	}
	
	/**
	 * Used to update all Experiment definitions on client side from server
	 * @param expDefsStream
	 * @param isRemote
	 */
	public void setExperimentDefs(ByteArrayOutputStream expDefsStream, boolean isRemote) {
		try {
			NBTTagCompound nbtExpDefs = CompressedStreamTools.readCompressed(new ByteArrayInputStream(expDefsStream.toByteArray()));
            NBTTagList nbtExpDefList = (NBTTagList) nbtExpDefs.getTag("expDefs");
			Hashtable<Integer, ExperimentDef> expDefs = new Hashtable<Integer, ExperimentDef>();
			
			for(int i =0;i<nbtExpDefList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtExpDefList.getCompoundTagAt(i);
				ExperimentDef test = new ExperimentDef();
				test.load(nbtFeat);
				expDefs.put(test.getID(), test);
			}
			
			experimentDefinitions.clear();
			experimentDefinitions.putAll(expDefs);
		} catch (Exception e) {
            System.out.println("I can't load initial Experiment Definitions, because: " + e.getStackTrace()[0]);
        }
	}

	public static Collection<ExperimentDef> getExperimentDefinitions(){
		return experimentDefinitions.values();
	}
	
	private void saveExpDefs() {
		NBTTagCompound nbtFeatures = new NBTTagCompound();
		NBTTagList nbtList = new NBTTagList();
		for(int i =0;i<experimentDefinitions.size();i++) {
			nbtList.appendTag(experimentDefinitions.get(i).save());
		}
		nbtFeatures.setTag("expDefs", nbtList);
		FileOutputStream fout = null;
		try {
			File file = new File(this.OUTPUT_FILE_NAME);//TODO CHANGE THIS FILE LOCATION
			fout = new FileOutputStream(file);
			
			if (!file.exists()) {
				file.createNewFile();
			}
			CompressedStreamTools.writeCompressed(nbtFeatures, fout);
			fout.flush();
			fout.close();
			
		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadExpDefs() {
		try {
        	experimentDefinitions.clear();
        	
        	File file = new File(this.OUTPUT_FILE_NAME);//TODO CHANGE THIS FILE LOCATION
        	InputStream is = new FileInputStream(file);

            NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(is);
            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("expDefs");
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				ExperimentDef tempExpDef = new ExperimentDef();
				tempExpDef.load(nbtFeat);
				experimentDefinitions.put(tempExpDef.getID(), tempExpDef);
			}
			
            is.close();

        } catch (Exception e) {
            System.out.println("I can't load experiment definitions, because " + e.getStackTrace()[0]);
        }
	}
	

	/**
	 * Internal class that keeps track of all experiments
	 * A static arraylist of this class, called #metadata is transferred between Server & Client
	 * for synchronization. This contains relevant, condensed information enabling efficient info
	 * transfer across the network. 
	 * The information is fired from {link #ExperimentManager.sendExperimentUpdates} and received by
	 * {link #ClientEnforcer}. It is currently rendered on {link #GuiExperimentList}
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
		public String expType;
		
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
		
		public ExperimentListMetaData(ExperimentOld type, String name) {
			if(type instanceof ExperimentCTB) {
				this.expName = name + " " + type.id;
				this.expType = "Stoop";
			}else {
				this.expName = name + " " + type.id;
				this.expType = "Flat"; //TODO: Declare these names as static within the class.
			}
			this.playersNeeded = type.getMaxPlayers();
			currentPlayers = 0;
			this.instructions = type.getInstructions();
			this.parameters = new ExperimentParameters(type);
			
		}
		
		public ExperimentListMetaData(String name, int maxPlayers, int currPlayers, String instructions, ExperimentParameters params, ExperimentOld type) {
			expName = name;
			playersNeeded = maxPlayers;
			currentPlayers = currPlayers;
			this.instructions = instructions; 
			this.parameters = params;
		}
		
		public void updateCurrentPlayers(int newPlayerCount) {
			currentPlayers = newPlayerCount;
		}
		
		@Deprecated
		public void updateParams(ExperimentParameters params) {
			this.parameters = params;
		}
		
		public void updateParams(int ExpID) {
			this.parameters = new ExperimentParameters(ExperimentManager.experiments.get(ExpID));
			this.instructions = ExperimentManager.experiments.get(ExpID).getInstructions(); //update instructions
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
	 * this is transmitted through the network (fired currently from {link GuiExperimentList} and received
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
}
