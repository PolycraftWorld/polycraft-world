package edu.utd.minecraft.mod.polycraft.util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerKnockBackEvent extends Event{
	
	public static EntityPlayer player = null;
	String knocked_list;
	
	public PlayerKnockBackEvent(EntityPlayer player,String knocked_list)
    {
    	super();
        this.player=player;
        this.knocked_list=knocked_list;
    }
	
}

