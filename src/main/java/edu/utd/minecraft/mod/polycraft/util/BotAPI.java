package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

import com.google.common.base.Enums;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class BotAPI {
	
	static String fromClient;
	static String toClient;

    static ServerSocket server;
    private static Thread APIThread;
    
    public static AtomicBoolean apiRunning = new AtomicBoolean(false);
    public static AtomicIntegerArray pos = new AtomicIntegerArray(6);
    public static ArrayList<Vec3> breakList = new ArrayList<Vec3>();
    
    public enum APICommand{
    	CHAT,
    	MOVE,
    	MOVE_NORTH,
    	MOVE_SOUTH,
    	MOVE_EAST,
    	MOVE_WEST,
    	MOVE_NORTH_EAST,
    	MOVE_NORTH_WEST,
    	MOVE_SOUTH_EAST,
    	MOVE_SOUTH_WEST,
    	TELEPORT,
    	WALK_TO,
    	JUMP,
    	TURN,
    	TURN_RIGHT,
    	TURN_LEFT,
    	BREAK_BLOCK,
    	COLLECT_FROM_BLOCK,
    	ATTACK,
    	USE,
    	INV_SELECT_ITEM,
    	INV_MOVE_ITEM,
    	INV_CRAFT_ITEM,
    	DATA,
    	RESET,
    	TREES,
    	DEFAULT
    }
    
	public static void moveNorth(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(player.posX, player.posY, player.posZ - 1, player.rotationYaw, player.rotationPitch);
//		player.motionZ = -0.3;
//		player.motionY = 0.5;
//		player.sendQueue.addToSendQueue(new C01PacketChatMessage("/time set day"));
	}
	public static void moveSouth(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(player.posX, player.posY, player.posZ + 1, player.rotationYaw, player.rotationPitch);
//		player.motionZ = 0.3;
//		player.motionY = 0.5;
	}
	public static void moveEast(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(player.posX + 1, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
//		player.motionX = 0.3;
//		player.motionY = 0.5;
	}
	public static void moveWest(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(player.posX - 1, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
//		player.motionX = -0.3;
//		player.motionY = 0.5;
	}
	
	protected static void turn(float yaw) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setAngles(yaw, 0);
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
	
	public static void breakBlock(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if(args.length == 3) {
    		BlockPos breakPos = new BlockPos(Integer.parseInt(args[1]), 4, Integer.parseInt(args[2]));
    		Block block = player.worldObj.getBlockState(breakPos).getBlock();
    		int count = 0;
    		while(count < 4)
				if(block.getMaterial() != Material.air) {
					BotAPI.breakList.add(new Vec3(breakPos.getX() + 0.5, breakPos.getY() + 0.5 + count, breakPos.getZ() + 0.5));
					block = player.worldObj.getBlockState(breakPos.add(0,++count,0)).getBlock();
				}
		}else {
    		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
    		for(String argument: args) {
    			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
    		}
    	}
	}
	
	public static void teleport(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if(args.length == 3) {
    		player.setLocationAndAngles(Integer.parseInt(args[1]) + 0.5, pos.get(1), Integer.parseInt(args[2]) + 0.5, player.rotationYaw, player.rotationPitch);
    	}else {
    		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
    		for(String argument: args) {
    			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
    		}
    	}
	}
	
	public static void walkTo(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	}
	
	public static void move(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	}
	
	public static void collectFrom(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		World world= player.worldObj;
		if(args.length > 3) {
			BlockPos invPos = new BlockPos(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			if(world.getTileEntity(invPos) != null && world.getTileEntity(invPos) instanceof TreeTapInventory) {
				for(int x = 0; x < 4; x++) {
					player.inventory.addItemStackToInventory(((TreeTapInventory)world.getTileEntity(invPos)).removeStackFromSlot(x));
					((TreeTapInventory)world.getTileEntity(invPos)).setInventorySlotContents(x, null);;
				}
				((TreeTapInventory)world.getTileEntity(invPos)).clear();
			}
		}
	}
	
	public static void Craft(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	    ContainerWorkbench dummyContainer = new ContainerWorkbench(player.inventory, player.worldObj, player.getPosition());
		InventoryCrafting craftMatrix = new InventoryCrafting(dummyContainer, 3, 3);
		
		craftMatrix.setInventorySlotContents(0, new ItemStack(Blocks.log));
		ItemStack resultItem = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.worldObj);
		
		HashMap<Integer, Integer> itemsNeeded = new HashMap<Integer, Integer>();
		
		if(resultItem != null) {
			boolean hasItemsNeeded = true;
			for(int x = 0; x < craftMatrix.getSizeInventory(); x++) {
				int itemID = Item.getIdFromItem(craftMatrix.getStackInSlot(x).getItem());
				if(itemsNeeded.containsKey(itemID)) {
					itemsNeeded.put(itemID, itemsNeeded.get(itemID) + 1);
				}else {
					itemsNeeded.put(itemID, 1);
				}
			}
			
			itemCheck: for(int itemID: itemsNeeded.keySet()) {
				int itemCount = 0;
				for(ItemStack itemStack: player.getInventory()) {
					if(Item.getIdFromItem(itemStack.getItem()) == itemID) {
						
					}
				}
			}
			
			player.inventory.addItemStackToInventory(resultItem);
		}else {
			player.sendChatMessage("No Item found for recipe");
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
			player.addChatComponentMessage(new ChatComponentText("Item not fount"));
			return;
		}else if(slotToTransfer == slot) {
			player.addChatComponentMessage(new ChatComponentText("Item already selected"));
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
		//EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), true);
		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), false);
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
		JsonElement result = gson.toJsonTree(map);
		jobject.add("map",result);
		jobject.addProperty("playerX", (int)player.posX - x);
		jobject.addProperty("playerZ", (int)player.posZ - z);
		toClient = jobject.toString();
        out.println(toClient);
        client.getOutputStream().flush();
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
		}
		
		
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
	
	public static void startAPIThread() {
		try {
			server = new ServerSocket(5000);
//	        clientSocket = serverSocket.accept();
//	        out = new PrintWriter(clientSocket.getOutputStream(), true);
//	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//To modify thread cases while running, use functions in BotAPI class so you don't have to restart the thread
		APIThread = new Thread("BOT API THREAD")
        {
            private static final String __OBFID = "CL_00001050";
            public void run()
            {
            	if(BotAPI.apiRunning.get()) {
            		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("API Started"));
	                while(BotAPI.apiRunning.get()) {
	                	try {
	                		int x = pos.get(0), y = pos.get(1), z = pos.get(2);
	                    	int xMax = pos.get(3), yMax = pos.get(4), zMax = pos.get(5);
	                    	
	                    	BlockPos pos = new BlockPos(x, y, z);
	                		Socket client = server.accept();
	                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	                        PrintWriter out = new PrintWriter(client.getOutputStream(),true);
	                        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	                        String fromClient = in.readLine();
	                        if(fromClient != null) {
	                        	String args[] =  fromClient.split("\\s+");
		                        switch(Enums.getIfPresent(APICommand.class, args[0]).or(APICommand.DEFAULT)) {
		                        case CHAT:
		                        	player.addChatComponentMessage(new ChatComponentText(fromClient));
		                        	break;
		                        case JUMP:
		                        	player.jump();
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
		                        case TURN_RIGHT:
		                        	BotAPI.turnRight(args);
		                        	break;
		                        case TURN_LEFT:
		                        	BotAPI.turnLeft(args);
		                        	break;
		                        case BREAK_BLOCK:
		                        	BotAPI.breakBlock(args);
		                        	break;
		                        case TELEPORT:
		                        	BotAPI.teleport(args);
		                        case DATA:
	                        		BotAPI.data(out, client);
		                        	break;
		                        case COLLECT_FROM_BLOCK:
		                        	BotAPI.collectFrom(args);
		                        	break;
		                        case INV_CRAFT_ITEM:
		                        	BotAPI.Craft(args);
		                        	break;
		                        case INV_SELECT_ITEM:
		                        	BotAPI.selectItem(args);
		                        	break;
		                        case USE:
		                        	BotAPI.useItem(args);
		                        	break;
		                        case RESET:
		                        	BotAPI.reset(args);
		                        	break;
		                        case TREES:
		                        	BotAPI.trees(args);
		                        	break;
		                        case DEFAULT:	//break fall through is intentional for default
		                        	BotAPI.defaultAction(args);
		                        default:
		                    		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
		                    		for(String argument: args) {
		                    			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
	                        		}
		                    		break;
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
}
