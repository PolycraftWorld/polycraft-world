package edu.utd.minecraft.mod.polycraft.util;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerExperimentEvent8 extends Event{

	public String playername;
	public String answers_string;

	public PlayerExperimentEvent8(String playername, String answers_string) {
		// TODO Auto-generated constructor stub
		super();
		this.playername=playername;
		this.answers_string=answers_string;
	}
}
