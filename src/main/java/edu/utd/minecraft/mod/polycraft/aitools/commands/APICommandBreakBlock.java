package edu.utd.minecraft.mod.polycraft.aitools.commands;

import org.apache.commons.lang3.math.NumberUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.BreakBlockMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.CommandResultMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.TeleportMessage;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class APICommandBreakBlock extends APICommandBase{

	public APICommandBreakBlock() {}
	
	public APICommandBreakBlock(float cost) {
		super(cost);
		side = Side.SERVER;
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		//get block in front of player to break
		BlockPos breakPos = new BlockPos(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY, player.posZ + player.getHorizontalFacing().getFrontOffsetZ());
		
		if(player.worldObj.isAirBlock(breakPos)) {
    		return new APICommandResult(args, APICommandResult.Result.FAIL, "Cannot break air block", this.stepCost);
    	}
		
		// attempt to break the block
    	player.theItemInWorldManager.tryHarvestBlock(breakPos);
    	
    	if(!player.worldObj.isAirBlock(breakPos))
    		return new APICommandResult(args, APICommandResult.Result.FAIL, "Failed to break block", this.stepCost);
    	else {	//When we succeed, break two blocks above, for case of trees
    		player.worldObj.setBlockToAir(breakPos.add(0,1,0));
    		player.worldObj.setBlockToAir(breakPos.add(0,2,0));
    		return new APICommandResult(args, APICommandResult.Result.SUCCESS, "", this.stepCost);
    	}
    	
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		return null;
	}

}
