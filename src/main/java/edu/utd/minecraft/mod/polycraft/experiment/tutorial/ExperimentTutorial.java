package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeChunkManager;

public class ExperimentTutorial{
	
	public final int id;	//id of the experiment. Should be unique
	public Vec3 pos1;	//starting Pos of experiment area
	public Vec3 pos2;
	private Vec3 genPos;
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
	protected boolean isDev = false;
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
	
	/**
	 * 
	 * @param id
	 * @param world
	 * @param options
	 * @param features
	 * @param isDev If True, will not genereate area and will not transport to dimension 8
	 */
	public ExperimentTutorial(int id, World world, TutorialOptions options, ArrayList<TutorialFeature> features, boolean isDev) {
		
		this.id = id;
		this.pos1 = options.pos;
		this.pos2 = options.size;
		this.genPos = pos1;
		this.currentState = State.WaitingToStart;
		this.world = world;
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
		
		if(isDev) {
			dim = 0;
		}else
			dim = 8;
		
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
				team.getPlayers().add(player.getDisplayName());//add player's name to the team
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
			if(genTick % 20 == 0) {
				for(Team team: scoreboard.getTeams()) {
					for(String player: team.getPlayers()) {
						EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
						playerEntity.addChatMessage(new ChatComponentText("\u00A7aGenerating..."));
					}
				}
			}
			
			//generateArea();
			
			if(this.generateArea()) {
				currentState = State.PreInit;
			}
			genTick++;
			break;
		case PreInit:
			for(TutorialFeature feature: features){
				feature.preInit(this);
			}
			TutorialManager.INSTANCE.sendTutorialFeatures(this.id);
			currentState = State.Running;
			break;
		case Running:
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
			for(int x = 0; x < activeFeatures.size(); x++){	//cycle through active features
				activeFeatures.get(x).onServerTickUpdate(this);
				if(activeFeatures.get(x).isDirty) {	//check if feature need to be updated on client side
					System.out.println("[Server] Sending Feature update");
					activeFeatures.get(x).isDirty = false;
					TutorialManager.INSTANCE.sendFeatureUpdate(this.id, x, activeFeatures.get(x), this.world);
				}
				if(activeFeatures.get(x).isDone()) {	//if the feature is complete, remove it from active features
					activeFeatures.remove(activeFeatures.get(x));
					TutorialManager.INSTANCE.sendTutorialActiveFeatures(this.id);
				}
			}
			break;
		case Ending:
			this.currentState = State.Done;
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
		}else {
			this.currentState = State.Running;
			for(int x = 0; x < activeFeatures.size(); x++){	//cycle through active features
				activeFeatures.get(x).onPlayerTickUpdate(this);
				if(activeFeatures.get(x).isDirty) {	//check if feature need to be updated on client side
					System.out.println("[Server] Sending Feature update");
					activeFeatures.get(x).isDirty = false;
					TutorialManager.INSTANCE.sendFeatureUpdate(this.id, x, activeFeatures.get(x), this.world);
				}
			}
		}
		
		
	}
	
	
	public void render(Entity entity){
		if(activeFeatures == null)
			return;
		for(TutorialFeature feature: activeFeatures){
			feature.render(entity);
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
	
	
	/**
	 * Generates the stoop approximately 1 chunk at a time. This function handles determining the size it needs
	 * This function assumes that the schematic file in question was generated by {@inheritDoc commands.dev.CommandDev.java}
	 * That means the block and data array are generated by the nested for loops: length (x), height (y), width (z), in that order.
	 * @return True if its done generating, False if it's still in progress
	 */
	protected boolean generateArea() {
		final int maxBlocksPerTick = 65536;
		
        Vec3 size = Vec3.createVectorHelper(Math.abs(Math.abs(pos1.xCoord) - Math.abs(pos2.xCoord)), 
        		Math.abs(Math.abs(pos1.yCoord) - Math.abs(pos2.yCoord)), 
        		Math.abs(Math.abs(pos1.zCoord) - Math.abs(pos2.zCoord)));
		
		//number of "lengths" to generate per tick (max X blocks), iterating through at least 1 X per tick, in case the height and width are really big.
		//we don't want the game to lag too much.
		final int maxXPerTick = (int)(Math.max(Math.floor((float)maxBlocksPerTick/(size.yCoord*size.zCoord)),1.0));
		
		//the position to begin counting in the blocks[] array.
		int count=(genTick*maxXPerTick)*((int)size.yCoord+1)*((int)size.zCoord+1);
		
		
		if(count >= blocks.length) { //we've generated all blocks already! or We don't need to generate the next area TODO: remove this.id > 1
			return true; 
		}

		//still have blocks in the blocks[] array we need to add to the world
		for(int x = (genTick*maxXPerTick); x < (genTick*maxXPerTick)+ maxXPerTick; x++){
			for(int y = 0; y<=(int)size.yCoord; y++){
				for(int z = 0; z<=(int)size.zCoord; z++){
					if(count>=blocks.length) { //in case the array isn't perfectly square (i.e. rectangular area was selected)
						return false;
					}
					int curblock = (int)blocks[count];
					
					if(curblock == 0 || curblock == 76) {
						if(!world.isAirBlock(x + (int)genPos.xCoord, y + (int)genPos.yCoord ,z + (int)genPos.zCoord))
							world.setBlockToAir(x + (int)genPos.xCoord, y + (int)genPos.yCoord ,z + (int)genPos.zCoord);
						count++;
						continue;
					}
					else if(curblock == 759) {
						count++;
						continue; //these are Gas Lamps - we don't care for these.
					}else if(curblock == 859) { //Polycrafting Tables (experiments!)
						world.setBlock(x + (int)genPos.xCoord, y + (int)genPos.yCoord , z + (int)genPos.zCoord, Block.getBlockById(curblock), data[count], 2);
						PolycraftInventoryBlock pbi = (PolycraftInventoryBlock) world.getBlock(x + (int)genPos.xCoord, y + (int)genPos.yCoord , z + (int)genPos.zCoord);
						ItemStack item = new ItemStack(Block.getBlockById((int)blocks[count]));
						pbi.onBlockPlacedBy(world, x + (int)genPos.xCoord, y + (int)genPos.yCoord, z + (int)genPos.zCoord, dummy, new ItemStack(Block.getBlockById((int)blocks[count])));
						count++;
					}else {
						world.setBlock(x + (int)genPos.xCoord, y + (int)genPos.yCoord ,z + (int)genPos.zCoord, Block.getBlockById(curblock), data[count], 2);
						count++;
					}
				}
			}
		}
		
		return false;
		
		
	}

}
