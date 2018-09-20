package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.Collection;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public abstract class Experiment {

	public final int size; 	//total size of experiment area size chunks by size chunks
	public final int id;	//id of the experiment. Should be unique
	public final int xPos;	//starting xPos of experiment area
	public final int yPos;	//starting yPos of experiment area
	public final int zPos;	//starting zPos of experiment area
	public final World world;
	protected final static Collection<EntityPlayerMP> players = Lists
			.newLinkedList();	//List of players participating in experiment instance
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
	public void onTickUpdate(){
		
	}
	
}
