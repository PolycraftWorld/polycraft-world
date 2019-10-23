package edu.utd.minecraft.mod.polycraft.aitools.domain;

import java.util.ArrayList;

import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;

public interface Domain {

	public ArrayList<BotAPI.APICommand> getActionSpace();
	
	public int getReward();
	
	public void reset();
}
