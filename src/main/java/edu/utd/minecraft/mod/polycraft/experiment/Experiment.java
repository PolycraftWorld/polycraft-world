package edu.utd.minecraft.mod.polycraft.experiment;

import java.util.Collection;

import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.scoreboards.CustomScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
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
	protected int playersNeeded = 4;
	protected int awaitingNumPlayers = playersNeeded;
	protected static int teamsNeeded = 2;
	protected static int teamSize = 2;
	protected int genTick = 0;
	
	
	public enum State{
		PreInit,
		Initializing, 
		WaitingToStart,
		GeneratingArea,
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
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard();
		for(int x = 0; x < teamsNeeded;x++) {
			this.scoreboard.addNewTeam();
			this.scoreboard.resetScores(0);
		}
		currentState = State.WaitingToStart;
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
	 * @param player
	 * @return
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
				playerCount++;
				awaitingNumPlayers--;
				if(playerCount == Experiment.teamSize*Experiment.teamsNeeded){
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
	public void start(){
		
	}
	
	public void stop() {
		this.currentState = State.Done;
		this.scoreboard = null;
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
	public static void setTeamsNeeded(int num) {
		Experiment.teamsNeeded=num;
	}
	
	/**
	 * Same as above. Teams are always filled on a first-come, first-serve basis, sequentially (team 1, then team 2, etc.)
	 * @param num max players per team.
	 */
	public static void setTeamSize(int num) {
		Experiment.teamSize = num;
	}
	
}
