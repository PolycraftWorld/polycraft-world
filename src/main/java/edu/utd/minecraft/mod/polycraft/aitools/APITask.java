package edu.utd.minecraft.mod.polycraft.aitools;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class APITask implements Runnable{

	private PrintWriter out;
	private Socket client;
	private String[] args;
	
	public APITask(PrintWriter out, Socket client, String[] args) {
		this.out = out;
		this.client = client;
		this.args = args;
	}
	
	@Override
	public void run() {
//		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("LL command received"));
//		BotAPI.lowLevel(args);
//		try {
//			BotAPI.data(out, client);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
