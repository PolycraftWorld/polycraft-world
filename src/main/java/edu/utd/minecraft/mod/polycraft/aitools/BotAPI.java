package edu.utd.minecraft.mod.polycraft.aitools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandBase;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandBreakBlock;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandChat;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandCraft;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandLook;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandMoveDir;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandMoveEgo;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandObservation;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandObservation.ObsType;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandPlaceBlock;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandTeleport;
import edu.utd.minecraft.mod.polycraft.aitools.commands.APICommandTilt;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentDefinition;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty.NoveltyParser;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.APICommandMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.InventoryMessage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.fml.relauncher.Side;

public class BotAPI {
	
	private static final int API_PORT = 9000;
	private static final int TIMEOUT_TICKS = 20;
	private static final int TICKS_PER_SEC = 20;
	private static final float MOVEMENT_SPEED = 4.3F; // meters per second
	private static final float LOOK_SPEED = 5.0F; // Avg human reaction time ~ 200ms or 5 looks per second
	private static final int STEP_COST_PER_TICK = 6;
	
	public static float movementSpeedMod = 1.0f;
	public static float teleportCost = TICKS_PER_SEC * STEP_COST_PER_TICK;
	public static float blockPlaceCost = TICKS_PER_SEC * STEP_COST_PER_TICK / 4;		// Assuming you can place 4 blocks a second
	public static float blockBreakCost = TICKS_PER_SEC * STEP_COST_PER_TICK;		// Assuming you can place 1 blocks a second
	public static float craftCostPerItem = TICKS_PER_SEC * STEP_COST_PER_TICK;
	public static float chatCost = TICKS_PER_SEC * STEP_COST_PER_TICK;
	public static float unitMovementCost = ((TICKS_PER_SEC / MOVEMENT_SPEED) * STEP_COST_PER_TICK) / movementSpeedMod;
	public static float unitLookCost = (TICKS_PER_SEC / LOOK_SPEED) * STEP_COST_PER_TICK;
	
	//sensing costs
	public static float senseScreenCost = STEP_COST_PER_TICK;
	public static float senseRecipeCost = TICKS_PER_SEC * STEP_COST_PER_TICK * 10;	// this should usually only happen once (unless you are dumbbbb)
	public static float senseInventoryCost = TICKS_PER_SEC * STEP_COST_PER_TICK / 2;	// assuming you can check inventory 2 times a second
	public static float senseLocationsCost = STEP_COST_PER_TICK;		// this should be a quick operation
	public static float senseMapCost = unitLookCost + senseScreenCost * 4;		
	public static float senseAllCost = senseInventoryCost + senseLocationsCost + senseMapCost;	

	public static AtomicReference<Float> totalCostIncurred = new AtomicReference<Float>(new Float(0));
	
	public static BotAPI INSTANCE= new BotAPI();
	static String fromClient;
	static String toClient;

    static ServerSocket server;
    private static Thread APIThread;
    private static final ClientEnforcer enforcer= ClientEnforcer.INSTANCE;
    
    public static AtomicBoolean apiRunning = new AtomicBoolean(true);
    public static AtomicBoolean stepEnd = new AtomicBoolean(false);
    public static AtomicBoolean sendScreen = new AtomicBoolean(false);
    public static AtomicReference<MinecraftFrames> frames = new AtomicReference<MinecraftFrames>();
    public static AtomicReference<APICommandResult> commandResult= new AtomicReference<APICommandResult>();
    private static APICommandResult serverResult = null;
    public static BlockingQueue<String> commandQ = new LinkedBlockingQueue<String>();
    private static HashMap<String, APICommandBase> availableCommands = new HashMap<String, APICommandBase>();
    public static AtomicIntegerArray pos = new AtomicIntegerArray(6);
    public static ArrayList<Vec3> breakList = new ArrayList<Vec3>();
    private static boolean breakingBlocks = false;
    private static boolean waitOnResult = false;	// wait for this command to process.  Includes actions done on server side
    private static int waitTimeout = TIMEOUT_TICKS;	// we should only wait about a second for a result
    static int delay = 0;
    static String tempQ = null;

	public static void setResult(APICommandResult result) {
		commandResult.set(result);
		serverResult = null;
		printResult();
		waitOnResult = false;
	}
	
	public static void setServerResult(APICommandResult result) {
		serverResult = result;
	}
	
	private static void printResult() {
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command " + commandResult.get().getResult() + ": " + commandResult.get().getCommand()));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Message: " + commandResult.get().getMessage()));
		int counter = 0;
		if(commandResult.get().getArgs() != null)
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Args: " + String.join(" ", commandResult.get().getArgs())));
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Action Cost: " + commandResult.get().getCost()));
	}
	
	public static void placeStoneBlock(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		List<Object> params = new ArrayList<Object>();
		params.add(String.join(" ", args));
		PolycraftMod.SChannel.sendToServer(new InventoryMessage(params));
		
		Vec3 placePos = new Vec3(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY - 1, player.posZ + player.getHorizontalFacing().getFrontOffsetZ());
		
		if(args.length == 4) 
    		placePos = new Vec3(Integer.parseInt(args[2]) + 0.5, 5, Integer.parseInt(args[3])+0.5);
		Block block = player.worldObj.getBlockState(new BlockPos(placePos)).getBlock();
		if(block.getMaterial() != Material.air) {
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Block \"" + block.getLocalizedName() + "\" already exists when trying to place block"));
			return;
		}else {
			player.sendChatMessage("/setblock " + placePos.xCoord + " " + placePos.yCoord + " " + placePos.zCoord + " stone");
		}
	}
	
	public static void selectItem(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		ItemStack inventory[] = player.inventory.mainInventory;
		int slotToTransfer = -1;
		loop: for(int x = 0; x < player.inventory.mainInventory.length; x++) {
			ItemStack item = player.inventory.mainInventory[x];
			if(item != null && Item.getItemById(Integer.parseInt(args[1])) == item.getItem()) {
				slotToTransfer = x;
				break loop;
			}
		}
		int slot = player.inventory.currentItem;
		if(slotToTransfer == -1) {
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Item not found"));
			return;
		}else if(slotToTransfer == slot) {
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Item already selected"));
			return;
		}
		if(inventory[slot] == null) {
			ItemStack stack = player.inventory.getStackInSlot(slotToTransfer);
			player.inventory.setInventorySlotContents(slot, stack);
			player.inventory.removeStackFromSlot(slotToTransfer);
		}else {
			ItemStack stack = player.inventory.getStackInSlot(slot).copy();
			player.inventory.setInventorySlotContents(slot, player.inventory.getStackInSlot(slotToTransfer));
			player.inventory.setInventorySlotContents(slotToTransfer, stack);
		}
	}
	
	public static void useItem(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), true);
		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), false);
		
		if(String.join(" ", args).toLowerCase().contains("macguffin")) {
			if(commandResult.get().getResult() == Result.SUCCESS) {
				if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {
        			for(TutorialFeature feat: TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getFeatures()) {
        				if(feat.getName().contains("dest")) {
        					double x = -Math.round(Math.sin(Math.toRadians(player.rotationYaw)));
        					double z = Math.round(Math.cos(Math.toRadians(player.rotationYaw)));
        					Vec3i targetPos = new Vec3i(player.posX + x, player.posY + 0.5, player.posZ + z);
        					if(feat.getPos().distanceSq(targetPos) < 1) {
            					APICommandResult temp = commandResult.get();
            					temp.setMessage("Mission Complete");
            					setResult(temp);
        					}
        				}
        			}
				}
			}
		}
	}
	
	public static void reset(String args[]){
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		BotAPI.pos.set(0, ((int)player.posX >> 4) << 4);
		BotAPI.pos.set(1, (int)player.posY);
		BotAPI.pos.set(2, ((int)player.posZ >> 4) << 4);
		BlockPos playerPos = new BlockPos(BotAPI.pos.get(0), BotAPI.pos.get(1),BotAPI.pos.get(2));
		int xMax = 16, yMax = 0, zMax = 16;
		
		if(args.length > 3) {
			BotAPI.pos.set(3, Integer.parseInt(args[1]));
			BotAPI.pos.set(4, Integer.parseInt(args[2]));
			BotAPI.pos.set(5, Integer.parseInt(args[3]));
			xMax = BotAPI.pos.get(3);
			yMax = BotAPI.pos.get(4);
			zMax = BotAPI.pos.get(5);
			
			player.sendChatMessage("/reset setup " + args[1] + " " + args[2] + " " + args[3]);
		}else if(args.length == 2) {
			player.sendChatMessage("/reset " + args[1]);
		}else if(args.length == 3) {
			player.sendChatMessage("/reset " + args[1] + " " + args[2]);
		}
		setResult(new APICommandResult(args, Result.ATTEMPT, "Attempting to start new experiment"));
	}

	public static void toggleAPIThread() {
		if(apiRunning.get()) {
			apiRunning.set(false);
			APIThread.interrupt();
		}
		else {
			apiRunning.set(true);
			startAPIThread();
		}
	}
	
	public static void onClientTick() {
		//we don't want the game to pause
		if(Minecraft.getMinecraft().gameSettings.pauseOnLostFocus)
		{
			Minecraft.getMinecraft().gameSettings.pauseOnLostFocus = false;
			Minecraft.getMinecraft().gameSettings.saveOptions();
		}
		
		if(waitOnResult) {
			if(serverResult != null) {
				setResult(serverResult);
				stepEnd.set(true);
				waitOnResult = false;
			}else if(waitTimeout-- > 0)
				return;
			else {
				commandResult.get().setResult(APICommandResult.Result.ACTION_TIMEOUT);
				commandResult.get().setMessage("Action Timed out. Unknown Result");;
				printResult();
				stepEnd.set(true);
				waitOnResult = false;
			}
		}else if(delay > 0) {
			delay--;
		}else {
			boolean usingTempQ = false;
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			if(tempQ == null)
				fromClient = commandQ.poll();
			else {
				fromClient = tempQ;
				tempQ = null;
				usingTempQ = true;
			}
			
			if(availableCommands.size() == 0) {
				availableCommands.put("MOVE_NORTH", new APICommandMoveDir(unitMovementCost, EnumFacing.NORTH));
				availableCommands.put("MOVE_SOUTH", new APICommandMoveDir(unitMovementCost, EnumFacing.SOUTH));
				availableCommands.put("MOVE_EAST", new APICommandMoveDir(unitMovementCost, EnumFacing.EAST));
				availableCommands.put("MOVE_WEST", new APICommandMoveDir(unitMovementCost, EnumFacing.WEST));
				availableCommands.put("SMOOTH_MOVE", new APICommandMoveEgo(unitMovementCost));	// TODO: look into separating for screen sensing
				
				availableCommands.put("LOOK_NORTH", new APICommandLook(unitLookCost, false, EnumFacing.NORTH));
				availableCommands.put("LOOK_SOUTH", new APICommandLook(unitLookCost, false, EnumFacing.SOUTH));
				availableCommands.put("LOOK_EAST", new APICommandLook(unitLookCost, false, EnumFacing.EAST));
				availableCommands.put("LOOK_WEST", new APICommandLook(unitLookCost, false, EnumFacing.WEST));
				availableCommands.put("SMOOTH_TURN", new APICommandLook(unitLookCost, true));
				availableCommands.put("SMOOTH_TILT", new APICommandTilt(unitLookCost));	// TODO: use Look command for tilt
				
				availableCommands.put("TELEPORT", new APICommandTeleport(unitMovementCost, teleportCost, false));
				availableCommands.put("TP_TO", new APICommandTeleport(unitMovementCost, teleportCost, true));
				availableCommands.put("CHAT", new APICommandChat(chatCost));
				
				availableCommands.put("CRAFT", new APICommandCraft(craftCostPerItem, ""));
				availableCommands.put("CRAFT_AXE", new APICommandCraft(craftCostPerItem, "CRAFT 1 minecraft:planks minecraft:planks 0 minecraft:planks minecraft:stick 0 0 minecraft:stick 0"));
				availableCommands.put("CRAFT_PLANKS", new APICommandCraft(craftCostPerItem, "CRAFT 1 minecraft:log 0 0 0"));
				availableCommands.put("CRAFT_STICKS", new APICommandCraft(craftCostPerItem, "CRAFT 1 minecraft:planks 0 minecraft:planks 0"));
				availableCommands.put("CRAFT_TREE_TAP", new APICommandCraft(craftCostPerItem, "CRAFT 1 minecraft:planks minecraft:stick minecraft:planks minecraft:planks 0 minecraft:planks 0 minecraft:planks 0"));
				availableCommands.put("CRAFT_POGO_STICK", new APICommandCraft(craftCostPerItem, "CRAFT 1 minecraft:stick minecraft:stick minecraft:stick minecraft:planks minecraft:stick minecraft:planks 0 polycraft:sack_polyisoprene_pellets 0"));
				availableCommands.put("CRAFT_CRAFTING_TABLE", new APICommandCraft(craftCostPerItem, "CRAFT 1 minecraft:planks minecraft:planks minecraft:planks minecraft:planks"));
				
				availableCommands.put("PLACE_BLOCK", new APICommandPlaceBlock(blockPlaceCost, ""));
				availableCommands.put("PLACE_MACGUFFIN", new APICommandPlaceBlock(blockPlaceCost, "PLACE_MACGUFFIN polycraft:macguffin MacGuffin"));
				availableCommands.put("PLACE_CRAFTING_TABLE", new APICommandPlaceBlock(blockPlaceCost, "PLACE_CRAFTING_TABLE minecraft:crafting_table"));
				availableCommands.put("PLACE_TREE_TAP", new APICommandPlaceBlock(blockPlaceCost, "PLACE_BLOCK polycraft:tree_tap"));
				
				availableCommands.put("BREAK_BLOCK", new APICommandBreakBlock(blockBreakCost));
				
				availableCommands.put("SENSE_ALL", new APICommandObservation(senseAllCost, ObsType.ALL));
				availableCommands.put("SENSE_INVENTORY", new APICommandObservation(senseInventoryCost, ObsType.INVENTORY));
				availableCommands.put("SENSE_LOCATIONS", new APICommandObservation(senseLocationsCost, ObsType.LOCATIONS));
				availableCommands.put("SENSE_SCREEN", new APICommandObservation(senseScreenCost, ObsType.SCREEN));
				availableCommands.put("SENSE_RECIPE", new APICommandObservation(senseRecipeCost, ObsType.RECIPES));
				// TODO: add command to retrieve
			} 
			
				
			if(fromClient != null) {

				Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
				Minecraft.getMinecraft().setIngameFocus();
	        	System.out.println(fromClient);
	        	String args[] =  fromClient.split("\\s+");
	        	
	        	boolean stepEndValue = true; //true for everything except multi-tick functions. ex. breakblock
	        	if(!usingTempQ)	//don't set this when using a tempQ command.  We want to report the original command result
	        		commandResult.set(new APICommandResult(args, APICommandResult.Result.SUCCESS, ""));
	        	
	        	if(availableCommands.containsKey(args[0].toUpperCase())){
	        		APICommandBase command = availableCommands.get(args[0].toUpperCase());
	        		if(command.getSide() == Side.CLIENT) {
	        			PolycraftMod.logger.info("Running Command on Client side: " + command.getClass().getName());
	        			setResult(command.clientExecute(args));
	        		}else {
	        			PolycraftMod.logger.info("Sending Command to SERVER side: " + command.getClass().getName());
	        			PolycraftMod.SChannel.sendToServer(new APICommandMessage(command, fromClient));
	        			stepEndValue = false;	// TODO: we should only need one of these - SG
	        			waitOnResult = true;
	        		}
	        	}else if(args[0].toUpperCase().startsWith("RESET")) {
	            	BotAPI.reset(args);
        		}else if(args[0].toUpperCase().startsWith("CHECK_COST")) {
        			setResult(new APICommandResult(args, Result.SUCCESS, String.format("Total Cost Incurred: %.1f", totalCostIncurred.get()), STEP_COST_PER_TICK));
        		}else {
	            	setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Command"));
	        		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
	        		for(String argument: args) {
	        			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
	        		}
	            }
	            stepEnd.set(stepEndValue);	//set stepEnd value to update python wrapper with current observations
	        }
		}
	}
	
	public static void startAPIThread() {
		//To modify thread cases while running, use functions in BotAPI class so you don't have to restart the thread
		APIThread = new Thread("BOT API THREAD")
        {
            private static final String __OBFID = "CL_00001050";
            public void run()
            {
            	if(BotAPI.apiRunning.get()) {
            		int port;
            		String portStr = System.getenv("Polycraft_Port");
            		if(portStr != null && NumberUtils.isNumber(portStr)) {
            			port = Integer.parseInt(portStr);
            		}else {
            			port = API_PORT;
            		}
        			try {
						server = new ServerSocket(port);
		                System.out.println("API STARTED");
					} catch (IOException e1) {
		                System.out.println("API FAILED TO START");
						e1.printStackTrace();
						return;
					}
        			while(BotAPI.apiRunning.get()) {
	                	try {
	                		int x = pos.get(0), y = pos.get(1), z = pos.get(2);
	                    	int xMax = pos.get(3), yMax = pos.get(4), zMax = pos.get(5);
	                    	
	                    	BlockPos pos = new BlockPos(x, y, z);
	                		Socket client = server.accept();
	                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	                        PrintWriter out = new PrintWriter(client.getOutputStream(),true);
	                        while(!client.isClosed()) {
	                        	String  fromClient = StringUtils.chomp(in.readLine());
	                        	System.out.println(fromClient);
	                        	try {
		                        	if(fromClient.startsWith("{")) {
		                        		JsonParser parser = new JsonParser();
		                        		JsonObject json = (JsonObject) parser.parse(fromClient);
		                        		fromClient = new String(json.get("command").getAsString() + " " + json.get("argument").getAsString());
		                        	}
	                        	}catch(Exception e) {
	                        		System.out.println("Error trying to parse JSON API call: " + fromClient);
	                        		e.printStackTrace();
	                        	}
	                        	String[] fromClientSplit = fromClient.split(" ");
	                        	final String fromClientFinal = fromClient;
		                        try {
		                        	IThreadListener mainThread;
		                        	
		                        	if(fromClient.startsWith("START")) {
		                        		mainThread = Minecraft.getMinecraft();
			                            mainThread.addScheduledTask(new Runnable()
			                            {
			                                @Override
			                                public void run()
			                                {
			                                	APIHelper.start(fromClientFinal.split(" "));
			                                }
			                            });
			                        }else if(fromClient.equals("LL")) {
			                        	System.out.println("TEST");
			                        	mainThread = Minecraft.getMinecraft();
			                            mainThread.addScheduledTask(new APITask(out, client, fromClient.split(" ")));
			                        }
		                        	else {
		                        		commandQ.put(fromClient);
		                        		stepEnd.set(false);
		                        		Instant time = Instant.now();
		                        		
		                        		while(!stepEnd.get() && Duration.between(time, Instant.now()).getSeconds() < 3) {
			                        		//do nothing until the step is complete
			                        	}
		                        		if(!stepEnd.get() && Duration.between(time, Instant.now()).getSeconds() >= 3)
		                        			setResult(new APICommandResult(fromClientSplit, Result.ACTION_TIMEOUT, "Action timed out on server side", -1337));
		                        		if(fromClient.startsWith("RESET")) {
		                        			totalCostIncurred.set(0F);
		                        			JsonObject jobj = new JsonObject();
		                        			jobj.add("recipes", PolycraftMod.recipeManagerRuntime.getRecipesJsonByContainerType(PolycraftContainerType.POLYCRAFTING_TABLE));
		                        			toClient = jobj.toString();
		                        			out.println(toClient);
		                        	        client.getOutputStream().flush();
		                        		}else {
		                        			// print command result instead of observations
		                        			//toClient = TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getObservations().toString();
		                        			totalCostIncurred.set(totalCostIncurred.get() + commandResult.get().getCost());
		                        			JsonObject jobj = commandResult.get().getJobject();
		                        	        jobj.add("command_result", commandResult.get().toJson());
		                        			toClient = jobj.toString();
		                        			out.println(toClient);
		                        	        client.getOutputStream().flush();
		                        		}
		                        	}
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                        }
	            		} catch (IOException e) {
	            			// TODO Auto-generated catch block
	            			e.printStackTrace();
	            		}
	                }
	            	Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("API Terminated"));
            	}
            }
            
        };
        APIThread.setDaemon(true);
        APIThread.start();
	}
	
	static public boolean setMinecraftClientClockSpeed(String[] args)
    {
		float ticksPerSecond = Float.parseFloat(args[1]);
        boolean devEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        // We need to know, because the member name will either be obfuscated or not.
        String timerMemberName = devEnv ? "timer" : "field_71428_T";
        // NOTE: obfuscated name may need updating if Forge changes - search for "timer" in Malmo\Minecraft\build\tasklogs\retromapSources.log
        Field timer;
        try
        {
            timer = Minecraft.class.getDeclaredField(timerMemberName);
            timer.setAccessible(true);
            timer.set(Minecraft.getMinecraft(), new Timer(ticksPerSecond));
            return true;
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
