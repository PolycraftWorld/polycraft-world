package edu.utd.minecraft.mod.polycraft.commands.dev;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment1PlayerCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialOptions;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FueledLampInventory;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
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
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
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
	private static final String chatCommandTutGUI = "gui";
	private final List aliases;
	

	static ArrayList<TutorialFeature> features = new ArrayList<TutorialFeature>();
	static TutorialOptions tutOptions = new TutorialOptions();
	static String outputFileName = "output";
	static String outputFileExt = ".psm";
	static Vec3 pos, size;
  
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
					registerNewExperiment(player);
				} else if (chatCommandTutjoin.equalsIgnoreCase(args[0])) {
					if(args.length > 1) {
						joinExperiment(Integer.parseInt(args[1]), player);
					}
				} else if (chatCommandTutStart.equalsIgnoreCase(args[0])) {
					if(args.length > 1) {
						startExperiment(Integer.parseInt(args[1]));
					}
				} else if (chatCommandTutGen.equalsIgnoreCase(args[0])) {	//generate tutorial rooms
					generateStructure(sender, sender.getPlayerCoordinates().posX, sender.getPlayerCoordinates().posY, sender.getPlayerCoordinates().posZ, player.getEntityWorld());
				} else if (chatCommandTutGUI.equalsIgnoreCase(args[0])) {	//generate tutorial rooms
					ClientEnforcer.INSTANCE.setShowTutorialRender(!ClientEnforcer.INSTANCE.getShowTutorialRender());
				}
			}
		}
		
	}
	
	private void save() {
//		if(tool.length > 1) {
//			this.outputFileName = tool[1];
//		}
//		if(pos1 && pos2)
//		{
//			int minX;
//			int maxX;
//			int minY;
//			int maxY;
//			int minZ;
//			int maxZ;
//			int[] intArray;
//			short height;
//			short length;
//			short width;
//			
//			if(x1<x2) {
//				minX=x1;
//				maxX=x2;
//			}else{
//				minX=x2;
//				maxX=x1;
//			}
//			if(y1<y2) {
//				minY=y1;
//				maxY=y2;
//			}else{
//				minY=y2;
//				maxY=y1;
//			}
//			if(z1<z2) {
//				minZ=z1;
//				maxZ=z2;
//			}else{
//				minZ=z2;
//				maxZ=z1;
//			}
//			length=(short)(maxX-minX+1);
//			height=(short)(maxY-minY+1);
//			width=(short)(maxZ-minZ+1);
//			int[] blocks = new int[length*height*width];
//			byte[] data = new byte[length*height*width];
//			int count=0;
//			NBTTagCompound nbt = new NBTTagCompound();
//			NBTTagList tiles = new NBTTagList();
//			
//				
//			TileEntity tile;
//			for(int i=0;i<length;i++) {
//				for(int j=0;j<height;j++) {
//					for(int k=0;k<width;k++) {
//						
//						tile = world.getTileEntity(minX+i, minY+j, minZ+k);
//						if(tile!=null){
//							NBTTagCompound tilenbt = new NBTTagCompound();
//							tile.writeToNBT(tilenbt);
//							tiles.appendTag(tilenbt);
//							
//						}
//							
//						Block blk = world.getBlock(minX+i, minY+j, minZ+k);
//						int id = blk.getIdFromBlock(blk);
//						blocks[count]=id;
//						data[count]=(byte) world.getBlockMetadata((int)(minX+i), (int)(minY+j), (int)(minZ+k));
//						count++;
//						
//					}
//				}
//			}
//			nbt.setTag("TileEntity", tiles);
//			FileOutputStream fout = null;
//			try {
//				fout = new FileOutputStream(this.outputFileName + this.outputFileExt);
//				nbt.setShort("Height", height);
//				nbt.setShort("Length", length);
//				nbt.setShort("Width", width);
//				nbt.setShort("OriginMinX", (short)minX);
//				nbt.setShort("OriginMinY", (short)minY);
//				nbt.setShort("OriginMinZ", (short)minZ);
//				nbt.setIntArray("Blocks", blocks);
//				nbt.setByteArray("Data", data);
//				
//				CompressedStreamTools.writeCompressed(nbt, fout);
//				fout.close();
//				
//			}catch (FileNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			
//			}catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//			}
//			
//		}
	}

	private void load() {
		try {
        	features.clear();
        	
        	File file = new File(this.outputFileName + this.outputFileExt);//TODO CHANGE THIS FILE LOCATION
        	InputStream is = new FileInputStream(file);

            NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(is);
            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("features");
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className).newInstance();
				test.load(nbtFeat);
				features.add(test);
			}
			
			tutOptions.load(nbtFeats.getCompoundTag("options"));
            is.close();

        } catch (Exception e) {
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
	}
	

	public void registerNewExperiment(EntityPlayer player) {
		load();
		tutOptions.name = "test name";
		tutOptions.numTeams = 1;
		tutOptions.teamSize = 1;
		
		int id = TutorialManager.INSTANCE.addExperiment(tutOptions, features, player.worldObj, true);

		player.addChatMessage(new ChatComponentText("Added New Experiment, ID = " + id));
	}
	
	private void joinExperiment(int expID, EntityPlayer player) {
		TutorialManager.INSTANCE.addPlayerToExperiment(expID, (EntityPlayerMP)player);
	}
	
	private void startExperiment(int expID) {
		TutorialManager.INSTANCE.startExperiment(expID);
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
