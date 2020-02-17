package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CraftMessageHandler implements IMessageHandler<CraftMessage, IMessage>{

	@Override
    public CollectMessage onMessage(final CraftMessage message, final MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
        mainThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
            	EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            	ContainerWorkbench dummyContainer = new ContainerWorkbench(player.inventory, player.worldObj, player.getPosition());
        		InventoryCrafting craftMatrix = new InventoryCrafting(dummyContainer, 3, 3);
            	
            	String args[] =  message.args.split("\\s+");
        		boolean missingItem = false;
        		String missingItems = "";
        		
            	if(args.length == 6) {	//example format "CRAFT 1 minecraft:planks minecraft:planks minecraft:planks minecraft:planks"
            		if(!NumberUtils.isNumber(args[1])) {	//check for number in first argument
            			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "Invalid syntax");
            			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
            			return;
            		}else {
            			
            		}
            			
            	}
            	
        		if(args.length > 9) {
        			craftLoop: for(int x = 1; x < 9; x++) {
        				if(Integer.parseInt(args[x]) == 0)
        					continue craftLoop;
        				for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
        					if(player.inventory.getStackInSlot(i) != null && Item.getIdFromItem(player.inventory.getStackInSlot(i).getItem()) == Integer.parseInt(args[x])) {
        						craftMatrix.setInventorySlotContents(x-1, player.inventory.decrStackSize(i, 1));
        						continue craftLoop;
        					}
        				}
        				missingItem = true;
        				missingItems += Integer.parseInt(args[x]) + "::";
        			}
        		}
        		
        		if(missingItem) {
        			//player.addChatComponentMessage(new ChatComponentText("Missing Item:" + missingItems));
        			APICommandResult result = new APICommandResult(args, APICommandResult.Result.FAIL, "missing items: " + missingItems);
        			PolycraftMod.SChannel.sendTo(new CommandResultMessage(result), player);
        			for(int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
        				player.inventory.addItemStackToInventory(craftMatrix.getStackInSlot(slot));
        			}
        			return;
        		}
        		
        		ItemStack resultItem = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.worldObj);
        		if(resultItem != null) {
        			player.inventory.addItemStackToInventory(resultItem);
        			craftMatrix.clear();
        			player.addChatComponentMessage(new ChatComponentText("Crafted: " + resultItem.getDisplayName()));
        		}else {
        			String result = "";
        			for(int x = 0; x < craftMatrix.getSizeInventory(); x ++) {
        				if(craftMatrix.getStackInSlot(x) != null)
        					result += craftMatrix.getStackInSlot(x).getDisplayName() + "::";
        				else
        					result += "null::";
        			}
        			player.addChatComponentMessage(new ChatComponentText("No Item found for recipe:" + result));
        		}
            }
        });
        return null;
    }

}
