package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
import java.util.UUID;
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
import net.minecraft.client.AnvilConverterException;
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
import net.minecraft.launchwrapper.Launch;
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
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;
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

public class BotAPI {
	
	private static final int API_PORT = 9000;
	public static BotAPI INSTANCE= new BotAPI();
	static String fromClient;
	static String toClient;

    static ServerSocket server;
    private static Thread APIThread;
    private static final ClientEnforcer enforcer= ClientEnforcer.INSTANCE;
    
    public static AtomicBoolean apiRunning = new AtomicBoolean(true);
    public static BlockingQueue<String> commandQ = new LinkedBlockingQueue<String>();
    public static AtomicIntegerArray pos = new AtomicIntegerArray(6);
    public static ArrayList<Vec3> breakList = new ArrayList<Vec3>();
    static final String tempMark = "TEMP_";
    static int delay = 0;
    static String tempQ = null;
    
    public enum APICommand{
    	LL, //LOW LEVEL COMMANDS
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
    	DATA_INV,
    	DATA_MAP,
    	DATA_BOT_POS,
    	RESET,
    	START,
    	TREES,
    	SPEED,
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
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode());
    		//right
    		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), Float.parseFloat(args[13]) == 1);
    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
    	}
    }
    
	public static void moveNorth(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX, player.posY, player.posZ - 1, 180F, 0F);
		//player.setPositionAndRotation(player.posX, player.posY, player.posZ - 1, player.rotationYaw, player.rotationPitch);
//		player.motionZ = -0.3;
//		player.motionY = 0.5;
//		player.sendQueue.addToSendQueue(new C01PacketChatMessage("/time set day"));
	}
	public static void moveSouth(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX, player.posY, player.posZ + 1, 0F, 0F);
//		player.setPositionAndRotation(player.posX, player.posY, player.posZ + 1, player.rotationYaw, player.rotationPitch);
//		player.motionZ = 0.3;
//		player.motionY = 0.5;
	}
	public static void moveEast(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX + 1, player.posY, player.posZ, -90F, 0F);
//		player.setPositionAndRotation(player.posX + 1, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
//		player.motionX = 0.3;
//		player.motionY = 0.5;
	}
	public static void moveWest(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
		player.setPositionAndRotation(player.posX - 1, player.posY, player.posZ, 90F, 0F);
//		player.setPositionAndRotation(player.posX - 1, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
//		player.motionX = -0.3;
//		player.motionY = 0.5;
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
				}else {
					player.addChatComponentMessage(new ChatComponentText("Tried to break air block at: " + breakPos.getX() + ", " + breakPos.getY() + ", " + breakPos.getZ()));
					count++;
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
		
		List<Object> params = new ArrayList<Object>();
		params.add(String.join(" ", args));
		PolycraftMod.SChannel.sendToServer(new InventoryMessage(params));
		
		if(args.length == 4) {
    		Vec3 breakPos = new Vec3(Integer.parseInt(args[2]) + 0.5, 5, Integer.parseInt(args[3]) + 0.5);
    		Block block = player.worldObj.getBlockState(new BlockPos(breakPos)).getBlock();
    		if(block.getMaterial() != Material.air) {
    			player.sendChatMessage("Block \"" + block.getLocalizedName() + "\" already exists when trying to place block");
    			return;
    		}
    		//first make the player look in the correct location
    		Vec3 vector = breakPos.addVector(0, -1, 0).subtract(new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ));
    		
    		double pitch = ((Math.atan2(vector.zCoord, vector.xCoord) * 180.0) / Math.PI) - 90.0;
    		double yaw  = ((Math.atan2(Math.sqrt(vector.zCoord * vector.zCoord + vector.xCoord * vector.xCoord), vector.yCoord) * 180.0) / Math.PI) - 90.0;
    		
    		player.addChatComponentMessage(new ChatComponentText("x: " + breakPos.xCoord + " :: Y: " + breakPos.yCoord + " :: Z:" + breakPos.zCoord));
    		
    		player.setPositionAndRotation(player.posX, player.posY, player.posZ, (float) pitch, (float) yaw);
    		tempQ = "USE";
    		delay = 10;
		}else {
    		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
    		for(String argument: args) {
    			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
    		}
    	}
		
		
	}
	
	public static void placeCraftingTable(String args[]) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
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
		
		if(args.length == 3) {
    		Vec3 breakPos = new Vec3(Integer.parseInt(args[1]) + 0.5, 5, Integer.parseInt(args[2])+0.5);
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
    		
//			KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), true);
//    		KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode());
//			KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), false);
		}else {
    		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
    		for(String argument: args) {
    			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
    		}
    	}
		
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
            s = 5000 + "_" + s;

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

        String searchString = tempMark + 5000 + "_";

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
		if(delay > 0) {
			delay--;
		}else {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			if(tempQ == null)
				fromClient = commandQ.poll();
			else {
				fromClient = tempQ;
				tempQ = null;
			}
			if(fromClient != null) {
	        	System.out.println(fromClient);
	        	String args[] =  fromClient.split("\\s+");
	            switch(Enums.getIfPresent(APICommand.class, args[0]).or(APICommand.DEFAULT)) {
	            case LL:
	            	lowLevel(args);
	            	break;
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
	        		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
	        		for(String argument: args) {
	        			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
	        		}
	        		break;
	            }
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
            		//Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("API Started"));
	                System.out.println("API STARTED");
        			while(BotAPI.apiRunning.get()) {
	                	try {
	                		int x = pos.get(0), y = pos.get(1), z = pos.get(2);
	                    	int xMax = pos.get(3), yMax = pos.get(4), zMax = pos.get(5);
	                    	
	                    	BlockPos pos = new BlockPos(x, y, z);
	                		Socket client = server.accept();
	                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	                        PrintWriter out = new PrintWriter(client.getOutputStream(),true);
	                        final String fromClient = in.readLine();
	                        
	                        try {
	                        	if(fromClient.contains("DATA_INV"))
	                        		dataInventory(out, client);
	                        	else if(fromClient.contains("DATA_MAP"))
	                        		dataMap(out, client);
	                        	else if(fromClient.contains("DATA_BOT_POS"))
	                        		dataBotPos(out, client);
	                        	else if(fromClient.contains("DATA"))
	                        		data(out, client);
	                        	else if(fromClient.contains("START")) {
	                        		IThreadListener mainThread = Minecraft.getMinecraft();
		                            mainThread.addScheduledTask(new Runnable()
		                            {
		                                @Override
		                                public void run()
		                                {
		                                	BotAPI.start(fromClient.split(" "));
		                                }
		                            });
		                        }
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
