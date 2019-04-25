package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import edu.utd.minecraft.mod.polycraft.entity.EntityPaintBall__Old;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandFlingColor extends CommandBase{

	private final List aliases;
  
	
	public CommandFlingColor(){
		aliases = new ArrayList(); 
        aliases.add("colour"); 
        aliases.add("color"); 
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "color";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/color [0-15]";
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
            System.out.println("Not processing on Client side"); 
        } 
		else
		{
			System.out.println("Processing on Server side"); 
			if (args.length > 0) {
				if(Integer.parseInt(args[0]) < 16 && Integer.parseInt(args[0]) >= 0) {
					EntityPaintBall__Old.colorLookup.put(player.getDisplayName(), Integer.parseInt(args[0]));
					player.addChatMessage(new ChatComponentText("Color chosen!"));
				} else {
					player.addChatMessage(new ChatComponentText("Invalid color"));
				}
			}else {
				player.addChatMessage(new ChatComponentText("use \"/register [color]\" to pick your slingshot color (1~15)"));
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
