package edu.utd.minecraft.mod.polycraft.privateproperty.network;

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
    public CollectMessage onMessage(final CraftMessage message, MessageContext ctx)
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

            	String args[] =  message.items.split("\\s+");
        		boolean missingItem = false;
        		String missingItems = "";
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
        			player.addChatComponentMessage(new ChatComponentText("Missing Item:" + missingItems));
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
