package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;

public class APICommandMove extends APICommandBase{

	public enum Aliases{
		MOVE, 				// Move Agent 1 block forward
		MOVE_FORWARD,		// Move Agent 1 block forward
    	MOVE_NORTH,			// Move agent 1 block North
    	MOVE_SOUTH,			// Move agent 1 block South
    	MOVE_EAST,			// Move agent 1 block East
    	MOVE_WEST			// Move agent 1 block West
	};

	@Override
	public CommandResult serverExecute(String command, String args) {
		// Don't do anything for server side
		return CommandResult.SUCCESS;
	}

	@Override
	public CommandResult clientExecute(String command, String args) {
		// Move player on client side
		Aliases alias = Aliases.valueOf(command);
		
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		EnumFacing facing = player.getHorizontalFacing();
		float yaw = player.rotationYaw;
		
		switch(alias) {
		case MOVE:
		case MOVE_FORWARD:
			break;
		case MOVE_NORTH:
			facing = EnumFacing.EAST;
			yaw = 180f;
			break;
		case MOVE_SOUTH:
			facing = EnumFacing.NORTH;
			yaw = 0f;
			break;
		case MOVE_EAST:
			facing = EnumFacing.SOUTH;
			yaw = -90f;
			break;
		case MOVE_WEST:
			facing = EnumFacing.WEST;
			yaw = 90f;
			break;
		default:
			break;
		
		}
		
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, yaw, 0f);
		player.setPositionAndRotation(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY + facing.getFrontOffsetY(), 
				player.posZ + facing.getFrontOffsetZ(),yaw, 0f);
		
		return CommandResult.SUCCESS;
	}

	@Override
	public String getUsage() {
		return "MOVE_FORWARD [blocks]";
	}

	@Override
	public String getName() {
		return "Move";
	}

}
