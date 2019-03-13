package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.entity.ai.EntityAICaptureBases;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityAndroid;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureBase;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.minigame.BoundingBox;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.ScoreboardManager;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeChunkManager;

public class ExperimentTutorial extends Experiment{

//	public ExperimentTutorial(int id, int size, int xPos, int zPos, World world, Schematic schematic) {
//		super(id, size, xPos, zPos, world, schematic);
//		// TODO Auto-generated constructor stub
//	}
//
//	
	

	protected ArrayList<FeatureBase> bases= new ArrayList<FeatureBase>();
	protected int tickCount = 0;
	private boolean hasGameEnded = false;
	public static int[][] spawnlocations = new int[4][3];
	public static List<Vec3> chests = new LinkedList<Vec3>();
	public static boolean hasBeenGenerated = false;
	
	//experimental params
	public int maxTicks = 12000; //Server drops ticks?
	private int WAIT_TELEPORT_UTD_TICKS = 400;
	//TODO: can you use a real clock instead of "skippable" server ticks??
	private int WAITSPAWNTICKS = 400;
	//animalStats
	
	
	private String stringToSend = "";
	
	/**
	 * 
	 * @param id
	 * @param size
	 * @param xPos
	 * @param zPos
	 * @param world
	 * @param maxteams
	 * @param teamsize
	 */
	public ExperimentTutorial(int id, int size, int xPos, int zPos, World world, int maxteams, int teamsize) {
		super(id, size, xPos, zPos, world, ExperimentManager.INSTANCE.flat_field);
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
		
		
		
		tickets = new ForgeChunkManager.Ticket[this.size*this.size];
		
		int maxBases = 8;
		int workarea = this.size*16;
		//int distBtwnBases = (int) ((workarea*1.0)/Math.sqrt(maxBases));
		int counter = 0;
//		for (int x = xPos + distBtwnBases; x < (xPos+size*16 - 1);x+=distBtwnBases){
//			for (int z = zPos + distBtwnBases; z < (zPos+size*16 - 1);z+=distBtwnBases){
//				counter++;
//				BoundingBox box = new BoundingBox(x + 0.5, z + 0.5, 6,yPos+1, yPos+2, Color.GRAY);
//				bases.add(new Base(x, yPos, z, box, Color.GRAY));
//			}
//		}
		int y = yPos + 8;
		int x_offset = 31;
		BoundingBox box = new BoundingBox(xPos + 25.5 + x_offset, zPos + 72.5, 6,y, y+3, Color.GRAY);
		bases.add(new FeatureBase(xPos + 25 + x_offset, y, zPos + 72, box, Color.GRAY));
//		box = new BoundingBox(xPos + 62.5 + x_offset, zPos + 72.5, 6,y, y+3, Color.GRAY);
//		bases.add(new FeatureBase(xPos + 62 + x_offset, y, zPos + 72, box, Color.GRAY));
//		box = new BoundingBox(xPos + 44.5 + x_offset, zPos + 114.5, 6,y, y+3, Color.GRAY);
//		bases.add(new FeatureBase(xPos + 44 + x_offset, y, zPos + 114, box, Color.GRAY));
//		box = new BoundingBox(xPos + 44.5 + x_offset, zPos + 30.5, 6,y, y+3, Color.GRAY);
//		bases.add(new FeatureBase(xPos + 44 + x_offset, y, zPos + 30, box, Color.GRAY));
	
		currentState = State.WaitingToStart;
		
		//add extra chests
//		chests.add(Vec3.createVectorHelper(xPos + 28 + x_offset, y, zPos + 97));
//		chests.add(Vec3.createVectorHelper(xPos + 59 + x_offset, y, zPos + 97));
//		chests.add(Vec3.createVectorHelper(xPos + 26 + x_offset, y, zPos + 57));
//		chests.add(Vec3.createVectorHelper(xPos + 60 + x_offset, y, zPos + 57));
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
			
			if(this.generateFlatArena(ExperimentManager.INSTANCE.flat_field)) {
				currentState = State.Starting;
			}
			genTick++;
		}
		else if(currentState == State.Starting){
			if(tickCount == 0){
				int index = 1; //hotfix for incorrect spawn location order
				world.setWorldTime(1000);
				for(Team team: scoreboard.getTeams()) {
					
					//Don't put armor on Animales
					if(team == null) {
						continue;
					}
					
					this.scoreboard.updateScore(team, 0);
					ItemStack[] armor = new ItemStack[4];
					//*** hotfix armor[3] = armors[currentArmor];
					
					incrementArmor();	//increment armor counter so next team gets a different armor
					if(index > 3) {//spawnlocations[].length)	//reset spawn location index to prevent null pointer exceptions
						index = 0;
					}
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
						player.setGameType(WorldSettings.GameType.ADVENTURE);
						player.setHealth(20); //provide players maximum health
						player.getFoodStats().addStats(20, 40);
						//give players a stick with knockback == 5.
						//ItemStack item = new ItemStack(GameData.getItemRegistry().getObject("stick"));
						//item.addEnchantment(Enchantment.knockback, 15); //give them a knockback of 5.
						
						//give players knockback bombs
						//ItemStack kbb = new ItemStack(PolycraftRegistry.getItem("Knockback Bomb"), 4);
						//ItemStack fkb = new ItemStack(PolycraftRegistry.getItem("Freezing Knockback Bomb"), 4);
						ItemStack carrot = new ItemStack(GameData.getItemRegistry().getObject("carrot"), 20);
						//add to their inventories.
						//player.inventory.addItemStackToInventory(item);
						//player.inventory.addItemStackToInventory(kbb);
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
				
				//EntityAnimal newAnimal = new EntitySheep();
				//newAnimal.setPosition(int x, int y, int z);
				//Spawn animals in experiment: world.spawnEntityInWorld(newAnimal)
				// team for animals
				this.scoreboard.resetScores(0);
				
				
				//Define spawn area for animals as a box bounded by the outer limits of the bases
				int xMax = 0;
				int xMin = 0;
				int zMax = 0;
				int zMin = 0;
				int currentYvalue = 0;
				
				for(FeatureBase currentBase : bases) {
					int currentXvalue = currentBase.getPositionArray()[0];
					currentYvalue = currentBase.getPositionArray()[1] + 2;
					int currentZvalue = currentBase.getPositionArray()[2];
					
					xMax = Math.max(xMax, currentXvalue);
					zMax = Math.max(zMax, currentZvalue);
				}
				
				xMin = xMax;
				zMin = zMax;
				
				for(FeatureBase currentBase : bases) {
					int currentXvalue = currentBase.getPositionArray()[0];
					int currentZvalue = currentBase.getPositionArray()[2];
					
					xMin = Math.min(xMin, currentXvalue);
					zMin = Math.min(zMin, currentZvalue);	
				}

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
			//if(tickCount == this.halfTimeTicks) {
			//	currentState = State.Halftime;
			//}
			if(tickCount >= maxTicks) {
				currentState = State.Ending;			
			}

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
				
				//kill all animals left in the arena
				List<Entity> list = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(this.xPos, this.yPos-5, this.zPos, 
						this.xPos + this.size*16, this.yPos + 20, this.zPos + this.size * 16));
				for(Entity e: list) {
					if(!(e instanceof EntityPlayer)) {
						((EntityLiving)e).setDead();
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
						player.addChatComponentMessage(new ChatComponentText("Congradulations!! You Won!!"));
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
					
					player.setGameType(WorldSettings.GameType.SURVIVAL); //incase the player changed their mode, otherwise, the foodlevel will throw a null pointer
					
					player.heal(19); //provide players maximum health
					
					player.getFoodStats().addStats(20, 40);
					
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
			secondsLeft = (this.maxTicks - this.tickCount)/20;
			
			stringColor = Color.green;
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
						base.currentState = FeatureBase.State.Claimed;
						Color newBaseColor = new Color((this.scoreboard.getTeam(base.getCurrentTeamName())).getColor().getRed()/255.0f,
								(this.scoreboard.getTeam(base.getCurrentTeamName())).getColor().getGreen()/255.0f,
								(this.scoreboard.getTeam(base.getCurrentTeamName())).getColor().getBlue()/255.0f,
								0.25f);
						base.setHardColor(newBaseColor);	//sets perm color and resets current color
					}
				}
				if(base.currentState != FeatureBase.State.Neutral) {	//push update to all players
					for(EntityPlayer player : scoreboard.getPlayersAsEntity()) {
						ServerEnforcer.INSTANCE.sendExperimentUpdatePackets(prepBoundingBoxUpdates(), (EntityPlayerMP)player);
					}
				}
				break;
			case Claimed: // Check if a base is becoming in contention and switch to neutral is taken
				//ClientEnforcer.INSTANCE.setShowTutorialRender(false);
				base.setHardColor(Color.GREEN);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
				//playerCount = 0;
			
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
	
	//TEMOC:
	//TEMOC ExperiMental Oasis Center
	
	public String getInstructions() {
		String inst = "";

		return inst;
		
	}
	
	private void incrementArmor() {
		
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

	public int getMaxTicks() {
		return maxTicks;
	}

	public int getWAIT_TELEPORT_UTD_TICKS() {
		return WAIT_TELEPORT_UTD_TICKS;
	}

	

	public int getWAITSPAWNTICKS() {
		return WAITSPAWNTICKS;
	}

	
	/**
	 * Takes in a chest and fills it randomly with materials for CTB experiments
	 * If halftime has already happened, materials for cleets will spawn as well
	 * @return Void
	 */


	@Override
	protected void updateParams(ExperimentParameters params) {
		//TODO: update Inventories and Chests
		
		//timing
		this.maxTicks = params.timingParameters.get("Min: Game Time")[0] * 20 * 60;
		this.WAITSPAWNTICKS = params.timingParameters.get("Sec: Pre-Game")[0] *20 ;
		this.WAIT_TELEPORT_UTD_TICKS = (params.timingParameters.get("Sec: Post-Game")[0])*20;
		
		//scoring:
		
		
		//animals
		
		//update half-time
		System.out.println("New Params installed");
		ExperimentManager.metadata.get(this.id - 1).updateParams(this.id);
		ExperimentManager.sendExperimentUpdates();
	}

}
