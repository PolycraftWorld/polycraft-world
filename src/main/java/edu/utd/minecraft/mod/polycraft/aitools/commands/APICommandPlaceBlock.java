package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class APICommandPlaceBlock extends APICommandBase{

	private String argsOverride;
	
	public APICommandPlaceBlock(float cost, String args) {
		super(cost);
		this.argsOverride = args;
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
    	int selectedSlot = player.inventory.currentItem + 36; // must add 36 as this is the first index of the hotbar
    	String[] clientArgs = args;	//store original input for command result
		
		if(!argsOverride.isEmpty())
			args = argsOverride.split(" ");
		
		//check that player has the item in inventory
		String itemID = args[1];
    	int slotToTransfer = -1;
		loop: for(int x = 0; x < player.inventoryContainer.inventorySlots.size(); x++) {
			ItemStack item = player.inventoryContainer.inventorySlots.get(x).getStack();
			if(item != null && Item.getByNameOrId(itemID) == item.getItem()) {
				slotToTransfer = x;
				break loop;
			}
		}
    	
    	if(slotToTransfer == -1) { 
    		// Item not found, return FAIL result
    		return new APICommandResult(clientArgs, APICommandResult.Result.FAIL, "Item not found in inventory", this.stepCost);
    	}else if(slotToTransfer != selectedSlot) {
			//Item not already selected, transfer items on server side
    		int inventoryId = 0; 	// id of the window which was clicked. 0 for player inventory.
            int rightClick = 0; 	// 1 for right-clicking, otherwise 0 
            int holdingShift = 0; 	// Is player holding shift key 
    		// first click slot with desired item to pick up the item
            Minecraft.getMinecraft().playerController.windowClick(inventoryId, slotToTransfer, rightClick, holdingShift, Minecraft.getMinecraft().thePlayer);
            // deposit item into selected slot.  This may also pick up an item in the slot
            Minecraft.getMinecraft().playerController.windowClick(inventoryId, selectedSlot, rightClick, holdingShift, Minecraft.getMinecraft().thePlayer);
            // attempt to deposit item that may have been picked up into the slot we just cleared earlier
            Minecraft.getMinecraft().playerController.windowClick(inventoryId, slotToTransfer, rightClick, holdingShift, Minecraft.getMinecraft().thePlayer);
		}
		
    	//item is selected, go ahead for **AIM and FIRE**  (look where we're placing the block, then right click)
		
		Vec3 targetPos = new Vec3(player.posX + player.getHorizontalFacing().getFrontOffsetX(), 
				player.posY + 0.5, player.posZ + player.getHorizontalFacing().getFrontOffsetZ());
		Vec3 targetPlayerPos = new Vec3(player.posX, player.posY, player.posZ);
		if(args.length == 4) 
    		targetPos = new Vec3(Integer.parseInt(args[2]) + 0.5, player.posY + 0.5, Integer.parseInt(args[3])+0.5);
		else if(args.length == 3 && args[2].equalsIgnoreCase("NONAV")) {
    		AxisAlignedBB area = null;
    		if(TutorialManager.INSTANCE.clientCurrentExperiment != -1) {	//can only get these values if there is a running experiment
				//For NONAV problems, we need to find an air block where we can place this block
				//	This block should be:
				//		1. In the experiment area
				//		2. next to another air block for the player to stand in
				//		3. at the player's y height
    			
    			// get experiment area calculations
    			BlockPos posOffset = new BlockPos(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).posOffset);
    			BlockPos pos2 = new BlockPos(TutorialManager.INSTANCE.getExperiment(TutorialManager.INSTANCE.clientCurrentExperiment).pos2).add(posOffset);
    			area = new AxisAlignedBB(posOffset, pos2);
    			
				//Find and Check for valid placement position
    			boolean isBlockAccessible = false;
		search: for(int x=(int)area.minX; x < area.maxX; x++) {	//search for available position
					for(int z=(int)area.minZ; z < area.maxZ; z++) {
						targetPos = new Vec3(x + 0.5, player.posY + 0.5, z + 0.5);	//set new target position
						if(!area.isVecInside(targetPos) || !player.worldObj.isAirBlock(new BlockPos(targetPos))) {	//skip if outside area or isn't air block
		    				continue;
		    			}
						for(EnumFacing facing: EnumFacing.HORIZONTALS) {	//check all sides of the target block
							if(player.worldObj.isAirBlock(new BlockPos(targetPos).offset(facing))) {
								if(!area.isVecInside(new Vec3(new BlockPos(targetPos).offset(facing))))
									continue;
								targetPlayerPos = new Vec3(new BlockPos(targetPos).offset(facing));
								Vec3 vector1 = targetPos.addVector(0, -1, 0).subtract(targetPlayerPos.addVector(0.5, player.getEyeHeight(), 0.5));
								
								float pitch1 = (float) (((Math.atan2(vector1.zCoord, vector1.xCoord) * 180.0) / Math.PI) - 90.0);
								float yaw1  = (float) (((Math.atan2(Math.sqrt(vector1.zCoord * vector1.zCoord + vector1.xCoord * vector1.xCoord), vector1.yCoord) * 180.0) / Math.PI) - 90.0);
								

								PolycraftMod.SChannel.sendToServer(new TeleportMessage(new BlockPos(targetPlayerPos), String.join(" ", args), pitch1, yaw1));
								System.out.println("Teleport to: " + targetPlayerPos.toString());
								isBlockAccessible = true;	// block is accessible at some point
								break;
							}
						}
						if(isBlockAccessible)
							break search;	//end search
					}
				}
    			if(!isBlockAccessible) {
    				return new APICommandResult(clientArgs, APICommandResult.Result.FAIL, "No available placement locations", this.stepCost);
    			}
    		}
		}else if(args.length == 3 && args[2].equalsIgnoreCase("MacGuffin")) {
			double x = -Math.round(Math.sin(Math.toRadians(player.rotationYaw)));
			double z = Math.round(Math.cos(Math.toRadians(player.rotationYaw)));
			targetPos = new Vec3(player.posX + x, player.posY + 0.5, player.posZ + z);
			System.out.println(targetPos.toString());
		}
		Block block = player.worldObj.getBlockState(new BlockPos(targetPos)).getBlock();
		if(block.getMaterial() != Material.air) {
			return new APICommandResult(clientArgs, APICommandResult.Result.FAIL, "Block \"" + block.getRegistryName() + "\" already exists when trying to place block", this.stepCost);
		}
		
		
		//first make the player look in the correct location
		Vec3 vector = targetPos.addVector(0, -1, 0).subtract(targetPlayerPos.addVector(0, player.getEyeHeight(), 0));
		
		double pitch = ((Math.atan2(vector.zCoord, vector.xCoord) * 180.0) / Math.PI) - 90.0;
		double yaw  = ((Math.atan2(Math.sqrt(vector.zCoord * vector.zCoord + vector.xCoord * vector.xCoord), vector.yCoord) * 180.0) / Math.PI) - 90.0;
		
		player.addChatComponentMessage(new ChatComponentText("x: " + targetPos.xCoord + " :: Y: " + targetPos.yCoord + " :: Z:" + targetPos.zCoord));
		
		player.setPositionAndRotation(player.posX, player.posY, player.posZ, (float) pitch, (float) yaw);
		Minecraft.getMinecraft().entityRenderer.getMouseOver(1);	// must force update minecraft mouseOver object. 
		// Right click
		Minecraft.getMinecraft().playerController.onPlayerRightClick(player, Minecraft.getMinecraft().theWorld, player.inventory.getCurrentItem(), 
				Minecraft.getMinecraft().objectMouseOver.getBlockPos(), Minecraft.getMinecraft().objectMouseOver.sideHit, Minecraft.getMinecraft().objectMouseOver.hitVec);
		
		// if the block is no longer air, we succeeded
		block = player.worldObj.getBlockState(new BlockPos(targetPos)).getBlock();
		if(block.getMaterial() != Material.air) 
			return new APICommandResult(clientArgs, APICommandResult.Result.SUCCESS, "Block \"" + block.getRegistryName() + "\" placed", this.stepCost);
		else 
			return new APICommandResult(clientArgs, APICommandResult.Result.FAIL, "Block not placed", this.stepCost);
	}
}
