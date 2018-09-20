package edu.utd.minecraft.mod.polycraft.minigame;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class PolycraftMinigame {
	
	public boolean active=false;
	
	PolycraftMinigame()
	{
		
	}
	
	public PolycraftMinigame(int id) {
		// TODO Auto-generated constructor stub
	}
	
	public void init()
	{
		
	}

	public void start(World world, int[] args, String envoker) {
		// TODO Auto-generated method stub
		
	}
	
	public void stop()
	{
		
	}
	
	public void onUpdate()
	{
		
	}
	
	//rename ? ClientMinigameSync or better.
	public boolean shouldUpdatePackets()//true if is on tick that needs to update packets
	{
		return false;
	}
	
	public void endCase()
	{
		
	}
	
	public void timer()//use to sync with clients
	{
		
	}
	
	public void queue()//Queue system/lobby for players entering minigame n
	{					//needs to be able to add and remove players from queue; + detect disconnections
		
	}
	
	public void freeze()//Streach goal , freeze minigame
	{
		
	}
	
	public Type getType()
	{
		Type t = new TypeToken<PolycraftMinigame>() {}.getType();
		return t;
	}
	
	public double getDouble() // this is not correct. we need a system to Get specific Minigame Information.
	{
		return 0;
	}

	public void UpdatePackets(String PolycraftMinigameJson) {
		Gson gson = new Gson();
		Type typeOfPolycraftMinigame = new TypeToken<PolycraftMinigame>() {}.getType();
		PolycraftMinigame temp = gson.fromJson(PolycraftMinigameJson, typeOfPolycraftMinigame);
		
		PolycraftMinigameManager.INSTANCE=temp;
	}

	public void onPlayerTick(PlayerTickEvent tick) {
		// TODO Auto-generated method stub
		
	}

	public void onServerTick(ServerTickEvent tick) {
		// TODO Auto-generated method stub
		
	}

	public void render(Entity entity) {
		// TODO Auto-generated method stub
		
	}


	
	
}
