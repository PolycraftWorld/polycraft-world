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

public class APICommandLook extends APICommandBase{
	
	private boolean isRelative;
	private EnumFacing dir;
	
	public APICommandLook(float cost, boolean isRelative, EnumFacing dir) {
		super(cost);
		this.isRelative = isRelative;
		this.dir = dir;
	}
	
	public APICommandLook(float cost, boolean isRelative) {
		this(cost, isRelative, null);
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Don't do anything for server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Turn on client side
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		if(isRelative) {
			if(args.length == 2) {
				if(NumberUtils.isNumber(args[1]) && Integer.parseInt(args[1]) % 15 == 0) {
					float angleDelta = Integer.parseInt(args[1]);
					float playerAngle = (((int)(player.rotationYaw / 15))*15);	// we want to snap to intervals of 15
					for(int x = 0; Math.abs(x) <= Math.abs(angleDelta); x+= angleDelta/5) {
						player.setPositionAndRotation(player.posX, player.posY, player.posZ, playerAngle + x, player.rotationPitch);
					}
					return new APICommandResult(args, APICommandResult.Result.SUCCESS, "", this.stepCost);
				}else {
					return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Input", this.stepCost);	
				}
			}else
				return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax", this.stepCost);
		}else if(dir != null && dir.getHorizontalIndex() != -1){	// no expected parameter
			player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5,dir.getHorizontalIndex() * 90, 0f);
			return new APICommandResult(args, APICommandResult.Result.SUCCESS, "", this.stepCost);
		}else {	// expected parameter is degrees
			if(args.length == 2) {
				if(NumberUtils.isNumber(args[1])) {
					float angle = Integer.parseInt(args[1]);
					player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5,angle, 0f);
					return new APICommandResult(args, APICommandResult.Result.SUCCESS, "", this.stepCost);
				}else 
					return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Input", this.stepCost);	
			}else
				return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax", this.stepCost);
		}
		
	}
}
