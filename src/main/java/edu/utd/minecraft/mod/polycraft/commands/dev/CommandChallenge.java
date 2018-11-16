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

public class CommandChallenge  extends CommandBase{

	private static final String chatCommandChallengeGen = "gen";
	private static final String chatCommandChallengenew = "new";
	private static final String chatCommandChallengejoin = "join";
	private static final String chatCommandChallengeStart = "start";
	private static final String chatCommandChallengeMaxTeams = "maxteams";
	private static final String chatCommandChallengeMaxTeamSize = "teamsize";
	private static final String chatCommandChallengeMaxTicks = "maxticks";
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
				if (chatCommandChallengeGen.equalsIgnoreCase(args[0])) {
					generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				}else if (chatCommandChallengenew.equalsIgnoreCase(args[0])){
					int id = ExperimentManager.getNextID();
					ExperimentManager.INSTANCE.registerExperiment(id, new ExperimentCTB(id, 8, 0, 144,DimensionManager.getWorld(8)));
					ExperimentManager.INSTANCE.init();
				}else if(chatCommandChallengejoin.equalsIgnoreCase(args[0])){
					if(args.length > 1){
						ExperimentManager.INSTANCE.addPlayerToExperiment(Integer.parseInt(args[1]), player);
					}
				}else if(chatCommandChallengeStart.equalsIgnoreCase(args[0])){
					if(args.length > 1) {
						try {
							ExperimentManager.INSTANCE.commandStart(Integer.parseInt(args[1]));
						}catch(NumberFormatException e) {
							sender.addChatMessage(new ChatComponentText("Incorrect format: enter Experiment ID to start"));
						}
					}
						
				}else if(chatCommandChallengeMaxTeams.equalsIgnoreCase(args[0])){
					if (args.length != 5) {
						sender.addChatMessage(new ChatComponentText("Incorrect format: id maxteams {#teams} teamsize {#players}"));
						return;
					}
					try{
						int expID = Integer.parseInt(args[1]);
						int maxTeams = Integer.parseInt(args[2]);
						int teamSize = Integer.parseInt(args[4]);
						sender.addChatMessage(new ChatComponentText(String.format("This command is deprecated")));
						//Experiment.INSTANCE.setTeamSize(teamSize);
						//ExperimentCTB.maxPlayersNeeded = Integer.parseInt(args[1]);
						//sender.addChatMessage(new ChatComponentText(String.format("Success: added experiment with maxteams: %d teamsize: %d", maxTeams, teamSize)));
					}catch(NumberFormatException e) {
						//ERROR
						sender.addChatMessage(new ChatComponentText("Incorrect format: maxteams {#teams} teamsize {#players}"));
					}
				}else if(chatCommandChallengeMaxTicks.equalsIgnoreCase(args[0])){
					try{
						//ExperimentCTB.maxTicks = Integer.parseInt(args[1]);
					}catch(NumberFormatException e) {
						//ERROR
					}
				}else{
					WorldServer worldserver = (WorldServer) player.getEntityWorld();
					EntityPlayerMP playerMP = (EntityPlayerMP) player;
					playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, Integer.parseInt(args[0]),	new PolycraftTeleporter(playerMP.mcServer.worldServerForDimension(Integer.parseInt(args[0]))));
				}
				
			}else {
				if(player.dimension == 8) {
					//Remove the player from the experiment if no arguments are passed
					ExperimentManager.INSTANCE.checkAndRemovePlayerFromExperimentLists(player.getDisplayName());
					//clear player inventory
					player.inventory.mainInventory = new ItemStack[36];
					player.inventory.armorInventory = new ItemStack[4];
					EntityPlayerMP playerMP = (EntityPlayerMP) player;
					playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, 0,	new PolycraftTeleporter(playerMP.mcServer.worldServerForDimension(0)));
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
