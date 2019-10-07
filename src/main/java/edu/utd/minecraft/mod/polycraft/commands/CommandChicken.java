package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.entity.ai.EntityAICaptureBases;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ChatComponentText;

public class CommandChicken extends CommandBase{

	private final List aliases;
	  
	public CommandChicken(){
		aliases = new ArrayList(); 
        aliases.add("spawnchicken"); 
	}
	
	@Override
	public String getCommandName() {
		return "spawnchicken";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/spawnchicken [count]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		//create chicken entity
		EntityAnimal newChicken = new EntityChicken(sender.getEntityWorld());
		newChicken.setCustomNameTag("hello World");
		//set chicken position
		newChicken.setPosition(sender.getPosition().getX(), sender.getPosition().getY(), sender.getPosition().getZ());
		//spawn the chicken in the world!
		sender.getEntityWorld().spawnEntityInWorld(newChicken);
		sender.addChatMessage(new ChatComponentText("Chicken: Hello World!"));
	}
}
