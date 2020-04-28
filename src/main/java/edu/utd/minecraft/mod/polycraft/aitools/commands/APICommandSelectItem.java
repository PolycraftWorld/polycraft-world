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

public class APICommandSelectItem extends APICommandBase{

	private String argsOverride;
	
	public APICommandSelectItem(float cost, String args) {
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
		
		if(args.length < 2)
			return new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid Syntax", this.stepCost);
		
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
		
    	//double check that item is selected
    	if(player.getHeldItem().getItem() == Item.getByNameOrId(itemID))
    		return new APICommandResult(clientArgs, APICommandResult.Result.SUCCESS, "", this.stepCost);
    	else
    		return new APICommandResult(clientArgs, APICommandResult.Result.FAIL, "unhandled error", this.stepCost);
    		
	}
}
