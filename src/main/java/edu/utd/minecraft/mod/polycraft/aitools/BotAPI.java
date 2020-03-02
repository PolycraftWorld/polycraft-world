package edu.utd.minecraft.mod.polycraft.aitools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

import javax.print.attribute.standard.NumberUpSupported;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.base.Enums;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.block.BlockMacGuffin;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftRecipe;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentDefinition;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty.NoveltyParser;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation.ObservationScreen;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.BreakBlockMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.CollectMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.CraftMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.InventoryMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.TeleportMessage;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleIndexedCodec;
import net.minecraftforge.fml.relauncher.Side;
import scala.actors.threadpool.Arrays;

public class BotAPI {
	
	private static final int API_PORT = 9000;
	private static final int TIMEOUT_TICKS = 20;
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
    public static AtomicIntegerArray pos = new AtomicIntegerArray(6);
    public static ArrayList<Vec3> breakList = new ArrayList<Vec3>();
    private static boolean breakingBlocks = false;
    private static boolean waitOnResult = false;	// wait for this command to process.  Includes actions done on server side
    private static int waitTimeout = TIMEOUT_TICKS;	// we should only wait about a second for a result
    static final String tempMark = "TEMP_";
    static int delay = 0;
    static String tempQ = null;
    
    public enum APICommand{
    	LL, //LOW LEVEL COMMANDS. Parameters: [forward] [backwards] [strafe-left] [strafe-right] [jump] [crouch] [sprint] [turn left] [turn right] [look up] [look down] [left click] [right click]
    	CHAT,	// send a chat message as the Player. Parameters: String message
    	MOVE,	// Not Implemented
    	MOVE_FORWARD,	// Move Agent 1 block forward
    	MOVE_NORTH,	// Move agent 1 block North
    	MOVE_SOUTH,	// Move agent 1 block South
    	MOVE_EAST,	// Move agent 1 block East
    	MOVE_WEST,	// Move agent 1 block West
    	MOVE_NORTH_EAST,	// Move agent 1 block Northeast 
    	MOVE_NORTH_WEST,	// Move agent 1 block Northwest    	
    	MOVE_SOUTH_EAST,	// Move agent 1 block Southeast
    	MOVE_SOUTH_WEST,	// Move agent 1 block Southwest 
    	SMOOTH_MOVE,
    	SMOOTH_TURN,
    	SMOOTH_TILT,
    	TELEPORT,	// Move agent to specific location. Parameters: int x, int y, int z
    	TP_TO,		// Teleport to a block or entity
    	WALK_TO,	// Not Implemented
    	JUMP,	// Agent jumps once
    	TURN,	// Agent turns left or right. Parameters: float deltaYaw
    	TURN_RIGHT,	// Agent turns right a number of degrees. Parameters: float degrees (default 5)
    	TURN_LEFT,	// Agent turns left a number of degrees. Parameters: float degrees (default 5)
    	LOOK_NORTH,	// Turns agent to face directly North
    	LOOK_SOUTH,	// Turns agent to face directly South
    	LOOK_EAST,	// Turns agent to face directly East
    	LOOK_WEST,	// Turns agent to face directly West
    	BREAK_BLOCK,	// Break a column of four blocks directly in front of agent. 
    	COLLECT_FROM_BLOCK,	// Collect all items from a tree tap
    	ATTACK,	// Left click once
    	USE,	// right click once
    	PLACE_BLOCK,	// Place a specified block in a specified location. Parameters: int blockID, int x, int y, int z
    					// Agent will first send a packet to the server to move item to hotbar and select item. Then look at the position to place block at. Then wait 10 ticks just in case packet transmission is slow, then right click with the "USE" command.
    	PLACE_MACGUFFIN,	//PLACE MACGUFFIN
    	PLACE_STONE,	// Places a stone block in front and at feet level of agent.
    	PLACE_CRAFTING_TABLE,	// Places a crafting table in front of player. Similar to PLACE_BLOCK
    	PLACE_TREE_TAP,		// Place a tree tap in front of player. Similar to PLACE_BLOCK
    	SELECT_AXE,		// Moves and axe to hotbar and selects axe.  Fails if there is no axe in player inventory.
    	SELECT_POGO_STICK,	// Moves and selects Pogo stick to hotbar. Fails if there is no pogo stick in player inventory
    	INV_SELECT_ITEM,	// Moves and selects Pogo stick to hotbar. Fails if there is no pogo stick in player inventory. Parameters: int itemID
    	INV_MOVE_ITEM,		// Not implemented
    	INV_CRAFT_ITEM,		// attempts to craft item if player has all items needed. Parameters: int itemID1, int itemID2, ... , int itemID8, int itemID9
    	CRAFT, 				// INV_CRAFT_ITEM alias
    	CRAFT_PLANKS,		// attempts to craft planks. Fails if not enough resources in player inventory
    	CRAFT_CRAFTING_TABLE,	// attempts to craft a crafting table. Fails if not enough resources in player inventory
    	CRAFT_STICKS,	// attempts to craft sticks. Fails if not enough resources in player inventory
    	CRAFT_AXE,	// attempts to craft an axe. Fails if not enough resources in player inventory
    	CRAFT_TREE_TAP,	// attempts to craft a tree tap. Fails if not enough resources in player inventory
    	CRAFT_POGO_STICK,	// attempts to craft a pogo stick. Fails if not enough resources in player inventory
    	DATA,	// Send all observations to agent
    	DATA_INV,	// Send inventory observation to agent
    	DATA_MAP,	// Send map observation to agent
    	DATA_BOT_POS,	// Send bot position observation to agent
    	DATA_SCREEN,
    	SENSE_ALL,
    	SENSE_LOCATIONS,
    	SENSE_INVENTORY,
    	SENSE_SCREEN,
    	SENSE_RECIPE,
    	GEN_NOVELTY,
    	RESET,	// used to reset the task. Parameters: String params.  Ex usage: "RESET domain pogo" (this will start up the pogostick task)
    	START,	// used to setup a single player flat world and join that world.  This command should be run first to initialize the client
    	TREES,	// Dev command. Add trees to pogo stick task.  
    	SPEED,	// increase or decrease tick rate of base minecraft. Default is 20 ticks per second. Parameters: int ticksPerSecond. Ex usage: "SPEED 200" This will make the client 10x faster.
    	DEFAULT
    }
    
    public static void lowLevel(String args[]) {
    	//args should be LL [forward] [backwards] [strafe-left] [strafe-right] [jump] [crouch] [sprint] [turn left] [turn right] [look up] [look down] [left click] [right click]
    	if(args.length == 14) {
    		//movement
    		//forward
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode(), Float.parseFloat(args[1]) == 1);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode());
    		//backward
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode(), Float.parseFloat(args[2]) == 1);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode());
    		//left
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode(), Float.parseFloat(args[3]) == 1);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode());
    		//right
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode(), Float.parseFloat(args[4]) == 1);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode());
    		
    		//jump
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode(), Float.parseFloat(args[5]) == 1);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode());
    		//crouch
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode(), Float.parseFloat(args[6]) == 1);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode());
    		//sprint
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(), Float.parseFloat(args[7]) == 1);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode());
    		
    		//turning/looking
    		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    		player.setAngles((Float.parseFloat(args[8]) - Float.parseFloat(args[9]) * 600), 
    				(Float.parseFloat(args[10]) - Float.parseFloat(args[11]) * 600));
//    		turn(Float.parseFloat(args[8]) * 600);
//    		turn(Float.parseFloat(args[9]) * -600);
//    		//looking
//    		look(Float.parseFloat(args[10]) * 600);
//    		look(Float.parseFloat(args[11]) * -600);
    		
    		//clicking
    		//left
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode(), Float.parseFloat(args[12]) == 1);
    		if(Float.parseFloat(args[12]) == 1)
    			KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode());
    		//right
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), Float.parseFloat(args[13]) == 1);
    		if(Float.parseFloat(args[13]) == 1)
    			KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
    	}
    }
    
    public static void moveForward(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY + player.getHorizontalFacing().getFrontOffsetY(), 
				player.posZ + player.getHorizontalFacing().getFrontOffsetZ(),player.rotationYaw, 0f);
	}
    
	public static void moveNorth(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX, player.posY, player.posZ - 1, 180F, 0F);
	}
	public static void moveSouth(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX, player.posY, player.posZ + 1, 0F, 0F);
	}
	public static void moveEast(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX + 1, player.posY, player.posZ, -90F, 0F);
	}
	public static void moveWest(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX - 1, player.posY, player.posZ, 90F, 0F);
	}
	
	public static void lookNorth(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, 180F, 0F);
	}
	
	public static void lookSouth(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, 0F, 0F);
	}
	
	public static void lookEast(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5,  -90F, 0F);
	}
	
	public static void lookWest(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, 90F, 0F);
	}
	
	public static void smoothMove(String args[]) {
		if(args.length == 2) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			int angle = 0;
			if(args[1].equalsIgnoreCase("w")) {
				angle = 0;
			}else if(args[1].equalsIgnoreCase("a")) {
				angle = -90;
			}else if(args[1].equalsIgnoreCase("d")) {
				angle = 90;
			}else if(args[1].equalsIgnoreCase("x")) {
				angle = 180;
			}else if(args[1].equalsIgnoreCase("q")) {
				angle = -45;
			}else if(args[1].equalsIgnoreCase("e")) {
				angle = 45;
			}else if(args[1].equalsIgnoreCase("z")) {
				angle = -135;
			}else if(args[1].equalsIgnoreCase("c")) {
				angle = 135;
			}else {
				setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Input"));
			}
			player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
			double x = -Math.round(Math.sin(Math.toRadians(player.rotationYaw + angle)));
			double z = Math.round(Math.cos(Math.toRadians(player.rotationYaw + angle)));
			System.out.println("X: " + x + " :: Z: " + z);
			//check if destination is free of collision 
			if(CheckIfBlockCollide(player.worldObj, player.getPosition().add(x, 0, z))) {
				setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Block in path"));
				return;
			}
			if(!(x == 0 || z == 0)) {	//check if path is free of collisions
				if(CheckIfBlockCollide(player.worldObj, player.getPosition().add(x, 0, 0)) ||
						CheckIfBlockCollide(player.worldObj, player.getPosition().add(0, 0, z))) {
					setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Block in path"));
					return;
				}
			}
			double newX = player.posX - Math.round(Math.sin(Math.toRadians(player.rotationYaw + angle)));
			double newZ = player.posZ + Math.round(Math.cos(Math.toRadians(player.rotationYaw + angle)));
			player.setPositionAndRotation(newX, player.posY, newZ,player.rotationYaw, 0f);
		}else
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax"));
	}
	
	public static boolean CheckIfBlockCollide(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if(block.getMaterial() == Material.air || block instanceof BlockMacGuffin) {
			return false;
		}
		return true;
	}
	
	public static void smoothTurn(String args[]) {
		if(args.length == 2) {
			if(NumberUtils.isNumber(args[1]) && Integer.parseInt(args[1]) % 15 == 0) {
				EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
				float angleDelta = Integer.parseInt(args[1]);
				float playerAngle = (((int)(player.rotationYaw / 15))*15);	// we want to snap to intervals of 15
				for(int x = 0; Math.abs(x) <= Math.abs(angleDelta); x+= angleDelta/5) {
					player.setPositionAndRotation(player.posX, player.posY, player.posZ, playerAngle + x, player.rotationPitch);
				}
			}else {
				setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Input"));	
			}
		}else
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax"));	
	}
	
	public static void smoothTilt(String args[]) {
		
		if(args.length == 2) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			if(args[1].equalsIgnoreCase("forward")) {
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 60F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 45F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 30F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 15F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 0F);
			}
			else if(args[1].equalsIgnoreCase("down")) {
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 0F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 15F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 30F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 45F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 60F);
			}
		}else
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax"));
	}
	
	protected static void turn(float yaw) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setAngles(yaw, player.cameraPitch);
	}
	
	protected static void look(float pitch) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setAngles(player.cameraYaw, pitch);
	}
	
	public static void turnRight(String args[]) {
		if(args.length > 1)
			turn(Float.parseFloat(args[1]));
		else
			turn(5F);
	}
	
	public static void turnLeft(String args[]) {
		if(args.length > 1)
			turn(-Float.parseFloat(args[1]));
		else
			turn(-5F);
	}
	
	public static void setResultWithWait(APICommandResult result) {
		waitOnResult = true;
		waitTimeout = TIMEOUT_TICKS;
		commandResult.set(result);
	}
	
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
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("args: \"" + String.join(" ", commandResult.get().getArgs())));
	}
	
	public static void breakBlock(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		int y = 0;	//y value to break block at. default is 0
		BlockPos breakPos = new BlockPos(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY, player.posZ + player.getHorizontalFacing().getFrontOffsetZ());
		if(args.length == 3) {
    		breakPos = new BlockPos(Integer.parseInt(args[1]), 4, Integer.parseInt(args[2]));
		}else if(args.length == 2) {
			if(NumberUtils.isNumber(args[1])) {
				y = Integer.parseInt(args[1]);
			}
		}
		Block block = player.worldObj.getBlockState(breakPos).getBlock();
		PolycraftMod.SChannel.sendToServer(new BreakBlockMessage(breakPos, String.join(" ", args)));
		waitOnResult = true;
//			BotAPI.breakList.add(new Vec3(breakPos.getX() + 0.5, breakPos.getY() + 0.5 + count, breakPos.getZ() + 0.5));
//			block = player.worldObj.getBlockState(breakPos.add(0,++count,0)).getBlock();
//			breakingBlocks = true;
		
	}
	
	public static void teleport(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if(args.length == 6) {	// expected command "TELEPORT [x] [y] [z] [pitch] [yaw]
			PolycraftMod.SChannel.sendToServer(new TeleportMessage(new BlockPos(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])), 
					String.join(" ", args), Float.parseFloat(args[4]), Float.parseFloat(args[5])));
    	}else {
    		setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax"));
    	}
	}
	
	public static void tpto(String args[]) {	//TP_TO 
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		// should hand one or two arguments.  First argument is target (ex. "BlockPos: x=1, y=4, z=3", "45236")
		//																BlockPos example is for a block.  Number example is for an Entity
		if(args.length > 1) {
			int blockDist = 0;	//default distance for entities.  Teleport to be on top of that entity (ex. picking up an item)
			BlockPos targetPos;
    		boolean isTargetBlock = false;	//False for Entities, true for blocks
    		
    		if(args[1].contains(",")) {//block identifiers are the block position delimited by commas
    			isTargetBlock = true;
				blockDist = 1;	//default block distance for blocks is 1 away or standing next to the block.
    			String[] delimBlockPos = args[1].split(",");
    			targetPos = new BlockPos(Integer.parseInt(delimBlockPos[0]),
    					Integer.parseInt(delimBlockPos[1]),
    					Integer.parseInt(delimBlockPos[2]));
    		}else {
    			int entityId = Integer.parseInt(args[1]);
    			targetPos = player.worldObj.getEntityByID(entityId).getPosition();
    		}
    		
    		//Check if target position is inside our working area
    		AxisAlignedBB area = null;
    		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {	//can only get these values if there is a running experiment
				BlockPos posOffset = new BlockPos(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).posOffset);
    			area = new AxisAlignedBB(posOffset,
    					new BlockPos(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).pos2).add(posOffset));
    			if(!area.isVecInside(new Vec3(targetPos))) {
    				setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Target is outside experiment zone"));
    				return;
    			}
			}
    		
			if(args.length == 3) {	// if blockDistance is a parameter, set it
				if(isTargetBlock && Integer.parseInt(args[2]) == 0) {
					setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Cannot teleport 0 blocks away from a block"));
					return;
				}
				blockDist = Integer.parseInt(args[2]);
    		}
			
			boolean isBlockAccessible = false; //boolean for error checking
			
			
			
			if(!isTargetBlock) {
				PolycraftMod.SChannel.sendToServer(new TeleportMessage(targetPos, String.join(" ", args), player.rotationYaw, player.rotationPitch));
				player.setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
			} else if( blockDist == 1) {
				for(EnumFacing facing: EnumFacing.HORIZONTALS) {
					if(player.worldObj.isAirBlock(targetPos.offset(facing))) {
						if(!area.isVecInside(new Vec3(targetPos.offset(facing))))
							continue;
						targetPos = targetPos.offset(facing);
						
						PolycraftMod.SChannel.sendToServer(new TeleportMessage(targetPos, String.join(" ", args), facing.getOpposite().getHorizontalIndex() * 90f, 0f));
						player.setLocationAndAngles(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, facing.getOpposite().getHorizontalIndex() * 90f, 0f);
						//System.out.println("FACING to: " + (facing.getOpposite().getHorizontalIndex() * 90f));
						isBlockAccessible = true;	// block is accessible at some point
						break;
					}
				}
			} else {
				setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled error"));
			}
			
			if(!isBlockAccessible)
				setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Block not accessible"));
			
    	}else {
    		setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax"));
    	}
	}
	
	public static void walkTo(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	}
	
	public static void move(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	}
	
	public static synchronized void collectFrom(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		World world= player.worldObj;
    	int mouseButtonClicked = 0;
		int mode = 0;
		
		BlockPos invPos = new BlockPos(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY, player.posZ + player.getHorizontalFacing().getFrontOffsetZ());
		if(args.length == 3) {
			invPos = new BlockPos(Integer.parseInt(args[1]), 4, Integer.parseInt(args[2]));
		}
			List<Object> params = new ArrayList<Object>();
			params.add(invPos);
			PolycraftMod.SChannel.sendToServer(new CollectMessage(params));
			waitOnResult = true;
			stepEnd.set(false);
//			NBTTagCompound nbtFeatures = new NBTTagCompound();
//			NBTTagList nbtList = new NBTTagList();
//			nbtFeatures.setString("player", player.getDisplayNameString());
//			nbtFeatures.setInteger("x", invPos.getX());
//			nbtFeatures.setInteger("y", invPos.getY());
//			nbtFeatures.setInteger("z", invPos.getZ());
//			
//			final ByteArrayOutputStream experimentUpdatesTemp = new ByteArrayOutputStream();	//must convert into ByteArray becuase converting with just Gson fails on reveiving end
//			try {
//				CompressedStreamTools.writeCompressed(nbtFeatures, experimentUpdatesTemp);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			Gson gson = new Gson();
//			Type gsonType = new TypeToken<ByteArrayOutputStream>(){}.getType();
//			final String experimentUpdates = gson.toJson(experimentUpdatesTemp, gsonType);
//			enforcer.sendAIPackets(experimentUpdates, 0);
					
			
//			Minecraft.getMinecraft().playerController.onPlayerRightClick(player, Minecraft.getMinecraft().theWorld, player.getCurrentEquippedItem(), invPos, EnumFacing.UP, new Vec3(0,0,0));
//			if(world.getTileEntity(invPos) != null && world.getTileEntity(invPos) instanceof TreeTapInventory) {
//				PolycraftCraftingContainer container = ((TreeTapInventory)world.getTileEntity(invPos)).getCraftingContainer(player.inventory);
//				int windowId = container.windowId;
//				for(int x = 0; x < 1; x++) {
////        					player.inventory.addItemStackToInventory(((TreeTapInventory)world.getTileEntity(invPos)).removeStackFromSlot(x));
////			        ItemStack itemstack = container.slotClick(x, mouseButtonClicked, mode, player);
////			        Minecraft.getMinecraft().playerController.windowClick(windowId, x, mouseButtonClicked, mode, player);
//					container.transferStackInSlot(player, x);
//			        Minecraft.getMinecraft().playerController.windowClick(windowId, x, mouseButtonClicked, mode, player);
//				}
//				//((TreeTapInventory)world.getTileEntity(invPos)).clear();
//			}
//			Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
//			Minecraft.getMinecraft().setIngameFocus();
//		}
	}
	
	public static void craft(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Object> params = new ArrayList<Object>();
    	params.add(String.join(" ", args));
    	PolycraftMod.SChannel.sendToServer(new CraftMessage(params));
    	waitOnResult = true;
    	stepEnd.set(false);
	}
	
	public static void craftPlanks(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Object> params = new ArrayList<Object>();
    	params.add(String.join(" ", args) + " 17 0 0 0 0 0 0 0 0");
    	PolycraftMod.SChannel.sendToServer(new CraftMessage(params));
    	waitOnResult = true;
    	stepEnd.set(false);
	}
	
	public static void craftCraftingTable(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Object> params = new ArrayList<Object>();
    	params.add(String.join(" ", args) + " 5 5 0 5 5 0 0 0 0");
    	PolycraftMod.SChannel.sendToServer(new CraftMessage(params));
    	waitOnResult = true;
    	stepEnd.set(false);
	}
	
	public static void craftAxe(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Object> params = new ArrayList<Object>();
    	params.add(String.join(" ", args) + " 5 5 0 5 280 0 0 280 0");
    	PolycraftMod.SChannel.sendToServer(new CraftMessage(params));
    	waitOnResult = true;
    	stepEnd.set(false);
	}
	
	public static void craftSticks(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Object> params = new ArrayList<Object>();
    	params.add(String.join(" ", args) + " 5 0 0 5 0 0 0 0 0");
    	PolycraftMod.SChannel.sendToServer(new CraftMessage(params));
    	waitOnResult = true;
    	stepEnd.set(false);
	}
	
	public static void craftTreeTap(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Object> params = new ArrayList<Object>();
    	params.add(String.join(" ", args) + " 5 280 5 5 0 5 0 5 0");
    	PolycraftMod.SChannel.sendToServer(new CraftMessage(params));
    	waitOnResult = true;
    	stepEnd.set(false);
	}
	
	public static void craftPogoStick(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Object> params = new ArrayList<Object>();
    	params.add(String.join(" ", args) + " 280 280 280 5 280 5 0 5399 0");
    	PolycraftMod.SChannel.sendToServer(new CraftMessage(params));
    	waitOnResult = true;
    	stepEnd.set(false);
	}
	
	public static void placeBlock(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		List<Object> params = new ArrayList<Object>();
		params.add(String.join(" ", args));
		
		//check that player has the item in inventory
		String itemID = args[1];
    	int slotToTransfer = -1;
		loop: for(int x = 0; x < player.inventory.mainInventory.length; x++) {
			ItemStack item = player.inventory.mainInventory[x];
			if(item != null && Item.getByNameOrId(itemID) == item.getItem()) {
				slotToTransfer = x;
				break loop;
			}
		}
    	int slot = player.inventory.currentItem;
		if(slotToTransfer == -1) {
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Item not found in inventory"));
			return;
		}else if(slotToTransfer != slot) {
			//Item not already selected, need to transfer items on server side
			PolycraftMod.SChannel.sendToServer(new InventoryMessage(params));
		}
		
		Vec3 targetPos = new Vec3(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY + 0.5, player.posZ + player.getHorizontalFacing().getFrontOffsetZ());
		Vec3 targetPlayerPos = new Vec3(player.posX, player.posY, player.posZ);
		if(args.length == 4) 
    		targetPos = new Vec3(Integer.parseInt(args[2]) + 0.5, player.posY + 0.5, Integer.parseInt(args[3])+0.5);
		else if(args.length == 3 && args[2].equalsIgnoreCase("NONAV")) {
    		AxisAlignedBB area = null;
    		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {	//can only get these values if there is a running experiment
				//For NONAV problems, we need to find an air block where we can place this block
				//	This block should be:
				//		1. In the experiment area
				//		2. next to another air block for the player to stand in
				//		3. at the player's y height
    			
    			// get experiment area calculations
    			BlockPos posOffset = new BlockPos(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).posOffset);
    			BlockPos pos2 = new BlockPos(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).pos2).add(posOffset);
    			area = new AxisAlignedBB(posOffset, pos2);
    			
				//Find and Check for valid placement position
    			boolean isBlockAccessible = false;
		search: for(int x=(int)area.minX; x < area.maxX; x++) {	//search for available position
					for(int z=(int)area.minZ; z < area.maxZ; z++) {
						targetPos = new Vec3(x + 0.5, player.posY + 0.5, z + 0.5);	//set new target position
						if(!area.isVecInside(targetPos) || !player.worldObj.isAirBlock(new BlockPos(targetPos))) {	//skip if outside area or isn't air block
		    				continue;
		    			}
						for(EnumFacing facing: EnumFacing.HORIZONTALS) {	//check all sides of the target block
							if(player.worldObj.isAirBlock(new BlockPos(targetPos).offset(facing))) {
								if(!area.isVecInside(new Vec3(new BlockPos(targetPos).offset(facing))))
									continue;
								targetPlayerPos = new Vec3(new BlockPos(targetPos).offset(facing));
								Vec3 vector1 = targetPos.addVector(0, -1, 0).subtract(targetPlayerPos.addVector(0.5, player.getEyeHeight(), 0.5));
								
								float pitch1 = (float) (((Math.atan2(vector1.zCoord, vector1.xCoord) * 180.0) / Math.PI) - 90.0);
								float yaw1  = (float) (((Math.atan2(Math.sqrt(vector1.zCoord * vector1.zCoord + vector1.xCoord * vector1.xCoord), vector1.yCoord) * 180.0) / Math.PI) - 90.0);
								

								PolycraftMod.SChannel.sendToServer(new TeleportMessage(new BlockPos(targetPlayerPos), String.join(" ", args), pitch1, yaw1));
								System.out.println("Teleport to: " + targetPlayerPos.toString());
								isBlockAccessible = true;	// block is accessible at some point
								break;
							}
						}
						if(isBlockAccessible)
							break search;	//end search
					}
				}
    			if(!isBlockAccessible) {
    				setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "No available placement locations"));
    				return;
    			}
    		}
		}else if(args.length == 3 && args[2].equalsIgnoreCase("MacGuffin")) {
			double x = -Math.round(Math.sin(Math.toRadians(player.rotationYaw)));
			double z = Math.round(Math.cos(Math.toRadians(player.rotationYaw)));
			targetPos = new Vec3(player.posX + x, player.posY + 0.5, player.posZ + z);
			System.out.println(targetPos.toString());
		}
		Block block = player.worldObj.getBlockState(new BlockPos(targetPos)).getBlock();
		if(block.getMaterial() != Material.air) {
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Block \"" + block.getRegistryName() + "\" already exists when trying to place block"));
			System.out.println(targetPos.toString());
			return;
		}
		
		
		//first make the player look in the correct location
		Vec3 vector = targetPos.addVector(0, -1, 0).subtract(targetPlayerPos.addVector(0, player.getEyeHeight(), 0));
		
		double pitch = ((Math.atan2(vector.zCoord, vector.xCoord) * 180.0) / Math.PI) - 90.0;
		double yaw  = ((Math.atan2(Math.sqrt(vector.zCoord * vector.zCoord + vector.xCoord * vector.xCoord), vector.yCoord) * 180.0) / Math.PI) - 90.0;
		
		player.addChatComponentMessage(new ChatComponentText("x: " + targetPos.xCoord + " :: Y: " + targetPos.yCoord + " :: Z:" + targetPos.zCoord));
		
		player.setPositionAndRotation(player.posX, player.posY, player.posZ, (float) pitch, (float) yaw);
		tempQ = "USE " + String.join(" ", args);
		delay = 10;
//		}else {
//    		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
//    		for(String argument: args) {
//    			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
//    		}
//    	}
		
		
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
	
	public static void placeMacGuffin(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	placeBlock(("PLACE_CRAFTING_TABLE polycraft:macguffin MacGuffin" + (args.length > 1? " " + String.join(" ", (String[])Arrays.copyOfRange(args, 1, args.length)): "")).split("\\s+"));
	}
	
	public static void placeCraftingTable(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	placeBlock(("PLACE_CRAFTING_TABLE minecraft:crafting_table" + (args.length > 1? " " + String.join(" ", (String[])Arrays.copyOfRange(args, 1, args.length)): "")).split("\\s+"));
	}
	
	public static void placeTreeTap(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	placeBlock(("PLACE_BLOCK polycraft:tree_tap " + (int)(player.posX + player.getHorizontalFacing().getFrontOffsetX()) + 
    			" " + (int)(player.posZ + player.getHorizontalFacing().getFrontOffsetZ())).split("\\s+"));
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
        			for(TutorialFeature feat: TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getActiveFeatures()) {
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
	
	public static void data(PrintWriter out, Socket client) throws IOException {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		Gson gson = new Gson();
		JsonObject jobject = new JsonObject();
		
		int x = pos.get(0), y = pos.get(1), z = pos.get(2);
    	int xMax = pos.get(3), yMax = pos.get(4), zMax = pos.get(5);
    	
    	BlockPos pos = new BlockPos(x, y, z);
		ArrayList<Integer> map = new ArrayList<Integer>();
		for(int i = 0; i <= xMax; i++) {
			for(int k = 0; k <= zMax; k++) {
				map.add(Block.getIdFromBlock(player.worldObj.getBlockState(pos.add(i, 0, k)).getBlock()));
			}
		}
		HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
		for(int i = 0; i < player.inventory.getSizeInventory(); i ++) {
			if(player.inventory.getStackInSlot(i) == null)
				continue;
			int id = Item.getIdFromItem(player.inventory.getStackInSlot(i).getItem());
			if(items.containsKey(id)) {
				items.put(id, items.get(id) + player.inventory.getStackInSlot(i).stackSize);
			}else {
				items.put(id, player.inventory.getStackInSlot(i).stackSize);
			}
		}
		JsonElement itemsResult = gson.toJsonTree(items);
		jobject.add("inventory", itemsResult);
		JsonElement result = gson.toJsonTree(map);
		jobject.add("map",result);
		jobject.addProperty("playerX", (int)player.posX - x);
		jobject.addProperty("playerZ", (int)player.posZ - z);
		toClient = jobject.toString();
        out.println(toClient);
        client.getOutputStream().flush();
	}
	
	public static void dataInventory(PrintWriter out, Socket client) throws IOException {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		Gson gson = new Gson();
		JsonObject jobject = new JsonObject();
		
		HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
		for(int i = 0; i < player.inventory.getSizeInventory(); i ++) {
			if(player.inventory.getStackInSlot(i) == null)
				continue;
			int id = Item.getIdFromItem(player.inventory.getStackInSlot(i).getItem());
			if(items.containsKey(id)) {
				items.put(id, items.get(id) + player.inventory.getStackInSlot(i).stackSize);
			}else {
				items.put(id, player.inventory.getStackInSlot(i).stackSize);
			}
		}
		JsonElement itemsResult = gson.toJsonTree(items);
		jobject.add("inventory", itemsResult);
		toClient = jobject.toString();
        out.println(toClient);
        client.getOutputStream().flush();
	}
	
	public static void dataMap(PrintWriter out, Socket client) throws IOException {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		Gson gson = new Gson();
		JsonObject jobject = new JsonObject();
		
		int x = pos.get(0), y = pos.get(1), z = pos.get(2);
    	int xMax = pos.get(3), yMax = pos.get(4), zMax = pos.get(5);
    	
    	BlockPos pos = new BlockPos(x, y, z);
		ArrayList<Integer> map = new ArrayList<Integer>();
		for(int i = 0; i <= xMax; i++) {
			for(int k = 0; k <= zMax; k++) {
				map.add(Block.getIdFromBlock(player.worldObj.getBlockState(pos.add(i, 0, k)).getBlock()));
			}
		}
		JsonElement result = gson.toJsonTree(map);
		jobject.add("map",result);
		toClient = jobject.toString();
        out.println(toClient);
        client.getOutputStream().flush();
	}
	
	public static void dataBotPos(PrintWriter out, Socket client) throws IOException {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		Gson gson = new Gson();
		JsonObject jobject = new JsonObject();
		
		int x = pos.get(0), y = pos.get(1), z = pos.get(2);
    	
		jobject.addProperty("playerX", (int)player.posX - x);
		jobject.addProperty("playerZ", (int)player.posZ - z);
		toClient = jobject.toString();
        out.println(toClient);
        client.getOutputStream().flush();
	}
	
	public static void senseAll(String args[]) {
		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {
			APICommandResult result = new APICommandResult(args, Result.SUCCESS, "");
			result.setJObject(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getObservations(args.length > 1 ? args[1] : null));
			setResult(result);
		}else {
			setResult(new APICommandResult(args, Result.FAIL, "Not currently in an experiment"));
		}
	}
	
	public static void senseInventory(String args[]) {
		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {
			APICommandResult result = new APICommandResult(args, Result.SUCCESS, "");
			result.setJObject(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getObservation("inventory", args.length > 1 ? args[1] : null));
			setResult(result);
		}else {
			setResult(new APICommandResult(args, Result.FAIL, "Not currently in an experiment"));
		}
	}

	public static void senseLocations(String args[]) {
		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {
			APICommandResult result = new APICommandResult(args, Result.SUCCESS, "");
			result.setJObject(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getLocationObservations(args.length > 1 ? args[1] : null));
			setResult(result);
		}else {
			setResult(new APICommandResult(args, Result.FAIL, "Not currently in an experiment"));
		}
	}
	
	public static void senseScreen(String args[]) {
		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {
			APICommandResult result = new APICommandResult(args, Result.SUCCESS, "");
			result.setJObject(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getVisualObservations(args.length > 1 ? args[1] : null));
			setResult(result);
		}else {
			setResult(new APICommandResult(args, Result.FAIL, "Not currently in an experiment"));
		}
	}
	
	public static void senseRecipe(String args[]) {
		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {
			APICommandResult result = new APICommandResult(args, Result.SUCCESS, "");
			JsonObject jobj = new JsonObject();
			jobj.add("recipes", PolycraftMod.recipeManagerRuntime.getRecipesJsonByContainerType(PolycraftContainerType.POLYCRAFTING_TABLE));
			result.setJObject(jobj);
			setResult(result);
		}else {
			setResult(new APICommandResult(args, Result.FAIL, "Not currently in an experiment"));
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
		
//		for(int i = 0; i <= xMax; i++) {
//			for(int k = 0; k <= zMax; k++) {
//				if(i == 0) {
//					player.worldObj.setBlockState(playerPos.add(i - 1, 0, k), Blocks.wool.getStateFromMeta(k%16), 2);
//					player.worldObj.setBlockState(playerPos.add(i - 1, 1, k), Blocks.wool.getStateFromMeta(k%16), 2);
//					if(k==0) {
//						player.worldObj.setBlockState(playerPos.add(i - 1, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
//						player.worldObj.setBlockState(playerPos.add(i - 1, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
//						player.worldObj.setBlockState(playerPos.add(i, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
//						player.worldObj.setBlockState(playerPos.add(i, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
//					}
//				}
//				if(i == xMax) {
//					player.worldObj.setBlockState(playerPos.add(i + 1, 0, k), Blocks.wool.getStateFromMeta(k%16), 2);
//					player.worldObj.setBlockState(playerPos.add(i + 1, 1, k), Blocks.wool.getStateFromMeta(k%16), 2);
//				}
//				if(k == 0) {
//					player.worldObj.setBlockState(playerPos.add(i + 1, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
//					player.worldObj.setBlockState(playerPos.add(i + 1, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
//				}
//				if(k == zMax) {
//					player.worldObj.setBlockState(playerPos.add(i, 0, k + 1), Blocks.wool.getStateFromMeta(15), 2);
//					player.worldObj.setBlockState(playerPos.add(i, 1, k + 1), Blocks.wool.getStateFromMeta(15), 2);
//				}
//				
//				for(int j = 0; j < 20; j++) {
//					player.worldObj.setBlockToAir(playerPos.add(i, j, k));
//				}
//			}
//		}
	}
	
	public static void start(String args[]){	
		long seed = getWorldSeedFromString("Polycraft AI Gym");
        WorldSettings worldsettings = new WorldSettings(seed, GameType.SURVIVAL, false, false, WorldType.FLAT);
        // This call to setWorldName allows us to specify the layers of our world, and also the features that will be created.
        // This website provides a handy way to generate these strings: http://chunkbase.com/apps/superflat-generator
        worldsettings.setWorldName("Polycraft AI Gym");
        worldsettings.enableCommands(); // Enables cheat commands.
        // Create a filename for this map - we use the time stamp to make sure it is different from other worlds, otherwise no new world
        // will be created, it will simply load the old one.
        createAndLaunchWorld(worldsettings, false);
        Minecraft.getMinecraft().getIntegratedServer().setDifficultyForAllWorlds(EnumDifficulty.PEACEFUL);
        Minecraft.getMinecraft().getIntegratedServer().setAllowFlight(true);;
        for(WorldServer server: Minecraft.getMinecraft().getIntegratedServer().worldServers) {
        	server.getGameRules().setOrCreateGameRule("doMobSpawning", "false");
        	server.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
//        	server.getGameRules().setOrCreateGameRule("doEntityDrops", "false");
//        	server.getGameRules().setOrCreateGameRule("doFireTick", "false");
        	server.getGameRules().setOrCreateGameRule("doImmediateRespawn", "true");
//        	server.getGameRules().setOrCreateGameRule("doMobLoot", "false");
        	server.getGameRules().setOrCreateGameRule("doTraderSpawning", "false");
        	server.getGameRules().setOrCreateGameRule("doWeatherCycle", "false");
        	server.setWorldTime(1000);
        }
        for(BiomeGenBase biome: BiomeGenBase.BIOME_ID_MAP.values()) {
        	biome.setDisableRain();
        }
	}
	
	/** Get a filename to use for creating a new Minecraft save map.<br>
     * Ensure no duplicates.
     * @param isTemporary mark the filename such that the file management code knows to delete this later
     * @return a unique filename (relative to the saves folder)
     */
    public static String getNewSaveFileLocation(boolean isTemporary) {
        File dst;
        File savesDir = FMLClientHandler.instance().getSavesDir();
        do {
            // We used to create filenames based on the current date/time, but this can cause problems when
            // multiple clients might be writing to the same save location. Instead, use a GUID:
            String s = UUID.randomUUID().toString();

            // Add our port number, to help with file management:
            s = 9000 + "_" + s;

            // If this is a temp file, mark it as such:
            if (isTemporary) {
                s = tempMark + s;
            }

            dst = new File(savesDir, s);
        } while (dst.exists());

        return dst.getName();
    }
    /**
     * Creates and launches a unique world according to the settings. 
     * @param worldsettings the world's settings
     * @param isTemporary if true, the world will be deleted whenever newer worlds are created
     * @return
     */
    public static boolean createAndLaunchWorld(WorldSettings worldsettings, boolean isTemporary)
    {
        String s = getNewSaveFileLocation(isTemporary);
        Minecraft.getMinecraft().launchIntegratedServer(s, s, worldsettings);
        cleanupTemporaryWorlds(s);
        return true;
    }
	
	public static long getWorldSeedFromString(String seedString)
    {
        // This seed logic mirrors the Minecraft code in GuiCreateWorld.actionPerformed:
        long seed = (new Random()).nextLong();
        if (seedString != null && !seedString.isEmpty())
        {
            try
            {
                long i = Long.parseLong(seedString);
                if (i != 0L)
                    seed = i;
            }
            catch (NumberFormatException numberformatexception)
            {
                seed = (long)seedString.hashCode();
            }
        }
        return seed;
    }
	
	/**
     * Attempts to delete all Minecraft Worlds with "TEMP_" in front of the name
     * @param currentWorld excludes this world from deletion, can be null
     */
    public static void cleanupTemporaryWorlds(String currentWorld){
        List<SaveFormatComparator> saveList;
        ISaveFormat isaveformat = Minecraft.getMinecraft().getSaveLoader();
        isaveformat.flushCache();

        try{
            saveList = isaveformat.getSaveList();
        } catch (AnvilConverterException e){
            e.printStackTrace();
            return;
        }

        String searchString = tempMark + 9000 + "_";

        for (SaveFormatComparator s: saveList){
            String folderName = s.getFileName();
            if (folderName.startsWith(searchString) && !folderName.equals(currentWorld)){
                isaveformat.deleteWorldDirectory(folderName);
            }
        }
    }
	
	public static void trees(String args[]){
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if(args.length > 1) {
			player.sendChatMessage("/reset trees " + args[1]);
		}else {
			player.sendChatMessage("/reset trees 10");
		}
	}
	
	public static void defaultAction(String args[]){
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		Minecraft.getMinecraft().setIngameFocus();
	}
	
	public static void GenNovelty(String args[]) {
		if(args.length == 4) {
			NoveltyParser parser = new NoveltyParser();
			ExperimentDefinition expDef = new ExperimentDefinition();
			expDef.loadJson(Minecraft.getMinecraft().theWorld, args[2]);
			expDef = parser.transform(expDef, args[1]);
			expDef.saveJson("", args[3]);
		}else {
			setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid format. Expected format: GEN_NOVELTY [novelty config json] [experiment template] [transformed experiment]"));
		}
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
		//if we're breaking blocks, don't do any other actions until we finish
		if(breakingBlocks) {
			if(breakList.isEmpty()) {
				breakingBlocks = false;
				//return DATA and rewards
				stepEnd.set(true);
			}
			else
				return;
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
				
			if(fromClient != null) {

				Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
				Minecraft.getMinecraft().setIngameFocus();
	        	System.out.println(fromClient);
	        	String args[] =  fromClient.split("\\s+");
	        	Method func = null; 
	        	// TODO: Switch to more generic function calling
//	        	try {
//					BotAPI.class.getMethod(Enums.getIfPresent(APICommand.class, args[0]).or(APICommand.DEFAULT).name(), args.getClass());
//				} catch (NoSuchMethodException | SecurityException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	        	try {
//					func.invoke(null, new Object[] {args});
//				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	        	boolean stepEndValue = true; //true for everything except multi-tick functions. ex. breakblock
	        	if(!usingTempQ)	//don't set this when using a tempQ command.  We want to report the original command result
	        		commandResult.set(new APICommandResult(args, APICommandResult.Result.SUCCESS, ""));
	            switch(Enums.getIfPresent(APICommand.class, args[0].toUpperCase()).or(APICommand.DEFAULT)) {
	            case LL:
	            	lowLevel(args);
	            	break;
	            case CHAT:
	            	player.addChatComponentMessage(new ChatComponentText(fromClient));
	            	break;
	            case JUMP:
	            	player.jump();
	            	break;
	            case MOVE_FORWARD:
	            	BotAPI.moveForward(args);
	            	break;
	            case MOVE_NORTH:
	            	BotAPI.moveNorth(args);
	            	break;
	            case MOVE_SOUTH:
	            	BotAPI.moveSouth(args);
	            	break;
	            case MOVE_EAST:
	            	BotAPI.moveEast(args);
	            	break;
	            case MOVE_WEST:
	            	BotAPI.moveWest(args);
	            	break;
	            case MOVE_NORTH_EAST:
	            	BotAPI.moveNorth(args);
	            	BotAPI.moveEast(args);
	            	break;
	            case MOVE_NORTH_WEST:
	            	BotAPI.moveNorth(args);
	            	BotAPI.moveWest(args);
	            	break;
	            case MOVE_SOUTH_EAST:
	            	BotAPI.moveSouth(args);
	            	BotAPI.moveEast(args);
	            	break;
	            case MOVE_SOUTH_WEST:
	            	BotAPI.moveSouth(args);
	            	BotAPI.moveWest(args);
	            	break;
            	case LOOK_NORTH:
	            	BotAPI.lookNorth(args);
	            	break;
	            case LOOK_SOUTH:
	            	BotAPI.lookSouth(args);
	            	break;
	            case LOOK_EAST:
	            	BotAPI.lookEast(args);
	            	break;
	            case LOOK_WEST:
	            	BotAPI.lookWest(args);
	            	break;
	            case SMOOTH_MOVE:
	            	BotAPI.smoothMove(args);
	            	break;
	            case SMOOTH_TURN:
	            	BotAPI.smoothTurn(args);
	            	break;
	            case SMOOTH_TILT:
	            	BotAPI.smoothTilt(args);
	            	break;
	            case TURN_RIGHT:
	            	BotAPI.turnRight(args);
	            	break;
	            case TURN_LEFT:
	            	BotAPI.turnLeft(args);
	            	break;
	            case BREAK_BLOCK:
	            	BotAPI.breakBlock(args);
	            	stepEndValue = false;	//action happens on server
	            	break;
	            case TELEPORT:
	            	BotAPI.teleport(args);
	            	break;
	            case TP_TO:
	            	BotAPI.tpto(args);
	            	break;
	            case DATA:
	            case SENSE_ALL:
	            	senseAll(args);
	            	break;
	            case DATA_INV:
	            case SENSE_INVENTORY:
	            	senseInventory(args);
	            	break;
	            case DATA_BOT_POS:
	            case SENSE_LOCATIONS:
	            	senseLocations(args);
	            	break;
	            case DATA_SCREEN:
	            case SENSE_SCREEN:
	            	senseScreen(args);
	            	break;
	            case SENSE_RECIPE:
	            	senseRecipe(args);
	            	break;
	            case COLLECT_FROM_BLOCK:
	            	BotAPI.INSTANCE.collectFrom(args);
	            	stepEndValue = false;	// action happens on server
	            	break;
	            case CRAFT_CRAFTING_TABLE:
	            	BotAPI.craftCraftingTable(args);
	            	stepEndValue = false;	// action happens on server
	            	break;
	            case CRAFT_PLANKS:
	            	BotAPI.craftPlanks(args);
	            	stepEndValue = false;	// action happens on server
	            	break;
	            case CRAFT_AXE:
	            	BotAPI.craftAxe(args);
	            	stepEndValue = false;	// action happens on server
	            	break;
	            case CRAFT_STICKS:
	            	BotAPI.craftSticks(args);
	            	stepEndValue = false;	// action happens on server
	            	break;
	            case CRAFT_TREE_TAP:
	            	BotAPI.craftTreeTap(args);
	            	stepEndValue = false;	// action happens on server
	            	break;
	            case CRAFT_POGO_STICK:
	            	BotAPI.craftPogoStick(args);
	            	stepEndValue = false;	// action happens on server
	            	break;
	            case CRAFT:
	            case INV_CRAFT_ITEM:
	            	BotAPI.craft(args);
	            	stepEndValue = false;	// action happens on server
	            	break;
	            case INV_SELECT_ITEM:
	            	BotAPI.selectItem(args);
	            	break;
	            case USE:
	            	BotAPI.useItem(args);
	            	break;
	            case PLACE_BLOCK:
	            	BotAPI.placeBlock(args);
	            	stepEndValue = false;	// must wait for USE delay to complete
	            	break;
	            case PLACE_MACGUFFIN:
	            	BotAPI.placeMacGuffin(args);
	            	stepEndValue = false;	//action happens on server
	            	break;
	            case PLACE_STONE:
	            	BotAPI.placeStoneBlock(args);
	            	break;
	            case PLACE_CRAFTING_TABLE:
	            	BotAPI.placeCraftingTable(args);
	            	break;
	            case PLACE_TREE_TAP:
	            	BotAPI.placeTreeTap(args);
	            	break;
	            case GEN_NOVELTY:
	            	BotAPI.GenNovelty(args);
	            	break;
	            case RESET:
	            	BotAPI.reset(args);
	            	break;
	            case START:
	            	BotAPI.start(args);
	            	break;
	            case TREES:
	            	BotAPI.trees(args);
	            	break;
	            case SPEED:
	            	BotAPI.setMinecraftClientClockSpeed(args);
	            	break;
	            case DEFAULT:	//break fall through is intentional for default
	            	BotAPI.defaultAction(args);
	            default:
	            	setResult(new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Command"));
	        		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
	        		for(String argument: args) {
	        			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
	        		}
	        		break;
	            }
	            stepEnd.set(stepEndValue);	//set stepEnd value to update python wrapper with current observations
	        }
			//TODO: return DATA and rewards
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
	                        	String  fromClient = in.readLine();
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
			                                	BotAPI.start(fromClientFinal.split(" "));
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
		                        		while(!stepEnd.get()) {
			                        		//do nothing until the step is complete
			                        	}
		                        		if(fromClient.startsWith("RESET")) {
		                        			
		                        			JsonObject jobj = new JsonObject();
		                        			jobj.add("recipes", PolycraftMod.recipeManagerRuntime.getRecipesJsonByContainerType(PolycraftContainerType.POLYCRAFTING_TABLE));
		                        			toClient = jobj.toString();
		                        			out.println(toClient);
		                        	        client.getOutputStream().flush();
		                        		}else {
		                        			// print command result instead of observations
		                        			//toClient = TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).getObservations().toString();
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
