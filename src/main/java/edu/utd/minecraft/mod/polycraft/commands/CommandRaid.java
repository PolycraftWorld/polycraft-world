package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigame;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class CommandRaid extends CommandBase {

	private final ArrayList<String> aliases;

	public CommandRaid() {
		aliases = new ArrayList<String>(1);
		aliases.add("raid");
	}

	@Override
	public String getCommandName() {
		return "raid";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/raid <start/stop> [x] [y] [z] [radius]";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws PlayerNotFoundException {

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();

		if (world.isRemote) {
			System.out.println("Not processing on Client side");
		} else {
			System.out.println("Processing on Server side");

			if (args[0].equals("start") && args.length >= 4) {
				PolycraftMinigameManager.INSTANCE.start(world,
						new int[] { Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]) },
						player.getDisplayNameString());
			} else if (args[0].equals("stop")) {
				PolycraftMinigameManager.INSTANCE.stop();
			}
		}
	}
}
