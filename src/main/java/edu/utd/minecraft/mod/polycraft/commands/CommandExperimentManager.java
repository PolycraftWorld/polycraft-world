package edu.utd.minecraft.mod.polycraft.commands;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
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
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import edu.utd.minecraft.mod.polycraft.proxy.CommonProxy;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.ClientScoreboard;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import edu.utd.minecraft.mod.polycraft.worldgen.ResearchAssistantLabGenerator;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class CommandExperimentManager  extends CommandBase{

	private static final String chatCommandExpGen = "gen";
	private static final String chatCommandExpnew = "new";
	private static final String chatCommandExpjoin = "join";
	private static final String chatCommandExpStart = "start";
	private final List aliases;
	

	static ArrayList<TutorialFeature> features = new ArrayList<TutorialFeature>();
	static TutorialOptions tutOptions = new TutorialOptions();
	static String outputFileName = "output";
	static String outputFileExt = ".psm";
	static Vec3 pos, size;
  
	public CommandExperimentManager(){
		aliases = new ArrayList(); 
        aliases.add("experimentmanager"); 
        aliases.add("expmanager"); 
        aliases.add("expman"); 
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "expmanager";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/expmanager";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws PlayerNotFoundException {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		
		if (!world.isRemote) 
        { 
            player.addChatMessage(new ChatComponentText("test"));
            PolycraftMod.proxy.openExperimentManagerGui(player);
        } 
		else
		{
			if (args.length > 0)
			{
				if (chatCommandExpnew.equalsIgnoreCase(args[0])) {
					//currently not used
				} 
			}
		}
		
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_, BlockPos blockPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
