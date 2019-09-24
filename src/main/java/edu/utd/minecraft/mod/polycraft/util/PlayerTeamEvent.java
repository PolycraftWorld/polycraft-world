package edu.utd.minecraft.mod.polycraft.util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.world.World;

import java.util.UUID;

import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentOld;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerTeamEvent extends Event{
	
	public EntityPlayer player;
	String TeamName;
	int id;
	
	public PlayerTeamEvent(int id, String TeamName, EntityPlayer player) {
		// TODO Auto-generated constructor stub
		super();
		this.id=id;
		this.TeamName=TeamName;
		this.player=player;
	}
}

