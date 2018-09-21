package edu.utd.minecraft.mod.polycraft.experiment;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ExperimentCTB extends Experiment{
	protected int[][] bases;
	protected ArrayList<BoundingBox> boundingBoxes= new ArrayList<BoundingBox>();
	protected ArrayList<Color> boxColor = new ArrayList<Color>();
	protected ArrayList<String> teamNames = new ArrayList<String>();
	protected int tickCount = 0;

	public ExperimentCTB(int id, int size, int xPos, int zPos, World world) {
		super(id, size, xPos, zPos, world);
		//teamNames.add("testing");
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard(teamNames);
		this.playersNeeded = 1;
		int maxBases = 20;
		bases = new int[maxBases][2];
		int workarea = size*16;
		int distBtwnBases = (int) ((workarea*1.0)/Math.sqrt(maxBases));
		int counter = 0;
		for (int x = xPos + distBtwnBases; x < (xPos+size*16 - 1);x+=distBtwnBases){
			for (int z = zPos + distBtwnBases; z < (zPos+size*16 - 1);z+=distBtwnBases){
				bases[counter][0] = x;
				bases[counter][1] = z;
				counter++;
				boundingBoxes.add(new BoundingBox(x + 0.5, z + 0.5, 6,yPos+1, yPos+11, Color.GRAY));
				boxColor.add(Color.GRAY);
			}
		}
	}
	
	@Override
	public void start(){
		PolycraftMod.logger.debug("Experiment 1 Started");
		currentState = State.Starting;
		tickCount = 0;
		for(BoundingBox box: boundingBoxes){
			box.setRendering(true);
		}
	}
	
	private void spawnPlayer(EntityPlayerMP player, int y){
		double x = Math.random()*(size*16 - 10) + xPos + 5;
		double z = Math.random()*(size*16 - 10) + zPos + 5;
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 8,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(8), (int)x, y, (int)z));
		String playerTeamName = player.getDisplayName();
		//For now, each player is on their own team!
		if(!this.teamNames.contains(playerTeamName)) {
			this.teamNames.add(playerTeamName);
			this.scoreboard.setTeams(teamNames);
			this.scoreboard.resetScores(0);
		}
		
		try {
			System.out.println("We are spawning a player");
			

			this.scoreboard.addPlayer(player, playerTeamName);
			//this.scoreboard.addPlayer(player, "testing");
			//this.scoreboard.updateScore("testing", );
		} catch (IOException e) {
			System.out.println("Something went wrong in adding player to scoreboard...");
		}
	}
	
	@Override
	public void onTickUpdate() {
		super.onTickUpdate();
		if(currentState == State.Starting){
			if(tickCount == 0){
				for(EntityPlayerMP player: players){
					player.addChatMessage(new ChatComponentText("Experiment Will be starting in 10 seconds!"));
					spawnPlayer(player, 126);
				}
			}else if(tickCount >= 200){
				for(EntityPlayerMP player: players){
					spawnPlayer(player, 93);
					player.addChatMessage(new ChatComponentText("�aSTART"));
					this.scoreboard.updateScore(player.getDisplayName(), 0);
				}
				//this.scoreboard.resetScores(0);
				currentState = State.Running;
			}
			tickCount++;
		}else if(currentState == State.Running){
			for(EntityPlayerMP player: players){
				if(player.dimension != 8) {
					players.remove(player);
					//ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(null, player);
					//return;
				}
				BoundingBox box = isPlayerInBox(player);
				if(box != null) {
 					this.scoreboard.updateScore(player.getDisplayName(), 1);
						if(box.getColor() == Color.GRAY){
						boxColor.set(boundingBoxes.indexOf(box), Color.blue);
						box.setColor(Color.BLUE);
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), player);
						//ServerEnforcer.INSTANCE.experimentUpdate(); //what the hell?
					
					}
				}
			}
		}
	}
	
	private final String prepBoundingBoxUpdates() {
		Gson gson = new Gson();
		Type gsonType = new TypeToken<ArrayList<BoundingBox>>() {}.getType();
		final String updateScoreJson = gson.toJson(this.boundingBoxes, gsonType);
		return updateScoreJson;
	}
	
	@Override
	protected void generateArea(int xPos, int yPos, int zPos, World world){
		super.generateArea(xPos, yPos, zPos, world);	//generate the base flat area
		super.generateSpectatorBox(xPos, yPos, zPos, world);
		for(int i = 0; i < bases.length;i++){	//generate bases
			int x = bases[i][0];
			int z = bases[i][1];
			generateBase(x,z);
		}
	}
	
	private void generateBase(int x, int z){
		Block stairs = PolycraftRegistry.getBlock("Stairs (PVC)");
		Block pvc = PolycraftRegistry.getBlock("Block (PVC)");
		world.setBlock(x, yPos, z, pvc, 1, 3);
		world.setBlock(x, yPos+1, z, pvc, 1, 3);
		world.setBlock(x, yPos+2, z, pvc, 1, 3);
		world.setBlock(x, yPos+3, z, pvc, 1, 3);
		world.setBlock(x, yPos+4, z, pvc, 1, 3);
		world.setBlock(x, yPos+5, z, pvc, 1, 3);
		world.setBlock(x, yPos+6, z, pvc, 1, 3);
		world.setBlock(x, yPos+7, z, pvc, 1, 3);
		world.setBlock(x, yPos+7, z+1, pvc, 1, 3);
		world.setBlock(x, yPos+7, z-1, pvc, 1, 3);
		world.setBlock(x+1, yPos+7, z, pvc, 1, 3);
		world.setBlock(x-1, yPos+7, z, pvc, 1, 3);
		world.setBlock(x, yPos+8, z, pvc, 1, 3);
		world.setBlock(x, yPos+8, z+1, pvc, 1, 3);
		world.setBlock(x, yPos+8, z-1, pvc, 1, 3);
		world.setBlock(x+1, yPos+8, z, pvc, 1, 3);
		world.setBlock(x-1, yPos+8, z, pvc, 1, 3);
		world.setBlock(x, yPos+9, z, pvc, 1, 3);
		world.setBlock(x, yPos+9, z+1, pvc, 1, 3);
		world.setBlock(x, yPos+9, z-1, pvc, 1, 3);
		world.setBlock(x+1, yPos+9, z, pvc, 1, 3);
		world.setBlock(x-1, yPos+9, z, pvc, 1, 3);
		world.setBlock(x, yPos+9, z, pvc, 1, 3);
		world.setBlock(x-1, yPos+1, z, stairs, 0, 3);
		world.setBlock(x-1, yPos+1, z-1, stairs, 0, 3);
		world.setBlock(x-1, yPos+1, z+1, stairs, 0, 3);
		world.setBlock(x+1, yPos+1, z, stairs, 1, 3);
		world.setBlock(x+1, yPos+1, z+1, stairs, 1, 3);
		world.setBlock(x+1, yPos+1, z-1, stairs, 1, 3);
		world.setBlock(x, yPos+1, z-1, stairs, 2, 3);
		world.setBlock(x, yPos+1, z+1, stairs, 3, 3);
		world.setBlock(x-1, yPos+6, z, stairs, 4, 3);
		world.setBlock(x-1, yPos+6, z+1, stairs, 4, 3);
		world.setBlock(x-1, yPos+6, z-1, stairs, 4, 3);
		world.setBlock(x+1, yPos+6, z, stairs, 5, 3);
		world.setBlock(x+1, yPos+6, z+1, stairs, 5, 3);
		world.setBlock(x+1, yPos+6, z-1, stairs, 5, 3);
		world.setBlock(x, yPos+6, z-1, stairs, 6, 3);
		world.setBlock(x, yPos+6, z+1, stairs, 7, 3);
	}
	
	private BoundingBox isPlayerInBox(EntityPlayerMP player){
		for(BoundingBox box: boundingBoxes){
			if(box.isInBox((player)))
				return box;
		}
		return null;
	}
	
	@Override
	public void render(Entity entity){
		for(BoundingBox box: boundingBoxes){
			if(box.isInBox(entity)){
				box.setColor(Color.BLUE);
			}else{
				box.setColor(boxColor.get(boundingBoxes.indexOf(box)));
			}
			//moving this call to the Client? 
			//box.render(entity);
		}
	}


}
