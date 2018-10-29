package edu.utd.minecraft.mod.polycraft.commands.dev;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.SuperChunk;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CommandFreeze  extends CommandBase{

	private final List aliases;
  
	public CommandFreeze(){
		aliases = new ArrayList(); 
        aliases.add("freeze");
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "freeze";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/freeze";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		System.out.println("command1");
		if (world.isRemote) 
        { 
            //Client Side
        } 
		else
		{
			//server side
			System.out.println("command2" + "::" + args.length);
			ServerEnforcer.INSTANCE.freezePlayer(true, player);
			if(args.length > 0)
				ServerEnforcer.INSTANCE.freezePlayer(false, player);
		}
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
