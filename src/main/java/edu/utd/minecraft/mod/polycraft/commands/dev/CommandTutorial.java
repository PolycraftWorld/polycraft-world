package edu.utd.minecraft.mod.polycraft.commands.dev;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment1PlayerCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FueledLampInventory;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.SuperChunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.ClientScoreboard;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import edu.utd.minecraft.mod.polycraft.worldgen.ResearchAssistantLabGenerator;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
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
				if (chatCommandTutnew.equalsIgnoreCase(args[0])) {
					//generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				} else if (chatCommandTutjoin.equalsIgnoreCase(args[0])) {
					//generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				} else if (chatCommandTutStart.equalsIgnoreCase(args[0])) {
					//generateStructure(sender, (int)player.posX, (int)player.posY, (int)player.posZ, player.getEntityWorld());
				} else if (chatCommandTutGen.equalsIgnoreCase(args[0])) {	//generate tutorial rooms
					generateStructure(sender, sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posY, sender.getPlayerCoordinates().posZ, player.getEntityWorld());
				}
			}
		}
		
	}
	
	public void generateStructure(ICommandSender sender, int xPos, int yPos, int zPos, World world)
	{

		final int maxBlocksPerTick = 65536;
		short n = 0;
		Schematic sch = new Schematic(new NBTTagList(), n, n, n, new int[] {0}, new byte[] {0});
		Schematic sh = sch.get("tutorial.psm");
		
		//number of "lengths" to generate per tick (max X blocks), iterating through at least 1 X per tick, in case the height and width are really big.
		//we don't want the game to lag too much.
		final int maxXPerTick = (int)(Math.max(Math.floor((float)maxBlocksPerTick/(sh.height*sh.width)),1.0));
		int count = 0;
		//still have blocks in the blocks[] array we need to add to the world
		for(int x = 0; x < (int)sh.length; x++){
			for(int y = 0; y<(int)sh.height; y++){
				for(int z = 0; z<(int)sh.width; z++){
					
					int curblock = (int)sh.blocks[count];
					
					if(curblock == 0 || curblock == 76) {
						count++;
						continue;
					}
					else if(curblock == 759) {
						count++;
						continue; //these are Gas Lamps - we don't care for these.
						
					}else if(curblock == 123 || curblock == 124) { //replace redstone lamps (inactive or active) with glowstone.
						world.setBlock(x + xPos, y + yPos , z + zPos, Block.getBlockById(89), 0, 2);
					}
					
					else if(curblock == 95) {
						world.setBlock(x + xPos, y + yPos , z + zPos, Block.getBlockById(curblock), sh.data[count], 2);
						if(sh.data[count] == 5) {
							world.setBlock(x + xPos, y + yPos + 1, z + zPos, Block.getBlockById(171), sh.data[count], 2); //add lime carpet
						}
	
						
					}else if(curblock == 35) {
						world.setBlock(x + xPos, y + yPos , z + zPos, Block.getBlockById(curblock), sh.data[count], 2);
						//System.out.println(x);
						if(sh.data[count] == 5) {
							world.setBlock(x + xPos, y + yPos + 1, z + zPos, Block.getBlockById(171), sh.data[count], 2); //add lime carpet
						}else if(sh.data[count] == 0) {
							world.setBlock(x + xPos, y + yPos + 1, z + zPos, Block.getBlockById(171), sh.data[count], 2); //add white carpet
						}
						
					} else {
						world.setBlock(x + xPos, y + yPos , z + zPos, Block.getBlockById(curblock), sh.data[count], 2);
					}
					
					count++;
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
