package edu.utd.minecraft.mod.polycraft.minigame;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class CommandRace extends CommandBase {

	private final ArrayList<String> aliases;

	public CommandRace() {
		aliases = new ArrayList<String>(1);
		aliases.add("race");
	}

	@Override
	public String getCommandName() {
		return "race";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/race <start/stop> [x1] [z1] [x2] [z2] [x3] [z3] [x4] [z4]";
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
	public void processCommand(ICommandSender sender, String[] args) {

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();

		if (world.isRemote) {
			System.out.println("Not processing on Client side");
		} else {
			System.out.println("Processing on Server side");

			if (args[0].equals("start") && args.length >= 9) {
				RaceGame.INSTANCE.start(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]),
						Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]),
						Integer.parseInt(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]));
				ServerEnforcer.INSTANCE.raceGameUpdate();
			} else if (args[0].equals("stop")) {
				RaceGame.INSTANCE.stop();
				ServerEnforcer.INSTANCE.raceGameUpdate();
			}
		}
	}
}
