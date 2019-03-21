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
	public final int xPos;	//starting xPos of experiment area
	public final int yPos;	//starting yPos of experiment area
	public final int zPos;	//starting zPos of experiment area
	public final World world;
	//protected static int[][] spawnlocations = new int[4][3];	//spawn locations [location][x,y,z]
	public CustomScoreboard scoreboard;
	//TODO: move these values into the ExperimentCTB class and also move their setter functions
	protected int teamsNeeded = 2;
	protected int teamSize = 2;
	protected int playersNeeded = teamsNeeded*teamSize;
	protected int awaitingNumPlayers = playersNeeded;
	protected ArrayList<TutorialFeature> expFeatures;
	
	
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
	

	protected ArrayList<TutorialFeature> features= new ArrayList<TutorialFeature>();
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
	public ExperimentTutorial(int id, int xPos, int zPos, World world, int maxteams, int teamsize) {
		
		this.id = id;
		this.xPos = xPos;
		this.yPos = 16;
		this.zPos = zPos;
		this.world = world;
		this.currentState = State.PreInit;
		
		this.teamsNeeded = maxteams;
		this.teamSize = teamsize;
		this.playersNeeded = teamsNeeded * teamSize;
		this.awaitingNumPlayers = this.playersNeeded;
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard();
		for(int x = 0; x < teamsNeeded;x++) {
			this.scoreboard.addNewTeam();
			this.scoreboard.resetScores(0);
		}
		
		int y = yPos + 8;
		int x_offset = 31;
	}
	
	public void onServerTickUpdate() {
		
		switch(currentState) {
		case WaitingToStart:
			break;
		case Starting:
			break;
		case PreInit:
			for(TutorialFeature feature: features){
				feature.preInit();
			}
			break;
		case Running:
			for(TutorialFeature feature: features){
				feature.onServerTickUpdate();
			}
			break;
		case Ending:
			break;
		case Done:
			break;
		default:
			break;
		
		}
		if(currentState == State.Running){
			for(TutorialFeature feature: features){
				feature.onServerTickUpdate();
			}
		}
	}
	

	public void onClientTickUpdate(){
		if(currentState == State.Starting){
			for(TutorialFeature feature: features){
				feature.onPlayerTickUpdate();
			}
		}	
	}
	
	
	public void render(Entity entity){
		for(TutorialFeature feature: features){
			feature.render(entity);
		}
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
