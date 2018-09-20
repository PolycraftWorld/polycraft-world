package edu.utd.minecraft.mod.polycraft.commands;

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

public class CommandChallenge  extends CommandBase{

	private static final String chatCommandChallengeGen = "gen";
	private static final String chatCommandChallengereg = "reg";
	private static final String chatCommandChallengeinit = "init";
	private static final String chatCommandChallengeAdd = "add";
	private static final String chatCommandChallengeStart = "start";
	private final List aliases;
  
	public CommandChallenge(){
		aliases = new ArrayList(); 
        aliases.add("challenge"); 
        aliases.add("chal"); 
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "challenge";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/challenge <dim>";
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
            //System.out.println("Not processing on Client side"); 
        } 
		else
		{
			if (args.length > 0)
			{
				// generate the challenge room 
				if (chatCommandChallengeGen.equalsIgnoreCase(args[0])) {
					generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				}else if (chatCommandChallengereg.equalsIgnoreCase(args[0])){
					ExperimentManager.INSTANCE.registerExperiment(1, new ExperimentCTB(1, 8, 0, 144,DimensionManager.getWorld(8)));
				}else if(chatCommandChallengeinit.equalsIgnoreCase(args[0])){
					ExperimentManager.INSTANCE.init();
				}else if(chatCommandChallengeAdd.equalsIgnoreCase(args[0])){
					ExperimentManager.INSTANCE.addPlayerToExperiment(1, player);
				}else if(chatCommandChallengeStart.equalsIgnoreCase(args[0])){
					ExperimentManager.INSTANCE.start(1);
				}else{
					WorldServer worldserver = (WorldServer) player.getEntityWorld();
					EntityPlayerMP playerMP = (EntityPlayerMP) player;
					playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, Integer.parseInt(args[0]),	new PolycraftTeleporter(playerMP.mcServer.worldServerForDimension(8)));
				}
				
			}
		}
		
	}
	
	public void generateStructure(ICommandSender sender, int xPos, int yPos, int zPos, World world)
	{
		Block grass = getBlockByText(sender, "grass");
		boolean result = false;
		for(int x = xPos; x < xPos + 10; x++){
			for(int z = zPos; z < zPos + 10; z++){
				result = world.setBlock(x, yPos, z, grass, 0, 3);
				sender.addChatMessage(new ChatComponentText("Result: " + result + "::X:" + x + "::Y:" + yPos + "::Z:" + z));
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
