package edu.utd.minecraft.mod.polycraft.experiment;

import net.minecraft.entity.player.EntityPlayerMP;

public class Participant {
	public final EntityPlayerMP player;
	public int teamID;
	
	public Participant(EntityPlayerMP player, int teamID){
		this.player = player;
		this.teamID = teamID;
	}

}
