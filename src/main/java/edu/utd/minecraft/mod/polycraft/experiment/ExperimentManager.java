package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.Hashtable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ExperimentManager {

	public static ExperimentManager INSTANCE = new ExperimentManager();
	private static int nextAvailableExperimentID = 1; 	//one indexed
	private static Hashtable<Integer, Experiment> experiments = new Hashtable<Integer, Experiment>();
	private static Hashtable<Integer, Class<? extends Experiment>> experimentTypes = new Hashtable<Integer, Class <? extends Experiment>>();

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
				ex.onTickUpdate();
			}
		}
	}
	
	public boolean addPlayerToExperiment(int expID, EntityPlayerMP player){
		return experiments.get(expID).addPlayer(player);
	}
	
	public static void UpdatePackets(String experimentJson,int id){
		Gson gson = new Gson();
		TypeToken<?> typeToken = TypeToken.get(ExperimentManager.class);
		ExperimentManager temp = gson.fromJson(experimentJson, typeToken.getType());
		
		INSTANCE=temp;
	}
	
	public void start(int expID){
		experiments.get(expID).start();
	}
	
	public void stop(){
		
	}
	
	public void reset(){
		
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
