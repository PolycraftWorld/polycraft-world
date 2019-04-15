package edu.utd.minecraft.mod.polycraft.util;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerHalfTimeGUIEvent extends Event{

	public String playername;
	public String Halftime_GUI_answers;

	public PlayerHalfTimeGUIEvent(String playername, String Halftime_GUI_answers) {
		// TODO Auto-generated constructor stub
		super();
		this.playername=playername;
		this.Halftime_GUI_answers=Halftime_GUI_answers;
	}
}
