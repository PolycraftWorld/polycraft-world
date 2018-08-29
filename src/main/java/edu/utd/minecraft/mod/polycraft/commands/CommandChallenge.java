package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandChallenge  extends CommandBase{

private final List aliases;
  
	
	public CommandChallenge(){
		aliases = new ArrayList(); 
        aliases.add("challenge"); 
        aliases.add("chal"); 
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "challenge";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/challenge <dim>";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		
		if (args.length > 0)
		{
			WorldServer worldserver = (WorldServer) player.getEntityWorld();
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			playerMP.mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, Integer.parseInt(args[0]),	new PolycraftTeleporter(playerMP.mcServer.worldServerForDimension(8)));
			/*
			if(!worldserver.isRemote) {
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
						PrivateProperty pp = new PrivateProperty(true, player, "testPP", "message", x, z, permissions);
						Enforcer.addPrivateProperty(pp);
						
					}
				}
				ServerEnforcer.INSTANCE.sendTempPPDataPackets();
				
			}
			*/
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
