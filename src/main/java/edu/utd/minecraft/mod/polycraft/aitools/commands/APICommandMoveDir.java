package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
import java.util.List;

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

public class APICommandMoveDir extends APICommandBase{

	private EnumFacing dir;
	
	public APICommandMoveDir(float cost, EnumFacing dir) {
		super(cost);
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
		
		// check if there is a block in our path
		if(CheckIfBlockCollide(player.worldObj, new BlockPos(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY + facing.getFrontOffsetY(), 
				player.posZ + facing.getFrontOffsetZ())))
			return new APICommandResult(args, Result.FAIL, "Block in path", this.stepCost);
		
		player.setPositionAndRotation(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY + facing.getFrontOffsetY(), 
				player.posZ + facing.getFrontOffsetZ(),yaw, 0f);
		
		return new APICommandResult(args, Result.SUCCESS, "", this.stepCost);
	}
	
	private boolean CheckIfBlockCollide(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if(block.getMaterial() == Material.air || block instanceof BlockMacGuffin ||  !block.canCollideCheck(world.getBlockState(pos), false)) {
			return false;
		}
		return true;
	}

}
