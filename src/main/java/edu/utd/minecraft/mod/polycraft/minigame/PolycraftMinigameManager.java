package edu.utd.minecraft.mod.polycraft.minigame;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import net.minecraft.world.WorldProvider;

public class PolycraftMinigameManager {
	
	public static PolycraftMinigame INSTANCE;
	public static int MINIGAME_ID=0;
	private static Hashtable<Integer, Class<? extends PolycraftMinigame>> minigames = new Hashtable<Integer, Class<? extends PolycraftMinigame>>();

	
	public static void init()//May use to set up minigame instance
	{					//use server arguments for which minigame is being ran on server?
						//only one minigame will be ran on one server!!!
		
		if (System.getProperty("Minigame") != null) {	//checks to see if this is a minigame server
			MINIGAME_ID=Integer.parseInt(System.getProperty("Minigame"));
			if(minigames.containsKey(MINIGAME_ID))	//checks to see if this minigame is registered 
			{
				Class<? extends PolycraftMinigame> mini= minigames.get(MINIGAME_ID);
				
				try {
					INSTANCE=mini.newInstance();
					INSTANCE.init();//?
					
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out
					.println("***************************** Minigame Initialized ***************************** ");
			//System.exit(0);
		}
		
		
	}
	
	public void onUpdate()
	{
		
	}
	
	public void onTickUpdate()
	{
		
	}
	
	public static void UpdatePackets(String PolycraftMinigameJson,int id)
	{
		Gson gson = new Gson();
		TypeToken<?> typeToken = TypeToken.get(minigames.get(id));
		PolycraftMinigame temp = gson.fromJson(PolycraftMinigameJson, typeToken.getType());
		
		INSTANCE=temp;
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public void reset()
	{
		
	}
	
	public void timer()//Use to sync with minigame clients
	{
		
	}
	
	public void queue()//Queue system/lobby for players entering minigame n
	{					//needs to be able to add and remove players from queue; + detect disconnections
		
	}
	
	 public static boolean isMinigameRegistered(int id)
	 {
	        return minigames.containsKey(id);
	 }
	 
	 
	 public static void registerMinigame(int id, Class c)
	 {
	    if (minigames.containsKey(id))
	    {
	        throw new IllegalArgumentException(String.format("Failed to register minigame for id %d, One is already registered", id));
	    }
	    minigames.put(id, c);
	 }


}
