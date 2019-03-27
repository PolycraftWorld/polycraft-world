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
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.experiment.feature.ExperimentFeature;
import edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureBase;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.CustomScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.ScoreboardManager;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
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
	public int xPos;	//starting xPos of experiment area
	public int yPos;	//starting yPos of experiment area
	public int zPos;	//starting zPos of experiment area
	//protected static int[][] spawnlocations = new int[4][3];	//spawn locations [location][x,y,z]
	World world;
	public CustomScoreboard scoreboard;
	//TODO: move these values into the ExperimentCTB class and also move their setter functions
	protected int teamsNeeded = 2;
	protected int teamSize = 2;
	protected int playersNeeded = teamsNeeded*teamSize;
	protected int awaitingNumPlayers = playersNeeded;
	protected int featureIndex = 0;
	protected ArrayList<TutorialFeature> features= new ArrayList<TutorialFeature>();
	protected ArrayList<TutorialFeature> activeFeatures = new ArrayList<TutorialFeature>();
	
	
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
	 * @param xPos
	 * @param zPos
	 * @param world
	 * @param maxteams
	 * @param teamsize
	 */
	public ExperimentTutorial(int id, World world, TutorialOptions options, ArrayList<TutorialFeature> features) {
		
		this.id = id;
		this.xPos = (int) options.pos.xCoord;
		this.yPos = 16;
		this.zPos = (int) options.pos.zCoord;
		this.currentState = State.PreInit;
		this.world = world;
		this.teamsNeeded = options.numTeams;
		this.teamSize = options.teamSize;
		this.playersNeeded = teamsNeeded * teamSize;
		this.awaitingNumPlayers = this.playersNeeded;
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard();
		for(int x = 0; x < teamsNeeded;x++) {
			this.scoreboard.addNewTeam();
			this.scoreboard.resetScores(0);
		}
		
		this.features.addAll(features);
		
		int y = yPos + 8;
		int x_offset = 31;
	}
	
	public ExperimentTutorial(int id, World world, ArrayList<TutorialFeature> features) {
		
		this.id = id;
		this.features.addAll(features);

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
					currentState = State.PreInit;
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
			break;
		case PreInit:
			for(TutorialFeature feature: features){
				feature.preInit();
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
					TutorialManager.INSTANCE.sendFeatureUpdate(this.id, x, activeFeatures.get(x));
				}
				if(activeFeatures.get(x).isDone()) {	//if the feature is complete, remove it from active features
					activeFeatures.remove(activeFeatures.get(x));
					TutorialManager.INSTANCE.sendTutorialActiveFeatures(this.id);
				}
			}
			break;
		case Ending:
			break;
		case Done:
			break;
		default:
			break;
		
		}
	}
	

	public void onClientTickUpdate(){
		if(currentState == State.Starting){
			for(TutorialFeature feature: activeFeatures){
				feature.onPlayerTickUpdate(this);
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

}
