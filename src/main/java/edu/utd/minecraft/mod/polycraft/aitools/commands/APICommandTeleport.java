package edu.utd.minecraft.mod.polycraft.aitools.commands;

import org.apache.commons.lang3.math.NumberUtils;

import com.sun.javafx.geom.Vec3f;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.TeleportMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;

public class APICommandTeleport extends APICommandBase{

	private boolean noNav;
	private float teleportPenalty;
	
	public APICommandTeleport() {}
	
	public APICommandTeleport(float cost, float teleportPenalty, boolean noNav) {
		super(cost);
		this.noNav = noNav;
		this.teleportPenalty = teleportPenalty;
		side = Side.SERVER;
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		try{			
			if(noNav) {
				if(args.length > 1) {
					int blockDist = 0;	//default distance for entities.  Teleport to be on top of that entity (ex. picking up an item)
					BlockPos targetPos, startPos = player.getPosition();
		    		boolean isTargetBlock = false;	//False for Entities, true for blocks
		    		
		    		if(args[1].contains(",")) {//block identifiers are the block position delimited by commas
		    			isTargetBlock = true;
						blockDist = 1;	//default block distance for blocks is 1 away or standing next to the block.
		    			String[] delimBlockPos = args[1].split(",");
		    			targetPos = new BlockPos(Integer.parseInt(delimBlockPos[0]),
		    					Integer.parseInt(delimBlockPos[1]),
		    					Integer.parseInt(delimBlockPos[2]));
		    		}else {
		    			int entityId = Integer.parseInt(args[1]);
		    			targetPos = player.worldObj.getEntityByID(entityId).getPosition();
		    		}
		    		
		    		// Calculate distance here
		    		float teleportDist = (float) Math.sqrt(targetPos.distanceSq(startPos.getX(), startPos.getY(), startPos.getZ()));  
		    		
		    		//Check if target position is inside our working area
		    		AxisAlignedBB area = null;
		    		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {	//can only get these values if there is a running experiment
						BlockPos posOffset = new BlockPos(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).posOffset);
		    			area = new AxisAlignedBB(posOffset,
		    					new BlockPos(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).pos2).add(posOffset));
		    			if(!area.isVecInside(new Vec3(targetPos))) {
		    				return (new APICommandResult(args, APICommandResult.Result.FAIL, "Target is outside experiment zone", this.teleportPenalty));
		    			}
					}
		    		
					if(args.length == 3) {	// if blockDistance is a parameter, set it
						if(isTargetBlock && Integer.parseInt(args[2]) == 0) {
							return (new APICommandResult(args, APICommandResult.Result.FAIL, "Cannot teleport 0 blocks away from a block", this.teleportPenalty));
						}
						blockDist = Integer.parseInt(args[2]);
		    		}
					
					boolean isBlockAccessible = false; //boolean for error checking
					
					
					
					if(!isTargetBlock) {	// target is entity, teleport on enttity
						player.setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
						player.playerNetServerHandler.setPlayerLocation(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, player.rotationYaw, player.rotationPitch);
						return (new APICommandResult(args, APICommandResult.Result.SUCCESS, "", (this.stepCost * teleportDist) + teleportPenalty));
					} else if(blockDist == 1) {
						for(EnumFacing facing: EnumFacing.HORIZONTALS) {
							if(player.worldObj.isAirBlock(targetPos.offset(facing))) {
								if(!area.isVecInside(new Vec3(targetPos.offset(facing))))
									continue;
								targetPos = targetPos.offset(facing);
								player.setLocationAndAngles(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, facing.getOpposite().getHorizontalIndex() * 90f, 0f);
						    	player.playerNetServerHandler.setPlayerLocation(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, facing.getOpposite().getHorizontalIndex() * 90f, 0f);
						    	return (new APICommandResult(args, APICommandResult.Result.SUCCESS, "", (this.stepCost * teleportDist) + teleportPenalty));
							}
						}
						return (new APICommandResult(args, APICommandResult.Result.FAIL, "Block not accessible", this.teleportPenalty));
					} else {
						return (new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled error", this.teleportPenalty));
					}
		    	}else {
		    		return (new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax", this.teleportPenalty));
		    	}
			}else {
				if(args.length == 6) {	// expected command "TELEPORT [x] [y] [z] [pitch] [yaw]
					player.setPosition(Integer.parseInt(args[1]) + 0.5, Integer.parseInt(args[2]) + 0.5, Integer.parseInt(args[3]) + 0.5);
			    	player.playerNetServerHandler.setPlayerLocation(Integer.parseInt(args[1]) + 0.5, 
			    			Integer.parseInt(args[2]) + 0.5, Integer.parseInt(args[3]) + 0.5, Float.parseFloat(args[4]), Float.parseFloat(args[5]));
					return new APICommandResult(args, Result.SUCCESS, "", stepCost);
				}else {
		    		return (new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax", this.teleportPenalty));
				}
			}
		}catch(NumberFormatException e) {
			return new APICommandResult(args, Result.FAIL, "Invalid Input. Expected number", this.teleportPenalty);
		}
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.noNav = buf.readBoolean();
		this.teleportPenalty = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeBoolean(noNav);
		buf.writeFloat(teleportPenalty);
	}
}
