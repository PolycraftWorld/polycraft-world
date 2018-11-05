package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CommandExitChallengeRoom extends CommandBase {
	private final List aliases;
	public CommandExitChallengeRoom() {
		aliases = new ArrayList();
		aliases.add("exit");
	}
	
	@Override
	public String getCommandName() {
	
		return "exit";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
	
		return "/exit if in an experiment";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		
		if (!world.isRemote) {
			if(player.dimension == 8) {
				//Remove the player from the experiment if no arguments are passed
				//this will also remove their scoreboards now.
				ExperimentManager.INSTANCE.checkAndRemovePlayerFromExperimentLists(player.getDisplayName());
				//unfreeze player
				ServerEnforcer.INSTANCE.freezePlayer(false, (EntityPlayerMP)player);
				//clear player inventory
				player.inventory.mainInventory = new ItemStack[36];
				player.inventory.armorInventory = new ItemStack[4];
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, 0,	new PolycraftTeleporter(playerMP.mcServer.worldServerForDimension(0)));
			}
		}

	}

}
