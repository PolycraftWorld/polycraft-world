package edu.utd.minecraft.mod.polycraft.aitools.commands;

import org.apache.commons.lang3.math.NumberUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.TeleportMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class APICommandTeleport extends APICommandBase{

	public APICommandTeleport() {}
	
	public APICommandTeleport(float cost) {
		super(cost);
		side = Side.SERVER;
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		try{			
			player.setPosition(Integer.parseInt(args[1]) + 0.5, Integer.parseInt(args[2]) + 0.5, Integer.parseInt(args[3]) + 0.5);
	    	player.playerNetServerHandler.setPlayerLocation(Integer.parseInt(args[1]) + 0.5, 
	    			Integer.parseInt(args[2]) + 0.5, Integer.parseInt(args[3]) + 0.5, Float.parseFloat(args[4]), Float.parseFloat(args[5]));
		}catch(NumberFormatException e) {
			return new APICommandResult(args, Result.FAIL, "Invalid Input. Expected number", stepCost);
		}
		return new APICommandResult(args, Result.SUCCESS, "", stepCost);
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		return null;
	}

}
