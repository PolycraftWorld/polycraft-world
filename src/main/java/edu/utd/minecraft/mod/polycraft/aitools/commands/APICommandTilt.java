package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import edu.utd.minecraft.mod.polycraft.block.BlockMacGuffin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class APICommandTilt extends APICommandBase{
	
	public APICommandTilt(float cost) {
		super(cost);
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Don't do anything for server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Turn on client side
		if(args.length == 2) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			if(args[1].equalsIgnoreCase("forward")) {
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 60F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 45F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 30F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 15F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 0F);
				return new APICommandResult(args, APICommandResult.Result.SUCCESS, "", this.stepCost);
			}
			else if(args[1].equalsIgnoreCase("down")) {
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 0F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 15F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 30F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 45F);
				player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, 60F);
				return new APICommandResult(args, APICommandResult.Result.SUCCESS, "", this.stepCost);
			}else {
				return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Input", this.stepCost);
			}
		}else
			return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax", this.stepCost);
	}
}
