package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandUpdateWhitelist extends CommandBase{

	private static final String chatCommandTeleportArgUTD = "utd";
	private static final String chatCommandTeleportArgUser = "user";
	private static final String chatCommandTeleportArgPrivateProperty = "pp";
	private final List aliases;
  
	
	public CommandUpdateWhitelist(){
		aliases = new ArrayList(); 
        aliases.add("updatewhitelist");
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "updatewhitelist";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/updatewhitelist";
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
		
		if (world.isRemote) 
        { 
            System.out.println("Not processing on Client side"); 
        } 
		else
		{
			System.out.println("Processing on Server side"); 
			player.addChatMessage(new ChatComponentText(ServerEnforcer.INSTANCE.forceUpdateWhitelist(world)));
		}		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return p_71519_1_.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
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

