package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockMacGuffin;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.IObservation;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationBlockInFront;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationDestinationPos;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationEntities;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationMacGuffinPos;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationMap;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationPlayerInventory;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationPlayerPos;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationScreen;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.rewards.ExperimentReward;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.ExpFeatureMessage;
import edu.utd.minecraft.mod.polycraft.scoreboards.CustomScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

public class ExperimentTutorial{
	private static final int AREA_PADDING = 48;
	
	public final int id;	//id of the experiment. Should be unique
	public Vec3 pos;	//starting Pos of experiment area
	public Vec3 pos2;
	public Vec3 posOffset;
	//protected static int[][] spawnlocations = new int[4][3];	//spawn locations [location][x,y,z]
	public Random rand = new Random();
	World world;
	public int dim;
	public CustomScoreboard scoreboard;
	private ResearchAssistantEntity dummy;
	//public int genTick = 0;	No longer used
	private boolean areaGenDone= false;
	//TODO: move these values into the ExperimentCTB class and also move their setter functions
	protected int teamsNeeded = 2;
	protected int teamSize = 2;
	protected int playersNeeded = teamsNeeded*teamSize;
	protected int awaitingNumPlayers = playersNeeded;
	protected int featureIndex = 0;
	protected HashMap<Long, ForgeChunkManager.Ticket> tickets = new HashMap<Long, ForgeChunkManager.Ticket>();	//tickets for keeping experiment chuncks loaded
	protected ArrayList<ExperimentReward> rewards = new ArrayList<ExperimentReward>();
	protected ArrayList<IObservation> observations = new ArrayList<IObservation>();
	protected float rewardValue = 0;
	protected ConcurrentLinkedQueue<TutorialFeature> features= new ConcurrentLinkedQueue<TutorialFeature>();
	
	public ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	
	public enum State{
		PreInit,
		WaitingToStart,
		Initializing,
		Starting,
		Running,
		Ending,
		Done;
		}
	
	public State currentState = State.WaitingToStart;
	

	protected int tickCount = 0;
	private boolean hasGameEnded = false;
	private boolean isServer = false;
	private boolean clientInit = false; // for checking if we've ran init functions on client side
	
	
	private String stringToSend = "";

	private PrivateProperty privateProperty;

	public TutorialOptions options;
	
	/**
	 * 
	 * @param id
	 * @param options
	 * @param features
	 */
	public ExperimentTutorial(int id, TutorialOptions options, ArrayList<TutorialFeature> features, boolean genInDim8) {
		
		this.id = id;
		this.isServer = true;
		this.options = options;
		rand = new Random(options.seed);
		Vec3 pos1 = new Vec3(Math.min(options.pos.getX(), options.pos2.getX()),
				Math.min(options.pos.getY(), options.pos2.getY()),
				Math.min(options.pos.getZ(), options.pos2.getZ()));
		Vec3 pos2 = new Vec3(Math.max(options.pos.getX(), options.pos2.getX()) - pos1.xCoord,
				Math.max(options.pos.getY(), options.pos2.getY()) - pos1.yCoord,
				Math.max(options.pos.getZ(), options.pos2.getZ()) - pos1.zCoord);
		if(genInDim8) {
			dim = 8;
			//this.posOffset = new Vec3(-pos1.xCoord + id*(size.xCoord + AREA_PADDING), 0, -pos1.zCoord);
			this.posOffset = new Vec3(-pos1.xCoord, 0, -pos1.zCoord);
			this.pos = new Vec3(pos1.xCoord + posOffset.xCoord, pos1.yCoord + posOffset.yCoord, pos1.zCoord + posOffset.zCoord);
			this.pos2 = new Vec3(pos2.xCoord + posOffset.xCoord, pos2.yCoord + posOffset.yCoord, pos2.zCoord + posOffset.zCoord);
		}else {
			dim = 0;
			this.posOffset = new Vec3(0,0,0);
			this.pos = pos1;
		}
		

		this.world = DimensionManager.getWorld(dim);
		this.currentState = State.WaitingToStart;
		this.teamsNeeded = options.numTeams;
		this.teamSize = options.teamSize;
		this.playersNeeded = teamsNeeded * teamSize;
		this.awaitingNumPlayers = this.playersNeeded;
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard();
		dummy = new ResearchAssistantEntity(world, true);
		for(int x = 0; x < teamsNeeded;x++) {
			this.scoreboard.addNewTeam();
			this.scoreboard.resetScores(0);
		}
		
		//createPrivateProperties();
		
		this.features.addAll(features);
	}
	
	public ExperimentTutorial(int id, World world, ArrayList<TutorialFeature> features) {
		
		this.id = id;
		this.features.addAll(features);
		
		//This is the wrong place to add these. Add in client tick function
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			observations.add(new ObservationMap());
			observations.add(new ObservationPlayerInventory());
			observations.add(new ObservationPlayerPos());
			observations.add(new ObservationBlockInFront());
			observations.add(new ObservationEntities());
		}
	}
	
	
	public void setAreaData(List<Chunk> chunks) {
		this.chunks.clear();
		this.chunks.addAll(chunks);
	}
	
	/**
	 * take in an Entity Player MP object and add JUST the player's name to the appropriate list.
	 * @param player the player to add to the list
	 * @return False if player is already in the list or could otherwise not be added; True if the player was added.
	 */
	public boolean addPlayer(EntityPlayerMP player){
		int playerCount = 0;
		for(Team team: this.scoreboard.getTeams()) {
			if(team.getPlayers().contains(player.getDisplayNameString())) { //check to see if the player's name 
				player.addChatMessage(new ChatComponentText("You have already joined this Experiment. Please wait to Begin."));
				return false;
			}
			playerCount += team.getSize();
		}
		for(Team team: this.scoreboard.getTeams()) {
			if(team.getSize() < teamSize) {
				//team.getPlayers()
				team.getPlayers().add(player.getDisplayNameString());//add player's name to the team
				player.addChatMessage(new ChatComponentText("You have been added to the " + team.getName() + " Team"));
				//TODO: Inform the player which team they're on over here instead of a chat
				//Pass this info to the ExperimentListMetaData as its sent to the player
				playerCount++;
				awaitingNumPlayers--;
				if(playerCount == teamSize*teamsNeeded){
					currentState = State.Starting;
				}
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Removes a player from team in an experiment
	 * This can be needed if a player disconnects, quits the experiment or other reasons.
	 * @param playerName name to be removed
	 * @return <b>True</b> if player found and removed, <b>False</b> if player not in this experiment
	 */
	public boolean removePlayer(String playerName) {
		for(Team team: this.scoreboard.getTeams()) {
			if(team.getPlayers().contains(playerName)) { //check to see if the player's name 
				team.getPlayers().remove(playerName);
				return true;
			}
		}
		return false;
	}
	
	public void onServerTickUpdate() {
		//First check to make sure all players are still connected
		for(Team team: scoreboard.getTeams()) {
			for(String player: team.getPlayers()) {
				if(ExperimentManager.INSTANCE.getPlayerEntity(player) == null)
					removePlayer(player);
			}
		}
//		if(!isServer)
//			return;
		switch(currentState) {
		case WaitingToStart:
			int playerCount = 0;
			for(Team team: this.scoreboard.getTeams()) {
				playerCount += team.getSize();
			}
			if(playerCount == teamSize*teamsNeeded){
				currentState = State.Starting;
			}
			break;
		case Starting:
			if(generateArea()) {	// wait for area generation to complete
				currentState = State.PreInit;
			}
			break;
		case PreInit:
			for(TutorialFeature feature: features){
				feature.preInit(this);
			}
			sendFeatures();	// send features to clients
			currentState = State.Running;
			break;
		case Running:
			for(Team team: scoreboard.getTeams()) {
				for(String player: team.getPlayers()) {
					EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
					playerEntity.getFoodStats().addStats(20, 5);	//constantly fill hunger so players don't starve
				}
			}
			// MAIN Feature loop
			for(TutorialFeature feature: features) {	
				if(feature.isDone)
					continue;	// don't run features that have ended
				feature.onServerTickUpdate(this);
				//System.out.println(this.id + "::featureTick:" + count++ + "," + feature.getFeatureType().toString() + "::" + feature.pos.toString());
				if(feature.isDirty || feature.isDone()) {	//check if feature need to be updated on client side
					System.out.println("[Server] Sending Feature update: " + feature.getName());
					feature.isDirty = false;
					sendFeatureToClient(feature);	// update this feature on the client side
				}
				// if this feature is blocking, exit the loop
				if(!feature.canProceed)
					break;
				// if we reach the end feature, end the experiment
				if(feature.getFeatureType() == TutorialFeatureType.END) {
					currentState = State.Ending;
					break;
				}
			}
			break;
		case Ending:
			for(TutorialFeature feature: features){
				feature.end(this);
			}
			currentState = State.Done;
			break;
		case Done:
			break;
		default:
			break;
		
		}
	}
	

	public void onClientTickUpdate(){
		//We shouldn't have to check experiment state on client side, Just need to run all features loaded on client side
		if(features.isEmpty()) {	// features is empty, so experiment must be over.
//			this.currentState = State.Done;
//			TutorialManager.INSTANCE.clientCurrentExperiment = -1;
		}else {
			if(!clientInit) {
				for(ExperimentReward reward: rewards) {
					reward.init(this);
				}
				if(observations.size() == 0) {
					observations.add(new ObservationBlockInFront());
					observations.add(new ObservationPlayerInventory());
					observations.add(new ObservationPlayerPos());
					observations.add(new ObservationDestinationPos());
					observations.add(new ObservationEntities());
					observations.add(new ObservationMap());
					if(options.outputScreen)
						observations.add(new ObservationScreen());
					search: for(int x = (int)pos.xCoord; x < (int)pos2.xCoord; x++) {
						for(int y = (int)pos.yCoord; y < (int)pos2.yCoord; y++) {
							for(int z = (int)pos.zCoord; z < (int)pos2.zCoord; z++) {
								if(getWorld().getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BlockMacGuffin) {
									observations.add(new ObservationMacGuffinPos());
									break search;
								}
							}
						}
					}
				}
				for(IObservation obs: observations) {
					obs.init(this);
				}
				clientInit = true;
				if(options.outputScreen) {
					try {
						//Change client resolution
						Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
						Minecraft.getMinecraft().setIngameFocus();
//		                Minecraft.getMinecraft().displayWidth = 256;
//		                Minecraft.getMinecraft().displayHeight = 256;
		                Display.setDisplayMode(new DisplayMode(256, 256));

			            Display.setFullscreen(false);
			            //Minecraft.getMinecraft().updateDisplay();
					}catch(LWJGLException e) {
						e.printStackTrace();
					}
				}
			}
			//TutorialManager.INSTANCE.clientCurrentExperiment = this.id;	// should we really be setting this inside the experiment?? -SG
			//this.currentState = State.Running;	// TODO: this should be controlled by the server with some error checking
			for(TutorialFeature feature: features){	//cycle through active features
				if(feature.isDone)
					continue;	// don't run features that have ended
				feature.onClientTickUpdate(this);
				//System.out.println("Client:" + this.id + "::featureTick:" + feature.uuid.toString().substring(0, 5) + "," + feature.getFeatureType().toString() + "::" + feature.pos.toString());
				if(feature.isDirty) {	//check if client needs to update server side feature
					System.out.println("[Client to Server] Sending Feature update");
					feature.isDirty = false;
					sendFeatureToServer(feature);	// update this feature on the server side
				}
				//if feature is blocking, exit loop
				if(!feature.canProceed)
					break;
			}
		}
	}
	
	public void rewardEvent(Event event) {
		for(ExperimentReward reward: rewards) {
			this.rewardValue += reward.rewardEvent(this, event);
		}
	}
	
	public JsonObject getObservations() {
		return getObservations(null);
	}
	
	public JsonObject getObservations(String args) {
		JsonObject jobject = new JsonObject();
		for(IObservation obs: observations) {
			if(!obs.getName().equals("screen"))
				jobject.add(obs.getName(), obs.getObservation(this, args));
		}
		return jobject;
	}
	
	public JsonObject getObservation(String key, String args) {
		JsonObject jobject = new JsonObject();
		for(IObservation obs: observations) {
			if(key.equals(obs.getName()))
				jobject.add(obs.getName(), obs.getObservation(this, args));
		}
		return jobject;
	}
	
	public JsonObject getLocationObservations(String args) {
		JsonObject jobject = new JsonObject();
		for(IObservation obs: observations) {
			if(obs.getName().equals("player") || obs.getName().equals("destinationPos") || obs.getName().equals("macGuffinPos"))
				jobject.add(obs.getName(), obs.getObservation(this, args));
		}
		return jobject;
	}
	
	public JsonObject getVisualObservations(String args) {
		JsonObject jobject = new JsonObject();
		for(IObservation obs: observations) {
			if(obs.getName().equals("screen"))
				jobject.add(obs.getName(), obs.getObservation(this, args));
		}
		return jobject;
	}
	
	
	/**
	 * Set the state to done and remove players from the scoreboard, effectively removing all players.
	 */
	public void stop() {
		this.currentState = State.Done;
		this.scoreboard.clearPlayers();
		for(Ticket tkt : this.tickets.values()) {
			ForgeChunkManager.releaseTicket(tkt);
		}
		
		//ExperimentManager.INSTANCE.sendExperimentUpdates();
		//this.scoreboard = null; //TODO: does this need to be null?
	}
	
	
	public void render(Entity entity){
		for(TutorialFeature feature: features){
			if(!feature.isDone)	// don't render for features that have ended
				feature.render(entity);
			if(!feature.canProceed)
				return;	// if we have a blocking feature, exit the function
		}
	}
	
	public void renderScreen(Entity entity){
		for(TutorialFeature feature: features){
			if(!feature.isDone)	// don't render for features that have ended
				feature.renderScreen(entity);
			if(!feature.canProceed)
				return;	// if we have a blocking feature, exit the function
		}
	}
	
	public void updateFeatures(ConcurrentSet<TutorialFeature> features) {
		this.features.clear();
		this.features.addAll(features);
	}
	
	private void sendFeatures() {
		for(EntityPlayer player: scoreboard.getPlayersAsEntity()) {
			PolycraftMod.SChannel.sendTo(new ExpFeatureMessage(ExpFeatureMessage.PacketType.All,
					this.id, new ArrayList<TutorialFeature>(this.features)), (EntityPlayerMP)player);
		}
	}
	
	private void sendFeatureToServer(TutorialFeature feature) {
		ArrayList features = new ArrayList<TutorialFeature>();
		features.add(feature);
		PolycraftMod.SChannel.sendToServer(new ExpFeatureMessage(ExpFeatureMessage.PacketType.SINGLE,
				this.id, features));
	}
	
	private void sendFeatureToClient(TutorialFeature feature) {
		ArrayList features = new ArrayList<TutorialFeature>();
		features.add(feature);
		for(EntityPlayer player: scoreboard.getPlayersAsEntity()) {
			PolycraftMod.SChannel.sendTo(new ExpFeatureMessage(ExpFeatureMessage.PacketType.SINGLE,
					this.id, features), (EntityPlayerMP)player);
		}
	}
	
	public boolean isPlayerInExperiment(String playerName){
		if(currentState != State.Done)	// if an experiment is done, it shouldn't have any players
			for(Team team: this.scoreboard.getTeams()) {
				for(String player: team.getPlayers()){
					if(player.equalsIgnoreCase(playerName))
						return true;
				}
			}
		return false;
	}
	
	
	protected void initArea() {
		int sizeX = (int) Math.ceil(Math.abs(pos2.xCoord - pos.xCoord));
		int sizeZ = (int) Math.ceil(Math.abs(pos2.zCoord - pos.zCoord));
//		tickets = new ForgeChunkManager.Ticket[sizeX*sizeZ];
		
//		for(int x = 0; x <= sizeX; x++) {
//			for(int z = 0; z <= sizeZ; z++) {
//				tickets[chunkCount] = ForgeChunkManager.requestTicket(PolycraftMod.instance, this.world, ForgeChunkManager.Type.NORMAL);
//				ForgeChunkManager.forceChunk(tickets[chunkCount], new ChunkCoordIntPair((this.xPos / 16) + x, (this.zPos / 16) + z));
//			}
//		}
	}
	
	
	/**
	 * Generates the stoop approximately 1 chunk at a time. This function handles determining the size it needs
	 * This function assumes that the schematic file in question was generated by {@inheritDoc commands.dev.CommandDev.java}
	 * That means the block and data array are generated by the nested for loops: length (x), height (y), width (z), in that order.
	 * @return True if its done generating, False if it's still in progress
	 */
	protected boolean generateArea() {
		// Generate world chunks from chunk list
		for(Chunk chunk: chunks) {
    		world.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition).setStorageArrays(chunk.getBlockStorageArray());
    		world.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition).setHeightMap(chunk.getHeightMap());
    		world.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition).setChunkModified();
    	}
		this.areaGenDone = true;
		
//		final int maxBlocksPerTick = 65536 * 2;
				
		//number of "lengths" to generate per tick (max X blocks), iterating through at least 1 X per tick, in case the height and width are really big.
		//we don't want the game to lag too much.
//		final int maxXPerTick = (int)(Math.max(Math.floor((float)maxBlocksPerTick/(pos2.yCoord*pos2.zCoord)),1.0));
		
		//the position to begin counting in the blocks[] array.
//		int count=(genTick*maxXPerTick)*((int)pos2.yCoord+1)*((int)pos2.zCoord+1);
		
//		if(count >= blocks.length) { //Check if we've written all of the blocks
//			return true; 
//		}
//		
//		//System.out.println("GenPos:" + pos.xCoord + "," + pos.yCoord + "," + pos.zCoord + "::" + count);
//
//		//still have blocks in the blocks[] array we need to add to the world
//		for(int x = (genTick*maxXPerTick); x < (genTick*maxXPerTick)+ maxXPerTick; x++){
//			for(int y = 0; y<=(int)size.yCoord; y++){
//				zLoop: for(int z = 0; z<=(int)size.zCoord; z++){
//					if(!this.tickets.containsKey(ChunkCoordIntPair.chunkXZ2Int((x + (int)pos.xCoord) >> 4, (z + (int)pos.zCoord) >> 4))){
//						tickets.put(ChunkCoordIntPair.chunkXZ2Int((x + (int)pos.xCoord) >> 4, (z + (int)pos.zCoord) >> 4), ForgeChunkManager.requestTicket(PolycraftMod.instance, this.world, ForgeChunkManager.Type.NORMAL));
//						ForgeChunkManager.forceChunk(tickets.get(ChunkCoordIntPair.chunkXZ2Int((x + (int)pos.xCoord) >> 4, (z + (int)pos.zCoord) >> 4)), new ChunkCoordIntPair((x + (int)pos.xCoord) >> 4, (z + (int)pos.zCoord) >> 4));
//						System.out.println("Added Chunk:" + ((x + (int)pos.xCoord) >> 4) + "," + ((z + (int)pos.zCoord) >> 4));
//					}
//					if(count>=blocks.length) { //in case the array isn't perfectly square (i.e. rectangular area was selected)
//						return false;
//					}
//					int curblock = (int)blocks[count];
//					
//					if(curblock == 0 || curblock == 76) {
//						if(!world.isAirBlock(new BlockPos(x + pos.xCoord, y + pos.yCoord ,z + pos.zCoord)))
//							world.setBlockToAir(new BlockPos(x + pos.xCoord, y + pos.yCoord ,z + pos.zCoord));
//						count++;
//						continue zLoop;
//					}else if(curblock == 759) {
//						count++;
//						continue zLoop; //these are Gas Lamps - we don't care for these.
//					}else {
//						if(Block.getIdFromBlock(world.getBlockState(new BlockPos(x + pos.xCoord, y + pos.yCoord ,z + pos.zCoord)).getBlock()) != curblock)
//							world.setBlockState(new BlockPos(x + pos.xCoord, y + pos.yCoord ,z + pos.zCoord), Block.getBlockById(curblock).getDefaultState(), 3);
//						count++;
//						continue zLoop;
//					}
//				}
//			}
//		}
		
		//verify blocks placed
//		count=(genTick*maxXPerTick)*((int)size.yCoord+1)*((int)size.zCoord+1);
//		for(int x = (genTick*maxXPerTick); x < (genTick*maxXPerTick)+ maxXPerTick; x++){
//			for(int y = 0; y<=(int)size.yCoord; y++){
//				for(int z = 0; z<=(int)size.zCoord; z++){
//					if(count>=blocks.length) { //in case the array isn't perfectly square (i.e. rectangular area was selected)
//						return false;
//					}
//					int curblock = (int)blocks[count];
//					
//					if(curblock == 0 || curblock == 76 || curblock == 759) {
//						//lets skip air blocks, redstone torches, and flood lights (from the stadium)
//						count++;
//						continue;
//					}else if(Block.getIdFromBlock(world.getBlockState(new BlockPos(x + pos.xCoord, y + pos.yCoord ,z + pos.zCoord)).getBlock()) != curblock){
//						world.setBlockState(new BlockPos(x + (int)pos.xCoord, y + (int)pos.yCoord ,z + (int)pos.zCoord), Block.getBlockById(curblock).getDefaultState(), 3);
//						count++;
//						System.out.println("Fixed error at: " + (x + pos.xCoord) + "," + (x + pos.yCoord) + "," + (z + pos.zCoord));
//						continue;
//					}else
//						count++;
//				}
//			}
//		}
		
		//System.out.println("Gen Count: " + count);
		return true;
	}

	
	public Collection<EntityPlayerMP> getEntityPlayersInExperiment() {
		return scoreboard.getPlayersAsEntity();
	}
	
	public Collection<String> getPlayersInExperiment() {
		return scoreboard.getPlayers();
	}
	
	public ConcurrentLinkedQueue<TutorialFeature> getFeatures() {
		return features;
	}
	
	public World getWorld() {
		return this.world;
	}
	
	private void createPrivateProperties() {
		if(!this.world.isRemote) {
			int endX = 0, endZ = 0;
			for(int x = (int)pos.xCoord - 8; Math.abs(x) <= Math.abs(pos.xCoord + pos2.xCoord) + 8; x += 16) {
				for(int z = (int)pos.zCoord - 8; Math.abs(z) <= Math.abs(pos.zCoord + pos2.zCoord) + 8; z += 16) {
					//don't feel like doing the math... 
					endX = x;
					endZ = z;
				}
			}
			PrivateProperty pp =  new PrivateProperty(
					false,
					null,
					"Tutorial",
					"Good Luck!",
					new PrivateProperty.Chunk(Math.min((int)pos.xCoord, endX) >> 4, Math.min((int)pos.zCoord, endZ) >> 4),
					new PrivateProperty.Chunk(Math.max((int)pos.xCoord, endX) >> 4, Math.max((int)pos.zCoord, endZ) >> 4),
					new int[] {0,3,4,5,6,7,32,44},
					8);
			
			ServerEnforcer.addExpPrivateProperty(pp);	
			this.privateProperty=pp;	
			ServerEnforcer.INSTANCE.sendExpPPDataPackets();
			System.out.println("x: " + ((int)pos.xCoord >> 4) + "::" + (endX >> 4) + "|| z: " + ((int)pos.zCoord >> 4) + "::" + (endZ >> 4));
		}
	}
}
