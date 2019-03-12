package edu.utd.minecraft.mod.polycraft.commands.dev;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.SuperChunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.scoreboards.ClientScoreboard;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CommandTutorial  extends CommandBase{

	private static final String chatCommandTutGen = "gen";
	private static final String chatCommandTutnew = "new";
	private static final String chatCommandTutjoin = "join";
	private static final String chatCommandTutStart = "start";
	private final List aliases;
  
	public CommandTutorial(){
		aliases = new ArrayList(); 
        aliases.add("tutorial"); 
        aliases.add("tut"); 
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "tutorial";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/tutorial";
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
		
		if (world.isRemote) 
        { 
            //processing on Client side - This isn't actually possible.
//			if(args.length == 0) {
//				//clear client side caches
//				ExperimentManager.INSTANCE = new ExperimentManager();
//				ExperimentManager.metadata.clear();
//				ClientScoreboard.INSTANCE.clearDisplay();
//			}
        } 
		else
		{
			if (args.length > 0)
			{
				// generate the challenge room 
				if (chatCommandTutGen.equalsIgnoreCase(args[0])) {
					//generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				} else if (chatCommandTutnew.equalsIgnoreCase(args[0])) {
					//generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				} else if (chatCommandTutjoin.equalsIgnoreCase(args[0])) {
					//generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				} else if (chatCommandTutStart.equalsIgnoreCase(args[0])) {
					//generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				}
			}
		}
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return true;
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
