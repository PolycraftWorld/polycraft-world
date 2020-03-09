package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.Arrays;

import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;

public class APICommandChat extends APICommandBase{

	public APICommandChat()	{}
	
	public APICommandChat(float cost) {
		super(cost);
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Do nothing on Server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		if(args.length > 1)
			Minecraft.getMinecraft().thePlayer.sendChatMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
		else
			return new APICommandResult(args, Result.FAIL, "Invalid Syntax", this.stepCost);
		return new APICommandResult(args, Result.SUCCESS, "", this.stepCost);
	}

}
