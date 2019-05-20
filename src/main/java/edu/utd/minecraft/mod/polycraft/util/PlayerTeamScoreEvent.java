package edu.utd.minecraft.mod.polycraft.util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.world.World;

import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.scoreboards.CustomScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerTeamScoreEvent extends Event{
	
	Float score;
	String teamname;
	int id;

	public PlayerTeamScoreEvent(int id, String teamname, Float score)
    {
    	super();
    	this.id=id;
        this.score=score;
        this.teamname=teamname;
    }
}

