package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandPP extends CommandBase{

private final List aliases;
  
	
	public CommandPP(){
		aliases = new ArrayList(); 
        aliases.add("PrivateProperty"); 
        aliases.add("PP"); 
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "PP";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/pp";
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
		
		if(args.length > 0) {
			int x0 = Integer.parseInt(args[0]);
			int z0 = Integer.parseInt(args[1]);
			if(!world.isRemote) {
				player.addChatMessage(new ChatComponentText("Code works"));
				int[] permissions = new int[] {
					0, //"Enter",
					5, //"OpenEnderChest"
					23, //"UsePressurePlate"
					33, //"UseDoor",			
					34, //"UseTrapDoor",
					35, //"UseFenceGate",
					7 //"UseCraftingTable",				
				};
				for(int x=x0; x<=x0+1; x++) {
					for(int z=z0; z<=z0+1; z++) {
						PrivateProperty pp = new PrivateProperty(true, player, "testPP", "message", new Chunk(x,z), new Chunk(x,z), permissions,0);
						Enforcer.addPrivateProperty(pp);
						
					}
				}
				ServerEnforcer.INSTANCE.sendTempPPDataPackets();
			}
		}
		
		
		
		
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return false;
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
