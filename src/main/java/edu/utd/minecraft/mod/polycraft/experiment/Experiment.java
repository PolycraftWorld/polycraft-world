package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.Collection;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.scoreboards.CustomScoreboard;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public abstract class Experiment {

	public final int size; 	//total size of experiment area size chunks by size chunks
	public final int id;	//id of the experiment. Should be unique
	public final int xPos;	//starting xPos of experiment area
	public final int yPos;	//starting yPos of experiment area
	public final int zPos;	//starting zPos of experiment area
	public final World world;
	protected CustomScoreboard scoreboard;
	protected final static Collection<EntityPlayerMP> players = Lists
			.newLinkedList();	//List of players participating in experiment instance
	protected int playersNeeded = 1;
	public enum State{
		PreInit,
		Initializing, 
		WaitingToStart,
		Starting,
		Running,
		Ending,
		Done;
		}
	public State currentState;
	
	/**
	 * Creates the experimentation zone.
	 * @param id 	id of the experiment that is indexed in experiment manager
	 * @param size 	square size (in chunks) of the experimentation zone
	 * @param xPos 	One corner (not sure which one, I'm guessing it's the lowest xPos value, as blocks get filled from XPos to XPos + 16*size.
	 * @param zPos 	Other Corner (lowest Zpos, as zpos gets incremented z++).
	 * @param world reference to the world.
	 */
	public Experiment(int id, int size, int xPos, int zPos, World world){
		this.id = id;
		this.size = size;
		this.xPos = xPos;
		this.yPos = 90;
		this.zPos = zPos;
		this.world = world;
		this.currentState = State.PreInit;
	}
	
	public boolean addPlayer(EntityPlayerMP player){
		if (players.contains(player)){
			return false;
		}else{
			players.add(player);
			if(players.size() == playersNeeded){
				start();
			}
			return true;
		}
	}
	
	public void init(){
		generateArea(xPos, yPos, zPos, world);
		currentState = State.WaitingToStart;
		
	}
	
	protected void generateArea(int xPos, int yPos, int zPos, World world){
		Block bedrock = Block.getBlockFromName("bedrock");
		Block dirt = Block.getBlockFromName("dirt");
		Block grass = Block.getBlockFromName("grass");
		boolean result = false;
		for(int x = xPos; x < xPos + 16*size; x++){
			for(int z = zPos; z < zPos + 16*size; z++){
				if(posIsWall(x, z)){
					for(int i = -3; i < 12; i++){
						world.setBlock(x, yPos + i, z, bedrock, 0, 3);
					}
				}else{
					world.setBlock(x, yPos - 4, z, bedrock, 0, 3);
					world.setBlock(x, yPos - 3, z, dirt, 0, 3);
					world.setBlock(x, yPos - 2, z, dirt, 0, 3);
					world.setBlock(x, yPos - 1, z, dirt, 0, 3);
					world.setBlock(x, yPos, z, grass, 0, 3);
				}
			}
		}
	}
	
	protected void generateSpectatorBox(int xPos, int yPos, int zPos, World world){
		Block glass = Block.getBlockFromName("stained_glass");
		boolean result = false;
		int y = yPos +30;
		for(int x = xPos; x < xPos + 16*size; x++){
			for(int z = zPos; z < zPos + 16*size; z++){
				if(posIsWall(x, z)){
					for(int i = -18; i < 6; i++){
						world.setBlock(x, y + i, z, glass, 0, 7);
					}
				}else{
					world.setBlock(x, y, z, glass, 0, 7);
					world.setBlock(x, y + 6, z, glass, 0, 7);
				}
			}
		}
	}
	
	private boolean posIsWall(int x, int z){
		if(x==xPos||z==zPos||x==xPos + (16*size)-1||z==zPos + (16*size)-1){
			return true;
		}else{
			return false;
		}
	}
	
	//Challenge Starts. Should run some time after init
	public void start(){
		
	}
	
	//Main update function for Experiments
	public void onServerTickUpdate(){
		
	}
	
	//Main update function for client sided events in Experiments
	public void onClientTickUpdate(){
		
	}
	
	public boolean isPlayerInExperiment(String playerName){
		for(EntityPlayerMP player: players){
			if(player.getDisplayName().equalsIgnoreCase(playerName))
				return true;
		}
		return false;
	}
	
	public void render(Entity entity){
		
	}
	
	public int[] getSpectatorLocation(){
		return new int[]{xPos + (size*8), yPos + 33, zPos + (size*8)};
	}
}
