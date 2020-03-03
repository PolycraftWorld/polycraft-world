package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.List;

import edu.utd.minecraft.mod.polycraft.aitools.APIHelper;

public abstract class APICommandBase {
	
	/**
	 * Server side actions to perform when this command is issued/called
	 * @return
	 */
	public abstract APIHelper.CommandResult serverExecute(String command, String args);
	
	/**
	 * Server side actions to perform when this command is issued/called
	 * @return
	 */
	public abstract APIHelper.CommandResult clientExecute(String command, String args);
	
	/**
	 * Return example parameters and usage of this command
	 * @return
	 */
	public abstract String getUsage();
	
	/**
	 * Name of this command
	 * @return
	 */
	public abstract String getName();
	
}
