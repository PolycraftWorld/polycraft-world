package edu.utd.minecraft.mod.polycraft.commands.dev;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.List;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

import java.util.ArrayList;

public class CommandTemp extends CommandBase {

	private final List aliases;
	
	public CommandTemp(){
		aliases = new ArrayList(); 
        aliases.add("temp");
	}
	
	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "temp";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/temp";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws PlayerNotFoundException{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		
		if (world.isRemote) { 
            System.out.println("Not processing on Client side"); 
        } 
		else{
			System.out.println("Processing on Server side"); 
			player.inventory.addItemStackToInventory(new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("ai_tool"))));
			
		}
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return true;
	}
}
