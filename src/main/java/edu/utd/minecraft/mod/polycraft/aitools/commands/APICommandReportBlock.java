package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import edu.utd.minecraft.mod.polycraft.block.BlockMacGuffin;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.InventoryMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.TeleportMessage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class APICommandReportBlock extends APICommandBase{
	
	public enum ReportBlockType{
		NONE,
		MACGUFFIN,
		TARGET
	}
	
	HashMap<BlockPos, Boolean> blockPosCalled = new HashMap<BlockPos, Boolean>();
	public boolean macguffinReported = false;
	public boolean targetReported = false;
	
	public APICommandReportBlock(float cost) {
		super(cost);
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Don't do anything for server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Process on client side
		
		//Initialize some vars
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	
		if(args.length != 4)
			return new APICommandResult(args, APICommandResult.Result.FAIL, "Missing parameters", this.stepCost);
		
		if(Integer.parseInt(args[1]) > ReportBlockType.values().length || Integer.parseInt(args[1]) < 0)
			return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid input", this.stepCost);
		
		ReportBlockType type = ReportBlockType.values()[Integer.parseInt(args[1])];
		int x = Integer.parseInt(args[2]);
		int z = Integer.parseInt(args[3]);
		BlockPos blockPos = new BlockPos(x, player.getPosition().getY(), z);
		if(blockPosCalled.containsKey(blockPos)) {
			return new APICommandResult(args, APICommandResult.Result.FAIL, "Block already reported", this.stepCost);
		}
		blockPosCalled.put(blockPos, true);
		
		switch(type) {
		case NONE:
			if(player.worldObj.getBlockState(new BlockPos(x, player.getPosition().getY(), z)).getBlock() instanceof BlockMacGuffin ||
					player.worldObj.getBlockState(new BlockPos(x, player.getPosition().getY() - 1, z)).getBlock() == Blocks.lapis_block) {
				APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Report inaccurate", this.stepCost);
				result.setScore(-100000);
				return result;
			}else {
				APICommandResult result = new APICommandResult(args, APICommandResult.Result.SUCCESS, "Report accurate", this.stepCost);
				if(macguffinReported && targetReported)
					return result;
				else
					result.setScore(30);
				return result;
			}
		case MACGUFFIN:
			if(macguffinReported)
				return new APICommandResult(args, APICommandResult.Result.FAIL, "Macguffin already reported", this.stepCost);
			if(player.worldObj.getBlockState(new BlockPos(x, player.getPosition().getY(), z)).getBlock() instanceof BlockMacGuffin) {
				APICommandResult result = new APICommandResult(args, APICommandResult.Result.SUCCESS, "Report accurate", this.stepCost);
				macguffinReported = true;
				if(targetReported)
					result.setScore(16670 + (1024-blockPosCalled.keySet().size())*30);
				else
					result.setScore(16670);
				return result;
			}else {
				APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Report inaccurate", this.stepCost);
				result.setScore(-100000);
				return result;
			}
		case TARGET:
			if(player.worldObj.getBlockState(new BlockPos(x, player.getPosition().getY() - 1, z)).getBlock() == Blocks.lapis_block) {	// TODO: make this more general
				targetReported = true;
				APICommandResult result = new APICommandResult(args, APICommandResult.Result.SUCCESS, "Report Accurate", this.stepCost);
				result.setScore(16670 + (1024-blockPosCalled.keySet().size())*30);
				return result;
			}else {
				APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Report Inaccurate", this.stepCost);
				result.setScore(-100000);
				return result;
			}
		default:
			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled Error", this.stepCost);
			return result;
		}
	}
}
