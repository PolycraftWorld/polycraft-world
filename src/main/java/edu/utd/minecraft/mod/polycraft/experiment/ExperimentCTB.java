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
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentList;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.scoreboards.ScoreboardManager;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
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
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

public class ExperimentCTB extends Experiment{
	protected ArrayList<Base> bases= new ArrayList<Base>();
	protected int tickCount = 0;
	private boolean hasGameEnded = false;
	
	private static final ItemStack[] armors = {
			new ItemStack(PolycraftRegistry.getItem("Golden Helmet")),
			new ItemStack(PolycraftRegistry.getItem("Kevlar Helmet")),
			new ItemStack(PolycraftRegistry.getItem("Sparkling Headgear")),
			new ItemStack(PolycraftRegistry.getItem("Jeffersonian Wig")),
			new ItemStack(PolycraftRegistry.getItem("Copper Cap")),
			new ItemStack(PolycraftRegistry.getItem("Rubber Shower Cap")),
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
	public int maxTicks = 12000; //Server drops ticks?
	//private float MAXSCORE = 1000; 
	private int halfTimeTicks = maxTicks/2; //(5 minutes)

	private int halfTimeTicksRemaining = 2400; //2 minutes
	private int WAIT_TELEPORT_UTD_TICKS = 400;
	//TODO: can you use a real clock instead of "skippable" server ticks??
	private int ticksToClaimBase = 120; //also the same number of ticks to steal base, for now.
	private float claimBaseScoreBonus = 50;
	private float stealBaseScoreBonus = 200;
	private int updateScoreOnTickRate = 20;
	private int ownedBaseScoreBonusOnTicks = 5;
	private int WAITSPAWNTICKS = 400;
	//public static int maxPlayersNeeded = 4;
	
	private String stringToSend = "";
	
	
	@Deprecated
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
		
		tickets = new ForgeChunkManager.Ticket[size*size];
		
		int maxBases = 8;
		int workarea = this.size*16;
		int distBtwnBases = (int) ((workarea*1.0)/Math.sqrt(maxBases));
		int counter = 0;
//		for (int x = xPos + distBtwnBases; x < (xPos+size*16 - 1);x+=distBtwnBases){
//			for (int z = zPos + distBtwnBases; z < (zPos+size*16 - 1);z+=distBtwnBases){
//				counter++;
//				BoundingBox box = new BoundingBox(x + 0.5, z + 0.5, 6,yPos+1, yPos+2, Color.GRAY);
//				bases.add(new Base(x, yPos, z, box, Color.GRAY));
//			}
//		}
		int y = yPos + 5;
		BoundingBox box = new BoundingBox(xPos + 85.5, zPos + 143.5, 6,y+1, y+2, Color.GRAY);
		bases.add(new Base(xPos + 85, y, zPos + 143, box, Color.GRAY));
		box = new BoundingBox(xPos + 142.5, zPos + 143.5, 6,y+1, y+2, Color.GRAY);
		bases.add(new Base(xPos + 142, y, zPos + 143, box, Color.GRAY));
		box = new BoundingBox(xPos + 114.5, zPos + 185.5, 6,y+1, y+2, Color.GRAY);
		bases.add(new Base(xPos + 114, y, zPos + 185, box, Color.GRAY));
		box = new BoundingBox(xPos + 114.5, zPos + 101.5, 6,y+1, y+2, Color.GRAY);
		bases.add(new Base(xPos + 114, y, zPos + 101, box, Color.GRAY));
	
		currentState = State.WaitingToStart;
		
	}
	
	@Override
	public void start(){
		
		if(currentState == State.WaitingToStart) {
			super.start(); //send the updates
			PolycraftMod.logger.debug("Experiment " + this.id +" Start Generation");
			//this.generateStoop();
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
	private void spawnPlayer(EntityPlayerMP player,int x, int y, int z){
		double xOff = Math.random()*6 + x - 3;	//3 block radius
		double zOff = Math.random()*6 + z - 3;	//3 block radius
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 8,	
				new PolycraftTeleporter(player.mcServer.worldServerForDimension(8), (int)xOff, y, (int)zOff));
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
	private void spawnPlayerInGame(EntityPlayerMP player, int x, int y, int z) {
		double xOff = Math.random()*6 + x - 3;	//3 block radius
		double zOff = Math.random()*6 + z - 3;	//3 block radius
		player.setPositionAndUpdate(xOff + .5, y, zOff + .5);
	}
	
	@Override
	public void onServerTickUpdate() {
		super.onServerTickUpdate();
		if(tickCount % 20 == 0) {
			//send a timing update:
			sendTimeUpdate();
		}
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
						playerEntity.addChatMessage(new ChatComponentText("\u00A7aGenerating..."));
					}
				}
			}
			
			//generateArea();
			
			if(this.generateStoop()) {
				currentState = State.Starting;
			}
			genTick++;
		}
		else if(currentState == State.Starting){
			if(tickCount == 0){
				int index = 0;
				world.setWorldTime(1000);
				for(Team team: scoreboard.getTeams()) {
					this.scoreboard.updateScore(team, 0);
					ItemStack[] armor = new ItemStack[4];
					armor[3] = armors[currentArmor];	//set current armor color to current team
					incrementArmor();	//increment armor counter so next team gets a different armor
					if(index > spawnlocations.length)	//reset spawn location index to prevent null pointer exceptions
						index = 0;
					team.setSpawn(spawnlocations[index][0], spawnlocations[index][1], spawnlocations[index][2]);
					for(EntityPlayer player: team.getPlayersAsEntity()) {
						player.addChatMessage(new ChatComponentText(String.format("Experiment Will be starting in %d seconds!", this.WAITSPAWNTICKS/20)));
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP)player);
						spawnPlayer((EntityPlayerMP)player, team.getSpawn()[0], team.getSpawn()[1], team.getSpawn()[2]);
						ServerEnforcer.INSTANCE.freezePlayer(true, (EntityPlayerMP)player);	//freeze players while they wait for the game to begin
						
						//clear player inventory
						player.inventory.mainInventory = new ItemStack[36];
						player.inventory.armorInventory = armor;
						//set health and food for all players
						player.setHealth(20); //provide players maximum health
						//player.getFoodStats().setFoodLevel(20);
						//give players a stick with knockback == 5.
						ItemStack item = new ItemStack(GameData.getItemRegistry().getObject("stick"));
						item.addEnchantment(Enchantment.knockback, 5); //give them a knockback of 5.
						
						//give players knockback bombs
						ItemStack kbb = new ItemStack(PolycraftRegistry.getItem("Knockback Bomb"), 4);
						ItemStack fkb = new ItemStack(PolycraftRegistry.getItem("Freezing Knockback Bomb"), 4);
						//add to their inventories.
						player.inventory.addItemStackToInventory(item);
						player.inventory.addItemStackToInventory(kbb);
						player.inventory.addItemStackToInventory(fkb);
					}
					
					//keep the chunks loaded after players enter
					//Tried putting this in GenerateArea and it seemed to make things more laggy?
					int chunkCount = 0;
					for(int x = 0;x < size;x++) {
						for(int z = 0; z < size; z++) {
							if(chunkCount < tickets.length) {
								tickets[chunkCount] = ForgeChunkManager.requestTicket(PolycraftMod.instance, this.world, ForgeChunkManager.Type.NORMAL);
								ForgeChunkManager.forceChunk(tickets[chunkCount], new ChunkCoordIntPair((this.xPos / 16) + x, (this.zPos / 16) + z));
							}
						}
					}
					index++;
				}
//			}else if(tickCount % (this.WAITSPAWNTICKS/10) == 0) {
//				for(Team team: scoreboard.getTeams()) {
//					for(EntityPlayer player: team.getPlayersAsEntity()) {
//						player.addChatMessage(new ChatComponentText(String.format("Experiment Will be starting in %d seconds!", (this.WAITSPAWNTICKS-tickCount)/20)));
//					}
//					this.scoreboard.updateScore(team, 0);
//				}
			}else if(tickCount >= this.WAITSPAWNTICKS){
				for(Team team: scoreboard.getTeams()) {
					for(EntityPlayer player: team.getPlayersAsEntity()) {
						spawnPlayerInGame((EntityPlayerMP)player, team.getSpawn()[0], team.getSpawn()[1], team.getSpawn()[2]); 	
						ServerEnforcer.INSTANCE.freezePlayer(false, (EntityPlayerMP)player);	//unfreeze players to start!
						player.addChatMessage(new ChatComponentText("\u00A7aSTART"));
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
//			for(Float score : this.scoreboard.getScores()) {
//				if (score >= MAXSCORE) { //end if the team reaches the maximum score.
//					currentState = State.Ending;
//					break;
//				}
//			}
			if(tickCount == this.halfTimeTicks) {
				currentState = State.Halftime;
			}
			else if(tickCount >= maxTicks) {
				
				currentState = State.Ending;
			
			}
			
//			else if(tickCount % 600 == 0) {
//				for(EntityPlayer player: scoreboard.getPlayersAsEntity()){
//					if(tickCount < this.halfTimeTicks) {
//						player.addChatMessage(new ChatComponentText("Seconds until half-time: §a" + (this.halfTimeTicks-tickCount)/20));
//					}else {
//					player.addChatMessage(new ChatComponentText("Seconds remaining: §a" + (maxTicks-tickCount)/20));
//					}
//				}
//			}else if(maxTicks-tickCount < 600) {
//				if(tickCount % 60 == 0) {
//					for(EntityPlayer player: scoreboard.getPlayersAsEntity()){
//						player.addChatMessage(new ChatComponentText("Seconds remaining: §a" + (maxTicks-tickCount)/20));
//					}
//				}
//			}
		//End of Running state
		}
		
		else if(currentState == State.Halftime){
			if(this.halfTimeTicksRemaining == 2400) {
				Map.Entry<Team, Float> maxEntry = null;
				for (Map.Entry<Team, Float> entry : this.scoreboard.getTeamScores().entrySet()) {
				    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)  {
				        maxEntry = entry;
				    }
				}
				
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					ServerEnforcer.INSTANCE.freezePlayer(true, (EntityPlayerMP)player);
					//clear player inventory
					
					if(this.scoreboard.getPlayerTeam(player.getDisplayName()).equals(maxEntry.getKey())) {
						player.addChatComponentMessage(new ChatComponentText("You're in the Lead!!"));
					} else {
						player.addChatComponentMessage(new ChatComponentText("Don't give up!"));
					}
					player.addChatComponentMessage(new ChatComponentText("It's Half-time! Game resuming in: " + this.halfTimeTicksRemaining/20 + "seconds"));
				}
			}
			
			this.halfTimeTicksRemaining--; //use the halfTimeTicksRemaining counter to
			
			if(this.halfTimeTicksRemaining == 0) {
				currentState = State.Running;
				for(EntityPlayer player: scoreboard.getPlayersAsEntity()) {
					player.addChatComponentMessage(new ChatComponentText("Game resuming... "));
					ServerEnforcer.INSTANCE.freezePlayer(false, (EntityPlayerMP)player);
					
					//After Half-Time, give all players cleats!
					ItemStack[] armor = player.inventory.armorInventory;
					armor[0] = new ItemStack(PolycraftRegistry.getItem("Cleats"));
					player.inventory.armorInventory = armor;
				}
			}
//			else if(this.halfTimeTicksRemaining % 400 == 0) {
//				for(EntityPlayer player: scoreboard.getPlayersAsEntity()) {
//					player.addChatComponentMessage(new ChatComponentText("Game resuming in: " + this.halfTimeTicksRemaining/20 + "seconds"));
//				}
//			}
//			if(this.halfTimeTicksRemaining < 400) {
//				if(this.halfTimeTicksRemaining % 200 == 0) {
//					for(EntityPlayer player: scoreboard.getPlayersAsEntity()) {
//						player.addChatComponentMessage(new ChatComponentText("Game resuming in: " + this.halfTimeTicksRemaining/20 + "seconds"));
//					}
//				}
//			}
			
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
				
				if(tickCount < maxTicks) {
					this.stringToSend = "Your Opponents Left!";
				}else {
					this.stringToSend = maxEntry.getKey().getName() + " Team wins!";
				}
				
				//ServerScoreboard.INSTANCE.sendGameOverUpdatePacket(this.scoreboard, stringToSend);
				
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
				for(ForgeChunkManager.Ticket ticket: tickets) {
					ForgeChunkManager.releaseTicket(ticket);
				}
			}
			tickCount++;
			if(tickCount >= maxTicks + this.WAIT_TELEPORT_UTD_TICKS) {
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					player.inventory.mainInventory = new ItemStack[36];
					player.inventory.armorInventory = new ItemStack[4];
					player.setHealth(20); //provide players maximum health
					//player.getFoodStats().setFoodLevel(20);
					ServerEnforcer.INSTANCE.freezePlayer(false, (EntityPlayerMP)player);
				}
				ServerScoreboard.INSTANCE.sendGameOverUpdatePacket(this.scoreboard, this.stringToSend);
				ExperimentManager.INSTANCE.stop(this.id); //End the experiment and kill this.
			}
			
		}
	}
	
	/**
	 * TODO: Move all of this to the ClientScoreboard and ServerScoreboard class. Contain the data in the CustomScoreboard class
	 * 
	 */
	private void sendTimeUpdate() {
		String clientString = "";
		Color stringColor = new Color(0);
		int secondsLeft = -1;
		switch(this.currentState) {
		case Starting:
			clientString = "Starting in: ";
			secondsLeft = (this.WAITSPAWNTICKS - this.tickCount)/20;
			stringColor = Color.white;
			break;
		case Running:
			clientString = " ";
			if(this.tickCount < this.halfTimeTicks) {
				secondsLeft = (this.halfTimeTicks - this.tickCount)/20;
			}else {
				secondsLeft = (this.maxTicks - this.tickCount)/20;
			}
			stringColor = Color.green;
			break;
		case Halftime:
			clientString = "Half-Time! ";
			secondsLeft = (this.halfTimeTicksRemaining)/20;
			stringColor = Color.yellow;
			break;
		case Ending:
			if(this.hasGameEnded) {
				clientString = "Teleporting in: ";
				secondsLeft = ((this.WAIT_TELEPORT_UTD_TICKS + this.maxTicks) - this.tickCount)/20;
			}
			stringColor = Color.white;
			break;
		default:
			break;
		}
		
		
		if(!clientString.equals("") && secondsLeft > -1) {
			Gson gson = new Gson();
			Type scoreUpdate = new TypeToken<ScoreboardManager.ColoredString>() {}.getType();
			ScoreboardManager.ColoredString cs = new ScoreboardManager.ColoredString(clientString, stringColor, secondsLeft);
			final String updateTimeJson = gson.toJson(cs, scoreUpdate);
			for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
				ServerEnforcer.INSTANCE.sendScoreboardUpdatePackets(updateTimeJson, (EntityPlayerMP) player, ScoreboardManager.DataType.UpdateTime.ordinal());
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
					this.scoreboard.updateScore(base.getCurrentTeam(), this.ownedBaseScoreBonusOnTicks);
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

	private void alertTeam(Team team) {
		for(String player: team.getPlayers()) {
			EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
			playerEntity.addChatMessage(new ChatComponentText("§4Alert: Someone is stealing your base!"));
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
	
	public String getInstructions() {
		String inst = "";
		inst += "Welcome to Capture the Base! Work with your team to collect points before time runs out. ";
		inst += String.format("\n\nYou\'ll have %d seconds to discuss strategy before the game starts, and %d minutes at halftime. ", this.WAITSPAWNTICKS/20, this.halfTimeTicksRemaining/20/60);
		inst += String.format("Run into a base aura to convert it to your team's color. \n" + 
				"\n" + 
				"Neutral base conversion: %2.0f pts. \n" + 
				"\n" + 
				"Enemy base conversion: %3.0f pts. \n\n"
				+ "Each base you control will generate %1.0f pts every %1.0f second.",
				(float)this.claimBaseScoreBonus, 
				(float)this.stealBaseScoreBonus, 
				(float)this.ownedBaseScoreBonusOnTicks, 
				(float)this.updateScoreOnTickRate/20.0);
		
		inst += "\n\n Press 'x' to re-open instructions. Learn more about these and other tools at:"; //ExperimentManager needs to update the URL?
		return inst;
		
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

	public int getHalfTimeTicks() {
		return halfTimeTicksRemaining;
	}

	public int getMaxTicks() {
		return maxTicks;
	}

	public int getWAIT_TELEPORT_UTD_TICKS() {
		return WAIT_TELEPORT_UTD_TICKS;
	}

	public int getTicksToClaimBase() {
		return ticksToClaimBase;
	}

	public float getClaimBaseScoreBonus() {
		return claimBaseScoreBonus;
	}

	public float getStealBaseScoreBonus() {
		return stealBaseScoreBonus;
	}

	public int getUpdateScoreOnTickRate() {
		return updateScoreOnTickRate;
	}

	public int getOwnedBaseScoreBonusOnTicks() {
		return ownedBaseScoreBonusOnTicks;
	}

	public int getWAITSPAWNTICKS() {
		return WAITSPAWNTICKS;
	}


}
