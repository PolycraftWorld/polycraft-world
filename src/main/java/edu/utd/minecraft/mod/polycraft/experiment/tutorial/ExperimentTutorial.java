package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.entity.ai.EntityAICaptureBases;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityAndroid;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentFlatCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.experiment.feature.ExperimentFeature;
import edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureBase;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FueledLampInventory;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.CustomScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.ScoreboardManager;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import edu.utd.minecraft.mod.polycraft.worldgen.ResearchAssistantLabGenerator;
import net.minecraft.block.Block;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class ExperimentTutorial{
	private static final int AREA_PADDING = 48;
	
	public final int id;	//id of the experiment. Should be unique
	public Vec3 pos;	//starting Pos of experiment area
	public Vec3 size;
	public Vec3 posOffset;
	//protected static int[][] spawnlocations = new int[4][3];	//spawn locations [location][x,y,z]
	World world;
	public int dim;
	public CustomScoreboard scoreboard;
	private ResearchAssistantEntity dummy;
	public int genTick = 0;
	//TODO: move these values into the ExperimentCTB class and also move their setter functions
	protected int teamsNeeded = 2;
	protected int teamSize = 2;
	protected int playersNeeded = teamsNeeded*teamSize;
	protected int awaitingNumPlayers = playersNeeded;
	protected int featureIndex = 0;
	protected ForgeChunkManager.Ticket[] tickets;	//tickets for keeping experiment chuncks loaded
	protected ArrayList<TutorialFeature> features= new ArrayList<TutorialFeature>();
	protected ArrayList<TutorialFeature> activeFeatures = new ArrayList<TutorialFeature>();
	
	private int[] blocks;
    private byte[] data;
	
	public enum State{
		PreInit,
		WaitingToStart,
		Initializing,
		Starting,
		Running,
		Ending,
		Done;
		}
	
	public State currentState;
	

	protected int tickCount = 0;
	private boolean hasGameEnded = false;
	
	
	private String stringToSend = "";

	private PrivateProperty privateProperty;
	
	/**
	 * 
	 * @param id
	 * @param options
	 * @param features
	 */
	public ExperimentTutorial(int id, TutorialOptions options, ArrayList<TutorialFeature> features, boolean genInDim8) {
		
		this.id = id;
		Vec3 pos1 = new Vec3(Math.min(options.pos.xCoord, options.size.xCoord),
				Math.min(options.pos.yCoord, options.size.yCoord),
				Math.min(options.pos.zCoord, options.size.zCoord));
		this.size = new Vec3(Math.max(options.pos.xCoord, options.size.xCoord) - pos1.xCoord,
				Math.max(options.pos.yCoord, options.size.yCoord) - pos1.yCoord,
				Math.max(options.pos.zCoord, options.size.zCoord) - pos1.zCoord);
		if(genInDim8) {
			dim = 8;
			this.posOffset = new Vec3(-pos1.xCoord + id*(size.xCoord + AREA_PADDING), 0, -pos1.zCoord);
			this.pos = new Vec3(pos1.xCoord + posOffset.xCoord, pos1.yCoord + posOffset.yCoord, pos1.zCoord + posOffset.zCoord);
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
		
		createPrivateProperties();
		
		this.features.addAll(features);
	}
	
	public ExperimentTutorial(int id, World world, ArrayList<TutorialFeature> features) {
		
		this.id = id;
		this.features.addAll(features);

	}
	
	
	public void setAreaData(int[] blocks, byte[] data) {
		this.blocks = blocks;
        this.data = data;
	}
	
	/**
	 * take in an Entity Player MP object and add JUST the player's name to the appropriate list.
	 * @param player the player to add to the list
	 * @return False if player is already in the list or could otherwise not be added; True if the player was added.
	 */
	public boolean addPlayer(EntityPlayerMP player){
		int playerCount = 0;
		for(Team team: this.scoreboard.getTeams()) {
			if(team.getPlayers().contains(player.getDisplayName())) { //check to see if the player's name 
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
	
	public void onServerTickUpdate() {
		if(this.world.isRemote)
			return;
		
		switch(currentState) {
		case WaitingToStart:
			break;
		case Starting:
			//generateArea();
			if(dim == 0) {
				if(genTick % 20 == 0) {
					for(Team team: scoreboard.getTeams()) {
						for(String player: team.getPlayers()) {
							EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
							playerEntity.addChatMessage(new ChatComponentText("\u00A7aGenerating..."));
						}
					}
				}
				if(this.generateArea()) {
					currentState = State.PreInit;
				}
				genTick++;
//				for(Team team: scoreboard.getTeams()) {
//					for(String player: team.getPlayers()) {
//						EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
//						playerEntity.addChatMessage(new ChatComponentText("\u00A7aRunning in Dev mode, not generating"));
//					}
//				}
//				currentState = State.PreInit;
			}else {
				if(genTick % 20 == 0) {
					for(Team team: scoreboard.getTeams()) {
						for(String player: team.getPlayers()) {
							EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
							playerEntity.addChatMessage(new ChatComponentText("\u00A7aGenerating..."));
						}
					}
				}
				if(this.generateArea()) {
					currentState = State.PreInit;
				}
				genTick++;
			}
			break;
		case PreInit:
			for(TutorialFeature feature: features){
				feature.preInit(this);
			}
			TutorialManager.INSTANCE.sendTutorialFeatures(this.id);
			currentState = State.Running;
			break;
		case Running:
			for(int x = 0; x < activeFeatures.size(); x++){	//cycle through active features
				activeFeatures.get(x).onServerTickUpdate(this);
				if(activeFeatures.get(x).isDirty) {	//check if feature need to be updated on client side
					System.out.println("[Server] Sending Feature update");
					activeFeatures.get(x).isDirty = false;
					TutorialManager.INSTANCE.sendFeatureUpdate(this.id, x, activeFeatures.get(x), false);
				}
				if(activeFeatures.get(x).isDone()) {	//if the feature is complete, remove it from active features
					activeFeatures.remove(activeFeatures.get(x));
					TutorialManager.INSTANCE.sendTutorialActiveFeatures(this.id);
				}
			}
			if(activeFeatures.isEmpty() && featureIndex == features.size())
				currentState = State.Ending;	//if active features is empty, and we've gone through all features, the experiment is over
			else if (featureIndex < features.size()) {	//if we've added the last feature, we don't need to run this anymore
				boolean addNext = true;
				for(TutorialFeature feature: activeFeatures) {
					if(!feature.canProceed)
						addNext = false;
				}
				if(addNext) {
					features.get(featureIndex).init();
					activeFeatures.add(features.get(featureIndex++));
					TutorialManager.INSTANCE.sendTutorialActiveFeatures(this.id);
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
		//We shouldn't have to check experiment state on client side, Just need to run all active features
		if(activeFeatures.isEmpty()) {	//active features is empty, so experiment must be over.
			this.currentState = State.Done;
			TutorialManager.INSTANCE.clientCurrentExperiment = -1;
		}else {
			TutorialManager.INSTANCE.clientCurrentExperiment = this.id;
			this.currentState = State.Running;
			for(int x = 0; x < activeFeatures.size(); x++){	//cycle through active features
				activeFeatures.get(x).onPlayerTickUpdate(this);
				if(activeFeatures.get(x).isDirty) {	//check if feature need to be updated on client side
					System.out.println("[Server] Sending Feature update");
					activeFeatures.get(x).isDirty = false;
					TutorialManager.INSTANCE.sendFeatureUpdate(this.id, x, activeFeatures.get(x), true);
				}
			}
		}
	}
	
	
	/**
	 * Set the state to done and remove players from the scoreboard, effectively removing all players.
	 */
	public void stop() {
		this.currentState = State.Done;
		this.scoreboard.clearPlayers();
		for(Ticket tkt : this.tickets) {
			ForgeChunkManager.releaseTicket(tkt);
		}
		
		//ExperimentManager.INSTANCE.sendExperimentUpdates();
		//this.scoreboard = null; //TODO: does this need to be null?
	}
	
	
	public void render(Entity entity){
		if(activeFeatures == null)
			return;
		for(TutorialFeature feature: activeFeatures){
			feature.render(entity);
		}
	}
	
	public void renderScreen(Entity entity){
		if(activeFeatures == null)
			return;
		for(TutorialFeature feature: activeFeatures){
			feature.renderScreen(entity);
		}
	}
	
	public void updateFeatures(ArrayList<TutorialFeature> features) {
		this.features = features;
	}
	
	public void updateActiveFeatures(ArrayList<TutorialFeature> activefeatures) {
		this.activeFeatures = activefeatures;
	}
	
	public boolean isPlayerInExperiment(String playerName){
		for(Team team: this.scoreboard.getTeams()) {
			for(String player: team.getPlayers()){
				if(player.equalsIgnoreCase(playerName))
					return true;
			}
		}
		return false;
	}
	
	
	protected void initArea() {
		int sizeX = (int) Math.ceil(Math.abs(size.xCoord - pos.xCoord));
		int sizeZ = (int) Math.ceil(Math.abs(size.zCoord - pos.zCoord));
		tickets = new ForgeChunkManager.Ticket[sizeX*sizeZ];
		
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
		final int maxBlocksPerTick = 65536;
				
		//number of "lengths" to generate per tick (max X blocks), iterating through at least 1 X per tick, in case the height and width are really big.
		//we don't want the game to lag too much.
		final int maxXPerTick = (int)(Math.max(Math.floor((float)maxBlocksPerTick/(size.yCoord*size.zCoord)),1.0));
		
		//the position to begin counting in the blocks[] array.
		int count=(genTick*maxXPerTick)*((int)size.yCoord+1)*((int)size.zCoord+1);
		
		if(count >= blocks.length) { //we've generated all blocks already! or We don't need to generate the next area TODO: remove this.id > 1
			return true; 
		}
		
		System.out.println("GenPos:" + pos.xCoord + "," + pos.yCoord + "," + pos.zCoord);

		//still have blocks in the blocks[] array we need to add to the world
		for(int x = (genTick*maxXPerTick); x < (genTick*maxXPerTick)+ maxXPerTick; x++){
			for(int y = 0; y<=(int)size.yCoord; y++){
				for(int z = 0; z<=(int)size.zCoord; z++){
					if(count>=blocks.length) { //in case the array isn't perfectly square (i.e. rectangular area was selected)
						return false;
					}
					int curblock = (int)blocks[count];
					
					if(curblock == 0 || curblock == 76) {
						if(!world.isAirBlock(new BlockPos(x + (int)pos.xCoord, y + (int)pos.yCoord ,z + (int)pos.zCoord)))
							world.setBlockToAir(new BlockPos(x + (int)pos.xCoord, y + (int)pos.yCoord ,z + (int)pos.zCoord));
						count++;
						continue;
					}
					else if(curblock == 759) {
						count++;
						continue; //these are Gas Lamps - we don't care for these.
//					}else if(curblock == 849) { //Polycrafting Tables (experiments!)
//						world.setBlock(x + (int)pos.xCoord, y + (int)pos.yCoord , z + (int)pos.zCoord, Block.getBlockById(curblock), data[count], 2);
//						PolycraftInventoryBlock pbi = (PolycraftInventoryBlock) world.getBlock(x + (int)pos.xCoord, y + (int)pos.yCoord , z + (int)pos.zCoord);
//						ItemStack item = new ItemStack(Block.getBlockById((int)blocks[count]));
//						pbi.onBlockPlacedBy(world, x + (int)pos.xCoord, y + (int)pos.yCoord, z + (int)pos.zCoord, dummy, new ItemStack(Block.getBlockById((int)blocks[count])));
//						count++;
					}else {
						world.setBlockState(new BlockPos(x + (int)pos.xCoord, y + (int)pos.yCoord ,z + (int)pos.zCoord), Block.getBlockById(curblock).getDefaultState(), 3);
						count++;
					}
				}
			}
		}
		return false;
	}

	
	public Collection<EntityPlayer> getEntityPlayersInExperiment() {
		return scoreboard.getPlayersAsEntity();
	}
	
	public Collection<String> getPlayersInExperiment() {
		return scoreboard.getPlayers();
	}
	
	private void createPrivateProperties() {
		if(!this.world.isRemote) {
			int endX = 0, endZ = 0;
			for(int x = (int)pos.xCoord - 8; Math.abs(x) <= Math.abs(pos.xCoord + size.xCoord) + 8; x += 16) {
				for(int z = (int)pos.zCoord - 8; Math.abs(z) <= Math.abs(pos.zCoord + size.zCoord) + 8; z += 16) {
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
					new Chunk(Math.min((int)pos.xCoord, endX) >> 4, Math.min((int)pos.zCoord, endZ) >> 4),
					new Chunk(Math.max((int)pos.xCoord, endX) >> 4, Math.max((int)pos.zCoord, endZ) >> 4),
					new int[] {0,3,4,5,6,7,32,44},
					8);
			
			ServerEnforcer.addExpPrivateProperty(pp);	
			this.privateProperty=pp;	
			ServerEnforcer.INSTANCE.sendExpPPDataPackets();
			System.out.println("x: " + ((int)pos.xCoord >> 4) + "::" + (endX >> 4) + "|| z: " + ((int)pos.zCoord >> 4) + "::" + (endZ >> 4));
		}
	}
}
