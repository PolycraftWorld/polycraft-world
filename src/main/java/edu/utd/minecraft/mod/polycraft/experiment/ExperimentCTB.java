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
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class ExperimentCTB extends Experiment{
	protected ArrayList<Base> bases= new ArrayList<Base>();
	protected ArrayList<String> teamNames = new ArrayList<String>();
	protected int tickCount = 0;
	
	//experimental params
	private final float MAXSCORE = 500;
	private final int ticksToClaimBase = 100;
	private final float claimBaseScoreBonus = 50;
	private final int updateScoreOnTickRate = 20;
	private final int scoreIncrementOnUpdate = 1;

	public ExperimentCTB(int id, int size, int xPos, int zPos, World world) {
		super(id, size, xPos, zPos, world);
		//teamNames.add("testing");
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard(teamNames);
		this.playersNeeded = 2;
		int maxBases = 20;
		int workarea = size*16;
		int distBtwnBases = (int) ((workarea*1.0)/Math.sqrt(maxBases));
		int counter = 0;
		for (int x = xPos + distBtwnBases; x < (xPos+size*16 - 1);x+=distBtwnBases){
			for (int z = zPos + distBtwnBases; z < (zPos+size*16 - 1);z+=distBtwnBases){
				counter++;
				BoundingBox box = new BoundingBox(x + 0.5, z + 0.5, 6,yPos+1, yPos+11, Color.GRAY);
				bases.add(new Base(x, yPos, z, box, Color.GRAY));
			}
		}
	}
	
	@Override
	public void start(){
		PolycraftMod.logger.debug("Experiment 1 Started");
		currentState = State.Starting;
		tickCount = 0;
		for(Base base: bases){
			base.setRendering(true);
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
	public void onServerTickUpdate() {
		super.onServerTickUpdate();
		if(currentState == State.Done) {
			//TODO: delete scoreboard from scoreboard manager.
			bases=null;
			teamNames=null;
		}
		else if(currentState == State.Starting){
			if(tickCount == 0){
				for(EntityPlayerMP player: players){
					player.addChatMessage(new ChatComponentText("Experiment Will be starting in 10 seconds!"));
					ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), player);
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
				tickCount = 0; //does this matter??
			}
			tickCount++;
		}else if(currentState == State.Running){
			tickCount++;
			updateBaseStates2();
			for(Float score : this.scoreboard.getScores()) {
				if (score > MAXSCORE) {
					currentState = State.Ending;
					break;
				}
			}
		//End of Running state
		} else if(currentState == State.Ending) {

			Map.Entry<Team, Float> maxEntry = null;
			for (Map.Entry<Team, Float> entry : this.scoreboard.getTeamScores().entrySet()) {
			    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)  {
			        maxEntry = entry;
			    }
			}
			
			for(EntityPlayerMP player : players) {
				if(this.scoreboard.getPlayerTeam(player).equals(maxEntry.getKey())) {
					player.addChatComponentMessage(new ChatComponentText("Congraduations!! You Won!!"));
				} else {
					player.addChatComponentMessage(new ChatComponentText("You Lost! Boooooooo."));
				}
				
			}
			ExperimentManager.INSTANCE.stop(this.id); //End the experiment and kill this.
		}
	}
	
	private void updateBaseStates2() {
		for(Base base : bases) {
			
			int playerCount = 0;
			switch(base.currentState) {
			case Neutral:
				base.setHardColor(Color.GRAY);
				base.tickCount = 0;
				for(Entity player : players) {
					if(base.isInBase(player)) {
						//base.tickCount++;
						base.setCurrentTeam(this.scoreboard.getPlayerTeam((EntityPlayerMP) player));
						base.currentState = Base.State.Occupied;
						Color newBaseColor = new Color(base.getCurrentTeam().getColor().getRed(),
								base.getCurrentTeam().getColor().getGreen(),
								base.getCurrentTeam().getColor().getBlue(),
								0.5f);
						
						base.setHardColor(newBaseColor);
						
					}
				}
				if(base.currentState!=Base.State.Neutral) {
					for(EntityPlayerMP player : players) {
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), player);
					}
				}
				break;
			
			case Occupied:
				
				base.tickCount++;
				//boolean noPlayers = true;
				//int playerCount = 0;
				for(Entity player : players) {
					if(base.isInBase(player)) {
						//noPlayers = false;
						playerCount++;
						if (!base.getCurrentTeam().equals(this.scoreboard.getPlayerTeam((EntityPlayerMP) player))) { 
								//reset case
								base.currentState = Base.State.Neutral;
								base.setHardColor(Color.GRAY);
								base.setCurrentTeam(null);
								//ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP) player);
						} else {
							//teammates entered the base.
							//TODO: do something.
						}
	
					}
				}
				if(playerCount==0) {
					//case no one in the previously occupied base:
					base.currentState = Base.State.Neutral;
					base.setHardColor(Color.GRAY);
					base.setCurrentTeam(null);
					break;
				}if(base.tickCount >= ticksToClaimBase) {
					base.currentState = Base.State.Claimed;
					base.setHardColor(base.getCurrentTeam().getColor());
					base.tickCount=0;
					//TODO: send score update for claiming here.
					this.scoreboard.updateScore(base.getCurrentTeam().toString(), this.claimBaseScoreBonus);
				}
				if(base.currentState != Base.State.Occupied) {
					for(EntityPlayerMP player : players) {
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), player);
					}
				}
				break;
			case Claimed:
				base.setHardColor(base.getCurrentTeam().getColor());
				//TODO: send score update
				if(this.tickCount%this.updateScoreOnTickRate == 0) {
					this.scoreboard.updateScore(base.getCurrentTeam().toString(), this.scoreIncrementOnUpdate);
				}
				//playerCount = 0;
				for(Entity player : players) {
					if(base.isInBase(player)) {
						playerCount++;
						if(!base.getCurrentTeam().equals(this.scoreboard.getPlayerTeam((EntityPlayerMP) player))) {
							base.tickCount++; //this goes faster for two players!
						}
					}
				}
				if(playerCount==0) {
					base.tickCount = 0;
				}
				if(base.tickCount>=this.ticksToClaimBase) {
					base.currentState = Base.State.Neutral;
					base.setHardColor(Color.GRAY);
					base.tickCount=0;
				}
				if(base.currentState != Base.State.Claimed) {
					for(EntityPlayerMP player : players) {
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), player);
					}
				}
				
				break;
			default:
				break;
			}
			
		}
	}
	
//	private void updateBaseStates() {
//		for (Base base : bases) {
//			//assume no players are in any base. we will check for players later.
//			if(base.currentState == Base.State.Occupied) {
//				base.currentState = Base.State.Claimed;
//			} else if (base.currentState == Base.State.Contested) {
//				//assume that the original owners won the battle (this can be checked later):
//				base.currentState = Base.State.Occupied;
//			}
//			
//			//now, check for conflicts
//			
//			//check to see if bases are occupied
//			//TODO: add the "occupied flag" to the BoundingBox class.
//			for(EntityPlayerMP player : players) {
//				if(base.isInBase(player)) {
//					if(base.currentState == Base.State.Occupied) {
//						if (!this.scoreboard.getPlayerTeam(player).equals(base.getCurrentTeam())) {
//							base.currentState = Base.State.Contested;
//							//base.setCurrentTeam(null);
//							//now, for any future players that are checked, this if statement will not trigger.
//						}
//					}
//					//if player enters a free base or they're actually inside their own base:
//					else if(base.currentState == Base.State.Neutral) {
//						base.currentState = Base.State.Occupied;
//						base.setCurrentTeam(this.scoreboard.getPlayerTeam(player)); //assign the team to the first player in the list.
//						base.setHardColor(this.scoreboard.getPlayerTeam(player).getColor());
//						System.out.println("Team assigned: " + base.getCurrentTeam().toString());
//					} else if(base.currentState == Base.State.Claimed) {
//						base.currentState = Base.State.Occupied;
//						//if (!base.getCurrentTeam().equals(this.scoreboard.getPlayerTeam(player))) {	
//						}
//					}
//					//if player enters 
//				}
//			}
//		}

	@Override
	public void onClientTickUpdate(){
		if(currentState == State.Starting){
			if(tickCount == 0){
				for(Base base: bases)
					base.setRendering(true);
				tickCount++;
			}
		}
				
	}
	
	private final String prepBoundingBoxUpdates() {
		Gson gson = new Gson();
		Type gsonType = new TypeToken<ArrayList<Base>>() {}.getType();
		final String updateScoreJson = gson.toJson(this.bases, gsonType);
		return updateScoreJson;
	}
	
	@Override
	protected void generateArea(int xPos, int yPos, int zPos, World world){
		super.generateArea(xPos, yPos, zPos, world);	//generate the base flat area
		super.generateSpectatorBox(xPos, yPos, zPos, world);
		for(Base base: bases){	//generate bases
			base.generate(world);
		}
	}
	
	private Base isPlayerInAnyBase(EntityPlayerMP player){
		for(Base base: bases){
			if(base.isInBase((Entity) player))
				return base;
		}
		return null;
	}
	
	@Override
	public void render(Entity entity){
		for(Base base: bases){
			if(base.isInBase(entity)){
				base.setColor(Color.BLUE);
			}else{
				base.resetColor();
			}
			base.render(entity);
		}
	}


}
