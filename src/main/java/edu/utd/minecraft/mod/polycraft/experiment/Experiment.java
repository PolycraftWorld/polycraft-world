package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.Collection;
import java.util.Random;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FueledLampInventory;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.GaslampInventory;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.CustomScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.ResearchAssistantLabGenerator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import scala.util.control.Exception.By;

public abstract class Experiment {

	public final int size; 	//total size of experiment area size chunks by size chunks
	public final int id;	//id of the experiment. Should be unique
	public final int xPos;	//starting xPos of experiment area
	public final int yPos;	//starting yPos of experiment area
	public final int zPos;	//starting zPos of experiment area
	public final World world;
	protected int[][] spawnlocations = new int[4][3];	//spawn locations [location][x,y,z]
	protected CustomScoreboard scoreboard;
	//TODO: move these values into the ExperimentCTB class and also move their setter functions
	protected int teamsNeeded = 2;
	protected int teamSize = 2;
	protected int playersNeeded = teamsNeeded*teamSize;
	protected int awaitingNumPlayers = playersNeeded;
	protected int genTick = 0;
	private Random random;
	ResearchAssistantEntity dummy;
	
	
	public enum State{
		PreInit,
		Initializing, 
		WaitingToStart,
		GeneratingArea,
		Starting,
		Running,
		Halftime,
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
		//this.size = size;
		this.size = (int)Math.ceil((float)ExperimentManager.INSTANCE.stoop.width/16.0);
		this.xPos = xPos;
		this.yPos = 40;
		this.zPos = zPos;
		this.world = world;
		this.currentState = State.PreInit;
		random = new Random();
		dummy = new ResearchAssistantEntity(world, true);
		
		
	}
	

	/**
	 * Removes a player from a team, if the player exists
	 * @param player the EntityPlayerMP to be removed
	 * @return true if the player existed and was removed. False if the player was not on a team.
	 */
	public boolean removePlayer(EntityPlayerMP player) {
		try {
			for(Team team: this.scoreboard.getTeams()) {
				if(team.getPlayers().remove(player.getDisplayName())) {
					awaitingNumPlayers++;
					return true;
				}
				
			}
			return false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Removes a player from a team, if the player exists (visible to the Package only)
	 * @param player the displayname of the player to be removed
	 * @return true if the player existed and was removed. False if the player was not on a team.
	 */
	boolean removePlayer(String player) {
		try {
			for(Team team: this.scoreboard.getTeams()) {
				if(team.getPlayers().remove(player)) {
					awaitingNumPlayers++;
					return true;
				}
				
			}
			return false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
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
					start();
				}
				return true;
			}
		}
		return false;
	}
	
	public void init(){
		System.out.println("CurrentState: " + currentState);
		currentState = State.WaitingToStart;
	}
	
	/**
	 * Generates the stoop approximately 1 chunk at a time. This function handles determining the size it needs
	 * This function assumes that the schematic file in question was generated by {@inheritDoc commands.dev.CommandDev.java}
	 * That means the block and data array are generated by the nested for loops: length (x), height (y), width (z), in that order.
	 * @return True if its done generating, False if it's still in progress
	 */
	protected boolean generateStoop() {
		//int x = this.xPos;
		//int z = this.zPos;
		//int y = this.yPos;
		
		final int maxBlocksPerTick = 65536;
		
		
		Schematic sh = ExperimentManager.INSTANCE.stoop;
		
		//number of "lengths" to generate per tick (max X blocks), iterating through at least 1 X per tick, in case the height and width are really big.
		//we don't want the game to lag too much.
		final int maxXPerTick = (int)(Math.max(Math.floor((float)maxBlocksPerTick/(sh.height*sh.width)),1.0));
		
		//the xPosition to begin at
		//int xCounter = Math.floorDiv(genTick, sh.length/maxXPerTick);
		//int zChunk = genTick%sh.height;
		
		
		System.out.println(String.format("Stoop Length, Height, & Width: %d %d %d", sh.length, sh.height, sh.width));
		//System.out.println(String.format("XChunk and zChunk: %d %d", xChunk, zChunk));
		
		//the position to begin counting in the blocks[] array.
		int count=(genTick*maxXPerTick)*sh.height*sh.width;
		
		System.out.println(String.format("Generating Stoop: blockCount: %d, genTick: %d", count, genTick));
		
		if(count >= sh.blocks.length) { //we've generated all blocks already!
//			System.out.println("Setting tile entities...: " + sh.tileentities.tagCount());
//			for (int k = 0; k < (int)sh.tileentities.tagCount(); k++)
//			{
//				NBTTagCompound nbt = sh.tileentities.getCompoundTagAt(k);
//				//TileEntity tile = world.getTileEntity(nbt.getInteger("x")+this.xPos, nbt.getInteger("y")+this.yPos, nbt.getInteger("z")+this.zPos);
//				//tile.readFromNBT(nbt);
//				//world.setTileEntity(nbt.getInteger("x")+this.xPos, nbt.getInteger("y")+this.yPos, nbt.getInteger("z")+this.zPos, tile);
//			}
			return true; 
		}

		//still have blocks in the blocks[] array we need to add to the world
		for(int x = (genTick*maxXPerTick); x < (genTick*maxXPerTick)+ maxXPerTick; x++){
			for(int y = 0; y<(int)sh.height; y++){
				for(int z = 0; z<(int)sh.width; z++){
					if(count>=sh.blocks.length) { //in case the array isn't perfectly square, but I'm not exactly sure why this is a problem lol...
						return false;
					}
					int curblock = (int)sh.blocks[count];
					
					//world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);

					
					if(curblock == 759) {
						//System.out.println("Why");										
						//world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(0), 0, 2);
//						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById((int)sh.blocks[count]), sh.data[count], 2);
//						ResearchAssistantEntity dummy = new ResearchAssistantEntity(world, true);
//						PolycraftInventoryBlock pbi = (PolycraftInventoryBlock) world.getBlock(x + this.xPos, y + this.yPos , z + this.zPos);
//						System.out.println(String.format("Found a tile entity & xyz: %s %d %d %d", pbi.getUnlocalizedName(), x + this.xPos,  y + this.yPos , z + this.zPos));
//						//System.out.println("Coordinates: ");
//						ItemStack item = new ItemStack(Block.getBlockById((int)sh.blocks[count]));
//						pbi.onBlockPlacedBy(world, x + this.xPos, y + this.yPos, z + this.zPos, dummy, new ItemStack(Block.getBlockById((int)sh.blocks[count])));
//						
//						FueledLampInventory lightInv = (FueledLampInventory) pbi.getInventory(world, x + this.xPos, y + this.yPos, z + this.zPos);
//						lightInv.setInventorySlotContents(0,
//								new ItemStack(random.nextFloat() > 0.5 ? ResearchAssistantLabGenerator.BUTANOL : ResearchAssistantLabGenerator.ETHANOL, 8 + random.nextInt(3)));
//
//						
						
						//Block dummyPlacesBlock = Block.getBlockById((int)sh.blocks[count]);
						//TileEntity te = new TileEntity(dummyPlacesBlock);
						//world.setTileEntity(p_147455_1_, p_147455_2_, p_147455_3_, p_147455_4_);
					}else if(curblock == 754) {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(89), 0, 2);
//						PolycraftInventoryBlock pbi = (PolycraftInventoryBlock) world.getBlock(x + this.xPos, y + this.yPos , z + this.zPos);;
//						System.out.println(String.format("Found a tile entity & xyz: %s %d %d %d", pbi.getUnlocalizedName(), x + this.xPos,  y + this.yPos , z + this.zPos));
//						//System.out.println("Coordinates: ");
//						ItemStack item = new ItemStack(Block.getBlockById((int)sh.blocks[count]));
//						pbi.onBlockPlacedBy(world, x + this.xPos, y + this.yPos, z + this.zPos, dummy, new ItemStack(Block.getBlockById((int)sh.blocks[count])));
//						
//						FueledLampInventory lightInv = (FueledLampInventory) pbi.getInventory(world, x + this.xPos, y + this.yPos, z + this.zPos);
//						lightInv.setInventorySlotContents(0,
//								new ItemStack(random.nextFloat() > 0.5 ? ResearchAssistantLabGenerator.BUTANOL : ResearchAssistantLabGenerator.ETHANOL, 8 + random.nextInt(3)));
//
//						
//					}
					
					}else if(curblock == 19){
						for(int i = 0; i < spawnlocations.length; i++) {
							if(spawnlocations[i][1] == 0){	// if the y value is zero, it hasn't been defined yet
								spawnlocations[i][0] = x + this.xPos;
								spawnlocations[i][1] = x + this.yPos + 2; //add two because we hide the block underground
								spawnlocations[i][2] = x + this.zPos;
								i = spawnlocations.length; 	//exit for loop
							}
						}
					}else {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
					}
					
					count++;
				}
			}
		}
		
		return false;
		
		
	}
	
	protected void generateArea(){
		Block bedrock = Block.getBlockFromName("bedrock");
		Block dirt = Block.getBlockFromName("dirt");
		Block grass = Block.getBlockFromName("grass");
		int xChunk = Math.floorDiv(genTick,size);
		int zChunk = genTick%size;
		for(int x = (xChunk*16)+xPos; x < (xChunk*16)+xPos + 16; x++){
			for(int z = (zChunk*16)+zPos; z < (zChunk*16)+zPos + 16; z++){
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
	
	protected void generateSpectatorBox(){
		Block glass = Block.getBlockFromName("stained_glass");
		boolean result = false;
		int y = yPos +30;
		int xChunk = Math.floorDiv(genTick,size);
		int zChunk = genTick%size;
		for(int x = (xChunk*16)+xPos; x < (xChunk*16)+xPos + 16; x++){
			for(int z = (zChunk*16)+zPos; z < (zChunk*16)+zPos + 16; z++){
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
	/**
	 * Sends update packets to clients indicating that this experiment has begun
	 * This needs to be overridden in experiment subclasses to actually initialize the experiment
	 * override using super.start() 
	 */
	public void start(){
		ExperimentManager.metadata.get(this.id-1).deactivate(); //prevents this experiment from showing up on the list.
		ExperimentManager.sendExperimentUpdates();
		//todo: Override this 
	}
	
	/**
	 * Set the state to done and remove players from the scoreboard, effectively removing all players.
	 */
	public void stop() {
		this.currentState = State.Done;
		this.scoreboard.clearPlayers();
		//ExperimentManager.INSTANCE.sendExperimentUpdates();
		//this.scoreboard = null; //TODO: does this need to be null?
	}
	
	//Main update function for Experiments
	public void onServerTickUpdate(){
		
	}
	
	//Main update function for client sided events in Experiments
	public void onClientTickUpdate(){
		
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
	
	public void render(Entity entity){
		
	}
	/**
	 * Maximum number of players that can be in this experiment
	 * @return Max Players. Used by the {@link ExperimentManager} for display in {@link GUIExperimentList}
	 */
	public int getMaxPlayers() {
		return playersNeeded;
	}
	
	public int getNumPlayersAwaiting() {
		return awaitingNumPlayers;
	}
	
	public int[] getSpectatorLocation(){
		return new int[]{xPos + (size*8), yPos + 33, zPos + (size*8)};
	}
	
	/**
	 * Debug parameters used by command functions right now
	 * In the future, this should be pulled from the experiment dashboard on our polycraftworld.com website
	 * @param num of teams needed
	 */
	public void setTeamsNeeded(int num) {
		teamsNeeded=num;
		this.playersNeeded = teamsNeeded*teamSize;
	}
	
	/**
	 * Same as above. Teams are always filled on a first-come, first-serve basis, sequentially (team 1, then team 2, etc.)
	 * @param num max players per team.
	 */
	public void setTeamSize(int num) {
		teamSize = num;
		this.playersNeeded = teamsNeeded*teamSize;
		//this.awaitingNumPlayers = playersNeeded;
	}
	
}
