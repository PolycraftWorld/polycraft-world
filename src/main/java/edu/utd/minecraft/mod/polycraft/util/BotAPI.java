package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;

import com.google.common.base.Enums;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftCraftingContainer;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.CollectMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.CraftMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.InventoryMessage;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import io.netty.channel.ChannelFutureListener;
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
import net.minecraft.client.gui.GuiScreen;
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
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleIndexedCodec;
import net.minecraftforge.fml.relauncher.Side;

public class BotAPI {
	
	private static final int API_PORT = 9000;
	public static BotAPI INSTANCE= new BotAPI();
	static String fromClient;
	static String toClient;

    static ServerSocket server;
    private static Thread APIThread;
    private static final ClientEnforcer enforcer= ClientEnforcer.INSTANCE;
    
    public static AtomicBoolean apiRunning = new AtomicBoolean(false);
    public static BlockingQueue<String> commandQ = new LinkedBlockingQueue<String>();
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
    	PLACE_BLOCK,
    	PLACE_CRAFTING_TABLE,
    	PLACE_TREE_TAP,
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
	
	public static synchronized void collectFrom(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		World world= player.worldObj;
    	int mouseButtonClicked = 0;
		int mode = 0;
		if(args.length > 3) {
			BlockPos invPos = new BlockPos(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			List<Object> params = new ArrayList<Object>();
			params.add(invPos);
			PolycraftMod.SChannel.sendToServer(new CollectMessage(params));
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
		}
	}
	
	public static void craft(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		List<Object> params = new ArrayList<Object>();
    	params.add(String.join(" ", args));
    	PolycraftMod.SChannel.sendToServer(new CraftMessage(params));
	}
	
	public static void placeBlock(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		if(args.length == 3) {
    		Vec3 breakPos = new Vec3(Integer.parseInt(args[1]), 4, Integer.parseInt(args[2]));
    		Block block = player.worldObj.getBlockState(new BlockPos(breakPos)).getBlock();
    		if(block.getMaterial() != Material.air) {
    			player.sendChatMessage("Block already exists when trying to place block");
    			return;
    		}
    		//first make the player look in the correct location
    		Vec3 vector = breakPos.addVector(0, -1, 0).subtract(new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ));
    		
    		double pitch = ((Math.atan2(vector.zCoord, vector.xCoord) * 180.0) / Math.PI) - 90.0;
    		double yaw  = ((Math.atan2(Math.sqrt(vector.zCoord * vector.zCoord + vector.xCoord * vector.xCoord), vector.yCoord) * 180.0) / Math.PI) - 90.0;
    		
    		player.addChatComponentMessage(new ChatComponentText("x: " + breakPos.xCoord + " :: Y: " + breakPos.yCoord + " :: Z:" + breakPos.zCoord));
    		
    		player.setPositionAndRotation(player.posX, player.posY, player.posZ, (float) pitch, (float) yaw);
    		
			KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), true);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
			KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), false);
		}else {
    		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
    		for(String argument: args) {
    			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
    		}
    	}
		
		
	}
	
	public static void placeCraftingTable(String args[]) {
//		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
//		if(player.inventory.getCurrentItem().getItem() == Blocks.crafting_table.getItem(player.worldObj, player.getPosition())) {
//			return;	//item already selected
//		}else {
//			int slot = 0;
//			for (slot = 0; slot < player.inventory.getSizeInventory(); ++slot)
//	            if (player.inventory.getStackInSlot(slot) != null && player.inventory.getStackInSlot(slot).getItem() == Blocks.crafting_table.getItem(player.worldObj, player.getPosition()))
//	                break;
//			if() {
//				
//			}else {
//				player.sendChatMessage("No Crafting table in inventory");
//			}
//		}
		

		List<Object> params = new ArrayList<Object>();
		params.add(String.join(" ", args));
		PolycraftMod.SChannel.sendToServer(new InventoryMessage(params));
//		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
//		ItemStack inventory[] = player.inventory.mainInventory;
//		int slotToTransfer = -1;
//		loop: for(int x = 0; x < player.inventory.mainInventory.length; x++) {
//			ItemStack item = player.inventory.mainInventory[x];
//			if(item != null && Blocks.crafting_table.getItem(player.worldObj, player.getPosition()) == item.getItem()) {
//				slotToTransfer = x;
//				break loop;
//			}
//		}
//		int slot = player.inventory.currentItem;
//		if(slotToTransfer == -1) {
//			player.addChatComponentMessage(new ChatComponentText("Item not fount"));
//			return;
//		}else if(slotToTransfer == slot) {
//			player.addChatComponentMessage(new ChatComponentText("Item already selected"));
//			return;
//		}
//		if(inventory[slot] == null) {
//			ItemStack stack = player.inventory.getStackInSlot(slotToTransfer);
//			player.inventory.setInventorySlotContents(slot, stack);
//			player.inventory.removeStackFromSlot(slotToTransfer);
//		}else {
//			ItemStack stack = player.inventory.getStackInSlot(slot).copy();
//			player.inventory.setInventorySlotContents(slot, player.inventory.getStackInSlot(slotToTransfer));
//			player.inventory.setInventorySlotContents(slotToTransfer, stack);
//		}
		
		//placeBlock(args);
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
		Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
		Minecraft.getMinecraft().setIngameFocus();
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

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		fromClient = commandQ.poll();
        if(fromClient != null) {
        	System.out.println(fromClient);
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
        		//BotAPI.data(out, client);
            	break;
            case COLLECT_FROM_BLOCK:
            	BotAPI.INSTANCE.collectFrom(args);
            	break;
            case INV_CRAFT_ITEM:
            	BotAPI.craft(args);
            	break;
            case INV_SELECT_ITEM:
            	BotAPI.selectItem(args);
            	break;
            case USE:
            	BotAPI.useItem(args);
            	break;
            case PLACE_BLOCK:
            	BotAPI.placeBlock(args);
            	break;
            case PLACE_CRAFTING_TABLE:
            	BotAPI.placeCraftingTable(args);
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
	}
	
	public static void startAPIThread() {
		
		//To modify thread cases while running, use functions in BotAPI class so you don't have to restart the thread
		APIThread = new Thread("BOT API THREAD")
        {
            private static final String __OBFID = "CL_00001050";
            public void run()
            {
            	if(BotAPI.apiRunning.get()) {

        			try {
						server = new ServerSocket(API_PORT);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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
	                        
	                        try {
	                        	if(fromClient.contains("DATA"))
	                        		data(out, client);
	                        	else
	                        		commandQ.put(fromClient);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
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
