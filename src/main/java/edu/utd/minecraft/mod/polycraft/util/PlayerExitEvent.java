package edu.utd.minecraft.mod.polycraft.util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.world.World;

import java.util.UUID;

import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerExitEvent extends Event{
	
	public int id=0;
	public String playerName2;
	
	public PlayerExitEvent(int id, String playerName2) {
		// TODO Auto-generated constructor stub
		super();
		this.id=id;
		this.playerName2=playerName2;
	}
}

