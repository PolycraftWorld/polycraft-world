package edu.utd.minecraft.mod.polycraft.experiment;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class ExperimentCTB extends Experiment{
	protected ArrayList<Base> bases= new ArrayList<Base>();
	protected int tickCount = 0;
	private boolean hasGameEnded = false;
	private final int WAITSPAWNTICKS = 400;
	private static final ItemStack[] armors = {
			new ItemStack(PolycraftRegistry.getItem("Golden Helmet")),
			new ItemStack(PolycraftRegistry.getItem("Rubber Shower Cap")),
			new ItemStack(PolycraftRegistry.getItem("Kevlar Helmet")),
			new ItemStack(PolycraftRegistry.getItem("Sparkling Headgear")),
			new ItemStack(PolycraftRegistry.getItem("Jeffersonian Wig")),
			new ItemStack(PolycraftRegistry.getItem("Copper Cap")),
			new ItemStack(PolycraftRegistry.getItem("Plumed Close Helm")),
			new ItemStack(PolycraftRegistry.getItem("Pepto Bismal Pink Cap")),
			new ItemStack(PolycraftRegistry.getItem("Fine Polyester Top Hat")),
			new ItemStack(PolycraftRegistry.getItem("SuperB Barbute")),
			new ItemStack(PolycraftRegistry.getItem("Spectra Helmet")),
			new ItemStack(PolycraftRegistry.getItem("Wolfram Great Helm")),
			new ItemStack(PolycraftRegistry.getItem("Brazen Bassinet")),
			new ItemStack(PolycraftRegistry.getItem("Comfortable Cap")),
			new ItemStack(PolycraftRegistry.getItem("Ripstop Nylon Beanie")),
			new ItemStack(PolycraftRegistry.getItem("SBR Swim Cap"))
	};
	private static int currentArmor = 0;
	
	//experimental params
	private final float MAXSCORE = 1000; 
	public static int maxTicks = 10000; //Server drops ticks. let's increase by 4x to 24000 to make the game last longer.
	private final int WAIT_TELEPORT_UTD_TICKS = 200;
	//TODO: can you use a real clock instead of "skippable" server ticks??
	private final int ticksToClaimBase = 120; //also the same number of ticks to steal base, for now.
	private final float claimBaseScoreBonus = 50;
	private final float stealBaseScoreBonus = 200;
	private final int updateScoreOnTickRate = 20;
	private final int scoreIncrementOnUpdate = 0;
	//public static int maxPlayersNeeded = 4;

	public ExperimentCTB(int id, int size, int xPos, int zPos, World world) {
		super(id, size, xPos, zPos, world);
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard();
		for(int x = 0; x < teamsNeeded;x++) {
			this.scoreboard.addNewTeam();
			this.scoreboard.resetScores(0);
		}
		//teamNames.add("testing");
		//this.playersNeeded = maxPlayersNeeded; //using playersNeeded from Experiments (for now)
		int maxBases = 8;
		int workarea = size*16;
		int distBtwnBases = (int) ((workarea*1.0)/Math.sqrt(maxBases));
		int counter = 0;
		for (int x = xPos + distBtwnBases; x < (xPos+size*16 - 1);x+=distBtwnBases){
			for (int z = zPos + distBtwnBases; z < (zPos+size*16 - 1);z+=distBtwnBases){
				counter++;
				BoundingBox box = new BoundingBox(x + 0.5, z + 0.5, 6,yPos+1, yPos+2, Color.GRAY);
				bases.add(new Base(x, yPos, z, box, Color.GRAY));
			}
		}
		
		currentState = State.WaitingToStart;
	}
	
	public ExperimentCTB(int id, int size, int xPos, int zPos, World world, int maxteams, int teamsize) {
		super(id, size, xPos, zPos, world);
		//teamNames.add("testing");
		//this.playersNeeded = maxPlayersNeeded; //using playersNeeded from Experiments (for now)
		this.teamsNeeded = maxteams;
		this.teamSize = teamsize;
		this.playersNeeded = teamsNeeded * teamSize;
		this.awaitingNumPlayers = this.playersNeeded;
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard();
		for(int x = 0; x < teamsNeeded;x++) {
			this.scoreboard.addNewTeam();
			this.scoreboard.resetScores(0);
		}
		
		int maxBases = 8;
		int workarea = size*16;
		int distBtwnBases = (int) ((workarea*1.0)/Math.sqrt(maxBases));
		int counter = 0;
		for (int x = xPos + distBtwnBases; x < (xPos+size*16 - 1);x+=distBtwnBases){
			for (int z = zPos + distBtwnBases; z < (zPos+size*16 - 1);z+=distBtwnBases){
				counter++;
				BoundingBox box = new BoundingBox(x + 0.5, z + 0.5, 6,yPos+1, yPos+2, Color.GRAY);
				bases.add(new Base(x, yPos, z, box, Color.GRAY));
			}
		}
	
		currentState = State.WaitingToStart;
		
	}
	
	@Override
	public void start(){
		
		if(currentState == State.WaitingToStart) {
			super.start(); //send the updates
			PolycraftMod.logger.debug("Experiment " + this.id +" Start Generation");
			currentState = State.GeneratingArea;
			tickCount = 0;
			for(Base base: bases){
				base.setRendering(true);
			}
			for(Team team: scoreboard.getTeams()) {
				for(String player:team.getPlayers()) {
					EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
					playerEntity.addChatMessage(new ChatComponentText("Expiriment will be starting Shortly. Please wait while the field is generated"));
				}
			}
		}
	}
	
	/**
	 * Used to dimensionally transport a player into the Experiments dimension (dim. 8)
	 * Player randomly is placed within the experiment zone using Math.random().
	 * TODO: spawn players within their "Team Spawn" Zones.
	 * @param player player to be teleported
	 * @param y height they should be dropped at.
	 */
	private void spawnPlayer(EntityPlayerMP player, int y){
		double x = Math.random()*(size*16 - 10) + xPos + 5;
		double z = Math.random()*(size*16 - 10) + zPos + 5;
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 8,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(8), (int)x, y, (int)z));
//		player.setPositionAndUpdate(x + .5,
//				player.worldObj.getTopSolidOrLiquidBlock((int)x, (int)z) + 3,
//				z + .5);
		player.setPositionAndUpdate(x + .5, y, z + .5);
	}
	
	/**
	 * Uses setPositionAndUpdate instead of the Dimensional Teleporter - hopefully this is faster and 
	 * decreases the weird not-being-able-to-see players bug
	 * Player randomly is placed within the experiment zone using Math.random().
	 * @param player the player to be moved
	 * @param y the height they should be dropped at (x & z are random)
	 */
	private void spawnPlayerInGame(EntityPlayerMP player, int y) {
		double x = Math.random()*(size*16 - 10) + xPos + 5;
		double z = Math.random()*(size*16 - 10) + zPos + 5;
		player.setPositionAndUpdate(x + .5, y, z + .5);
	}
	
	@Override
	public void onServerTickUpdate() {
		super.onServerTickUpdate();
		if(currentState == State.Done) {
			//TODO: delete scoreboard from scoreboard manager.
			//This should NEVER run anymore.
			//bases=null;
			//scoreboard=null;
		}
		else if(currentState == State.GeneratingArea){
			if(genTick % 20 == 0) {
				for(Team team: scoreboard.getTeams()) {
					for(String player: team.getPlayers()) {
						EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
						playerEntity.addChatMessage(new ChatComponentText("§aGenerating..."));
					}
				}
			}
			generateArea();
			genTick++;
			if(genTick >= size * size) {
				currentState = State.Starting;
			}
		}
		else if(currentState == State.Starting){
			if(tickCount == 0){
				for(Team team: scoreboard.getTeams()) {
					ItemStack[] armor = new ItemStack[4];
					armor[3] = armors[currentArmor];	//set current armor color to current team
					incrementArmor();	//increment armor counter so next team gets a different armor
					for(EntityPlayer player: team.getPlayersAsEntity()) {
						player.addChatMessage(new ChatComponentText(String.format("Experiment Will be starting in %d seconds!", this.WAITSPAWNTICKS/20)));
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP)player);
						spawnPlayer((EntityPlayerMP)player, 124);
						ServerEnforcer.INSTANCE.freezePlayer(true, (EntityPlayerMP)player);	//freeze players while they wait for the game to begin
						
						//clear player inventory
						player.inventory.mainInventory = new ItemStack[36];
						player.inventory.armorInventory = armor;
						//give players a stick with knockback == 10.
						ItemStack item = new ItemStack(GameData.getItemRegistry().getObject("stick"));
						item.addEnchantment(Enchantment.knockback, 5); //give them a knockback of 5.
						//add to their inventories.
						player.inventory.addItemStackToInventory(item);
					}
				}
			}else if(tickCount % (this.WAITSPAWNTICKS/10) == 0) {
				for(Team team: scoreboard.getTeams()) {
					for(EntityPlayer player: team.getPlayersAsEntity()) {
						player.addChatMessage(new ChatComponentText(String.format("Experiment Will be starting in %d seconds!", (this.WAITSPAWNTICKS-tickCount)/20)));
					}
				}
			}else if(tickCount >= this.WAITSPAWNTICKS){
				for(Team team: scoreboard.getTeams()) {
					for(EntityPlayer player: team.getPlayersAsEntity()) {
						spawnPlayerInGame((EntityPlayerMP)player, 93); 	
						ServerEnforcer.INSTANCE.freezePlayer(false, (EntityPlayerMP)player);	//unfreeze players to start!
						player.addChatMessage(new ChatComponentText("§aSTART"));
					}
					this.scoreboard.updateScore(team, 0);
				}
				//this.scoreboard.resetScores(0);
				currentState = State.Running;
				tickCount = 0; 
			}
			tickCount++;
		}
		else if(currentState == State.Running){
			tickCount++;
			updateBaseStates2();
			for(Float score : this.scoreboard.getScores()) {
				if (score >= MAXSCORE) { //end if the team reaches the maximum score.
					currentState = State.Ending;
					break;
				}
			}
			if(tickCount >= maxTicks) {
				currentState = State.Ending;
			}else if(tickCount % 600 == 0) {
				for(EntityPlayer player: scoreboard.getPlayersAsEntity()){
					player.addChatMessage(new ChatComponentText("Seconds remaining: §a" + (maxTicks-tickCount)/20));
				}
			}else if(maxTicks-tickCount < 600) {
				if(tickCount % 60 == 0) {
					for(EntityPlayer player: scoreboard.getPlayersAsEntity()){
						player.addChatMessage(new ChatComponentText("Seconds remaining: §a" + (maxTicks-tickCount)/20));
					}
				}
			}
		//End of Running state
		}
		else if(currentState == State.Ending) {
			if(!this.hasGameEnded) { //do this once only!
				this.hasGameEnded = true;
				Map.Entry<Team, Float> maxEntry = null;
				for (Map.Entry<Team, Float> entry : this.scoreboard.getTeamScores().entrySet()) {
				    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)  {
				        maxEntry = entry;
				    }
				}
				
				String stringToSend = maxEntry.getKey().getName() + " Team wins!";
				
				ServerScoreboard.INSTANCE.sendGameOverUpdatePacket(this.scoreboard, stringToSend);
				
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					ServerEnforcer.INSTANCE.freezePlayer(true, (EntityPlayerMP)player);
					//clear player inventory
					
					if(this.scoreboard.getPlayerTeam(player.getDisplayName()).equals(maxEntry.getKey())) {
						player.addChatComponentMessage(new ChatComponentText("Congraduations!! You Won!!"));
					} else {
						player.addChatComponentMessage(new ChatComponentText("You Lost! Better Luck Next Time."));
					}
					player.addChatComponentMessage(new ChatComponentText("Teleporting to UTD in: " + this.WAIT_TELEPORT_UTD_TICKS/20 + "seconds"));
				}
				tickCount = maxTicks;
			}
			tickCount++;
			if(tickCount >= maxTicks + this.WAIT_TELEPORT_UTD_TICKS) {
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					player.inventory.mainInventory = new ItemStack[36];
					player.inventory.armorInventory = new ItemStack[4];
					ServerEnforcer.INSTANCE.freezePlayer(false, (EntityPlayerMP)player);
				}
				ExperimentManager.INSTANCE.stop(this.id); //End the experiment and kill this.
			}
			
		}
	}
	
	private void updateBaseStates2() {
		for(Base base : bases) {
			
			int playerCount = 0;
			switch(base.currentState) {
			case Neutral:
				base.setHardColor(Color.GRAY);
				base.tickCount = 0;
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					if(base.isInBase(player)) {
						//base.tickCount++;
						base.setCurrentTeam(this.scoreboard.getPlayerTeam(player.getDisplayName()).getName());
						base.currentState = Base.State.Occupied;
						Color newBaseColor = new Color((this.scoreboard.getTeam(base.getCurrentTeam())).getColor().getRed()/255.0f,
								(this.scoreboard.getTeam(base.getCurrentTeam())).getColor().getGreen()/255.0f,
								(this.scoreboard.getTeam(base.getCurrentTeam())).getColor().getBlue()/255.0f,
								0.25f);
						base.setHardColor(newBaseColor);	//sets perm color and resets current color
						((EntityPlayerMP) player).addChatComponentMessage(new ChatComponentText("Attempting to Capture Base!"));
					}
				}
				if(base.currentState!=Base.State.Neutral) {	//push update to all players
					for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP)player);
					}
				}
				break;
			
			case Occupied:
				base.tickCount++;
				//boolean noPlayers = true;
				//int playerCount = 0;
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					if(base.isInBase(player)) {
						//noPlayers = false;
						playerCount++;
						if (base.getCurrentTeam() != null && !this.scoreboard.getPlayerTeam(player.getDisplayName()).equals(base.getCurrentTeam())) { 
								//reset case
								base.currentState = Base.State.Neutral;
								base.setHardColor(Color.GRAY);
								base.setCurrentTeam(null);
								//ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP) player);
						} else {
							if(base.tickCount % 20 == 0) {
								((EntityPlayerMP) player).addChatComponentMessage(new ChatComponentText("Base Captured in: " + (ticksToClaimBase - base.tickCount)/20 + "seconds"));
							}
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
					base.setHardColor((this.scoreboard.getTeam(base.getCurrentTeam())).getColor());
					base.tickCount=0;
					//TODO: send score update for claiming here.
					this.scoreboard.updateScore(base.getCurrentTeam(), this.claimBaseScoreBonus);
					//TODO: Add Fireworks
//					ItemStack item= new ItemStack(new ItemFirework());
//					item.getItem().
//					EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(world, base.xPos, base.yPos, base.zPos, item);
//		            world.spawnEntityInWorld(entityfireworkrocket);
				}
				if(base.currentState != Base.State.Occupied) {
					for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP)player);
					}
				}
				break;
			case Claimed:
				base.setHardColor((this.scoreboard.getTeam(base.getCurrentTeam())).getColor());
				//TODO: send score update
				if(this.tickCount%this.updateScoreOnTickRate == 0) {
					this.scoreboard.updateScore(base.getCurrentTeam(), this.scoreIncrementOnUpdate);
				}
				//playerCount = 0;
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					if(base.isInBase(player)) {
						playerCount++;
						if(!this.scoreboard.getPlayerTeam(player.getDisplayName()).equals(base.getCurrentTeam())) {
							base.tickCount++; //this goes faster for two players!
							//alert players that a user is stealing their base
							if(base.tickCount%20==0) {
								((EntityPlayerMP) player).addChatComponentMessage(new ChatComponentText("Base Reset to Neutral in: " + (ticksToClaimBase - base.tickCount)/20 + "seconds"));
								alertTeam(this.scoreboard.getTeam(base.getCurrentTeam()));
							}
							if(base.tickCount>=this.ticksToClaimBase) {
								base.currentState = Base.State.Neutral;
								base.setHardColor(Color.GRAY);
								base.tickCount=0;
								this.scoreboard.updateScore(this.scoreboard.getPlayerTeam(player.getDisplayName()).getName(), this.stealBaseScoreBonus);
								//break;// (only allow one player to claim the team bonus) remove this if we want the bonuses to stack.
							}
						}
					}
				}
				if(playerCount==0) {
					base.tickCount = 0;
				}
				
				if(base.currentState != Base.State.Claimed) {
					for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP)player);
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

	private void alertTeam(Team team) {
		for(String player: team.getPlayers()) {
			EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
			playerEntity.addChatMessage(new ChatComponentText("�4Alert: Someone is stealing your base!"));
		}
	}

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
	
	private void incrementArmor() {
		if(currentArmor < armors.length)
			currentArmor++;
		else
			currentArmor = 0;
	}
	
	private final String prepBoundingBoxUpdates() {
		Gson gson = new Gson();
		Type gsonType = new TypeToken<Base[]>(){}.getType();
		final String updateScoreJson = gson.toJson(this.bases.toArray(), gsonType);
		return updateScoreJson;
	}
	
	@Override
	protected void generateArea(){
		super.generateArea();	//generate the base flat area
		super.generateSpectatorBox();
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
