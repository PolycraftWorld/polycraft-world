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
	
	/**
	 * Aliases of this command
	 * @return
	 *  Enum values
	 */
	public abstract Enum<?>[] getAliases();
	
	/**
	 * Checks if a String is recognized as this command
	 * @return
	 * 	<b>True</b> if the command matches<br>
	 * 	<b>False</b> if the command doesn't match
	 */
	public abstract boolean checkCommand(String command);
}
