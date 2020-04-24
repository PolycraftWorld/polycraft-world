package edu.utd.minecraft.mod.polycraft.aitools.commands;

import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;

public class APICommandCollect extends APICommandBase{
	
	public APICommandCollect() {}
	
	public APICommandCollect(float cost) {
		super(cost);
		side = Side.SERVER;
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {	
		//get block in front of player to check if Inventory
		BlockPos targetPos = new BlockPos(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY, player.posZ + player.getHorizontalFacing().getFrontOffsetZ());
		if(player.worldObj.getBlockState(targetPos).getBlock() == Block.getBlockFromName("polycraft:tree_tap")) {
			for(EnumFacing facing: EnumFacing.HORIZONTALS) {
				if(player.worldObj.getBlockState(targetPos.offset(facing)).getBlock() == Blocks.log) {
					player.inventory.addItemStackToInventory(new ItemStack(Item.getByNameOrId("polycraft:sack_polyisoprene_pellets"),1));
					return new APICommandResult(args, Result.SUCCESS, "", stepCost);
				}else
					continue;
			}
			return (new APICommandResult(args, APICommandResult.Result.FAIL, "No log block near tree tap", this.stepCost));
		}else {
    		return (new APICommandResult(args, APICommandResult.Result.FAIL, "No tree tap found", this.stepCost));
		}
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
	}
}
