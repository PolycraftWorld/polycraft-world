package edu.utd.minecraft.mod.polycraft.experiment;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.storage.core.Logger;

import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentList;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureBase;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.scoreboards.ScoreboardManager;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.util.Analytics;
import edu.utd.minecraft.mod.polycraft.util.Analytics.Category;
import edu.utd.minecraft.mod.polycraft.util.PlayerExperimentEvent0;
import edu.utd.minecraft.mod.polycraft.util.PlayerExperimentEvent1;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeChunkManager;

public class ExperimentCTB extends Experiment{
	protected ArrayList<FeatureBase> bases= new ArrayList<FeatureBase>();
	protected int tickCount = 0;
	private boolean hasGameEnded = false;
	public static int[][] spawnlocations = new int[4][3];
	public static LinkedList<Vec3> chests = new LinkedList<Vec3>();
	public static boolean hasBeenGenerated = false;
	
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
	private int maxWaitTimeHalfTime = halfTimeTicksRemaining;
	private int WAIT_TELEPORT_UTD_TICKS = 400;
	//TODO: can you use a real clock instead of "skippable" server ticks??
	private int ticksToClaimBase = 120; //also the same number of ticks to steal base, for now.
	private float claimBaseScoreBonus = 50;
	private float stealBaseScoreBonus = 200;
	private int updateScoreOnTickRate = 20;
	private int ownedBaseScoreBonusOnTicks = 5;
	private int WAITSPAWNTICKS = 400;
	private int ticksToUpdateChests = 100;	//default 5 seconds to update all chest item stacks
	private int itemKBBChance = 7;		//default 70% chance to spawn Knockback bomb 
	private int itemIceChance = 3;		//default 30% chance to spawn packed ice
	private int itemWoodChance = 1;	//default 10% chance to spawn wood
	private int itemNRChance = 1;		//default 10% chance to spawn natural rubber
	private int itemAlumChance = 1;	//default 10% chance to spawn aluminum 
	//public static int maxPlayersNeeded = 4;
	
	private String stringToSend = "";
	
	
	public ExperimentCTB(int id, int size, int xPos, int zPos, World world, int maxteams, int teamsize) {
		super(id, size, xPos, zPos, world);
		this.teamsNeeded = maxteams;
		this.teamSize = teamsize;
		this.playersNeeded = teamsNeeded * teamSize;
		this.awaitingNumPlayers = this.playersNeeded;
		this.scoreboard = ServerScoreboard.INSTANCE.addNewScoreboard();
		for(int x = 0; x < teamsNeeded;x++) {
			this.scoreboard.addNewTeam();
			this.scoreboard.resetScores(0);
		}
		
		//update default parameters:
		this.updateParams(ExperimentParameters.DEFAULT_PARAMS);
		
		tickets = new ForgeChunkManager.Ticket[this.size*this.size];
		
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
		int y = yPos + 6;
		int xOffset = 1;
		int zOffset = 1;
		BoundingBox box = new BoundingBox(xPos + 95.5 + xOffset, zPos + 142.5 + zOffset, 6,y, y+1, Color.GRAY);
		bases.add(new FeatureBase(xPos + 95 + xOffset, y, zPos + 142+ zOffset, box, Color.GRAY));
		box = new BoundingBox(xPos + 132.5 + xOffset, zPos + 142.5, 6+ zOffset,y, y+1, Color.GRAY);
		bases.add(new FeatureBase(xPos + 132 + xOffset, y, zPos + 142+ zOffset, box, Color.GRAY));
		box = new BoundingBox(xPos + 114.5 + xOffset, zPos + 184.5, 6+ zOffset,y, y+1, Color.GRAY);
		bases.add(new FeatureBase(xPos + 114 + xOffset, y, zPos + 184+ zOffset, box, Color.GRAY));
		box = new BoundingBox(xPos + 114.5 + xOffset, zPos + 100.5, 6+ zOffset,y, y+1, Color.GRAY);
		bases.add(new FeatureBase(xPos + 114 + xOffset, y, zPos + 100+ zOffset, box, Color.GRAY));
	
		currentState = State.WaitingToStart;
		
		
		//add extra chests
		chests.add(Vec3.createVectorHelper(xPos + 104, y, zPos + 160));
		chests.add(Vec3.createVectorHelper(xPos + 124, y, zPos + 160));
		chests.add(Vec3.createVectorHelper(xPos + 104, y, zPos + 125));
		chests.add(Vec3.createVectorHelper(xPos + 124, y, zPos + 125));
		
	}
	
	@Override
	public void start(){
		
		if(currentState == State.WaitingToStart) {
			super.start(); //send the updates
			PolycraftMod.logger.debug("Experiment " + this.id +" Start Generation");
			//this.generateStoop();
			currentState = State.GeneratingArea;
			tickCount = 0;
			for(FeatureBase base: bases){
				base.setRendering(true);
			}
			for(Team team: scoreboard.getTeams()) {
				for(String player:team.getPlayers()) {
					EntityPlayer playerEntity = ExperimentManager.INSTANCE.getPlayerEntity(player);
					playerEntity.addChatMessage(new ChatComponentText("Experiment will be starting Shortly. Please wait while the field is generated"));
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
						//set health and food for all players
						player.setGameType(WorldSettings.GameType.ADVENTURE);
						player.setHealth(20); //provide players maximum health
						player.getFoodStats().addStats(20, 40);
						//player.getFoodStats().setFoodLevel(20);
						//give players a stick with knockback == 5.
						ItemStack item = new ItemStack(GameData.getItemRegistry().getObject("stick"));
						item.addEnchantment(Enchantment.knockback, 5); //give them a knockback of 5.
						
						//give players knockback bombs
						ItemStack kbb = new ItemStack(PolycraftRegistry.getItem("Knockback Bomb"), 4);
						//ItemStack fkb = new ItemStack(PolycraftRegistry.getItem("Freezing Knockback Bomb"), 4);
						ItemStack carrot = new ItemStack(GameData.getItemRegistry().getObject("carrot"), 20);
						//add to their inventories.
						player.inventory.addItemStackToInventory(item);
						player.inventory.addItemStackToInventory(kbb);
						//player.inventory.addItemStackToInventory(fkb);
						player.inventory.addItemStackToInventory(carrot);
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
			
			int i=0;
			if(tickCount%20==0) {
			for(Team team: scoreboard.getTeams()) {
				for(EntityPlayer player: team.getPlayersAsEntity()) {
					PlayerExperimentEvent1 event = new PlayerExperimentEvent1(this.id, this.size, this.xPos, this.zPos,this.world, this.teamsNeeded, this.teamSize,player, this.scoreboard.getScores().get(i));
					edu.utd.minecraft.mod.polycraft.util.Analytics.onExperimentEvent1(event);
					}
				i=i+1;
			}
			}
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
			

			if(tickCount % ticksToUpdateChests == 0) {
				for(Vec3 chestPos: chests) {
					TileEntity entity = (TileEntity) world.getTileEntity((int)chestPos.xCoord, (int)chestPos.yCoord , (int)chestPos.zCoord);
					if(entity != null && entity instanceof TileEntityChest) {
						//clear chest contents.
						TileEntityChest chest = (TileEntityChest) InventoryHelper.clearChestContents(entity);
						chestAddRandMats(chest, tickCount > this.halfTimeTicks);
					}
				}
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
			if(this.halfTimeTicksRemaining == this.maxWaitTimeHalfTime) {
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
						player.addChatComponentMessage(new ChatComponentText("Congratulations!! You Won!!"));
								PlayerExperimentEvent0 event = new PlayerExperimentEvent0(this.id, this.size, this.xPos, this.zPos,this.world, this.teamsNeeded, this.teamSize, player,player.getDisplayName());
								edu.utd.minecraft.mod.polycraft.util.Analytics.onExperimentEvent0(event);
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
					
					player.setGameType(WorldSettings.GameType.SURVIVAL); //incase the player changed their mode
					
					player.heal(19); //provide players maximum health
					
					player.getFoodStats().addStats(20, 40);
					
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
		for(FeatureBase base : bases) {
			
			int playerCount = 0;
			switch(base.currentState) {
			case Neutral:
				base.setHardColor(Color.GRAY);
				base.tickCount = 0;
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					if(base.isInBase(player)) {
						//base.tickCount++;
						base.setCurrentTeam(this.scoreboard.getPlayerTeam(player.getDisplayName()).getName());
						base.currentState = FeatureBase.State.Occupied;
						Color newBaseColor = new Color((this.scoreboard.getTeam(base.getCurrentTeamName())).getColor().getRed()/255.0f,
								(this.scoreboard.getTeam(base.getCurrentTeamName())).getColor().getGreen()/255.0f,
								(this.scoreboard.getTeam(base.getCurrentTeamName())).getColor().getBlue()/255.0f,
								0.25f);
						base.setHardColor(newBaseColor);	//sets perm color and resets current color
						((EntityPlayerMP) player).addChatComponentMessage(new ChatComponentText("Attempting to Capture Base: " + (ticksToClaimBase - base.tickCount)/20 + "seconds"));
					}
				}
				if(base.currentState!=FeatureBase.State.Neutral) {	//push update to all players
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
						if (base.getCurrentTeamName() != null && !this.scoreboard.getPlayerTeam(player.getDisplayName()).equals(base.getCurrentTeamName())) { 
								//reset case
								base.currentState = FeatureBase.State.Neutral;
								base.setHardColor(Color.GRAY);
								base.setCurrentTeam(null);
								//ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP) player);
						} else {
							if(base.tickCount % 20 == 0) {
								((EntityPlayerMP) player).addChatComponentMessage(new ChatComponentText((ticksToClaimBase - base.tickCount)/20 + "seconds"));
							}
						}
	
					}
				}
				if(playerCount==0) {
					//case no one in the previously occupied base:
					base.currentState = FeatureBase.State.Neutral;
					base.setHardColor(Color.GRAY);
					base.setCurrentTeam(null);
					break;
				}if(base.tickCount >= ticksToClaimBase) {
					base.currentState = FeatureBase.State.Claimed;
					base.setHardColor((this.scoreboard.getTeam(base.getCurrentTeamName())).getColor());
					base.tickCount=0;
					//TODO: send score update for claiming here.
					this.scoreboard.updateScore(base.getCurrentTeamName(), this.claimBaseScoreBonus);
					//TODO: Add Fireworks
//					ItemStack item= new ItemStack(new ItemFirework());
//					item.getItem().
//					EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(world, base.xPos, base.yPos, base.zPos, item);
//		            world.spawnEntityInWorld(entityfireworkrocket);
				}
				if(base.currentState != FeatureBase.State.Occupied) {
					for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP)player);
					}
				}
				break;
			case Claimed:
				base.setHardColor((this.scoreboard.getTeam(base.getCurrentTeamName())).getColor());
				//TODO: send score update
				if(this.tickCount%this.updateScoreOnTickRate == 0) {
					this.scoreboard.updateScore(base.getCurrentTeamName(), this.ownedBaseScoreBonusOnTicks);
				}
				//playerCount = 0;
				for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
					if(base.isInBase(player)) {
						playerCount++;
						if(!this.scoreboard.getPlayerTeam(player.getDisplayName()).equals(base.getCurrentTeamName())) {
							base.tickCount++; //this goes faster for two players!
							//alert players that a user is stealing their base
							if(base.tickCount%20==0) {
								((EntityPlayerMP) player).addChatComponentMessage(new ChatComponentText("Base Reset to Neutral in: " + (ticksToClaimBase - base.tickCount)/20 + "seconds"));
								alertTeam(this.scoreboard.getTeam(base.getCurrentTeamName()));
							}
							if(base.tickCount>=this.ticksToClaimBase) {
								base.currentState = FeatureBase.State.Neutral;
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
				
				if(base.currentState != FeatureBase.State.Claimed) {
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
			playerEntity.addChatMessage(new ChatComponentText("\u00A74Alert: Someone is stealing your base!"));
		}
	}

	@Override
	public void onClientTickUpdate(){
		if(currentState == State.Starting){
			if(tickCount == 0){
				for(FeatureBase base: bases)
					base.setRendering(true);
				tickCount++;
			}
		}	
	}
	
	public String getInstructions() {
		String inst = "";
		inst += String.format("Welcome to Capture the Base at Twickenham Stadium! Work with your team to collect points before time runs out. You will have %2.0f minutes.",
				(float)this.maxTicks/60/20);
		inst += String.format("\n\nYou\'ll have %d seconds to discuss strategy before the game starts, and %d:%02d minutes at halftime. ",
				this.WAITSPAWNTICKS/20,
				this.halfTimeTicksRemaining/20/60,
				(this.halfTimeTicksRemaining/20) % 60);
		inst += String.format("Run into a base aura to convert it to your team's color. \n" + 
				"\n" + 
				"Neutral base conversion: %2.0f pts. \n" + 
				"\n" + 
				"Enemy base conversion: %3.0f pts. \n\n"
				+ "Each base you control will generate %1.0f pts every %1.0f second(s).",
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
		Type gsonType = new TypeToken<FeatureBase[]>(){}.getType();
		final String updateScoreJson = gson.toJson(this.bases.toArray(), gsonType);
		return updateScoreJson;
	}
	
	@Override
	protected void generateArea(){
		super.generateArea();	//generate the base flat area
		super.generateSpectatorBox();
		for(FeatureBase base: bases){	//generate bases
			base.generate(world);
		}
	}
	
	private FeatureBase isPlayerInAnyBase(EntityPlayerMP player){
		for(FeatureBase base: bases){
			if(base.isInBase((Entity) player))
				return base;
		}
		return null;
	}
	
	@Override
	public void render(Entity entity){
		for(FeatureBase base: bases){
			if(base.isInBase(entity)){
				base.setColor(Color.BLUE);
			}else{
				base.resetColor();
			}
			base.render(entity);
		}
	}
	
	/**
	 * Dynamic get function for getting multiple features of children experiments
	 * @return specified feature
	 */
	public Object getFeature(String feature) {
		if(feature.equals("bases"))
			return bases;
		return null;
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
	
	public int getTicksToUpdateChests() {
		return ticksToUpdateChests;
	}

	public void setTicksToUpdateChests(int ticksToUpdateChests) {
		this.ticksToUpdateChests = ticksToUpdateChests;
	}

	public int getItemKBBChance() {
		return itemKBBChance;
	}

	public void setItemKBBChance(int itemKBBChance) {
		this.itemKBBChance = itemKBBChance;
	}

	public int getItemIceChance() {
		return itemIceChance;
	}

	public void setItemIceChance(int itemIceChance) {
		this.itemIceChance = itemIceChance;
	}

	public int getItemWoodChance() {
		return itemWoodChance;
	}

	public void setItemWoodChance(int itemWoodChance) {
		this.itemWoodChance = itemWoodChance;
	}

	public int getItemNRChance() {
		return itemNRChance;
	}

	public void setItemNRChance(int itemNRChance) {
		this.itemNRChance = itemNRChance;
	}

	public int getItemAlumChance() {
		return itemAlumChance;
	}

	public void setItemAlumChance(int itemAlumChance) {
		this.itemAlumChance = itemAlumChance;
	}
	
	/**
	 * Takes in a chest and fills it randomly with materials for CTB experiments
	 * If halftime has already happened, materials for cleets will spawn as well
	 * @return Void
	 */
	private void chestAddRandMats(TileEntityChest entity, boolean isHalftimeOver) {
		Item kbb = PolycraftRegistry.getItem("Knockback Bomb");
		Item ice = Item.getItemFromBlock(Block.getBlockFromName("packed_ice"));
		Item wood = Item.getItemFromBlock(Block.getBlockById(17)); //Oak Wood Logs
		Item aluminum = Item.getItemFromBlock(PolycraftRegistry.getBlock("Block of Aluminum"));
		Item nr = Item.getItemFromBlock(PolycraftRegistry.getBlock("Block (Natural Rubber)"));
		
		WeightedRandomChestContent[] chestContents = 
				new WeightedRandomChestContent[] 
						{new WeightedRandomChestContent(kbb, 0, 3, 5, itemKBBChance), 
						new WeightedRandomChestContent(ice, 0, 2, 5, itemIceChance), 
						new WeightedRandomChestContent(wood, 0, 1, 2, itemWoodChance), 
						new WeightedRandomChestContent(aluminum, 0, 1, 2, itemAlumChance), 
						new WeightedRandomChestContent(nr, 0, 1, 2, itemNRChance)};
		
		WeightedRandomChestContent.generateChestContents(new Random(), chestContents, entity, 5);
		
	}

	@Override
	protected void updateParams(ExperimentParameters params) {
		//TODO: update Inventories and Chests
		this.ticksToUpdateChests = params.extraParameters.get("Chest: Update Interval")[0]*20;
		this.itemKBBChance = params.extraParameters.get("Chest: KBB wt")[0];
		this.itemIceChance = params.extraParameters.get("Chest: Ice wt")[0];
		this.itemWoodChance = params.extraParameters.get("Chest: Wood wt")[0];
		this.itemNRChance = params.extraParameters.get("Chest: Rubber wt")[0];
		this.itemAlumChance = params.extraParameters.get("Chest: Aluminum wt")[0];
		
		//timing
		this.maxTicks = params.timingParameters.get("Min: Game Time")[0] * 20 * 60;
		this.halfTimeTicksRemaining = params.timingParameters.get("Sec: Half Time")[0] * 20;
		this.WAITSPAWNTICKS = params.timingParameters.get("Sec: Pre-Game")[0] *20;
		this.WAIT_TELEPORT_UTD_TICKS = (params.timingParameters.get("Sec: Post-Game")[0])*20;
		
		//scoring:
		//
		this.claimBaseScoreBonus = (float)Float.parseFloat(params.scoringParameters.get("Pts: Claim Base")[0].toString());
		this.stealBaseScoreBonus = (float)Float.parseFloat(params.scoringParameters.get("Pts: Steal Base")[0].toString());
		this.updateScoreOnTickRate = (int) Math.round((Float.parseFloat(params.scoringParameters.get("Sec: Base Pts Gen")[0].toString())) * 20);
		this.ownedBaseScoreBonusOnTicks = (int) Math.round(Float.parseFloat(params.scoringParameters.get("Pts: Owned Base")[0].toString()));
		this.ticksToClaimBase = (int) Math.round((Float.parseFloat(params.scoringParameters.get("Sec: Claim Base")[0].toString()))* 20);
		
		//update half-time
		
		if(this.ticksToClaimBase == 0) {
			this.ticksToClaimBase = 5;
		}
		
		this.halfTimeTicks = this.maxTicks/2;
		this.maxWaitTimeHalfTime = this.halfTimeTicksRemaining;
		System.out.println("New Params installed");
		if(params == ExperimentParameters.DEFAULT_PARAMS) return;
		ExperimentManager.metadata.get(this.id - 1).updateParams(this.id);
		ExperimentManager.sendExperimentUpdates();
	}


}
