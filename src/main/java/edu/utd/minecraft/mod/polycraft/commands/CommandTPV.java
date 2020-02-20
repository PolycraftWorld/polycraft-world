package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
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

public class CommandTPV extends CommandBase{

	private static final String chatCommandTeleportArgUTD = "utd";
	private static final String chatCommandTeleportArgUser = "user";
	private static final String chatCommandTeleportArgPrivateProperty = "pp";
	private final List aliases;
  
	
	public CommandTPV(){
		aliases = new ArrayList(); 
        aliases.add("teleportv"); 
        aliases.add("tpv"); 
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "tpv";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/tpv x y z yaw pitch";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws PlayerNotFoundException {
		
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		
		if (world.isRemote) 
        { 
            //System.out.println("Not processing on Client side"); 
        } 
		else
		{
			//System.out.println("Processing on Server side"); 
			if (args.length == 5) {
				// teleport to specific location and camera angles
				BlockPos targetPos= new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				float pitch = Float.parseFloat(args[3]);
				float yaw = Float.parseFloat(args[4]);
				player.setPosition(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
            	player.playerNetServerHandler.setPlayerLocation(targetPos.getX() + 0.5, 
            			targetPos.getY() + 0.5, targetPos.getZ() + 0.5, pitch, yaw); //setPlayerLocation is backwards from display in game
				
			}else {
				player.addChatMessage(new ChatComponentText("Position: " + player.getPosition().toString() + "::Yaw: " + player.rotationYaw + "::Pitch: " + player.rotationPitch));
			}
		}		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_, BlockPos blockPos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

}
