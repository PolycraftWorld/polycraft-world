package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.Hashtable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ExperimentManager {

	public static ExperimentManager INSTANCE = new ExperimentManager();
	private static Hashtable<Integer, Experiment> experiments = new Hashtable<Integer, Experiment>();
	private static Hashtable<Integer, Class<? extends Experiment>> experimentTypes = new Hashtable<Integer, Class <? extends Experiment>>();

	//initialize new experiments before players join
	public static void init(){					
		World world = DimensionManager.getWorld(8);
		for(Experiment ex: experiments.values()){
			ex.init();
		}
	}
	
	@SubscribeEvent
	public void onWorldTick(final TickEvent.WorldTickEvent event){
		for(Experiment ex: experiments.values()){
			ex.onTickUpdate();
		}
	}
	
	public boolean addPlayerToExperiment(int expID, EntityPlayerMP player){
		return experiments.get(expID).addPlayer(player);
	}
	
	public static void UpdatePackets(String experimentJson,int id){
//		Gson gson = new Gson();
//		TypeToken<?> typeToken = TypeToken.get(experiments.get(id));
//		ExperimentManager temp = gson.fromJson(experimentJson, typeToken.getType());
//		
//		INSTANCE=temp;
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
	
	//Queue system/lobby for players entering experiment n
	//needs to be able to add and remove players from queue; + detect disconnections
	public void queue(){
		
	}
	
	//I need things.
	public int[] playerAttemptToConnect(EntityPlayerMP player, int experimentId) {
		//check if experimentId is valid
		//check if experimentId can take in player
		//check if any other issues exist. 
		
		//return an integer (or maybe enum?) that will display to the client the error/success message. Maybe it could also just return a string...
		
		return new int[]{255, 60, 255};
	}
	
	
	 
	public static void registerExperiment(int id, Experiment ex)
	{
		if (experiments.containsKey(id))
		{
			//you can just call put? 
		    throw new IllegalArgumentException(String.format("Failed to register experiment for id %d, One is already registered", id));
		}
		experiments.put(id, ex);
	}
	public static void registerExperimentTypes()
	{
		experimentTypes.put(1, ExperimentCTB.class);
	}
}
