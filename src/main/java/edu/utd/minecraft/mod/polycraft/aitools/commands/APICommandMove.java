package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;

public class APICommandMove extends APICommandBase{

	private float blocksToMove;
	private EnumFacing dir;
	
	public APICommandMove(float cost, float blocksToMove, EnumFacing dir) {
		super(cost);
		this.blocksToMove = blocksToMove;
		this.dir = dir;
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Don't do anything for server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Move player on client side
		
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		EnumFacing facing = player.getHorizontalFacing();
		float yaw = player.rotationYaw;
		
		if(dir != null && dir.getHorizontalIndex() != -1) {
			facing = dir;
			yaw = facing.getHorizontalIndex() * 90;
		}
		
		player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, yaw, 0f);
		player.setPositionAndRotation(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY + facing.getFrontOffsetY(), 
				player.posZ + facing.getFrontOffsetZ(),yaw, 0f);
		
		return new APICommandResult(args, Result.SUCCESS, "", this.stepCost);
	}

}
