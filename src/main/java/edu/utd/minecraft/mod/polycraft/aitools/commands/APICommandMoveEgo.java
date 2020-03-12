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

public class APICommandMoveEgo extends APICommandBase{
	
	public APICommandMoveEgo(float cost) {
		super(cost);
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Don't do anything for server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Move player on client side
		if(args.length == 2) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			int angle = 0;
			if(args[1].equalsIgnoreCase("w")) {
				angle = 0;
			}else if(args[1].equalsIgnoreCase("a")) {
				angle = -90;
			}else if(args[1].equalsIgnoreCase("d")) {
				angle = 90;
			}else if(args[1].equalsIgnoreCase("x")) {
				angle = 180;
			}else if(args[1].equalsIgnoreCase("q")) {
				angle = -45;
			}else if(args[1].equalsIgnoreCase("e")) {
				angle = 45;
			}else if(args[1].equalsIgnoreCase("z")) {
				angle = -135;
			}else if(args[1].equalsIgnoreCase("c")) {
				angle = 135;
			}else {
				return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Input", this.stepCost);
			}
			player.setPositionAndRotation(Math.floor(player.posX) + 0.5, player.posY, Math.floor(player.posZ) + 0.5, player.rotationYaw, player.rotationPitch);
			double x = -Math.round(Math.sin(Math.toRadians(player.rotationYaw + angle)));
			double z = Math.round(Math.cos(Math.toRadians(player.rotationYaw + angle)));
			System.out.println("X: " + x + " :: Z: " + z);
			//check if destination is free of collision 
			if(CheckIfBlockCollide(player.worldObj, player.getPosition().add(x, 0, z)))
				return new APICommandResult(args, APICommandResult.Result.FAIL, "Block in path", this.stepCost);
			
			if(!(x == 0 || z == 0)) {	//check if path is free of collisions
				if(CheckIfBlockCollide(player.worldObj, player.getPosition().add(x, 0, 0)) ||
						CheckIfBlockCollide(player.worldObj, player.getPosition().add(0, 0, z))) {
					return new APICommandResult(args, APICommandResult.Result.FAIL, "Block in path", this.stepCost);
				}
			}
			double newX = player.posX - Math.round(Math.sin(Math.toRadians(player.rotationYaw + angle)));
			double newZ = player.posZ + Math.round(Math.cos(Math.toRadians(player.rotationYaw + angle)));
			player.setPositionAndRotation(newX, player.posY, newZ,player.rotationYaw, 0f);
			return new APICommandResult(args, APICommandResult.Result.SUCCESS, "", this.stepCost);
		}else
			return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax", this.stepCost);
	}
	
	private boolean CheckIfBlockCollide(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		if(block.getMaterial() == Material.air || block instanceof BlockMacGuffin) {
			return false;
		}
		return true;
	}

}
