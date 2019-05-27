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

public class PlayerAIScoreEvent extends Event{
	
	public float score=1.0f;
	public int id1=0;
	public int size1=1;
	public int xPos1=2;
	public int zPos1=3;
	public final World world;
	public int maxteams1=1;
	public int teamsize1=2;
	String teamname;
	//public final String message, username;
    //public final EntityPlayerMP player;
    //public ChatComponentTranslation component;
    public PlayerAIScoreEvent(int id1, int size1, int xPos1, int zPos1,World world1, int maxteams1, int teamsize1,String teamname, Float score)
    {
    	super();
    	//Experiment exp = ExperimentManager.INSTANCE.getExperiment(ExperimentManager.INSTANCE.getRunningExperiment());
        //super(id1,size1, xPos1,zPos1,world1, maxteams1,teamsize1);
        this.id1 = id1;
        this.size1 = size1;
        this.xPos1 = xPos1;
        this.zPos1= zPos1;
        this.world=world1;
        this.maxteams1=maxteams1;
        this.teamsize1=teamsize1;
        this.score=score;
        this.teamname=teamname;
    }
}

