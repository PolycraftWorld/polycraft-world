package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.minigame.KillWall;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigame;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandMinigame extends CommandBase{
	private final List aliases;
	
	
	public CommandMinigame(){
		aliases = new ArrayList(); 
        aliases.add("minigame"); 
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
				((EntityPlayer) player).addChatComponentMessage(new ChatComponentText("test: "+args[0]));
				if(args[0].equals("start")) {
					
					int[] argsArray;
					if(args.length>1)
					{
						argsArray = new int[args.length-1];
						for(int i=1;i<args.length;i++)
						{
							argsArray[i-1]=Integer.parseInt(args[i]);
						}
					}
					else
					{
						argsArray=null;
					}
					String envoker = player.getDisplayName();
					//PolycraftMinigame test =PolycraftMinigameManager.INSTANCE;
					if(PolycraftMinigameManager.INSTANCE!=null)
					{
						PolycraftMinigameManager.INSTANCE.start(world,argsArray,envoker);
					}
				}
				else if(args[0].equals("stop")) {
					PolycraftMinigameManager.INSTANCE.stop();
					PolycraftMinigameManager.INSTANCE.active=false;
				}
//				else if(args[0].equals("set")) {
//					if(value.length>0)
//					{
//						
//						KillWall.INSTANCE.radius=((double)Integer.parseInt(value[0]));
//						
//					}
//					
//					
//				}
			}
		}
	}
	
	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "Game";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/minigame <command> <values>";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}
	
	

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	 public int getRequiredPermissionLevel()
    {
        return 3;
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
