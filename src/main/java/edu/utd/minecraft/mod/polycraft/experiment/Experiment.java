package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.Collection;
import java.util.Random;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
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
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import scala.util.control.Exception.By;

public abstract class Experiment {

	public final int size; 	//total size of experiment area size chunks by size chunks
	public final int id;	//id of the experiment. Should be unique
	public final int xPos;	//starting xPos of experiment area
	public final int yPos;	//starting yPos of experiment area
	public final int zPos;	//starting zPos of experiment area
	public final World world;
	protected static int[][] spawnlocations = new int[4][3];	//spawn locations [location][x,y,z]
	protected CustomScoreboard scoreboard;
	//TODO: move these values into the ExperimentCTB class and also move their setter functions
	protected int teamsNeeded = 2;
	protected int teamSize = 2;
	protected int playersNeeded = teamsNeeded*teamSize;
	protected int awaitingNumPlayers = playersNeeded;
	protected int genTick = 0;
	protected Schematic sch;
	private Random random;
	protected ForgeChunkManager.Ticket[] tickets;
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
		this.yPos = 16;
		this.zPos = zPos;
		this.world = world;
		this.currentState = State.PreInit;
		random = new Random();
		dummy = new ResearchAssistantEntity(world, true);
		this.sch = null;
		
	}
	
	/**
	 * Creates the experimentation zone.
	 * @param id 	id of the experiment that is indexed in experiment manager
	 * @param size 	square size (in chunks) of the experimentation zone
	 * @param xPos 	One corner (not sure which one, I'm guessing it's the lowest xPos value, as blocks get filled from XPos to XPos + 16*size.
	 * @param zPos 	Other Corner (lowest Zpos, as zpos gets incremented z++).
	 * @param world reference to the world.
	 */
	public Experiment(int id, int size, int xPos, int zPos, World world, Schematic schematic){
		this.id = id;
		//this.size = size;
		this.size = (int)Math.ceil((float)schematic.width/16.0);
		this.xPos = xPos;
		this.yPos = 16;
		this.zPos = zPos;
		this.world = world;
		this.currentState = State.PreInit;
		random = new Random();
		dummy = new ResearchAssistantEntity(world, true);
		this.sch = schematic;
		
		
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

		final int maxBlocksPerTick = 65536;
		Schematic sh = ExperimentManager.INSTANCE.stoop;
		
		//number of "lengths" to generate per tick (max X blocks), iterating through at least 1 X per tick, in case the height and width are really big.
		//we don't want the game to lag too much.
		final int maxXPerTick = (int)(Math.max(Math.floor((float)maxBlocksPerTick/(sh.height*sh.width)),1.0));
		
		//the position to begin counting in the blocks[] array.
		int count=(genTick*maxXPerTick)*sh.height*sh.width;
		
		
		if(count >= sh.blocks.length || this.id > 1) { //we've generated all blocks already! or We don't need to generate the next area TODO: remove this.id > 1
			
			//lets put in the chests!
			for(int i = 0; i < spawnlocations.length; i++) {
				int x = spawnlocations[i][0];
				int y = spawnlocations[i][1];
				int z = spawnlocations[i][2];
				TileEntity entity;
				if(world.blockExists(x, y, z)) {
					entity = (TileEntity) world.getTileEntity(x, y , z);
					if(entity != null && entity instanceof TileEntityChest) {
						//clear chest contents.
						TileEntityChest chest = (TileEntityChest) InventoryHelper.clearChestContents(entity);
						entity = chest; //set this updated chest to the entity object.
					}
					
				} else {
					world.setBlock(x, y, z, Block.getBlockFromName("chest"));
					entity = (TileEntity) world.getTileEntity(x, y , z);
				}
				
				if(entity != null && entity instanceof TileEntityChest) {
					System.out.println("I put in a chest!");
					ItemStack someIce = new ItemStack(Block.getBlockFromName("packed_ice"), 4);
					ItemStack someWood = new ItemStack(Block.getBlockById(17), 4); //Oak Wood Logs
					ItemStack someAluminum = new ItemStack(Block.getBlockById(209), 4); //Aluminum Blocks
					ItemStack someNR = new ItemStack(Block.getBlockById(428), 4); //Black Natural Rubber -
					TileEntityChest chest = (TileEntityChest) entity;
					chest.setInventorySlotContents(0, someIce);
					chest.setInventorySlotContents(1, someWood);
					chest.setInventorySlotContents(2, someAluminum);
					chest.setInventorySlotContents(3, someNR);
				}
				
			}
			
			return true; 
		}

		//still have blocks in the blocks[] array we need to add to the world
		for(int x = (genTick*maxXPerTick); x < (genTick*maxXPerTick)+ maxXPerTick; x++){
			for(int y = 0; y<(int)sh.height; y++){
				for(int z = 0; z<(int)sh.width; z++){
					if(count>=sh.blocks.length) { //in case the array isn't perfectly square (i.e. rectangular area was selected)
						return false;
					}
					int curblock = (int)sh.blocks[count];
					
					if(curblock == 0 || curblock == 76) {
						count++;
						continue;
					}
					else if(curblock == 759) {
						count++;
						continue; //these are Gas Lamps - we don't care for these.
						
					}else if(curblock == 123 || curblock == 124) { //replace redstone lamps (inactive or active) with glowstone.
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(89), 0, 2);
					}
					
					else if(curblock == 95) {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						if(sh.data[count] == 5) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add lime carpet
						}
	
						
					}else if(curblock == 35) {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						//System.out.println(x);
						if(sh.data[count] == 5) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add lime carpet
						}else if(sh.data[count] == 0) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add white carpet
						}
						
					}
					
					
					else if(curblock == 754) { //spotlights - we like these
						//world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(0), 0, 2);
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						//ResearchAssistantEntity dummy = new ResearchAssistantEntity(world, true);
						PolycraftInventoryBlock pbi = (PolycraftInventoryBlock) world.getBlock(x + this.xPos, y + this.yPos , z + this.zPos);
						System.out.println(String.format("Found a tile entity & xyz: %s %d %d %d", pbi.getUnlocalizedName(), x + this.xPos,  y + this.yPos , z + this.zPos));
						//System.out.println("Coordinates: ");
						ItemStack item = new ItemStack(Block.getBlockById((int)sh.blocks[count]));
						pbi.onBlockPlacedBy(world, x + this.xPos, y + this.yPos, z + this.zPos, dummy, new ItemStack(Block.getBlockById((int)sh.blocks[count])));
						
						FueledLampInventory lightInv = (FueledLampInventory) pbi.getInventory(world, x + this.xPos, y + this.yPos, z + this.zPos);
						lightInv.setInventorySlotContents(0,
								new ItemStack(random.nextFloat() > 0.5 ? ResearchAssistantLabGenerator.BUTANOL : ResearchAssistantLabGenerator.ETHANOL, 8 + random.nextInt(3)));

					
					}else if(curblock == 19){ //sponges mark the spawn locations, but are located two blocks below the surface.
						for(int i = 0; i < spawnlocations.length; i++) {
							if(spawnlocations[i][1] == 0){	// if the y value is zero, it hasn't been defined yet
								spawnlocations[i][0] = x + this.xPos;
								spawnlocations[i][1] = y + this.yPos + 2; //add two because we hide the block underground
								spawnlocations[i][2] = z + this.zPos;
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
	
	
	/**
	 * Generates the stoop approximately 1 chunk at a time. This function handles determining the size it needs
	 * This function assumes that the schematic file in question was generated by {@inheritDoc commands.dev.CommandDev.java}
	 * That means the block and data array are generated by the nested for loops: length (x), height (y), width (z), in that order.
	 * @return True if its done generating, False if it's still in progress
	 */
	protected boolean generateStoop(Schematic schematic) {

		final int maxBlocksPerTick = 65536;
		Schematic sh = schematic;
		
		//number of "lengths" to generate per tick (max X blocks), iterating through at least 1 X per tick, in case the height and width are really big.
		//we don't want the game to lag too much.
		final int maxXPerTick = (int)(Math.max(Math.floor((float)maxBlocksPerTick/(sh.height*sh.width)),1.0));
		
		//the position to begin counting in the blocks[] array.
		int count=(genTick*maxXPerTick)*sh.height*sh.width;
		
		
		if(count >= sh.blocks.length || this.id > 1) { //we've generated all blocks already! or We don't need to generate the next area TODO: remove this.id > 1
			
			//lets put in the chests!
			for(int i = 0; i < spawnlocations.length; i++) {
				int x = spawnlocations[i][0];
				int y = spawnlocations[i][1];
				int z = spawnlocations[i][2];
				TileEntity entity;
				if(world.blockExists(x, y, z)) {
					entity = (TileEntity) world.getTileEntity(x, y , z);
					if(entity != null && entity instanceof TileEntityChest) {
						//clear chest contents.
						TileEntityChest chest = (TileEntityChest) InventoryHelper.clearChestContents(entity);
						entity = chest; //set this updated chest to the entity object.
					}
					
				} else {
					world.setBlock(x, y, z, Block.getBlockFromName("chest"));
					entity = (TileEntity) world.getTileEntity(x, y , z);
				}
				
				if(entity != null && entity instanceof TileEntityChest) {
					System.out.println("I put in a chest!");
					ItemStack someIce = new ItemStack(Block.getBlockFromName("packed_ice"), 4);
					ItemStack someWood = new ItemStack(Block.getBlockById(17), 4); //Oak Wood Logs
					ItemStack someAluminum = new ItemStack(Block.getBlockById(209), 4); //Aluminum Blocks
					ItemStack someNR = new ItemStack(Block.getBlockById(428), 4); //Black Natural Rubber -
					TileEntityChest chest = (TileEntityChest) entity;
					chest.setInventorySlotContents(0, someIce);
					chest.setInventorySlotContents(1, someWood);
					chest.setInventorySlotContents(2, someAluminum);
					chest.setInventorySlotContents(3, someNR);
				}
				
			}
			
			return true; 
		}

		//still have blocks in the blocks[] array we need to add to the world
		for(int x = (genTick*maxXPerTick); x < (genTick*maxXPerTick)+ maxXPerTick; x++){
			for(int y = 0; y<(int)sh.height; y++){
				for(int z = 0; z<(int)sh.width; z++){
					if(count>=sh.blocks.length) { //in case the array isn't perfectly square (i.e. rectangular area was selected)
						return false;
					}
					int curblock = (int)sh.blocks[count];
					
					if(curblock == 0 || curblock == 76) {
						count++;
						continue;
					}
					else if(curblock == 759) {
						count++;
						continue; //these are Gas Lamps - we don't care for these.
						
					}else if(curblock == 123 || curblock == 124) { //replace redstone lamps (inactive or active) with glowstone.
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(89), 0, 2);
					}
					
					else if(curblock == 95) {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						if(sh.data[count] == 5) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add lime carpet
						}
	
						
					}else if(curblock == 35) {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						//System.out.println(x);
						if(sh.data[count] == 5) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add lime carpet
						}else if(sh.data[count] == 0) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add white carpet
						}
						
					}
					
					
					else if(curblock == 754) { //spotlights - we like these
						//world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(0), 0, 2);
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						//ResearchAssistantEntity dummy = new ResearchAssistantEntity(world, true);
						PolycraftInventoryBlock pbi = (PolycraftInventoryBlock) world.getBlock(x + this.xPos, y + this.yPos , z + this.zPos);
						System.out.println(String.format("Found a tile entity & xyz: %s %d %d %d", pbi.getUnlocalizedName(), x + this.xPos,  y + this.yPos , z + this.zPos));
						//System.out.println("Coordinates: ");
						ItemStack item = new ItemStack(Block.getBlockById((int)sh.blocks[count]));
						pbi.onBlockPlacedBy(world, x + this.xPos, y + this.yPos, z + this.zPos, dummy, new ItemStack(Block.getBlockById((int)sh.blocks[count])));
						
						FueledLampInventory lightInv = (FueledLampInventory) pbi.getInventory(world, x + this.xPos, y + this.yPos, z + this.zPos);
						lightInv.setInventorySlotContents(0,
								new ItemStack(random.nextFloat() > 0.5 ? ResearchAssistantLabGenerator.BUTANOL : ResearchAssistantLabGenerator.ETHANOL, 8 + random.nextInt(3)));

					
					}else if(curblock == 19){ //sponges mark the spawn locations, but are located two blocks below the surface.
						for(int i = 0; i < spawnlocations.length; i++) {
							if(spawnlocations[i][1] == 0){	// if the y value is zero, it hasn't been defined yet
								spawnlocations[i][0] = x + this.xPos;
								spawnlocations[i][1] = y + this.yPos + 2; //add two because we hide the block underground
								spawnlocations[i][2] = z + this.zPos;
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
		for(Ticket tkt : this.tickets) {
			ForgeChunkManager.releaseTicket(tkt);
		}
		
		//ExperimentManager.INSTANCE.sendExperimentUpdates();
		//this.scoreboard = null; //TODO: does this need to be null?
	}
	
	//Main update function for Experiments
	public void onServerTickUpdate(){
		if(this.currentState == State.Running || this.currentState == State.Halftime)
			this.checkAnyPlayersLeft();
		
	}
	
	private void checkAnyPlayersLeft() {
		for(Team tm : this.scoreboard.getTeams()) {
			if(tm.getSize() == 0) {
				this.currentState = State.Ending;
				return;
			}
		}
		
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
	
	/**
	 * Base classes need to define their own experiment strings
	 * @return The instructions that are sent to the player's GUI screen
	 */
	public abstract String getInstructions();
	
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


	protected abstract void updateParams(ExperimentParameters params);
	
}
