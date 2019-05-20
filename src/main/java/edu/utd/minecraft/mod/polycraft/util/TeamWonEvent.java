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

public class TeamWonEvent extends Event {
	
	public String playername = null;
	public static EntityPlayer player = null;
	public int id1=0;
	public int size1=1;
	public int xPos1=2;
	public int zPos1=3;
	public final World world;
	public int maxteams1=1;
	public int teamsize1=2;
	//public final String message, username;
    //public final EntityPlayerMP player;
    //public ChatComponentTranslation component;
    public TeamWonEvent(int id1, int size1, int xPos1, int zPos1,World world1, int maxteams1, int teamsize1, EntityPlayer player, String playername)
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
        this.player=player;
        this.playername=playername;
    }
}

