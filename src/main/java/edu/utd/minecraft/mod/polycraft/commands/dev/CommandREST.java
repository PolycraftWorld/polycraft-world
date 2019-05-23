package edu.utd.minecraft.mod.polycraft.commands.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandREST extends CommandBase{

	private final List aliases;
	private static final String chatCommandConsent = "consent";
	private static final String chatCommandSkillLevel = "skill_level";
	
  
	
	public CommandREST(){
		aliases = new ArrayList(); 
        aliases.add("rest"); 
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "rest";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/rest [opt:get/set] [True/False]";
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
			String response;
			System.out.println("Processing on Server side"); 
			switch(args[0].toLowerCase()) {
			case chatCommandConsent:
				if (args.length > 2) {
					if(args[2].toLowerCase().equals("true")) {
						response = ServerEnforcer.INSTANCE.IRBTest(sender.getCommandSenderEntity().getName().toLowerCase(), args[1].toLowerCase(), true);
					}else {
						response = ServerEnforcer.INSTANCE.IRBTest(sender.getCommandSenderEntity().getName().toLowerCase(), args[1].toLowerCase(), false);
					}
					player.addChatMessage(new ChatComponentText(response));
				}else {
					player.addChatMessage(new ChatComponentText("use \"/rest consent [opt:get/set] [True/False]\" to give or withdraw IRB consent"));
				}
				break;
			case chatCommandSkillLevel:
				if (args.length > 2) {
						response = SkillLevelTest(sender.getCommandSenderEntity().getName().toLowerCase(), args[1].toLowerCase(), args[2]);
					player.addChatMessage(new ChatComponentText(response));
				}else {
					player.addChatMessage(new ChatComponentText("use \"/rest skill_level [opt:get/set] [skill_level]\" to get or set Skill Level"));
				}
			}
			
		}		
	}

	public String SkillLevelTest(String minecraftUserName, String option, String skill_level) {
		try {
			Map<String, String> params = Maps.newHashMap();
			params.put("skill_level", skill_level);
			String response = "";
			if(option.equals("set"))
				response = NetUtil.post(String.format("%s/skill_level_set/%s/", ServerEnforcer.portalRestUrl, minecraftUserName),params);
			else if(option.equals("get"))
				response = NetUtil.post(String.format("%s/skill_level_get/%s/", ServerEnforcer.portalRestUrl, minecraftUserName),params);
			if(response.length() > 500)
				response = "Error processing command";
			return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error";
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return true;
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
