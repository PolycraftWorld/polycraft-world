package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class BotAPI {
	
	static String fromClient;
	static String toClient;

    static ServerSocket server;
    private static Thread APIThread;
	
    public static AtomicBoolean apiRunning = new AtomicBoolean(false);
    
	public static void moveNorth() {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(player.posX, player.posY, player.posZ - 1, player.rotationYaw, player.rotationPitch);
//		player.motionZ = -0.3;
//		player.motionY = 0.5;
//		player.sendQueue.addToSendQueue(new C01PacketChatMessage("/time set day"));
	}
	public static void moveSouth() {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(player.posX, player.posY, player.posZ + 1, player.rotationYaw, player.rotationPitch);
//		player.motionZ = 0.3;
//		player.motionY = 0.5;
	}
	public static void moveEast() {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(player.posX + 1, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
//		player.motionX = 0.3;
//		player.motionY = 0.5;
	}
	public static void moveWest() {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		player.setPositionAndRotation(player.posX - 1, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
//		player.motionX = -0.3;
//		player.motionY = 0.5;
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
		
		APIThread = new Thread("BOT API THREAD")
        {
            private static final String __OBFID = "CL_00001050";
            public void run()
            {
            	Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("API Started"));
                while(BotAPI.apiRunning.get()) {
                	try {
                    	int x = 160, y = 4, z = 16;
                    	BlockPos pos = new BlockPos(x, y, z);
                		Socket client = server.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        PrintWriter out = new PrintWriter(client.getOutputStream(),true);
                        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                        String fromClient = in.readLine();
                        if(fromClient != null) {
                        	String args[] =  fromClient.split("\\s+");
	                        switch(args[0]) {
	                        case "hello":
	                        	player.addChatComponentMessage(new ChatComponentText(fromClient));
	                        	break;
	                        case "jump":
	                        	player.jump();
	                        	break;
	                        case "movenorth":
	                        	BotAPI.moveNorth();
	                        	break;
	                        case "movesouth":
	                        	BotAPI.moveSouth();
	                        	break;
	                        case "moveeast":
	                        	BotAPI.moveEast();
	                        	break;
	                        case "movewest":
	                        	BotAPI.moveWest();
	                        	break;
	                        case "movenortheast":
	                        	BotAPI.moveNorth();
	                        	BotAPI.moveEast();
	                        	break;
	                        case "movenorthwest":
	                        	BotAPI.moveNorth();
	                        	BotAPI.moveWest();
	                        	break;
	                        case "movesoutheast":
	                        	BotAPI.moveSouth();
	                        	BotAPI.moveEast();
	                        	break;
	                        case "movesouthwest":
	                        	BotAPI.moveSouth();
	                        	BotAPI.moveWest();
	                        	break;
	                        case "turnright":
	                        	player.setAngles(5,0);
	                        	break;
	                        case "turnleft":
	                        	player.setAngles(-5,0);
	                        	break;
	                        case "break":
	                        	if(args.length == 3) {
	                        		BlockPos breakPos = new BlockPos(Integer.parseInt(args[1]), y, Integer.parseInt(args[2]));
	                        		Block block = player.worldObj.getBlockState(breakPos).getBlock();
	                        		if(block.getMaterial() != Material.air) {
	                        			Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(block.stepSound.getBreakSound()), (block.stepSound.getVolume() + 1.0F) / 8.0F, block.stepSound.getFrequency() * 0.5F, Integer.parseInt(args[1]), y, Integer.parseInt(args[2])));
	    	                            
		                        		Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, EnumFacing.UP));
		                        		Minecraft.getMinecraft().playerController.onPlayerDestroyBlock(breakPos, EnumFacing.UP);

		                        		Minecraft.getMinecraft().theWorld.destroyBlock(breakPos, true);
	                        		}
                        		}else {
	                        		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
	                        		for(String argument: args) {
	                        			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
	                        		}
	                        	}
	                        	break;
	                        case "goto":
	                        	if(args.length == 3) {
	                        		player.setLocationAndAngles(Integer.parseInt(args[1]) + 0.5, y, Integer.parseInt(args[2]) + 0.5, player.rotationYaw, player.rotationPitch);
	                        	}else {
	                        		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Command not recognized: " + fromClient));
	                        		for(String argument: args) {
	                        			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(argument));
	                        		}
	                        	}
	                        case "collect":
                        		Gson gson = new Gson();
            					JsonObject jobject = new JsonObject();
            					
            					ArrayList<Integer> map = new ArrayList<Integer>();
            					for(int i = 0; i < 16; i++) {
            						for(int k = 0; k < 16; k++) {
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
	                        	break;
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
        };
        APIThread.setDaemon(true);
        APIThread.start();
	}
}
