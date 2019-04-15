package edu.utd.minecraft.mod.polycraft.util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.world.World;

import java.util.UUID;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;

public class PlayerKnockedBackEvent extends Event{
	
	public static EntityPlayer player = null;
	String entity1;
	
    public PlayerKnockedBackEvent( String entity1, EntityPlayer player)
    {
    	super();
    	this.player=player;
    	this.entity1=entity1;
    }
}

